package com.freddieptf.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.freddieptf.reader.api.Chapter
import com.freddieptf.reader.api.ChapterProvider
import com.freddieptf.reader.data.ReaderDataManager
import com.freddieptf.reader.data.models.ChapterCache
import com.freddieptf.reader.widgets.ReaderViewPager

/**
 * Created by fred on 3/22/15.
 */
class ReaderFragment : Fragment(), ReaderViewPager.ReadProgressListener {

    private var adapter: PicPagerAdapter? = null
    private var pos = 0
    private var viewPager: ReaderViewPager? = null
    private lateinit var chapterTitle: String
    private lateinit var parent: String
    private lateinit var viewModel: ReaderFragViewModel
    private var dialog: AlertDialog? = null

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reader, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if ((activity as AppCompatActivity).supportActionBar != null) {
            (activity as AppCompatActivity).supportActionBar!!.title = chapterTitle
        }
    }

    private fun showChapter(chapter: Chapter) {
        val pages = chapter.paths
        chapterTitle = chapter.chapter
        parent = chapter.parent
        (activity as AppCompatActivity).supportActionBar!!.title = chapterTitle

        adapter = PicPagerAdapter(pages)
        viewPager!!.adapter = adapter

        viewModel.getChCache(parent, chapterTitle).observe(this, observer)
    }

    private var observer = Observer<ChapterCache> { cache ->
        if(cache != null && cache.id == parent +"/"+chapterTitle) {
            pos = cache.page
            viewPager!!.setCurrentItem(pos, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPager = view.findViewById<View>(R.id.pager_MangaPics) as ReaderViewPager
        viewPager!!.setReadProgressListener(this)

        viewModel = ViewModelProviders.of(this).get(ReaderFragViewModel::class.java)

        val chapter = ChapterProvider.getProvider().getCurrentRead()
        showChapter(chapter)

    }

    override fun onSwipeToNextCh() {
        val hasNext = ChapterProvider.getProvider().hasNextRead()
        if (hasNext && (dialog == null || !dialog!!.isShowing)) {
            dialog = AlertDialog.Builder(context!!)
                    .setMessage("Would you like to move the next chapter")
                    .setPositiveButton("Next") { dialogInterface, _ ->
                        cacheLastSeenPage()
                        showChapter(ChapterProvider.getProvider().getNextRead()!!)
                        dialogInterface!!.dismiss()
                    }
                    .setNegativeButton("Cancel", { dialogInterface, _ -> dialogInterface.dismiss() })
                    .show()
        }

    }

    override fun onSwipeToPreviousCh() {
        val hasPrev = ChapterProvider.getProvider().hasPreviousRead()
        if (hasPrev && (dialog == null || !dialog!!.isShowing)) {
            dialog = AlertDialog.Builder(context!!)
                    .setMessage("Would you like to move the previous chapter")
                    .setPositiveButton("Previous") { dialogInterface, _ ->
                        cacheLastSeenPage()
                        showChapter(ChapterProvider.getProvider().getPreviousRead()!!)
                        dialogInterface!!.dismiss()
                    }
                    .setNegativeButton("Cancel", { dialogInterface, i -> dialogInterface.dismiss() })
                    .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cacheLastSeenPage()
    }

    private fun cacheLastSeenPage() {
        ReaderDataManager.save(ChapterCache(parent, chapterTitle, viewPager!!.currentItem, adapter!!.count))
    }

}
