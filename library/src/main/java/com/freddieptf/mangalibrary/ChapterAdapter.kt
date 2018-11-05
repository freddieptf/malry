package com.freddieptf.mangalibrary

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.freddieptf.mangalibrary.data.models.Chapter

/**
 * Created by freddieptf on 9/1/18.
 */
class ChapterAdapter : RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder>(), View.OnClickListener {

    private var data: List<Chapter> = ArrayList()
    private lateinit var chapterClickListener: ChapterClickListener

    fun swapData(data: List<Chapter>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun getData(): List<Chapter> {
        return data
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        holder.itemView.tag = position
        holder.itemView.setOnClickListener(this)
        holder.bind(data.get(position))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        return ChapterViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.detail_frag_ch_item, parent, false)
        )
    }

    fun setChapterClickListener(clickListener: ChapterClickListener) {
        this.chapterClickListener = clickListener
    }

    override fun onClick(v: View) {
        val i = v.tag!! as Int
        if (chapterClickListener != null)
            chapterClickListener.onChapterClick(data.get(i), i)
    }

    class ChapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var tvName: TextView
        private lateinit var tvType: TextView

        init {
            tvName = itemView.findViewById(R.id.tv_ch_name)
        }

        fun bind(chapter: Chapter) {
            tvName.text = chapter.name
            val typedVal = TypedValue()
            if (!chapter.read) {
                itemView.context.theme.resolveAttribute(
                        android.R.attr.textColorPrimary, typedVal, true)
            } else {
                itemView.context.theme.resolveAttribute(
                        android.R.attr.textColorSecondary, typedVal, true)
            }
            tvName.setTextColor(ContextCompat.getColor(itemView.context, typedVal.resourceId))
        }

    }

    interface ChapterClickListener {
        fun onChapterClick(chapter: Chapter, pos: Int)
    }
}