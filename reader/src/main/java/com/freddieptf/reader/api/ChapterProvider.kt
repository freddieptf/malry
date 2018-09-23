package com.freddieptf.reader.api

/**
 * Created by freddieptf on 9/22/18.
 */
object ChapterProvider {

    private var provider: Provider? = null

    fun useProvider(provider: Provider) {
        this.provider = provider
    }

    internal fun getProvider(): Provider {
        return provider!!
    }

}