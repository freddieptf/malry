package com.freddieptf.malry.detail

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.freddieptf.malry.App
import com.freddieptf.malry.api.Chapter
import com.freddieptf.malry.api.LibraryItem
import com.freddieptf.malry.di.LibViewModelFactory
import com.freddieptf.mangatest.R
import com.freddieptf.reader.ChapterProvider
import com.freddieptf.reader.ReaderActivity
import javax.inject.Inject

/**
 * Created by freddieptf on 9/1/18.
 */
class ChapterListFragment : Fragment(), ChapterAdapter.ChapterClickListener {

    companion object {

        private val LIBRARY_ITEM_URI = "library_item_uri"
        private val LIBRARY_ITEM_NAME = "library_item_name"

        fun newInstance(libraryItem: LibraryItem): ChapterListFragment {
            val bundle = Bundle().apply {
                putParcelable(LIBRARY_ITEM_URI, libraryItem.uri)
                putString(LIBRARY_ITEM_NAME, libraryItem.title)
            }
            return ChapterListFragment().apply {
                arguments = bundle
            }
        }

    }

    private lateinit var recycler: RecyclerView
    private lateinit var viewModel: DetailViewModel
    private val adapter = ChapterAdapter()

    @Inject
    lateinit var viewModelFactory: LibViewModelFactory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.detail_frag, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.title = arguments!!.getString(LIBRARY_ITEM_NAME)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity!!.application as App).component.inject(this)

        recycler = view.findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter
        adapter.setChapterClickListener(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailViewModel::class.java)


        val libraryItemUri = arguments!!.getParcelable<Uri>(LIBRARY_ITEM_URI)

        viewModel.getDbChapterList(libraryItemUri).observe(this,
                Observer {
                        adapter.swapData(data = it)
                })

    }

    override fun onChapterClick(chapter: Chapter, pos: Int) {
        viewModel.getChapterProvider(chapter).observe(this, Observer { provider ->
            ChapterProvider.useProvider(provider)
            val i = ReaderActivity.newIntent(context!!)
            startActivity(i)
        })
    }

}