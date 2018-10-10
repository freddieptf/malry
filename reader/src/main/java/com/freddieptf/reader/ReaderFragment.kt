package com.freddieptf.reader

import android.os.Bundle
import android.view.*
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
import java.util.*
import kotlin.collections.ArrayList

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
    private var currentRead: Chapter? = null

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.reader_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        when (ReaderPrefUtils.getReadDirection(context!!)) {
            ReaderViewPager.DIRECTION.LEFT_TO_RIGHT -> {
                menu.findItem(R.id.menu_read_ltr).setChecked(true)
            }
            ReaderViewPager.DIRECTION.RIGHT_TO_LEFT -> {
                menu.findItem(R.id.menu_read_rtl).setChecked(true)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_read_ltr -> {
                ReaderPrefUtils.setReadDirection(context!!, ReaderViewPager.DIRECTION.LEFT_TO_RIGHT)
                activity?.invalidateOptionsMenu()
                pos = viewPager!!.currentItem
                showChapter(currentRead!!)
            }
            R.id.menu_read_rtl -> {
                ReaderPrefUtils.setReadDirection(context!!, ReaderViewPager.DIRECTION.RIGHT_TO_LEFT)
                activity?.invalidateOptionsMenu()
                pos = viewPager!!.currentItem
                showChapter(currentRead!!)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showChapter(chapter: Chapter) {
        this.currentRead = chapter

        val pages = ArrayList<String>()
        pages += currentRead!!.paths
        chapterTitle = currentRead!!.chapter
        parent = currentRead!!.parent
        (activity as AppCompatActivity).supportActionBar!!.title = chapterTitle

        val direction = ReaderPrefUtils.getReadDirection(context!!)
        viewPager!!.setReadDirection(direction)

        when (direction) {
            ReaderViewPager.DIRECTION.LEFT_TO_RIGHT -> {
                Collections.reverse(pages)
                adapter = PicPagerAdapter(pages)
                viewPager!!.adapter = adapter
            }
            ReaderViewPager.DIRECTION.RIGHT_TO_LEFT -> {
                adapter = PicPagerAdapter(pages)
                viewPager!!.adapter = adapter
            }
        }

        if (pos == 0) viewModel.getChCache(parent, chapterTitle).observe(this, observer)
        else viewPager!!.setCurrentItem(pos, false)
    }

    private var observer = Observer<ChapterCache> { cache ->
        if (cache != null && cache.id == parent + "/" + chapterTitle) {
            pos = cache.page
            viewPager!!.setCurrentItem(pos, false)
        } else viewPager!!.setCurrentItem(0, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPager = view.findViewById<View>(R.id.pager_MangaPics) as ReaderViewPager
        viewPager!!.setReadProgressListener(this)

        viewModel = ViewModelProviders.of(this).get(ReaderFragViewModel::class.java)

        showChapter(ChapterProvider.getProvider().getCurrentRead())

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
