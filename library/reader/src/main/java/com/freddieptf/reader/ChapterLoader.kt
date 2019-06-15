package com.freddieptf.reader

import com.freddieptf.malry.api.ChapterProvider

/**
 * Created by freddieptf on 9/22/18.
 */
object ChapterLoader {

    private var provider: ChapterProvider? = null

    fun useProvider(provider: ChapterProvider) {
        this.provider = provider
    }

    fun getProvider(): ChapterProvider {
        return provider!!
    }

}