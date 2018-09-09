package com.freddieptf.mangalibrary.detail

import com.freddieptf.mangalibrary.data.Chapter

/**
 * Created by freddieptf on 9/1/18.
 */
interface Contract {

    interface View: ChapterAdapter.ChapterClickListener {
        fun onChaptersLoad(data: List<Chapter>)
        fun showTitle(title: String)
    }

}