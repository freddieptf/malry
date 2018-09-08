package com.freddieptf.mangalibrary

import android.support.v4.provider.DocumentFile
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by freddieptf on 9/1/18.
 */
class LibraryAdapter: RecyclerView.Adapter<LibraryAdapter.DirItemViewHolder>(), View.OnClickListener {

    private var data: List<DocumentFile> = ArrayList()
    private var clickListener: ClickListener? = null

    fun swapData(data: List<DocumentFile>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    override fun getItemCount(): Int {
       return data.size
    }

    override fun onBindViewHolder(holder: DirItemViewHolder, position: Int) {
        holder.itemView.tag = position
        holder.itemView.setOnClickListener(this)
        holder.bind(data.get(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirItemViewHolder {
        return DirItemViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.library_list_item, parent, false)
        )
    }

    override fun onClick(p0: View?) {
        if(clickListener != null)
            clickListener!!.onDirClick(data.get(p0!!.tag as Int))
    }


    class DirItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var tvName: TextView
        private var tvSize: TextView

        init {
            tvName = itemView.findViewById(R.id.tv_name)
            tvSize = itemView.findViewById(R.id.tv_size)
        }

        fun bind(dir: DocumentFile) {
            tvName.text = dir.name
            tvSize.text = dir.listFiles().size.toString()
        }

    }

    interface ClickListener {
        fun onDirClick(dir: DocumentFile)
    }

}