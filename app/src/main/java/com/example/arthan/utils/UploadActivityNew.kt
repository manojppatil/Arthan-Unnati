package com.example.arthan.utils

import android.Manifest
import android.R.id.message
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.bumptech.glide.Glide
import com.crashlytics.android.Crashlytics
import com.example.arthan.R
import com.example.arthan.global.ArthanApp
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.network.S3UploadFile
import com.example.arthan.network.S3Utility
import com.example.arthan.ocr.CardResponse
import com.example.arthan.profile.MyProfileActivity
import com.example.arthan.views.activities.BaseActivity
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import kotlinx.android.synthetic.main.activity_upload_new.*
import kotlinx.android.synthetic.main.activity_upload_new.btn_next
import kotlinx.android.synthetic.main.activity_upload_new.img_document_front
import kotlinx.coroutines.*
import java.io.File
import kotlin.coroutines.CoroutineContext


class UploadActivityNew : BaseActivity() , CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main
    private var mCardData: CardResponse? = null

    private var mUri: Uri? = null
    private var mDocUrl: String? = null
    private var leadId: String = ""
    override fun contentView(): Int {

        return R.layout.activity_upload_new
    }

    override fun init() {


        btn_next.visibility=View.GONE
        btn_attach_document.setOnClickListener {
            val request = permissionsBuilder(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).build()
            request.listeners {
                onAccepted {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                        100
                    )
                }
                onDenied {

                }
                onPermanentlyDenied {
                }
            }
            request.send()


        }
        btn_next.setOnClickListener {
            if(mUri.toString().isNotEmpty())
//            uploadToS3(mUri.toString())
                sendToS3()

        }
        btn_take_picture.setOnClickListener {
            val request = permissionsBuilder(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).build()
            request.listeners {
                onAccepted {
                   capture()
                }
                onDenied {
                    btn_take_picture?.isEnabled = false
                }
                onPermanentlyDenied {
                }
            }
            request.send()
        }

    }

    private fun sendToS3() {
        val loader = ProgrssLoader(this!!)
        loader.showLoading()
        loadImage(this!!, img_document_front, mUri!!, { filePath ->
            try {

                val file: File = File(filePath)
                val url = file.name
                val fileList: MutableList<S3UploadFile> = mutableListOf()
                fileList.add(S3UploadFile(file, url))
                S3Utility.getInstance(this)
                    .uploadFile(fileList,
                        {
                            mDocUrl = fileList[0].url ?: filePath
                            ThreadUtils.runOnUiThread{
                                loader.dismmissLoading()
                                val intent2 = Intent()
                                intent2.putExtra("docUrl", mDocUrl)
                                intent2.putExtra("pos", intent.getSerializableExtra("pos"))
                                intent2.putExtra("docName", intent.getSerializableExtra("loanId").toString()+"_"+intent.getSerializableExtra("docName")+"."+File(filePath).extension)
                                setResult(Activity.RESULT_OK, intent2)
                                finish()
                            }
                        })
            } catch (e: Exception) {
                ThreadUtils.runOnUiThread { loader.dismmissLoading() }
                e.printStackTrace()
                Crashlytics.log(e.message)

            }
        })
    }

    private fun getOutputMediaFile(): File {
        val dir = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "Arthan"
        )/*val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Arthan"
        )*/

        if (!dir.exists())
            dir.mkdirs()
        return File(
            dir.absolutePath + "/"+intent.getSerializableExtra("loanId")+"_"+intent.getSerializableExtra("docName")+".jpg"
        )
    }
    private fun navigateToCamera() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        this?.let {
            mUri = FileProvider.getUriForFile(
                it, this?.applicationContext?.packageName + ".provider",
                getOutputMediaFile()
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri)
        }

        startActivityForResult(intent, 101)
    }

    private fun capture() {
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
    private  fun uploadToS3(
        filePath: String
    ) {

        val progressBar = ProgrssLoader(this)
        progressBar.showLoading()
        val fileList = mutableListOf(
            S3UploadFile(
                File(filePath),
                intent.getStringExtra("loanId")+"_"+intent.getStringExtra("docName")+"."+File(filePath).extension
            )
        )
        S3Utility.getInstance()
            .uploadFile(fileList,
                {
                    if (mCardData == null) {
                        mCardData = CardResponse("", "", "", "", null)
                    }
                    mCardData?.cardFrontUrl = fileList[0].url

                    Log.e("URL", ":::: ${fileList[0].url}")

                    CoroutineScope(uiContext).launch {

                        if (mCardData?.status?.equals(
                                ConstantValue.CardStatus.Ok,
                                true
                            ) == true
                        ){
                            mDocUrl= mCardData!!.cardFrontUrl
                            progressBar.dismmissLoading()
                            val intent = Intent()
                            intent.putExtra("docUrl", mDocUrl)
                            intent.putExtra("pos", intent.getSerializableExtra("pos"))
                            intent.putExtra("docName", intent.getSerializableExtra("loanId").toString()+"_"+intent.getSerializableExtra("docName")+"."+File(filePath).extension)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                        else {
                            progressBar.dismmissLoading()
                            Toast.makeText(
                                this@UploadActivityNew,
                                "Please capture again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }
            ) {
                Toast.makeText(
                    this,
                    "$it",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onToolbarBackPressed() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            100 -> {
                img_document_front.visibility = View.VISIBLE
                mUri=data?.data
                Glide.with(this).load(mUri).into(img_document_front)
                btn_next.visibility=View.VISIBLE

            }
            101 -> {
                img_document_front.visibility = View.VISIBLE
                Glide.with(this).load(mUri).into(img_document_front)
                btn_next.visibility=View.VISIBLE

            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
    override fun screenTitle(): String {
       return "Upload Documents"
    }
}
