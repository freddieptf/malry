package com.freddieptf.mangalibrary

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


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

        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)

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
        i.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
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
            return ArrayList<DocumentFile>()
        }
        var libraryDocFile = DocumentFile.fromTreeUri(getContext()!!, LibraryPrefs.getLibUri(context!!)!!)!!
        for(item in libraryDocFile.listFiles()) {
            println(item.uri.path + "::" + item.type + "::" + item.name + "::")
        }
        return libraryDocFile.listFiles().asList()
    }

}