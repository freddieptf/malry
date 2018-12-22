package com.freddieptf.malry.commons

/**
 * Created by freddieptf on 11/5/18.
 */
open class SingleEvent<T>(private val data: T) {

    var seen = false

    fun getData(): T? {
        return if(!seen) {
            seen = true
            data
        } else {
            null
        }
    }

}