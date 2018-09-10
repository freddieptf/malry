package com.freddieptf.mangalibrary.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.freddieptf.mangalibrary.R
import com.freddieptf.mangalibrary.data.Chapter

/**
 * Created by freddieptf on 9/1/18.
 */
class ChapterAdapter: RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder>(), View.OnClickListener {

    private var data: List<Chapter> = ArrayList()
    private lateinit var chapterClickListener: ChapterClickListener

    fun swapData(data: List<Chapter>) {
        this.data = data
        notifyDataSetChanged()
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
            chapterClickListener.onChapterClick(data.get(i))
    }

    class ChapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private var tvName: TextView
        private lateinit var tvType: TextView

        init {
            tvName = itemView.findViewById(R.id.tv_ch_name)
        }

        fun bind(chapter: Chapter) {
            tvName.text = chapter.name
        }

    }

    interface ChapterClickListener {
        fun onChapterClick(chapter: Chapter)
    }
}