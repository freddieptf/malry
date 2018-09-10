package com.freddieptf.reader

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.ArrayList

/**
 * Created by fred on 3/24/15.
 */
class ReaderActivity : AppCompatActivity() {

    companion object {

        fun newIntent(ctx: Context, chapter: String, parent:String, paths: List<String>): Intent {
            val intent = Intent(ctx, ReaderActivity::class.java)
            val bundle = Bundle()
            bundle.putStringArrayList(ReaderFragment.PIC_URIS_EXTRA, paths as ArrayList<String>)
            bundle.putString(ReaderFragment.CHAPTER_TITLE_EXTRA, chapter)
            bundle.putString(ReaderFragment.CHAPTER_PARENT_EXTRA, parent)
            intent.putExtras(bundle)
            return intent
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)

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

}
