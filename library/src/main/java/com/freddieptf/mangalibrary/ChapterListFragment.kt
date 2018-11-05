package com.freddieptf.mangalibrary

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
import com.freddieptf.mangalibrary.data.models.Chapter
import com.freddieptf.mangalibrary.data.models.LibraryItem
import com.freddieptf.reader.ReaderActivity
import com.freddieptf.reader.api.ChapterProvider

/**
 * Created by freddieptf on 9/1/18.
 */
class ChapterListFragment : Fragment(), ChapterAdapter.ChapterClickListener {

    companion object {

        private val LIBRARY_ITEM = "library_item"

        fun newInstance(libraryItem: LibraryItem): ChapterListFragment {
            val bundle = Bundle()
            bundle.putParcelable(LIBRARY_ITEM, libraryItem)
            val frag = ChapterListFragment()
            frag.arguments = bundle
            return frag
        }

    }

    private lateinit var recycler: RecyclerView
    private lateinit var viewModel: LibraryViewModel
    private val adapter = ChapterAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.detail_frag, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.title = arguments!!.getParcelable<LibraryItem>(LIBRARY_ITEM).name
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler = view.findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter
        adapter.setChapterClickListener(this)
        viewModel = ViewModelProviders.of(activity!!).get(LibraryViewModel::class.java)

        val item = arguments!!.getParcelable<LibraryItem>(LIBRARY_ITEM)

        viewModel.getDbChapterList(item.dirUri).observe(this,
                Observer {
                    if (it.size < item.itemCount) {
                        viewModel.syncChaptersFromDisk(context!!, item.dirUri)
                    } else {
                        adapter.swapData(data = it)
                    }
                })

    }

    override fun onChapterClick(chapter: Chapter, pos: Int) {
        val provider = LocalLibChProvider().setRead(pos, adapter.getData())
        ChapterProvider.useProvider(provider)
        val i = ReaderActivity.newIntent(context!!)
        startActivity(i)
    }

}