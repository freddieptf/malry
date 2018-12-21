package com.freddieptf.reader

import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.github.chrisbanes.photoview.PhotoViewAttacher

/**
 * Created by freddieptf on 12/16/18.
 */
class PagerRecyclerAdapter() :
        RecyclerView.Adapter<PagerRecyclerAdapter.Holder>() {

    var pages = ArrayList<String>()

    fun swap(data: ArrayList<String>) {
        this.pages = data
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.itemView.tag = position
        holder.bind(pages.get(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.pager_pic_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return pages?.size ?: 0
    }


    class Holder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView

        init {
            imageView = itemView.findViewById(R.id.pager_ImageView_item)
        }

        fun bind(path: String) {
            var p = PhotoViewAttacher(imageView)
            Glide.with(imageView.context).load(path).into(object :
                    CustomViewTarget<View, Drawable>(imageView) {
                override fun onResourceCleared(placeholder: Drawable?) {}
                override fun onLoadFailed(errorDrawable: Drawable?) {}
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    var rh = resource.intrinsicHeight
                    var rw = resource.intrinsicWidth
                    var ivh = imageView.height
                    var ivw = imageView.width
                    var matrix = Matrix()
                    val scale: Float
                    if (rw * ivh > ivw * rh) {
                        scale = ivh.toFloat() / rh.toFloat()
                    } else {
                        scale = ivw.toFloat() / rw.toFloat()
                    }
                    matrix.setScale(scale, scale)
                    imageView.scaleType = ImageView.ScaleType.MATRIX
                    imageView.imageMatrix = matrix
                    imageView.setImageDrawable(resource)
                }
            })
        }

    }
}