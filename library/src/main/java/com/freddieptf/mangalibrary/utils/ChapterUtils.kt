package com.freddieptf.mangalibrary.utils

import android.net.Uri
import com.freddieptf.mangalibrary.data.models.Chapter
import org.apache.commons.compress.utils.IOUtils

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipFile


/**
 * Created by freddieptf on 9/8/18.
 */
object ChapterUtils {

    fun getChapterPathFromDocID(docID: String): String {
        return "/storage/" + docID.replace(":", "/")
    }

}