package com.freddieptf.mangalibrary

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.support.v4.provider.DocumentFile
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView


/**
 * Created by freddieptf on 8/28/18.
 */
class LibraryFragment : Fragment(), LibraryAdapter.ClickListener {

    lateinit var btn: Button
    private val adapter = LibraryAdapter()
    private lateinit var recyler: RecyclerView

    interface LibraryFragmentContainer {
        fun getFragmentContainerId(): Int
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.library_frag, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyler = view.findViewById(R.id.rv_libraryList)
        recyler.layoutManager = LinearLayoutManager(context!!)
        recyler.adapter = adapter
        adapter.setClickListener(this)
        if (hasStoragePerms()) adapter.swapData(genTopLevelDirs())
        else throw SecurityException("no storage permissions")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity!!.setTitle("Library")
    }

    override fun onDirClick(dir: DocumentFile) {
        val detailFragment = DetailFragment.newInstance(dir.uri)
        fragmentManager!!.beginTransaction()
                .replace(
                        (activity!! as LibraryFragmentContainer).getFragmentContainerId(),
                        detailFragment,
                        DetailFragment::class.java.simpleName
                )
                .addToBackStack(DetailFragment::class.java.simpleName)
                .commit()
    }

    private fun startLibSelector() {
        var i = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivityForResult(i, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            println(data!!.data)
            LibraryPrefs.addLibUri(context!!, data!!.data)
            adapter.swapData(genTopLevelDirs())
        }
    }

    private fun hasStoragePerms(): Boolean {
        return (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED)
    }

    fun genTopLevelDirs(): List<DocumentFile> {
        var uri = LibraryPrefs.getLibUri(context!!)
        if (uri == null) {
            startLibSelector()
            return ArrayList()
        }
        var libraryDocFile = DocumentFile.fromTreeUri(context, LibraryPrefs.getLibUri(context!!))
        for(item in libraryDocFile.listFiles()) {
            println(item.uri.path + "::" + item.type + "::" + item.name + "::")
        }
        return libraryDocFile.listFiles().asList()
    }

}