package com.freddieptf.imageloader

import android.content.Context
import android.graphics.Bitmap
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.common.executors.UiThreadImmediateExecutorService
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.core.ImagePipeline
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.datasource.BaseBitmapReferenceDataSubscriber
import com.facebook.imagepipeline.image.CloseableBitmap
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import java.io.File

object ImageLoader {

    private var imagePipeline: ImagePipeline? = null
    private val map = HashMap<String, CloseableReference<*>>()

    fun init(context: Context, diskCacheMaxSize: Long) {
        if (!Fresco.hasBeenInitialized()) {
            initFresco(context, diskCacheMaxSize)
            imagePipeline = Fresco.getImagePipeline()
        }
    }

    private fun initFresco(context: Context, diskCacheMaxSize: Long) {
        val diskCacheConfig = DiskCacheConfig.newBuilder(context)
                .setMaxCacheSize(diskCacheMaxSize)
                .build()
        Fresco.initialize(context,
                ImagePipelineConfig.newBuilder(context).setMainDiskCacheConfig(diskCacheConfig).build())
    }

    fun clearCache() {
        imagePipeline!!.clearDiskCaches()
    }

    fun getUsedDiskCacheSize(): Long {
        return imagePipeline!!.usedDiskCacheSize
    }

    fun rebuild(context: Context, diskCacheMaxSize: Long) {
        Fresco.shutDown()
        initFresco(context, diskCacheMaxSize)
        imagePipeline = Fresco.getImagePipeline()
    }

    private fun checkPipelineNotNull() {
        if (imagePipeline == null)
            throw IllegalStateException("ImageLoader not initialized. call ImageLoader.init(context)")
    }

    private fun from(url: String): ImageRequest =
            if (url.startsWith("/")) {
                ImageRequest.fromFile(File(url))!!
            } else {
                ImageRequest.fromUri(url)!!
            }

    fun load(url: String, thumb: Boolean = false, load: (Bitmap) -> Unit) {
        checkPipelineNotNull()

        val imageRequestBuilder = ImageRequestBuilder.fromRequest(from(url))
        if (thumb) {
            imageRequestBuilder
                    .setLocalThumbnailPreviewsEnabled(true)
                    .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                    .resizeOptions = ResizeOptions(50, 80)
        }
        val imageRequest = imageRequestBuilder.build()
        var dataSource: DataSource<CloseableReference<CloseableImage>> =
                imagePipeline!!.fetchImageFromBitmapCache(imageRequest, null);
        try {
            val imageReference = dataSource.result
            if (imageReference != null) {
                println("fetchImageFromBitmapCache")
                map.put(url, imageReference)
                load((imageReference.get() as CloseableBitmap).underlyingBitmap)
            } else {
                dataSource = imagePipeline!!.fetchDecodedImage(imageRequest, null)
                dataSource.subscribe(object : BaseBitmapReferenceDataSubscriber() {
                    override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>?) {
                        println("fetchDecodedImage failure")
                        dataSource!!.failureCause!!.printStackTrace()
                    }

                    override fun onNewResultImpl(bitmapReference: CloseableReference<Bitmap>?) {
                        println("fetchDecodedImage")
                        map.put(url, bitmapReference!!)
                        load(bitmapReference.get())
                    }

                }, UiThreadImmediateExecutorService.getInstance())
            }
        } finally {
        }
    }

    fun recycle(vararg url: String?) {
        url.forEach {
            var ref = map.remove(it)
            CloseableReference.closeSafely(ref)
        }
    }

    fun preload(vararg url: String?) {
        checkPipelineNotNull()
        url.forEach {
            it?.let {
                val imageRequest: ImageRequest = from(it)
                imagePipeline!!.prefetchToDiskCache(imageRequest, null)
            }
        }
    }

}