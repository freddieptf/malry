package com.freddieptf.reader.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat

import androidx.viewpager.widget.ViewPager

/**
 * Created by fred on 5/19/15.
 */
class ReaderViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ViewPager(context, attrs) {

    companion object {
        private val TAG = ReaderViewPager::class.java.simpleName
    }

    private var startDragXPos = 0f
    private var readProgressListener: ReadProgressListener? = null
    private var readSignals: ReadSignals? = null
    private var direction: DIRECTION? = null
    private var detector: GestureDetectorCompat? = null

    enum class DIRECTION {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT
    }

    fun setReadProgressListener(readProgressListener: ReadProgressListener) {
        this.readProgressListener = readProgressListener
    }

    fun setReadDirection(direction: DIRECTION) {
        this.direction = direction
    }

    fun setReadSignalCallback(callbacks: ReadSignals) {
        this.readSignals = callbacks
    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        var i = if (direction == DIRECTION.LEFT_TO_RIGHT) adapter!!.count - item - 1 else item
        super.setCurrentItem(i, smoothScroll)
    }

    override fun setCurrentItem(item: Int) {
        var i = if (direction == DIRECTION.LEFT_TO_RIGHT) adapter!!.count - item - 1 else item
        super.setCurrentItem(i)
    }

    override fun getCurrentItem(): Int {
        if (direction == DIRECTION.LEFT_TO_RIGHT) {
            return (adapter?.count ?: 0) - super.getCurrentItem() - 1
        }
        return super.getCurrentItem()
    }


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val inDragX = ev.x
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startDragXPos = ev.x
            }
            MotionEvent.ACTION_MOVE -> {
                if (currentItem == 0) {
                    when {
                        (direction == DIRECTION.LEFT_TO_RIGHT && startDragXPos > inDragX) ||
                                (direction == DIRECTION.RIGHT_TO_LEFT && startDragXPos < inDragX) -> {
                            readProgressListener!!.onSwipeToPreviousCh()
                        }
                    }
                } else if (currentItem == (adapter?.count ?: 0) - 1) {
                    when {
                        (direction == DIRECTION.LEFT_TO_RIGHT && startDragXPos < inDragX) ||
                                (direction == DIRECTION.RIGHT_TO_LEFT && startDragXPos > inDragX) ->
                            readProgressListener!!.onSwipeToNextCh()
                    }
                }

            }
        }

        return super.onInterceptTouchEvent(ev)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (detector == null)
            detector = GestureDetectorCompat(context, gestureListenere)
        detector!!.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    interface ReadProgressListener {
        fun onSwipeToNextCh()
        fun onSwipeToPreviousCh()
    }

    interface ReadSignals {
        fun pageReadToggle()
    }

    private var gestureListenere = object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return true
        }
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            readSignals?.pageReadToggle()
            return true
        }
    }

}
