package com.freddieptf.reader.pagelist

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import com.freddieptf.reader.utils.ReadSignals

/**
 * Created by freddieptf on 12/20/18.
 */
class CustomRecyclerView constructor(ctx: Context, attributeSet: AttributeSet? = null) :
        RecyclerView(ctx, attributeSet) {

    private var readSignals = ArrayList<ReadSignals.SimpleReadSignals>()
    private var detector: GestureDetectorCompat

    private var gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            readSignals.forEach { it.onPageTapToFocus() }
            return true
        }
    }

    init {
        detector = GestureDetectorCompat(ctx, gestureListener)
    }

    fun addReadSignalCallback(callbacks: ReadSignals.SimpleReadSignals) {
        if (!readSignals.contains(callbacks)) readSignals.add(callbacks)
    }

    // last resort since i can't seem to do the onTouchListener right
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        detector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

}