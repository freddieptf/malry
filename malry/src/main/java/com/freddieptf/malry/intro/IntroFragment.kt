package com.freddieptf.malry.intro

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.freddieptf.malry.PrefUtils
import com.freddieptf.malry.library.LibraryFragment
import com.freddieptf.malry.library.LibraryPrefs
import com.freddieptf.mangatest.R

/**
 * Created by freddieptf on 11/26/18.
 */
class IntroFragment : Fragment() {

    private val STORAGE_REQ_CODE = 100

    private lateinit var btnSelectLocation: TextView
    private lateinit var tvTitle: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.intro_frag, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar!!.hide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tvTitle = view.findViewById(R.id.tv_introTitle)
        btnSelectLocation = view.findViewById(R.id.btn_selectLibLocation)

        btnSelectLocation.setOnClickListener {
            if (hasStoragePerms()) startLibSelector()
            else {
                requestPermissions(
                        Array<String>(1) { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                        STORAGE_REQ_CODE);
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_REQ_CODE && permissions.size > 0 &&
                permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLibSelector()
            } else {
                TODO("handle this...probably suggest installing an external provider")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            when (resultCode) {
                RESULT_OK -> {
                    LibraryPrefs.addLibUri(context!!, data!!.data)
                    finishSetup()
                }
                RESULT_CANCELED -> {
                    tvTitle.text = context!!.getString(R.string.set_lib_loc_b4_continue)
                }
            }
        }
    }

    private fun startLibSelector() {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        i.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        startActivityForResult(i, 100)
    }

    private fun finishSetup() {
        (activity as AppCompatActivity).supportActionBar!!.show()
        PrefUtils.setFirstSetupComplete(context!!)
        fragmentManager!!.beginTransaction()
                .replace(R.id.container, LibraryFragment(), LibraryFragment::class.java.simpleName)
                .commit()
    }

    private fun hasStoragePerms(): Boolean {
        return (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
    }

}