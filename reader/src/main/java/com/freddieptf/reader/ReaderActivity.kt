package com.freddieptf.reader

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout

/**
 * Created by fred on 3/24/15.
 */
class ReaderActivity : AppCompatActivity() {

    companion object {

        fun newIntent(ctx: Context): Intent {
            val intent = Intent(ctx, ReaderActivity::class.java)
            return intent
        }

    }

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        drawerLayout = findViewById(R.id.drawerLayout)

        if (supportFragmentManager.findFragmentByTag(ReaderFragment::class.java.simpleName) == null) {
            val readerFragment = ReaderFragment()
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, readerFragment, ReaderFragment::class.java.simpleName)
                    .commit()

        }

        if (supportFragmentManager.findFragmentByTag(SideFrag::class.java.simpleName) == null) {
            val slidingFrag = SideFrag()
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.sliding_container, slidingFrag, SideFrag::class.java.simpleName)
                    .commit()
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

    fun hideDrawer() {
        drawerLayout.closeDrawer(Gravity.RIGHT)
    }

    fun lockDrawer(lock: Boolean) {
        if (lock)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        else
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED)
    }

    fun hideSystemUI() {
        // Enables regular immersive mode.
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        supportActionBar!!.hide()
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        supportActionBar!!.show()
    }


}
