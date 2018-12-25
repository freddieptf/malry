package com.freddieptf.malry

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.freddieptf.malry.intro.IntroFragment
import com.freddieptf.malry.library.LibraryFragment
import com.freddieptf.malry.providers.ProviderFrag
import com.freddieptf.mangatest.R


class MainActivity : AppCompatActivity(), LibraryFragment.LibraryFragmentContainer {
    internal val TAG = this.javaClass.simpleName
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar_actionBar)
        setSupportActionBar(toolbar)

        if (PrefUtils.hasSetUp(this)) showLibraryFragment()
        else showIntroFragment()

    }

    private fun showLibraryFragment() {
        var libraryFragment = supportFragmentManager
                .findFragmentByTag(LibraryFragment::class.java.simpleName)
        if (libraryFragment == null) {
            libraryFragment = LibraryFragment()
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, libraryFragment, LibraryFragment::class.java.simpleName)
                    .commit()
        }
    }

    private fun showIntroFragment() {
        var fragment = supportFragmentManager
                .findFragmentByTag(IntroFragment::class.java.simpleName)
        if (fragment == null) {
            fragment = IntroFragment()
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, fragment, IntroFragment::class.java.simpleName)
                    .commit()
        }
    }

    override fun getFragmentContainerId(): Int {
        return R.id.container
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_manga_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        when (id) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_settings -> {
                val intent = Intent(this, Settings::class.java)
                startActivity(intent)
            }
            R.id.menu_providers -> {

                if (supportFragmentManager.findFragmentByTag(ProviderFrag.javaClass.simpleName) != null)
                    return true

                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, ProviderFrag(), ProviderFrag.javaClass.simpleName)
                        .addToBackStack(null)
                        .commit()

            }
        }

        return super.onOptionsItemSelected(item)
    }

}




