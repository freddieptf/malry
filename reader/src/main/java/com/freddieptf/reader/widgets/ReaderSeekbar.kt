package com.freddieptf.reader.widgets

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.freddieptf.reader.R
import com.freddieptf.reader.pagelist.CustomSnapHelper
import com.freddieptf.reader.utils.ReadMode
import com.github.rubensousa.previewseekbar.PreviewSeekBar
import com.github.rubensousa.previewseekbar.PreviewView


/**
 * Created by freddieptf on 11/28/18.
 */
class ReaderSeekbar @JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null)
    : PreviewSeekBar(context, attrs), CustomSnapHelper.OnPageChangeListener {

    private var direction: ReadMode? = null

    init {
    }

    fun setUp(direction: ReadMode, progress: Int, max: Int) {
        this.direction = direction
        setSeekerProgressDrawable(direction)
        this.max = max
        this.progress = progress
    }

    override fun onPageChange(position: Int) {
        progress = position
    }

    fun setOnSeekListener(onSeekListener: OnSeekListener?) {
        addOnPreviewChangeListener(SimplePreviewChangeListener(onSeekListener))
    }

    // couldn't override getProgress..it messes up the preview seekbar
    fun getRealProgress(): Int {
        return if (direction == ReadMode.LEFT_TO_RIGHT) max - getProgress()
        else getProgress()
    }

    private fun setSeekerProgressDrawable(direction: ReadMode) {
        when (direction) {
            ReadMode.LEFT_TO_RIGHT -> {
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
            ReadMode.RIGHT_TO_LEFT -> {
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


    interface OnSeekListener {
        fun onSeekTo(position: Int)
    }

    internal class SimplePreviewChangeListener constructor(var onSeekListener: OnSeekListener?) : PreviewView.OnPreviewChangeListener {
        override fun onPreview(previewView: PreviewView?, progress: Int, fromUser: Boolean) {}
        override fun onStartPreview(previewView: PreviewView?, progress: Int) {}
        override fun onStopPreview(previewView: PreviewView?, progress: Int) {
            onSeekListener?.onSeekTo(progress)
        }
    }


}
