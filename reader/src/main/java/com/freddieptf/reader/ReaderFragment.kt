package com.freddieptf.reader

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.freddieptf.reader.api.Chapter
import com.freddieptf.reader.api.ChapterProvider
import com.freddieptf.reader.widgets.ReaderViewPager
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by fred on 3/22/15.
 */
class ReaderFragment : Fragment(), ReaderViewPager.ReadProgressListener, ReaderViewPager.ReadSignals {

    private var adapter: PicPagerAdapter? = null
    private lateinit var viewPager: ReaderViewPager
    private lateinit var chapterTitle: String
    private lateinit var parent: String
    private lateinit var viewModel: ReaderFragViewModel
    private var dialog: AlertDialog? = null
    private var currentRead: Chapter? = null
    private var showingBars = 1

    companion object {
        private val SHOWING_BARS = "showing_bars"
    }

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reader, container, false)
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
                showChapter(currentRead!!)
            }
            R.id.menu_read_rtl -> {
                ReaderPrefUtils.setReadDirection(context!!, ReaderViewPager.DIRECTION.RIGHT_TO_LEFT)
                activity?.invalidateOptionsMenu()
                showChapter(currentRead!!)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewPager = view.findViewById<View>(R.id.pager_MangaPics) as ReaderViewPager
        viewPager.setReadProgressListener(this)
        viewPager.setReadSignalCallback(this)

        savedInstanceState?.let { showingBars = it.getInt(SHOWING_BARS, 1) }
        if(showingBars != 1) (activity as ReaderActivity).hideSystemUI()
        (activity as ReaderActivity).lockDrawer(showingBars == 0)

        viewModel = ViewModelProviders.of(activity!!).get(ReaderFragViewModel::class.java)

        viewModel.openChapterChannel().observe(this, androidx.lifecycle.Observer {
            ChapterProvider.getProvider().setCurrentRead(it)
            showChapter(ChapterProvider.getProvider().getCurrentRead())
            (activity as ReaderActivity).hideDrawer()
        })

        launch {
            val read = ChapterProvider.getProvider().getCurrentRead()
            launch(UI) {
                showChapter(read)
            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        addSystemUiVisibiltyChangeListener()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putInt(SHOWING_BARS, showingBars)
        }
    }

    // just hides the system bars
    override fun pageReadToggle() {
        if(showingBars == 1) (activity!! as ReaderActivity).hideSystemUI()
        else (activity!! as ReaderActivity).showSystemUI()
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

    private fun showChapter(chapter: Chapter) {
        this.currentRead = chapter

        val pages = ArrayList<String>()
        pages += currentRead!!.paths
        chapterTitle = currentRead!!.chapter
        parent = currentRead!!.parent
        (activity as AppCompatActivity).supportActionBar!!.title = chapterTitle

        val direction = ReaderPrefUtils.getReadDirection(context!!)
        viewPager.setReadDirection(direction)

        when (direction) {
            ReaderViewPager.DIRECTION.LEFT_TO_RIGHT -> {
                Collections.reverse(pages)
                adapter = PicPagerAdapter(pages)
                viewPager.adapter = adapter
            }
            ReaderViewPager.DIRECTION.RIGHT_TO_LEFT -> {
                adapter = PicPagerAdapter(pages)
                viewPager.adapter = adapter
            }
        }

        viewPager.setCurrentItem(viewModel.getLastViewedChPage(parent, chapterTitle), false)
        viewModel.setCurrentRead(currentRead!!)

    }

    private fun cacheLastSeenPage() {
        viewModel.saveLastViewedPage(parent, chapterTitle, viewPager.currentItem, adapter!!.count)
    }

    private fun addSystemUiVisibiltyChangeListener() {
        (activity!! as AppCompatActivity).window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                // The system bars are visible.
                (activity as AppCompatActivity).supportActionBar!!.show()
                showingBars = 1
                (activity as ReaderActivity).lockDrawer(false)
            } else {
                // The system bars are NOT visible.
                showingBars = 0
                (activity as ReaderActivity).lockDrawer(true)
            }
        }
    }
}
