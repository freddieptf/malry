package com.freddieptf.mangalibrary

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
import com.freddieptf.mangalibrary.data.models.Chapter
import com.freddieptf.reader.ReaderActivity
import com.freddieptf.reader.api.ChapterProvider

/**
 * Created by freddieptf on 9/1/18.
 */
class ChapterListFragment : Fragment(), ChapterAdapter.ChapterClickListener {

    companion object {

        private val DIR_URI = "dir_uri"
        private val DIR_NAME = "dir_name"

        fun newInstance(dirUri: Uri, dirName: String): ChapterListFragment {
            val bundle = Bundle()
            bundle.putParcelable(DIR_URI, dirUri)
            bundle.putString(DIR_NAME, dirName)
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
        (activity as AppCompatActivity).supportActionBar!!.title = arguments!!.getString(DIR_NAME)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler = view.findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter
        adapter.setChapterClickListener(this)
        viewModel = ViewModelProviders.of(activity!!).get(LibraryViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getChapters(context!!, arguments!!.get(DIR_URI) as Uri).observe(this, Observer {
            adapter.swapData(it)
        })
    }

    override fun onChapterClick(chapter: Chapter, pos: Int) {
        val provider = LocalLibChapterProvider().useCtx(context!!).setRead(pos, adapter.getData())
        ChapterProvider.useProvider(provider)
        val i = ReaderActivity.newIntent(context!!)
        startActivity(i)
    }

}