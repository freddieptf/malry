package com.freddieptf.comicreader

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by fred on 3/24/15.
 */
class ReaderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)

        if (supportFragmentManager.findFragmentByTag(ReaderFragment.javaClass.simpleName) == null) {
            val readerFragment = ReaderFragment()
            readerFragment.arguments = intent.extras
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, readerFragment, ReaderFragment.javaClass.simpleName)
                    .commit()
        }

    }

}
