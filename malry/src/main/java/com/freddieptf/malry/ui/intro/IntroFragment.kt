package com.freddieptf.malry.ui.intro

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.freddieptf.malry.App
import com.freddieptf.malry.PrefUtils
import com.freddieptf.malry.di.LibViewModelFactory
import com.freddieptf.malry.ui.library.LibraryFragment
import com.freddieptf.mangatest.R
import kotlinx.android.synthetic.main.intro_frag.*
import javax.inject.Inject

/**
 * Created by freddieptf on 11/26/18.
 */
class IntroFragment : Fragment() {

    private val STORAGE_REQ_CODE = 100

    private lateinit var btnSelectLocation: TextView
    private lateinit var tvTitle: TextView
    @Inject
    lateinit var viewModelFactory: LibViewModelFactory
    lateinit var introViewModel: IntroViewModel

    private fun startLibSelector() {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        i.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        startActivityForResult(i, 100)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.intro_frag, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar!!.hide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity!!.application as App).component.inject(this)
        introViewModel = ViewModelProviders.of(this, viewModelFactory).get(IntroViewModel::class.java)

        tvTitle = view.findViewById(R.id.tv_introTitle)
        btnSelectLocation = view.findViewById(R.id.btn_selectLibLocation)

        btnSelectLocation.setOnClickListener {
            if (hasStoragePerms()) startLibSelector()
            else {
                requestPermissions(
                        Array(1) { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                        STORAGE_REQ_CODE);
            }
        }

        introViewModel.getImportState().observe(this, Observer {
            if (!it) {
                (activity as AppCompatActivity).supportActionBar!!.show()
                fragmentManager!!.beginTransaction()
                        .replace(R.id.container, LibraryFragment(), LibraryFragment::class.java.simpleName)
                        .commit()
            } else {
                intro_ll_content.visibility = View.GONE
                intro_pb_import.visibility = View.VISIBLE
            }
        })

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_REQ_CODE && permissions.size > 0 &&
                permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLibSelector()
            } else {
                AlertDialog.Builder(context!!)
                        .setTitle(getString(R.string.perm_dialog_title))
                        .setMessage(getString(R.string.perm_dialog_desc))
                        .setPositiveButton(getString(R.string.ok)) { d, _ ->
                            requestPermissions(
                                    Array(1) { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                    STORAGE_REQ_CODE);
                            d.dismiss();
                        }
                        .setNeutralButton(getString(R.string.later)) { d, _ ->
                            Toast.makeText(context!!, "I'm kinda useless like this...", Toast.LENGTH_SHORT).show(); d.dismiss()
                        }
                        .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            when (resultCode) {
                RESULT_OK -> {
                    introViewModel.addLibraryURI(context!!, data!!.data!!)
                    PrefUtils.setFirstSetupComplete(context!!)
                }
                RESULT_CANCELED -> {
                    tvTitle.text = context!!.getString(R.string.set_lib_loc_b4_continue)
                }
            }
        }
    }

    private fun hasStoragePerms(): Boolean {
        return (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
    }

}