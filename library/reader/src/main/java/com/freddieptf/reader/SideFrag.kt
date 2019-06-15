package com.freddieptf.reader

import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.freddieptf.malry.api.Chapter
import com.freddieptf.reader.utils.DisplayUtils

/**
 * Created by freddieptf on 11/5/18.
 */
class SideFrag : Fragment() {

    private lateinit var viewModel: ReaderFragViewModel
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: Adapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.frag_sliding_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler = view.findViewById(R.id.rv_slidingChList)
        setBottomMargin()

        recycler.layoutManager = LinearLayoutManager(context!!)
        adapter = Adapter(recycler.layoutManager!! as LinearLayoutManager)
        recycler.adapter = adapter

        savedInstanceState?.apply {
            adapter.state = getParcelable("lay")
        }

        viewModel = ViewModelProviders.of(activity!!).get(ReaderFragViewModel::class.java)
        viewModel.getReadList().observe(this, Observer {
            adapter.swapData(it as ArrayList<Chapter>)
        })

        viewModel.observeCurrentRead().observe(this, Observer {
            adapter.setActive(it)
        })

        adapter.setClickCallbacks(object: Adapter.ClickCallbacks {
            override fun onClick(chapter: Chapter) {
                viewModel.notifyCurrentChapterChange(chapter)
                (activity as ReaderActivity).hideDrawer(null)
            }
        })

    }

    override fun onSaveInstanceState(outState: Bundle) {
        val state = recycler.layoutManager!!.onSaveInstanceState()
        outState.putParcelable("lay", state)
        super.onSaveInstanceState(outState)
    }

    private fun setBottomMargin() {
        if(context!!.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) return
        val params: LinearLayout.LayoutParams = recycler.layoutParams as LinearLayout.LayoutParams
        params.setMargins(0, 0, 0, DisplayUtils.getNavigationBarSize(context!!).y)
        recycler.layoutParams = params
    }

    internal class ChHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val tvChTitle: TextView
        private var isSelected = false
        init {
            tvChTitle = itemView.findViewById(R.id.tv_chTitle)
        }
        fun bind(chapter: Chapter) {
            tvChTitle.text = chapter.title

            val typedVal = TypedValue()
            if (chapter.totalPages == 0 || chapter.lastReadPage < chapter.totalPages - 1) {
                itemView.context.theme.resolveAttribute(
                        android.R.attr.textColorPrimary, typedVal, true)
            } else {
                itemView.context.theme.resolveAttribute(
                        android.R.attr.textColorSecondary, typedVal, true)
            }
            tvChTitle.setTextColor(ContextCompat.getColor(itemView.context, typedVal.resourceId))

            if(isSelected) {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.grey))
            } else {
                itemView.setBackground(null)
            }

        }
        fun setSelected(selected: Boolean): ChHolder {
            isSelected = selected
            return this
        }
    }

    internal class Adapter(val layoutManager: LinearLayoutManager): RecyclerView.Adapter<ChHolder>(), View.OnClickListener {
        private var data = ArrayList<Chapter>()
        private var clickCallbacks: ClickCallbacks? = null
        var state: LinearLayoutManager.SavedState? = null
            set
        var activeReadPos = -1
            get
            private set
        private var activeReadCh : Chapter? = null

        fun setClickCallbacks(clickCallbacks: ClickCallbacks) {
            this.clickCallbacks = clickCallbacks
        }

        fun swapData(data: ArrayList<Chapter>) {
            this.data = data
            if(activeReadCh != null) setActive(activeReadCh!!)
            notifyDataSetChanged()
        }

        fun setActive(chapter: Chapter) {
            this.activeReadCh = chapter
            if(data.isEmpty()) return
            val old = activeReadPos
            this.activeReadPos = data.indexOf(chapter)
            notifyItemChanged(activeReadPos)
            if(old > -1) notifyItemChanged(old)
            if(state != null) layoutManager.onRestoreInstanceState(state)
            state = null
        }

        override fun getItemCount(): Int = data.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChHolder =
            ChHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.chapter_list_item, parent, false)
            )

        override fun onBindViewHolder(holder: ChHolder, position: Int) {
            holder.itemView.tag = position
            holder.itemView.setOnClickListener(this)
            holder.setSelected(activeReadPos == position).bind(data[position])
        }

        override fun onClick(p0: View) {
            val pos = p0.tag as Int
            clickCallbacks?.onClick(data[pos])

        }

        interface ClickCallbacks {
            fun onClick(chapter: Chapter)
        }

    }


}