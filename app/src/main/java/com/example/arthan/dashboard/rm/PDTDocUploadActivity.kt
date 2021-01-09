package com.example.arthan.dashboard.rm

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.example.arthan.R
import com.example.arthan.dashboard.rm.adapters.PDTDocAdapter
import com.example.arthan.dashboard.rm.adapters.PDTDocAdapterNew
import com.example.arthan.global.Crashlytics
import com.example.arthan.model.RMDocs
import com.example.arthan.model.RMPendingDocs
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.network.S3UploadFile
import com.example.arthan.network.S3Utility
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.utils.loadImage
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import kotlinx.android.synthetic.main.activity_lisiting.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PDTDocUploadActivity : AppCompatActivity() {

    private var docUri: Uri? = null
    private var currentSelection=-1
    lateinit var rmDocsRequestBuilder:RMPendingDocs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lisiting)

        toolbar_title?.text = "Upload PDD/OTC"

        loadDataFromServer()

        submitDocs.visibility=View.VISIBLE
        submitDocs.setOnClickListener {
            sendDataToServer()
        }
        back_button?.setOnClickListener { onBackPressed() }
    }

    private fun sendDataToServer() {
        rmDocsRequestBuilder.loanId=intent.getStringExtra("loanId")

        val progrssLoader=ProgrssLoader(this)
        progrssLoader.showLoading()
        val map=HashMap<String,Any>()
        map["loanId"]=rmDocsRequestBuilder.loanId
        map["pendingDocs"]=rmDocsRequestBuilder.pendingDocs
        CoroutineScope(Dispatchers.IO).launch {

            val res=RetrofitFactory.getApiService().submitRMPendingDocs(map)
            if(res.body()!=null)
            {
                withContext(Dispatchers.Main)
                {
                    progrssLoader.dismmissLoading()
                    finish()
                }
            }
        }
    }

    fun onUpload(pos: Int) {
        val request = permissionsBuilder(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).build()
        request.listeners {
            onAccepted {
                navigateToCamera(pos)
            }
            onDenied {
            }
            onPermanentlyDenied {
            }
        }
        request.send()
    }
    private fun loadDataFromServer() {

        val progress=ProgrssLoader(this)
        progress.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            val res=RetrofitFactory.getApiService().getRMPendingDocs(intent.getStringExtra("loanId"))

            if(res?.body()!=null)
            {
                rmDocsRequestBuilder=res.body()!!
                withContext(Dispatchers.Main)
                {
                    progress.dismmissLoading()
                    rv_listing.adapter=PDTDocAdapterNew(this@PDTDocUploadActivity,res.body()!!.pendingDocs,res.body()!!.loanId)

                }
            }
        }
    }

    private fun getOutputMediaFile(): File {
       /* val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Arthan"
        )  */
        val dir = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "Arthan"
        )
        if (!dir.exists())
            dir.mkdirs()
        return File(
            dir.absolutePath + "/IMG_doc.jpg"
        )
    }

    private fun navigateToCamera(position:Int) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        currentSelection=position
        docUri = FileProvider.getUriForFile(
            this, applicationContext.packageName + ".provider",
            getOutputMediaFile()
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, docUri)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            100 -> {
                (rv_listing.adapter as PDTDocAdapterNew).onUploadSuccess(data)

            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
     fun sendToS3() {
        val loader = ProgrssLoader(this)
        loader.showLoading()
        loadImage(this, img_dummy, docUri!!, { filePath ->
            try {

                val file: File = File(filePath)
                val url = file.name
                val fileList: MutableList<S3UploadFile> = mutableListOf()
                fileList.add(S3UploadFile(file, url))
                S3Utility.getInstance(this)
                    .uploadFile(fileList,
                        {
                            var mDocUrl = fileList[0].url ?: filePath
                            rmDocsRequestBuilder.pendingDocs[currentSelection].docUrl=mDocUrl
                            rmDocsRequestBuilder.pendingDocs[currentSelection].docStatus="upload"

                            ThreadUtils.runOnUiThread{
                                loader.dismmissLoading()
                            }
                        })
            } catch (e: Exception) {
                ThreadUtils.runOnUiThread { loader.dismmissLoading() }
                e.printStackTrace()
                Crashlytics.log(e.message)

            }
        })
    }

}