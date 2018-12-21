package com.freddieptf.reader.utils

/**
 * Created by fred on 5/19/15.
 */
interface ReadSignals {

    fun onPageTapToFocus()
    fun onPageLongPress()

    open class SimpleReadSignals : ReadSignals {
        override fun onPageTapToFocus() {}
        override fun onPageLongPress() {}
    }

}
