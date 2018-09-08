package com.freddieptf.mangalibrary

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.freddieptf.comicreader.ReaderActivity
import com.freddieptf.comicreader.ReaderFragment
import com.freddieptf.mangalibrary.data.Chapter
import com.freddieptf.mangalibrary.detail.ChapterAdapter
import com.freddieptf.mangalibrary.detail.Contract
import com.freddieptf.mangalibrary.detail.Presenter
import java.util.ArrayList

/**
 * Created by freddieptf on 9/1/18.
 */
class DetailFragment: Fragment(), Contract.View {

    companion object {

        private val DIR_URI = "dir_uri"

        fun newInstance(dirUri: Uri): DetailFragment {
            val bundle = Bundle()
            bundle.putParcelable(DIR_URI, dirUri)
            val frag = DetailFragment()
            frag.arguments = bundle
            return frag
        }

    }

    private lateinit var recycler: RecyclerView
    private lateinit var presenter: Presenter
    private val adapter = ChapterAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.detail_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler = view.findViewById(R.id.recycler)

        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter
        adapter.setChapterClickListener(this)

        var uri = arguments!!.get(DIR_URI) as Uri

        presenter = Presenter(this, uri)
        presenter.startLoad(context!!)

    }

    override fun onChaptersLoad(data: List<Chapter>) {
        println("chapterSize=" + data.size.toString())
        adapter.swapData(data)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity!!.setTitle("Detail")
    }

    override fun onChapterClick(chapter: Chapter) {
        val bundle = Bundle()
        bundle.putStringArrayList(ReaderFragment.PIC_URIS,
                presenter.openChapterDir(context!!, chapter) as ArrayList<String>)
        val i = Intent(context!!, ReaderActivity::class.java)
        i.putExtras(bundle)
        startActivity(i)
    }

}