package com.freddieptf.reader

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freddieptf.malry.api.Chapter
import com.freddieptf.reader.pagelist.CustomRecyclerView
import com.freddieptf.reader.pagelist.CustomSnapHelper
import com.freddieptf.reader.utils.*
import com.freddieptf.reader.widgets.ReaderSeekbar
import com.github.rubensousa.previewseekbar.PreviewLoader
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by fred on 3/22/15.
 */
class ReaderFragment : Fragment(), PreviewLoader, ReaderSeekbar.OnSeekListener {

    private lateinit var recyclerAdapter: PagerRecyclerAdapter
    private lateinit var seekbar: ReaderSeekbar
    private lateinit var actionLayout: View
    private lateinit var previewImage: ImageView
    private lateinit var recyclerView: CustomRecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var chapterTitle: String
    private lateinit var parent: String
    private lateinit var viewModel: ReaderFragViewModel
    private var dialog: AlertDialog? = null
    private var currentRead: Chapter? = null
    private var currentPage: Int = 0
    private lateinit var readDirection: ReadMode
    private var showingBars = 1 // if one, then showing
    private var startDragXPos = 0f
    private lateinit var simpleReadSignals: ReadSignals.SimpleReadSignals

    companion object {
        private val SHOWING_BARS = "showing_bars"
        private val CURRENT_PAGE = "cur_page"
    }

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.reader_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        when (ReaderPrefUtils.getReadDirection(context!!)) {
            ReadMode.LEFT_TO_RIGHT -> {
                menu.findItem(R.id.menu_read_ltr).setChecked(true)
            }
            ReadMode.RIGHT_TO_LEFT -> {
                menu.findItem(R.id.menu_read_rtl).setChecked(true)
            }
        }
        when (ReaderPrefUtils.getPageApsectMode(context!!)) {
            PageApsectMode.PAGE_FILL -> menu.findItem(R.id.menu_page_aspect_fill).setChecked(true)
            PageApsectMode.PAGE_FIT -> menu.findItem(R.id.menu_page_aspect_fit).setChecked(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_read_ltr -> {
                activity?.invalidateOptionsMenu()
                viewModel.setReadDirection(context!!, ReadMode.LEFT_TO_RIGHT)
            }
            R.id.menu_read_rtl -> {
                activity?.invalidateOptionsMenu()
                viewModel.setReadDirection(context!!, ReadMode.RIGHT_TO_LEFT)
            }
            R.id.menu_page_aspect_fit -> {
                activity?.invalidateOptionsMenu()
                ReaderPrefUtils.setPageAspectMode(context!!, PageApsectMode.PAGE_FIT)
                recyclerAdapter.setPageAspect(PageApsectMode.PAGE_FIT)
            }
            R.id.menu_page_aspect_fill -> {
                activity?.invalidateOptionsMenu()
                ReaderPrefUtils.setPageAspectMode(context!!, PageApsectMode.PAGE_FILL)
                recyclerAdapter.setPageAspect(PageApsectMode.PAGE_FILL)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reader, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel = ViewModelProviders.of(activity!!).get(ReaderFragViewModel::class.java)

        seekbar = view.findViewById(R.id.reader_seekBar)
        previewImage = view.findViewById(R.id.imageView)
        actionLayout = view.findViewById(R.id.reader_actionLayout)
        recyclerView = view.findViewById(R.id.recycler)

        savedInstanceState?.let {
            showingBars = it.getInt(SHOWING_BARS, 1)
            currentPage = it.getInt(CURRENT_PAGE, 0)
        }
        if (showingBars != 1)
            (activity as ReaderActivity).hideSystemUI()
        else
            actionLayout.visibility = View.VISIBLE

        (activity as ReaderActivity).lockDrawer(showingBars == 0)

        readDirection = ReaderPrefUtils.getReadDirection(context!!)

        layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerAdapter = PagerRecyclerAdapter()
        recyclerView.adapter = recyclerAdapter
        recyclerAdapter.setPageAspect(ReaderPrefUtils.getPageApsectMode(context!!))
        val snapHelper = CustomSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        snapHelper.addOnPageChangeListener(seekbar)
        snapHelper.addOnPageChangeListener(object : CustomSnapHelper.OnPageChangeListener {
            override fun onPageChange(position: Int) {
                currentPage = position
            }
        })
        seekbar.setOnSeekListener(this)
        seekbar.attachPreviewFrameLayout(view.findViewById(R.id.previewFrameLayout))
        seekbar.setPreviewLoader(this)

        setActionBarMargin()

        simpleReadSignals = object : ReadSignals.SimpleReadSignals() {
            override fun onPageTapToFocus() {
                if (showingBars == 1)
                    (activity!! as ReaderActivity).hideSystemUI()
                else
                    (activity!! as ReaderActivity).showSystemUI()
            }

            override fun onPageLongPress() {}
        }

        recyclerView.addReadSignalCallback(simpleReadSignals)
        addRecyclerItemTouchListener()

        viewModel.observeCurrentRead().observe(this, androidx.lifecycle.Observer {
            cacheLastSeenPage()
            currentRead = it
            // if saveInstance is not null we override the last read page here
            savedInstanceState?.let { currentRead!!.lastReadPage = currentPage }
            showChapter(currentRead!!)
        })

        viewModel.observeReadDirection().observe(this, androidx.lifecycle.Observer {
            if (it.seen) return@Observer
            val direction = it.getData()!!
            if (this.readDirection == direction) return@Observer // it changed
            this.readDirection = direction // manga or comic..
            currentPage = recyclerAdapter.itemCount - currentPage - 1 // flip this..kinda safe since there's really only two states we can be in
            configureForDirection(direction, ArrayList(currentRead!!.paths))
        })


        viewModel.initializeChapterProvider()

    }

    private fun addRecyclerItemTouchListener() {
        recyclerView.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, ev: MotionEvent): Boolean {
                val inDragX = ev.x
                when (ev.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startDragXPos = ev.x
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (currentPage == 0) {
                            when {
                                readDirection == ReadMode.LEFT_TO_RIGHT && startDragXPos < inDragX -> {
                                    onSwipeToNextCh()
                                }
                                readDirection == ReadMode.RIGHT_TO_LEFT && startDragXPos < inDragX -> {
                                    onSwipeToPreviousCh()
                                }
                            }
                        } else if (currentPage == recyclerAdapter.itemCount - 1) {
                            when {
                                readDirection == ReadMode.LEFT_TO_RIGHT && startDragXPos > inDragX -> {
                                    onSwipeToPreviousCh()
                                }
                                readDirection == ReadMode.RIGHT_TO_LEFT && startDragXPos > inDragX -> {
                                    onSwipeToNextCh()
                                }
                            }
                        }
                    }
                }
                return false
            }
        })
    }

    override fun loadPreview(currentPosition: Long, max: Long) {
        Glide.with(this)
                .load(recyclerAdapter.pages.get(currentPosition.toInt()))
                .thumbnail()
                .into(previewImage)
    }

    override fun onSeekTo(position: Int) {
        layoutManager.scrollToPosition(position)
        currentPage = position
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        addSystemUiVisibiltyChangeListener()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putInt(SHOWING_BARS, showingBars)
            putInt(CURRENT_PAGE, getTrueCurrentPage())
        }
    }

    fun onSwipeToNextCh() {
        val hasNext = ChapterProvider.getProvider().hasNextRead()
        if (hasNext && (dialog == null || !dialog!!.isShowing)) {
            dialog = AlertDialog.Builder(context!!)
                    .setMessage("Would you like to move the next chapter")
                    .setPositiveButton("Next") { dialogInterface, _ ->
                        viewModel.switchChapter(ReaderFragViewModel.ChapterSwitch.NEXT)
                        dialogInterface!!.dismiss()
                    }
                    .setNegativeButton("Cancel", { dialogInterface, _ -> dialogInterface.dismiss() })
                    .show()
        }

    }

    fun onSwipeToPreviousCh() {
        val hasPrev = ChapterProvider.getProvider().hasPreviousRead()
        if (hasPrev && (dialog == null || !dialog!!.isShowing)) {
            dialog = AlertDialog.Builder(context!!)
                    .setMessage("Would you like to move the previous chapter")
                    .setPositiveButton("Previous") { dialogInterface, _ ->
                        viewModel.switchChapter(ReaderFragViewModel.ChapterSwitch.PREVIOUS)
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
        val pages = ArrayList<String>(currentRead!!.paths)
        currentPage = if (readDirection == ReadMode.LEFT_TO_RIGHT) pages.size - currentRead!!.lastReadPage - 1
        else currentRead!!.lastReadPage
        chapterTitle = currentRead!!.title
        parent = currentRead!!.parentTitle ?: ""
        (activity as AppCompatActivity).supportActionBar!!.title = chapterTitle

        val direction = ReaderPrefUtils.getReadDirection(context!!)
        configureForDirection(direction, pages)
    }

    private fun configureForDirection(direction: ReadMode, pages: ArrayList<String>) {
        when (direction) {
            ReadMode.LEFT_TO_RIGHT -> {
                Collections.reverse(pages)
            }
        }
        recyclerAdapter.swap(pages)
        seekbar.setUp(direction, currentPage, pages.size - 1)
        layoutManager.scrollToPosition(currentPage)
    }

    private fun cacheLastSeenPage() {
        if (currentRead == null) return
        val i = getTrueCurrentPage()
        viewModel.saveLastViewedPage(currentRead!!.id, i, recyclerAdapter.itemCount)
    }

    // get current page in normal linear orientation?
    private fun getTrueCurrentPage(): Int {
        val i = if (readDirection == ReadMode.LEFT_TO_RIGHT) recyclerAdapter.itemCount - currentPage - 1
        else currentPage
        return i
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
