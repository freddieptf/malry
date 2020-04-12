package com.freddieptf.malry.ui.library

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.freddieptf.malry.App
import com.freddieptf.malry.api.LibraryItem
import com.freddieptf.malry.di.LibViewModelFactory
import com.freddieptf.malry.ui.detail.MangaDetailFragment
import com.freddieptf.mangatest.R
import com.freddieptf.reader.ChapterLoader
import com.freddieptf.reader.ReaderActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by freddieptf on 8/28/18.
 */
class LibraryFragment : Fragment(), LibraryAdapter.ClickListener {

    private val adapter = LibraryAdapter()
    private lateinit var recyler: RecyclerView
    private lateinit var swRefresh: SwipeRefreshLayout
    private lateinit var viewModel: LibraryViewModel

    @Inject
    lateinit var viewModelFactory: LibViewModelFactory

    interface LibraryFragmentContainer {
        fun getFragmentContainerId(): Int
    }

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.library_frag, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity!!.application as App).component.inject(this)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(LibraryViewModel::class.java)

        recyler = view.findViewById(R.id.rv_libraryList)
        swRefresh = view.findViewById(R.id.sw_library_refresh)
        recyler.layoutManager = LinearLayoutManager(context!!)
        recyler.adapter = adapter
        adapter.setClickListener(this)

        swRefresh.setOnRefreshListener {

        }

        viewModel.getData().observe(this, Observer {
            swRefresh.isRefreshing = it.progress
            adapter.swapData(it.data)
        })

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar!!.title = "Library"
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)
    }


    override fun onLibraryItemClick(libraryItem: LibraryItem) {
        GlobalScope.launch(Dispatchers.Main) {
            resumeOrNot(libraryItem)
        }
    }

    private fun openChapterListFrag(dir: LibraryItem) {
        val detailFragment = MangaDetailFragment.newInstance(dir)
        fragmentManager!!.beginTransaction()
                .replace(
                        (activity!! as LibraryFragmentContainer).getFragmentContainerId(),
                        detailFragment,
                        MangaDetailFragment::class.java.simpleName
                )
                .addToBackStack(MangaDetailFragment::class.java.simpleName)
                .commit()
    }

    private fun resumeOrNot(libraryItem: LibraryItem) {
        viewModel.getLastRead(libraryItem).observe(this, Observer { it ->
            if (it.chapter == null) {
                openChapterListFrag(libraryItem)
            } else {
                ChapterLoader.useProvider(it.provider!!)
                val i = ReaderActivity.newIntent(context!!)
                startActivity(i)
            }
        })


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.library_menu, menu)
        val searchView: SearchView = menu.findItem(R.id.menu_manga_search).actionView as SearchView
        searchView.queryHint = "manga title"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                viewModel.search(p0!!)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                viewModel.search(p0!!)
                return true
            }

        })
    }

}