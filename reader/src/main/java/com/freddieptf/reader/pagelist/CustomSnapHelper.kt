package com.freddieptf.reader.pagelist

import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by freddieptf on 12/20/18.
 */
class CustomSnapHelper : PagerSnapHelper() {

    interface OnPageChangeListener {
        fun onPageChange(position: Int)
    }

    private var onPageChangeListeners: List<OnPageChangeListener> = ArrayList()

    fun addOnPageChangeListener(onPageChangeListener: OnPageChangeListener) {
        if (!onPageChangeListeners.contains(onPageChangeListener))
            onPageChangeListeners += onPageChangeListener
    }

    override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager,
                                        velocityX: Int, velocityY: Int): Int {
        val i = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
        onPageChangeListeners.forEach { it.onPageChange(i) } // well, kinda changed...idk
        return i
    }

}