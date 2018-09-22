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

    fun getChapterUrlFromDocID(docID: String): String {
        return "/storage/" + docID.replace(":", "/")
    }

    fun getChapter(cachePath: String, chapter: Chapter): File {
        val cacheDir = File(cachePath)
        var chDir: File? = null

        var parentDir = cacheDir.listFiles().find { it.name.equals(chapter.parentName) }

        if (parentDir != null) {
            chDir = parentDir.listFiles().find { it.name.equals(chapter.name) }
        }

        if (chDir == null) {
            return extractCbz(getChapterUrlFromDocID(chapter.docID),
                    cachePath + File.pathSeparator + chapter.parentName)
        }

        return chDir
    }

    @Throws(IOException::class)
    private fun extractCbz(cbzFilePath: String, cacheDir: String): File {
        val zipFile = ZipFile(cbzFilePath)
        val destinationDir = File(cacheDir, Uri.parse(cbzFilePath).lastPathSegment)
        destinationDir.mkdirs()

        val entries = zipFile.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            val entryDestination = File(destinationDir.path, entry.name)
            val entryStream = zipFile.getInputStream(entry)
            val fileStream = FileOutputStream(entryDestination)
            IOUtils.copy(entryStream, fileStream)
            IOUtils.closeQuietly(entryStream)
            fileStream.close()
        }

        return destinationDir
    }

}