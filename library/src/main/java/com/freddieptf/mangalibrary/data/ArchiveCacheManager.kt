package com.freddieptf.mangalibrary.data

import com.freddieptf.mangalibrary.data.models.Chapter
import com.freddieptf.mangalibrary.utils.ChapterUtils
import org.apache.commons.compress.utils.IOUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipFile

/**
 * Created by freddieptf on 10/14/18.
 */
object ArchiveCacheManager {

    private lateinit var cacheDir: File
    private val DEFAULT_MAX_SIZE: Long = 100 * 1024 * 1024
    private var maxSize: Long? = null

    fun useCache(cachePath: String, maxSize: Long?) {
        cacheDir = File(cachePath)
        this.maxSize = maxSize?:DEFAULT_MAX_SIZE
        if (!cacheDir.exists()) cacheDir.mkdirs()
    }

    @Throws(IOException::class)
    private fun extractCbz(cbzFilePath: String, chapter: Chapter): File {
        val zipFile = ZipFile(cbzFilePath)
        val destinationDir = File(cacheDir.path, "${chapter.parentName}-${chapter.name}")
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

    fun getChapterFile(chapter: Chapter): File {
        var cacheEntry = cacheDir.listFiles().find { it.name.equals("${chapter.parentName}-${chapter.name}") }
        if (cacheEntry == null) {
            val cacheDirSize = getCacheDirSize()
            if (cacheDirSize >= maxSize!!) {
                println("trim cache: $cacheDirSize/$maxSize")
                val f = File(ChapterUtils.getChapterPathFromDocID(chapter.docID))
                trimToFit(f.length(), cacheDirSize)
            }
            cacheEntry = extractCbz(ChapterUtils.getChapterPathFromDocID(chapter.docID), chapter)
        }
        return cacheEntry
    }

    fun getCacheDirSize(): Long {
        var i: Long = 0
        cacheDir.walkBottomUp().filter { it.isFile }.forEach {
            i += it.length()
        }
        return i
    }

    private fun trimToFit(fitSize: Long, cacheSize: Long) {
        val s = maxSize!! - fitSize
        var cSize = cacheSize
        cacheDir.walkBottomUp().maxDepth(1).filter { !it.name.equals(cacheDir.name) }.forEach {
            if (cSize > s) {
                it.walkTopDown().forEach { img ->
                    val l = img.length()
                    if (img.delete()) cSize -= l
                }
                it.delete()
            }
        }
    }

    fun clearAll() {
        cacheDir.walkBottomUp().filter { !it.name.equals(cacheDir.name) }
                .forEach {
                    it.deleteRecursively()
                }
    }

}