package com.freddieptf.malry

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.freddieptf.malry.data.cache.ArchiveCacheManager
import com.freddieptf.mangatest.R
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Created by fred on 3/1/15.
 */
class Settings : AppCompatActivity(), Preference.OnPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preferences)
        val toolbar: Toolbar = findViewById(R.id.toolbar_actionBar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Settings"

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        fragmentManager.beginTransaction().replace(
                R.id.preferencesContainer,
                SettingsFragment()).commit()
    }

    override fun onPreferenceChange(preference: Preference, o: Any?): Boolean {
        val v = o!!.toString()

        if (preference is ListPreference) {
            val index = preference.findIndexOfValue(v)
            if (index >= 0) preference.setSummary(preference.entries[index].toString())
        } else {
            when (preference.key) {
                getString(R.string.cbz_cache_size_pref_key) -> {
                    (application as App).initChCache(v.toLong())
                    preference.summary = "${(ArchiveCacheManager.getDirSize(ArchiveCacheManager.getCacheDir())) / (1024 * 1024)}MB / ${v}MB"
                }
                getString(R.string.image_cache_size_limit_key) -> {
                    val newSize = v.toLong()
                    val oldSize = PreferenceManager.getDefaultSharedPreferences(preference.context)
                            .getString(preference.key, null).toLong()
                    if (newSize != oldSize) {
                        GlobalScope.launch(Dispatchers.IO) {
                            CommonGlide.get(applicationContext).clearDiskCache()
                            CommonGlide.tearDown()
                            withContext(Dispatchers.Main) {
                                preference.summary = "${getImgCacheSize(baseContext)}MB / ${v}MB"
                            }
                        }
                    }
                    preference.summary = "${getImgCacheSize(this)}MB / ${v}MB"
                }
                else -> {
                    preference.summary = v
                }
            }
        }

        return true
    }

    fun bindPrefToSummary(preference: Preference) {
        preference.onPreferenceChangeListener = this
        onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.context)
                        .getString(preference.key, null))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }

        return super.onOptionsItemSelected(item)

    }

    companion object {
        private fun getImgCacheSize(ctx: Context): Long {
            var imgCacheSize: Long = ArchiveCacheManager.getDirSize(CommonGlide.getPhotoCacheDir(ctx)!!)
            imgCacheSize /= (1024 * 1024).toLong()
            return imgCacheSize
        }
    }
    

    class SettingsFragment : PreferenceFragment(), CoroutineScope {

        private lateinit var job: Job

        override val coroutineContext: CoroutineContext
            get() = job + Dispatchers.Main

        override fun onAttach(context: Context?) {
            super.onAttach(context)
            job = Job()
        }

        override fun onDestroy() {
            job.cancel()
            super.onDestroy()
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preference)

            val cbzCacheLimitPref = findPreference(getString(R.string.cbz_cache_size_pref_key))
            val imgCacheLimitPref = findPreference(getString(R.string.image_cache_size_limit_key))
            (activity as Settings).bindPrefToSummary(cbzCacheLimitPref)
            (activity as Settings).bindPrefToSummary(imgCacheLimitPref)

            val imgCacheSize: Long = getImgCacheSize(activity!!)
            val cbzCacheSize = (ArchiveCacheManager.getDirSize(ArchiveCacheManager.getCacheDir())) / (1024 * 1024)

            findPreference(getString(R.string.clear_cache_key)).summary =
                    (cbzCacheSize.plus(imgCacheSize)).toString() + "MB used"

            findPreference(getString(R.string.clear_cache_key)).onPreferenceClickListener = Preference.OnPreferenceClickListener { preference ->
                MaterialDialog(activity!!).show {
                    title(R.string.clear_cache)
                    listItemsMultiChoice(R.array.caches) { _, i, selected ->
                        i.forEach {
                            when (it) {
                                0 -> {
                                    launch {
                                        withContext(Dispatchers.IO) {
                                            CommonGlide.get(activity!!).clearDiskCache()
                                        }
                                        val imgCacheSize: Long = getImgCacheSize(activity!!)
                                        val cbzCacheSize = (ArchiveCacheManager.getDirSize(ArchiveCacheManager.getCacheDir())) / (1024 * 1024)
                                        preference.summary = "${cbzCacheSize.plus(imgCacheSize) - imgCacheSize}MB Used"
                                        imgCacheLimitPref.summary = "OMB / " +
                                                "${imgCacheLimitPref.sharedPreferences.getString(imgCacheLimitPref.key, R.integer.glide_cache_default_size.toString())}MB"
                                    }
                                }
                                1 -> {
                                    ArchiveCacheManager.clearAll()
                                    val imgCacheSize: Long = getImgCacheSize(activity!!)
                                    val cbzCacheSize = (ArchiveCacheManager.getDirSize(ArchiveCacheManager.getCacheDir())) / (1024 * 1024)
                                    preference.summary = "${cbzCacheSize.plus(imgCacheSize) - cbzCacheSize}MB Used"
                                    cbzCacheLimitPref.summary = "OMB / " +
                                            "${cbzCacheLimitPref.sharedPreferences.getString(cbzCacheLimitPref.key, R.integer.cbz_cache_default_size.toString())}MB"
                                }
                            }
                        }
                    }
                    positiveButton(R.string.delete)
                    negativeButton(R.string.cancel) { dismiss() }
                }
                true
            }

            findPreference(getString(R.string.pref_about_title)).onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val packageManager = activity.packageManager
                val packageName = activity.packageName
                var version: String

                try {
                    val info = packageManager.getPackageInfo(packageName, 0)
                    version = info.versionName
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                    version = ""
                }

                MaterialDialog(activity).show {
                    title(R.string.app_name)
                    positiveButton(R.string.ok) { dismiss() }
                }
                true
            }


        }
    }

}
