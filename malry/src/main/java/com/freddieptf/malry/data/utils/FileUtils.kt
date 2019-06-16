package com.freddieptf.malry.data.utils

object FileUtils {

    fun toMB(long: Long): Long {
        return long / (1024 * 1024);
    }
}