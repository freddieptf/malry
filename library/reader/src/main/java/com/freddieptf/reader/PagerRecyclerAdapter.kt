package com.freddieptf.reader

import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.freddieptf.imageloader.ImageLoader
import com.freddieptf.reader.utils.PageApsectMode
import com.freddieptf.reader.utils.ReadMode
import com.github.chrisbanes.photoview.PhotoViewAttacher

/**
 * Created by freddieptf on 12/16/18.
 */
internal class PagerRecyclerAdapter() :
        RecyclerView.Adapter<PagerRecyclerAdapter.Holder>() {

    var pages = ArrayList<String>()
    private var pageAspect: PageApsectMode? = null
    var mode: ReadMode? = null

    fun swap(data: ArrayList<String>, mode: ReadMode) {
        this.pages = data
        ImageLoader.recycle(*data.toTypedArray())
        this.mode = mode
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
        tryPreload(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.pager_pic_item, parent, false)
        return Holder(view)
    }

    override fun onViewRecycled(holder: Holder) {
        holder.recycle()
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int {
        return pages.size
    }

    // @todo @fixme this just looks bad
    private fun tryPreload(currentPos: Int) {
        val pagesToPreload = mutableListOf<String?>()
        if (mode == ReadMode.LEFT_TO_RIGHT) {
            repeat(4) {
                pagesToPreload.add(pages.getOrNull(currentPos - 1 - it))
            }
        } else {
            repeat(4) {
                pagesToPreload.add(pages.getOrNull(currentPos + 1 + it))
            }
        }
        ImageLoader.preload(*pagesToPreload.toTypedArray())
    }

    inner class Holder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var imageView: ImageView
        var aspect: PageApsectMode? = null

        init {
            imageView = itemView.findViewById(R.id.pager_ImageView_item)
        }

        fun bind(path: String) {
            ImageLoader.load(url = path) { bitmap ->
                var m = showImage(bitmap)
                m?.let {
                    PhotoViewAttacher(imageView).apply { setDisplayMatrix(m) }
                }
            }
        }

        fun recycle() {
            ImageLoader.recycle(pages.getOrNull(itemView.tag as Int))
        }

        private fun showImage(resource: Bitmap): Matrix? {
            var matrix: Matrix? = null
            if (aspect?.equals(PageApsectMode.PAGE_FILL) == true) {
                var rh = resource.height
                var rw = resource.width
                var ivh = imageView.height
                var ivw = imageView.width
                val scale: Float
                if (rw * ivh > ivw * rh) {
                    scale = ivh.toFloat() / rh.toFloat()
                } else {
                    scale = ivw.toFloat() / rw.toFloat()
                }
                matrix = Matrix()
                matrix.setScale(scale, scale)
                imageView.scaleType = ImageView.ScaleType.MATRIX
                imageView.imageMatrix = matrix
            } else {
                imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            }
            imageView.setImageBitmap(resource)
            return matrix
        }

    }
}