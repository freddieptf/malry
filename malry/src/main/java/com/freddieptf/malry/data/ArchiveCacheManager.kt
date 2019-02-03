package com.freddieptf.malry.data

import com.freddieptf.malry.api.Chapter
import com.freddieptf.malry.data.utils.ChapterUtils
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
    private var maxSize: Long? = null

    fun useCache(cachePath: String, maxSize: Long) {
        cacheDir = File(cachePath)
        this.maxSize = maxSize
        if (!cacheDir.exists()) cacheDir.mkdirs() else trimToFit(cacheDir, maxSize, getDirSize(cacheDir))
    }

    fun getCacheDir(): File = cacheDir

    @Throws(IOException::class)
    private fun extractCbz(cbzFilePath: String, chapter: Chapter): File {
        val zipFile = ZipFile(cbzFilePath)
        val destinationDir = File(cacheDir.path, "${chapter.parentTitle}-${chapter.title}")
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

    internal fun getChapterFile(chapter: Chapter): File {
        var cacheEntry = cacheDir.listFiles().find { it.name.equals("${chapter.parentTitle}-${chapter.title}") }
        if (cacheEntry == null) {
            val cacheDirSize = getDirSize(cacheDir)
            if (cacheDirSize >= maxSize!!) {
                println("trim cache: $cacheDirSize/$maxSize")
                val f = File(ChapterUtils.getChapterPathFromDocID(chapter.id))
                trimToFit(cacheDir, f.length(), cacheDirSize)
            }
            cacheEntry = extractCbz(ChapterUtils.getChapterPathFromDocID(chapter.id), chapter)
        }
        return cacheEntry
    }

    fun getDirSize(dir: File): Long {
        var i: Long = 0
        dir.walkBottomUp().filter { it.isFile }.forEach {
            i += it.length()
        }
        return i
    }

    fun trimToFit(dir: File, maxDepth: Int, fitSize: Long, cacheSize: Long) {
        if (fitSize == cacheSize || cacheSize < fitSize) return
        val s = maxSize!! - fitSize
        var cSize = cacheSize

        val walk = dir.walkBottomUp()
        if (maxDepth > 0) {
            walk.maxDepth(maxDepth)
        }
        walk.filter { !it.name.equals(dir.name) }.forEach {
            if (cSize > s) {
                it.walkTopDown().forEach { img ->
                    val l = img.length()
                    if (img.delete()) cSize -= l
                }
                it.delete()
            }
        }
    }

    private fun trimToFit(dir: File, fitSize: Long, cacheSize: Long) {
        trimToFit(dir, 1, fitSize, cacheSize)
    }

    fun clearAll() {
        cacheDir.walkBottomUp().filter { !it.name.equals(cacheDir.name) }
                .forEach {
                    it.deleteRecursively()
                }
    }

}