package com.freddieptf.localstorage.utils


/**
 * Created by freddieptf on 9/8/18.
 */
object ChapterUtils {

    fun getChapterPathFromDocID(docID: String): String {
        return "/storage/" + docID.replace(":", "/")
    }

}