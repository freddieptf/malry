package com.freddieptf.malry.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.freddieptf.malry.api.LibraryItem
import com.freddieptf.localstorage.R

/**
 * Created by freddieptf on 9/1/18.
 */
class LibraryAdapter: RecyclerView.Adapter<LibraryAdapter.DirItemViewHolder>(), View.OnClickListener {

    private var data: List<LibraryItem> = ArrayList()
    private var clickListener: ClickListener? = null

    fun swapData(data: List<LibraryItem>) {
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
            clickListener!!.onLibraryItemClick(data.get(p0!!.tag as Int))
    }


    class DirItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var tvName: TextView
        private var tvSize: TextView

        init {
            tvName = itemView.findViewById(R.id.tv_name)
            tvSize = itemView.findViewById(R.id.tv_size)
        }

        fun bind(item: LibraryItem) {
            tvName.text = item.title
            tvSize.text = item.itemCount.toString() + " items"
        }

    }

    interface ClickListener {
        fun onLibraryItemClick(libraryItem: com.freddieptf.malry.api.LibraryItem)
    }

}