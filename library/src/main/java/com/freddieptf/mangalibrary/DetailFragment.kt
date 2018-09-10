package com.freddieptf.mangalibrary

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.freddieptf.reader.ReaderActivity
import com.freddieptf.reader.ReaderFragment
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

        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)

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

    override fun showTitle(title: String) {
        activity!!.setTitle(title)
    }

    override fun onChapterClick(chapter: Chapter) {
        val i = ReaderActivity.newIntent(
                context!!,
                chapter.name,
                chapter.parent,
                presenter.openChapter(context!!, chapter))
        startActivity(i)
    }

}