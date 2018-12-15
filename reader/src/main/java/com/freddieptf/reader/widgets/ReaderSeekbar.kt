package com.freddieptf.reader.widgets

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.freddieptf.reader.R
import com.github.rubensousa.previewseekbar.PreviewSeekBar
import com.github.rubensousa.previewseekbar.PreviewView


/**
 * Created by freddieptf on 11/28/18.
 */
class ReaderSeekbar @JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null) : PreviewSeekBar(context, attrs) {

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

    // couldn't override getProgress..it messes up the preview seekbar
    private fun getRealProgress(): Int {
        return if (direction == ReaderViewPager.DIRECTION.LEFT_TO_RIGHT) max - getProgress()
        else getProgress()
    }

    fun setUpWithPager(pager: ReaderViewPager) {
        if (pager.adapter == null) throw IllegalStateException("cannot setup with pager that has no adapter")

        direction = pager.direction!!
        setSeekerProgressDrawable(direction!!)
        max = pager.adapter!!.count - 1
        progress = pager.currentItem

        pager.addOnPageChangeListener(pagerPageChangeListener)
        pager.addReadSignalCallback(readDirectionChangeListener)

        addOnPreviewChangeListener(object : PreviewView.OnPreviewChangeListener {
            override fun onPreview(previewView: PreviewView?, progress: Int, fromUser: Boolean) {}
            override fun onStartPreview(previewView: PreviewView?, progress: Int) {}
            override fun onStopPreview(previewView: PreviewView?, progress: Int) {
                if (getRealProgress() != pager.currentItem)
                    pager.setCurrentItem(getRealProgress(), false)
            }
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
