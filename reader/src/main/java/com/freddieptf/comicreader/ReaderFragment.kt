package com.freddieptf.comicreader

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.freddieptf.comicreader.widgets.CustomViewPager

/**
 * Created by fred on 3/22/15.
 */
class ReaderFragment : Fragment() {
    private var adapter: PicPagerAdapter? = null
    private val pos = 0
    private var viewPager: CustomViewPager? = null
    private var chapterTitle: String? = null

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
        val pages = bundle.getStringArrayList(PIC_URIS)
        chapterTitle = bundle.getString(CHAPTER_TITLE)

        viewPager = view.findViewById<View>(R.id.pager_MangaPics) as CustomViewPager
        adapter = PicPagerAdapter(pages)
        viewPager!!.adapter = adapter
        viewPager!!.setCurrentItem(pos, false)
    }

    companion object {

        val PIC_URIS = "pic_urls"
        val CHAPTER_TITLE = "chapterTitle"
    }
}
