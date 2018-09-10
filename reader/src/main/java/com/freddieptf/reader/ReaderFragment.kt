package com.freddieptf.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.freddieptf.reader.data.ReaderDataManager
import com.freddieptf.reader.data.models.ChapterCache
import com.freddieptf.reader.widgets.CustomViewPager
import java.util.*

/**
 * Created by fred on 3/22/15.
 */
class ReaderFragment : Fragment() {

    companion object {

        val PIC_URIS_EXTRA = "reader.pic_uris"
        val CHAPTER_TITLE_EXTRA = "reader.chapter_title"
        val CHAPTER_PARENT_EXTRA = "reader.chapter_parent"

        fun newInstance(chapter: String, parent:String, paths: List<String>): ReaderFragment {
            val frag = ReaderFragment()
            val bundle = Bundle()
            bundle.putStringArrayList(PIC_URIS_EXTRA, paths as ArrayList<String>)
            bundle.putString(CHAPTER_TITLE_EXTRA, chapter)
            bundle.putString(CHAPTER_PARENT_EXTRA, parent)
            frag.arguments = bundle
            return frag
        }

    }

    private var adapter: PicPagerAdapter? = null
    private val pos = 0
    private var viewPager: CustomViewPager? = null
    private lateinit var chapterTitle: String

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reader, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if ((activity as AppCompatActivity).supportActionBar != null) {
            (activity as AppCompatActivity).supportActionBar!!.subtitle = chapterTitle
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments!!
        val pages = bundle.getStringArrayList(PIC_URIS_EXTRA)
        chapterTitle = bundle.getString(CHAPTER_TITLE_EXTRA)
        val parent = bundle.getString(CHAPTER_PARENT_EXTRA)

        viewPager = view.findViewById<View>(R.id.pager_MangaPics) as CustomViewPager
        adapter = PicPagerAdapter(pages)
        viewPager!!.adapter = adapter
        viewPager!!.setCurrentItem(pos, false)
    }

}
