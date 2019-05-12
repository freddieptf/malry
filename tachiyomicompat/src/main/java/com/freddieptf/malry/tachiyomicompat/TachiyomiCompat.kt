package com.freddieptf.malry.tachiyomicompat

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import com.freddieptf.malry.tachiyomicompat.data.MangaSource
import com.freddieptf.malry.tachiyomicompat.data.MangaSourceDao
import dalvik.system.PathClassLoader
import eu.kanade.tachiyomi.source.SourceFactory
import eu.kanade.tachiyomi.source.online.HttpSource
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.InjektScope
import uy.kohesive.injekt.registry.default.DefaultRegistrar

object TachiyomiCompat {

    val TAG = TachiyomiCompat.javaClass.simpleName

    private const val EXTENSION_FEATURE = "tachiyomi.extension"
    private const val METADATA_SOURCE_CLASS = "tachiyomi.extension.class"
    private const val PACKAGE_FLAGS = PackageManager.GET_CONFIGURATIONS or PackageManager.GET_SIGNATURES
    private const val LIB_VERSION_MIN = 1
    private const val LIB_VERSION_MAX = 1
    internal var sources = mutableListOf<HttpSource>()
        get
        private set

    fun init(app: Application) {
        Injekt = InjektScope(DefaultRegistrar())
        Injekt.importModule(DepModule(app))
    }

    suspend fun createSourceManager(context: Context, db: MangaSourceDao) {
        sources = loadTachiyomiExtensions(context, db)
    }

    suspend private fun loadTachiyomiExtensions(context: Context, db: MangaSourceDao): MutableList<HttpSource> {
        val mangaSources: MutableList<MangaSource> = db.getSources()
        val sources: MutableList<HttpSource>

        val installedPkgs = context.packageManager.getInstalledPackages(PACKAGE_FLAGS)
        val extPkgs = installedPkgs.filter { isPackageATachiyomiExtension(it) }

        if (extPkgs.isEmpty()) {
            sources = mutableListOf()
        } else {
            // Load each extension concurrently and wait for completion
            sources = runBlocking {
                val deferred = extPkgs.map { pkgInfo ->
                    async {
                        val extSources = loadExtension(context, pkgInfo).filter { it.lang == "en" }
                        mangaSources.addAll(extSources.map { httpSource ->
                            println("${httpSource.name}")
                            MangaSource(
                                    httpSource.id,
                                    pkgInfo.packageName,
                                    httpSource.javaClass.canonicalName.removePrefix(pkgInfo.packageName),
                                    httpSource.name)
                                    .apply {
                                        installed = true
                                        lang = httpSource.lang
                                    }
                        })
                        extSources
                    }
                }
                deferred.map { it.await() }.flatten()
            }.toMutableList()
        }

        mangaSources.forEach { mangaSource ->
            if (sources.none { it.id == mangaSource.id }) {
                mangaSource.installed = false
                mangaSources.set(mangaSources.indexOf(mangaSource), mangaSource)
            }
        }

        db.insert(mangaSources)

        return sources
    }

    /**
     * Loads an extension given its package name.
     *
     * @param context The application context.
     * @param pkgInfo The package info of the extension.
     */
    private fun loadExtension(context: Context, pkgInfo: PackageInfo): List<HttpSource> {
        val pkgManager = context.packageManager

        val appInfo = try {
            pkgManager.getApplicationInfo(pkgInfo.packageName, PackageManager.GET_META_DATA)
        } catch (error: PackageManager.NameNotFoundException) {
            // Unlikely, but the package may have been uninstalled at this point
            return emptyList()
        }

        val extName = pkgManager.getApplicationLabel(appInfo)?.toString()
                .orEmpty().substringAfter("Tachiyomi: ")
        val versionName = pkgInfo.versionName
        val versionCode = pkgInfo.versionCode

        // Validate lib version
        val majorLibVersion = versionName.substringBefore('.').toInt()
        if (majorLibVersion < LIB_VERSION_MIN || majorLibVersion > LIB_VERSION_MAX) {
            val exception = Exception("Lib version is $majorLibVersion, while only versions " +
                    "$LIB_VERSION_MIN to $LIB_VERSION_MAX are allowed")
            Log.e(TAG, exception.message)
            return emptyList()
        }

        val classLoader = PathClassLoader(appInfo.sourceDir, null, context.classLoader)
        return appInfo.metaData.getString(METADATA_SOURCE_CLASS)
                .split(";")
                .map {
                    val sourceClass = it.trim()
                    if (sourceClass.startsWith("."))
                        pkgInfo.packageName + sourceClass
                    else
                        sourceClass
                }
                .flatMap {
                    try {
                        val obj = Class.forName(it, false, classLoader).newInstance()
                        when (obj) {
                            is HttpSource -> listOf(obj)
                            is SourceFactory -> obj.createSources().filterIsInstance(HttpSource::class.java)
                            else -> throw Exception("Unknown source class type! ${obj.javaClass}")
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        Log.e(TAG, "Extension load error: $extName. $e")
                        return emptyList()
                    }
                }
    }


    /**
     * Returns true if the given package is an extension.
     *
     * @param pkgInfo The package info of the application.
     */
    private fun isPackageATachiyomiExtension(pkgInfo: PackageInfo): Boolean {
        return pkgInfo.reqFeatures.orEmpty().any { it.name == EXTENSION_FEATURE }
    }


}