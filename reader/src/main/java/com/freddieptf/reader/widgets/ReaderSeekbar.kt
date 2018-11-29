package com.freddieptf.reader.widgets

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.freddieptf.reader.R


/**
 * Created by freddieptf on 11/28/18.
 */
class ReaderSeekbar @JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null) : SeekBar(context, attrs) {

    private val pagerPageChangeListener: ViewPager.SimpleOnPageChangeListener
    private val readDirectionChangeListener: ReaderViewPager.SimpleReadSignals
    private var direction: ReaderViewPager.DIRECTION? = null

    init {

        pagerPageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                progress = position
            }
        }

        readDirectionChangeListener = object : ReaderViewPager.SimpleReadSignals() {
            override fun onReadDirectionChange(direction: ReaderViewPager.DIRECTION) {
                this@ReaderSeekbar.direction = direction
                setSeekerProgressDrawable(direction)
            }
        }

    }

    override fun getProgress(): Int {
        return if (direction == ReaderViewPager.DIRECTION.LEFT_TO_RIGHT) max - super.getProgress()
        else super.getProgress()
    }

    fun setUpWithPager(pager: ReaderViewPager) {
        if (pager.adapter == null) throw IllegalStateException("cannot setup with pager that has no adapter")

        direction = pager.direction!!
        setSeekerProgressDrawable(direction!!)
        max = pager.adapter!!.count - 1
        progress = pager.currentItem

        pager.addOnPageChangeListener(pagerPageChangeListener)
        pager.addReadSignalCallback(readDirectionChangeListener)

        setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar) {}
            override fun onStopTrackingTouch(p0: SeekBar) {
                if (p0.progress != pager.currentItem) pager.setCurrentItem(p0.progress, true)
            }

            override fun onProgressChanged(p0: SeekBar, p1: Int, p2: Boolean) {}
        })

    }

    private fun setSeekerProgressDrawable(direction: ReaderViewPager.DIRECTION) {
        when (direction) {
            ReaderViewPager.DIRECTION.LEFT_TO_RIGHT -> {
                val ld = getProgressDrawable() as LayerDrawable // lol if this ever changes
                val d1 = ld.findDrawableByLayerId(android.R.id.background)
                val d2 = ld.findDrawableByLayerId(android.R.id.progress)

                val tVal = TypedValue()
                context!!.theme.resolveAttribute(android.R.attr.colorAccent, tVal, true)
                d1.setColorFilter(ContextCompat.getColor(context, tVal.resourceId),
                        PorterDuff.Mode.SRC_OVER)
                d2.setColorFilter(ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_IN)
            }
            ReaderViewPager.DIRECTION.RIGHT_TO_LEFT -> {
                val ld = getProgressDrawable() as LayerDrawable  // lol if this ever changes
                val d1 = ld.findDrawableByLayerId(android.R.id.background)
                val d2 = ld.findDrawableByLayerId(android.R.id.progress)

                val tVal = TypedValue()
                context!!.theme.resolveAttribute(android.R.attr.colorAccent, tVal, true)
                d1.setColorFilter(ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_IN)
                d2.setColorFilter(ContextCompat.getColor(context, tVal.resourceId),
                        PorterDuff.Mode.SRC_OVER)
            }
        }
    }

}
