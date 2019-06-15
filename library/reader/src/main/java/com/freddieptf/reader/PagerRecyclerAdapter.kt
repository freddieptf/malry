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
import com.freddieptf.reader.utils.PageApsectMode
import com.github.chrisbanes.photoview.PhotoViewAttacher

/**
 * Created by freddieptf on 12/16/18.
 */
internal class PagerRecyclerAdapter() :
        RecyclerView.Adapter<PagerRecyclerAdapter.Holder>() {

    var pages = ArrayList<String>()
    private var pageAspect: PageApsectMode? = null

    fun swap(data: ArrayList<String>) {
        this.pages = data
        notifyDataSetChanged()
    }

    fun setPageAspect(aspect: PageApsectMode) {
        this.pageAspect = aspect
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.itemView.tag = position
        holder.aspect = pageAspect
        holder.bind(pages.get(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.pager_pic_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return pages.size
    }

    inner class Holder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var imageView: ImageView
        var aspect: PageApsectMode? = null

        init {
            imageView = itemView.findViewById(R.id.pager_ImageView_item)
        }

        private var customViewTarget = object : CustomViewTarget<View, Drawable>(imageView) {
            override fun onResourceCleared(placeholder: Drawable?) {
                imageView.setImageDrawable(placeholder)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {}
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                if (aspect?.equals(PageApsectMode.PAGE_FILL) == true) {
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
                } else {
                    imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                }
                imageView.setImageDrawable(resource)
            }
        }

        fun bind(path: String) {
            var p = PhotoViewAttacher(imageView)
            Glide.with(imageView.context)
                    .load(path)
                    .into(customViewTarget)
        }

    }
}