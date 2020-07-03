package com.example.arthan.dashboard.rm

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.arthan.R
import com.example.arthan.dashboard.rm.adapters.PDTDocAdapter
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import kotlinx.android.synthetic.main.activity_lisiting.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.io.File

class PendingDocFragment : Fragment() {

    private var docUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_lisiting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back_button?.setOnClickListener { activity?.onBackPressed() }
        init()
    }

    fun init() {

//        toolbar.visibility = View.GONE
        rv_listing.adapter = PDTDocAdapter(activity!!, object : PDTDocAdapter.OnUploadListener {

            override fun onUpload() {
                val request = permissionsBuilder(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).build()
                request.listeners {
                    onAccepted {
                        navigateToCamera()
                    }
                    onDenied {
                    }
                    onPermanentlyDenied {
                    }
                }
                request.send()
            }
        })
    }

    private fun getOutputMediaFile(): File {
       /* val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Arthan"
        )*/
         val dir = File(
           activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
           "Arthan"
       )
        if (!dir.exists())
            dir.mkdirs()
        return File(
            dir.absolutePath + "/IMG_shop.jpg"
        )
    }

    private fun navigateToCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        docUri = FileProvider.getUriForFile(
            activity!!, activity!!.applicationContext.packageName + ".provider",
            getOutputMediaFile()
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, docUri)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            100 -> {
                (rv_listing.adapter as PDTDocAdapter).onUploadSuccess()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
}