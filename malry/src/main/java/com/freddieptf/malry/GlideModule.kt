package com.freddieptf.malry

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule

/**
 * Created by freddieptf on 12/25/18.
 */
@GlideModule(glideName = "CommonGlide")
class GlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val diskCacheSizeBytes = PrefUtils.getImgCacheSizeLimit(context)
        builder.setDiskCache(
                ExternalPreferredCacheDiskCacheFactory(context, diskCacheSizeBytes)
        )
    }

}