package com.freddieptf.reader

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.freddieptf.malry.api.Chapter
import com.freddieptf.reader.utils.DisplayUtils
import com.freddieptf.reader.widgets.ReaderSeekbar
import com.freddieptf.reader.widgets.ReaderViewPager
import com.github.rubensousa.previewseekbar.PreviewLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by fred on 3/22/15.
 */
class ReaderFragment : Fragment(), ReaderViewPager.ReadProgressListener, PreviewLoader {

    private var adapter: PicPagerAdapter? = null
    private lateinit var viewPager: ReaderViewPager
    private lateinit var seekbar: ReaderSeekbar
    private lateinit var actionLayout: View
    private lateinit var previewImage: ImageView
    private lateinit var chapterTitle: String
    private lateinit var parent: String
    private lateinit var viewModel: ReaderFragViewModel
    private var dialog: AlertDialog? = null
    private var currentRead: Chapter? = null
    private var showingBars = 1 // if one, then showing
    private lateinit var simpleReadSignals: ReaderViewPager.SimpleReadSignals

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
        viewPager = view.findViewById(R.id.reader_pager)
        seekbar = view.findViewById(R.id.reader_seekBar)
        previewImage = view.findViewById(R.id.imageView)
        actionLayout = view.findViewById(R.id.reader_actionLayout)

        setActionBarMargin()
        viewPager.setReadProgressListener(this)

        simpleReadSignals = object : ReaderViewPager.SimpleReadSignals() {
            override fun onPagerTapToFocus() {
                if (showingBars == 1)
                    (activity!! as ReaderActivity).hideSystemUI()
                else
                    (activity!! as ReaderActivity).showSystemUI()
            }

            override fun onPageLongPress() {}
        }

        viewPager.addReadSignalCallback(simpleReadSignals)

        savedInstanceState?.let { showingBars = it.getInt(SHOWING_BARS, 1) }
        if (showingBars != 1) (activity as ReaderActivity).hideSystemUI()
        else actionLayout.visibility = View.VISIBLE
        (activity as ReaderActivity).lockDrawer(showingBars == 0)

        viewModel = ViewModelProviders.of(activity!!).get(ReaderFragViewModel::class.java)

        viewModel.openChapterChannel().observe(this, androidx.lifecycle.Observer {
            if (it.seen) return@Observer
            GlobalScope.launch {
                cacheLastSeenPage()
                ChapterProvider.getProvider().setCurrentRead(it.getData()!!)
                currentRead = ChapterProvider.getProvider().getCurrentRead()
                GlobalScope.launch(Dispatchers.Main) {
                    (activity as ReaderActivity).hideDrawer(object : DrawerLayout.SimpleDrawerListener() {
                        override fun onDrawerClosed(drawerView: View) {
                            showChapter(currentRead!!)
                        }
                    })
                }
            }
        })

        GlobalScope.launch {
            val read = ChapterProvider.getProvider().getCurrentRead()
            GlobalScope.launch(Dispatchers.Main) {
                showChapter(read)
            }
        }

    }

    override fun loadPreview(currentPosition: Long, max: Long) {
        Glide.with(this)
                .load(adapter!!.pages!!.get(currentPosition.toInt()))
                .thumbnail()
                .into(previewImage)
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
        cacheLastSeenPage()
        super.onDestroy()
    }

    private fun showChapter(chapter: Chapter) {
        this.currentRead = chapter

        val pages = ArrayList<String>()
        pages += currentRead!!.paths
        chapterTitle = currentRead!!.title
        parent = currentRead!!.parentTitle
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

        seekbar.attachPreviewFrameLayout(view!!.findViewById(R.id.previewFrameLayout))
        seekbar.setPreviewLoader(this)
        seekbar.setUpWithPager(viewPager)
        viewPager.setCurrentItem(chapter.lastReadPage, false)
        viewModel.setCurrentRead(currentRead!!)

    }

    private fun cacheLastSeenPage() {
        viewModel.saveLastViewedPage(currentRead!!.id, viewPager.currentItem, adapter!!.count)
    }

    private fun addSystemUiVisibiltyChangeListener() {
        (activity!! as AppCompatActivity).window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                // The system bars are visible.
                (activity as AppCompatActivity).supportActionBar!!.show()
                showingBars = 1
                (activity as ReaderActivity).lockDrawer(false)
                actionLayout.visibility = View.VISIBLE
            } else {
                // The system bars are NOT visible.
                showingBars = 0
                (activity as ReaderActivity).lockDrawer(true)
                actionLayout.visibility = View.INVISIBLE
            }
        }
    }

    private fun setActionBarMargin() {
        val params: CoordinatorLayout.LayoutParams = actionLayout.layoutParams as CoordinatorLayout.LayoutParams
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else {
            params.bottomMargin = DisplayUtils.getNavigationBarSize(context!!).y
        }
        actionLayout.layoutParams = params
    }

}
