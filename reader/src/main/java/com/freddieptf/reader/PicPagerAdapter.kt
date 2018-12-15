package com.freddieptf.reader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView

/**
 * Created by fred on 3/22/15.
 */
class PicPagerAdapter(val pages: List<String>?) : PagerAdapter() {

    override fun getCount(): Int {
        return pages?.size ?: 0
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(R.layout.pager_pic_item, container, false)
        val viewHolder = ViewHolder(view)
        viewHolder.bind(pages!![position])
        container.addView(view)
        return view
    }

    internal inner class ViewHolder(view: View) {
        var imageView: PhotoView
        var pageNumber: TextView

        init {
            imageView = view.findViewById<View>(R.id.pager_ImageView_item) as PhotoView
            pageNumber = view.findViewById<View>(R.id.tv_mangaPageNumber) as TextView
        }

        fun bind(path: String) {
            Glide.with(imageView.context).load(path).into(imageView)
        }
    }
}


