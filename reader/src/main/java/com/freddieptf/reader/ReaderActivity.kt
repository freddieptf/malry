package com.freddieptf.reader

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.util.ArrayList

/**
 * Created by fred on 3/24/15.
 */
class ReaderActivity : AppCompatActivity() {

    companion object {

        fun newIntent(ctx: Context, chapter: String, parent: String, paths: List<String>): Intent {
            val intent = Intent(ctx, ReaderActivity::class.java)
            val bundle = Bundle()
            bundle.putStringArrayList(ReaderFragment.PIC_URIS_EXTRA, paths as ArrayList<String>)
            bundle.putString(ReaderFragment.CHAPTER_TITLE_EXTRA, chapter)
            bundle.putString(ReaderFragment.CHAPTER_PARENT_EXTRA, parent)
            intent.putExtras(bundle)
            return intent
        }

    }

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (supportFragmentManager.findFragmentByTag(ReaderFragment.javaClass.simpleName) == null) {
            val bundle = intent.extras!!
            val readerFragment = ReaderFragment.newInstance(
                    bundle.getString(ReaderFragment.CHAPTER_TITLE_EXTRA),
                    bundle.getString(ReaderFragment.CHAPTER_PARENT_EXTRA),
                    bundle.getStringArrayList(ReaderFragment.PIC_URIS_EXTRA)
            )
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, readerFragment, ReaderFragment.javaClass.simpleName)
                    .commit()

        }

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        addSystemUiVisibiltyChangeListener()
        hideSystemUI()
    }

    private fun addSystemUiVisibiltyChangeListener() {
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                // The system bars are visible.
                supportActionBar!!.show()
                eventuallyHide()
            } else {
                // The system bars are NOT visible.
                supportActionBar!!.hide()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun eventuallyHide(){
        Handler().postDelayed(Runnable {
            hideSystemUI()
        }, 5000)
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }


}
