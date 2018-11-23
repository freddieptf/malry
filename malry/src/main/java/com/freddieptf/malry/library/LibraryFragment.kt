package com.freddieptf.malry.library

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.freddieptf.malry.App
import com.freddieptf.malry.ProviderManager
import com.freddieptf.malry.detail.ChapterListFragment
import com.freddieptf.malry.api.LibraryItem
import com.freddieptf.localstorage.R
import com.freddieptf.reader.ChapterProvider
import com.freddieptf.reader.ReaderActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by freddieptf on 8/28/18.
 */
class LibraryFragment : Fragment(), LibraryAdapter.ClickListener {

    lateinit var btn: Button
    private val adapter = LibraryAdapter()
    private lateinit var recyler: RecyclerView
    private lateinit var viewModel: LibraryViewModel

    @Inject
    lateinit var viewModelFactory: LibViewModelFactory
    @Inject
    lateinit var providerManager: ProviderManager

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
        if (!hasStoragePerms()) throw SecurityException("no storage permissions")
        (activity!!.application as App).component.inject(this)

        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(LibraryViewModel::class.java)

        recyler = view.findViewById(R.id.rv_libraryList)
        recyler.layoutManager = LinearLayoutManager(context!!)
        recyler.adapter = adapter
        adapter.setClickListener(this)

        var uri = LibraryPrefs.getLibUri(context!!)
        if (uri == null) {
            startLibSelector()
        } else swapData(uri)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar!!.title = "Library"
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)
    }

    override fun onLibraryItemClick(libraryItem: LibraryItem) {
        onDirClick(libraryItem)
    }

    fun onDirClick(item: LibraryItem) {
        GlobalScope.launch(Dispatchers.Main) {
            resumeOrNot(item)
        }
    }

    private fun openChapterListFrag(dir: LibraryItem) {
        val detailFragment = ChapterListFragment.newInstance(dir)
        fragmentManager!!.beginTransaction()
                .replace(
                        (activity!! as LibraryFragmentContainer).getFragmentContainerId(),
                        detailFragment,
                        ChapterListFragment::class.java.simpleName
                )
                .addToBackStack(ChapterListFragment::class.java.simpleName)
                .commit()
    }

     private fun resumeOrNot(libraryItem: LibraryItem) {
        val chapter = providerManager.getLastRead(libraryItem)
        if (chapter == null) {
            openChapterListFrag(libraryItem)
        } else {
            val provider = providerManager.getChapterProvider(chapter)
            ChapterProvider.useProvider(provider)
            val i = ReaderActivity.newIntent(context!!)
            startActivity(i)
        }

    }

    private fun startLibSelector() {
        var i = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        i.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        startActivityForResult(i, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            println(data!!.data)
            LibraryPrefs.addLibUri(context!!, data!!.data)
            swapData(data!!.data)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.library_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_set_lib_location -> {
                startLibSelector()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun hasStoragePerms(): Boolean {
        return (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun swapData(uri: Uri) {
        viewModel.getLibraryDirs(context!!, uri).observe(this, Observer {
            adapter.swapData(it)
        })
    }

}