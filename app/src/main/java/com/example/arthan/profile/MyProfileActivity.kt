package com.example.arthan.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.bumptech.glide.Glide
import com.crashlytics.android.Crashlytics
import com.example.arthan.R
import com.example.arthan.network.S3UploadFile
import com.example.arthan.network.S3Utility
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.utils.loadImage
import com.example.arthan.views.fragments.BaseFragment
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import kotlinx.android.synthetic.main.activity_my_profile.*
import java.io.*

class MyProfileActivity : BaseFragment(){ //: AppCompatActivity() {

    override fun contentView()= R.layout.activity_my_profile

    override fun init() {
        val request = permissionsBuilder(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).build()
        request.listeners {
            onAccepted {
                btn_take_picture?.isEnabled = true
            }
            onDenied {
                btn_take_picture?.isEnabled = false
            }
            onPermanentlyDenied {
            }
        }
        request.send()

        btn_take_picture?.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        }

      //  btn_next?.setOnClickListener { finish() }
        img_clear_front?.setOnClickListener {
            img_clear_front?.visibility = View.VISIBLE
            profileImage = ""
            Glide.with(this).clear(img_document_front)
        }    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                if (data == null || data.data == null) {
                    return
                }
                val loader = ProgrssLoader(activity!!)
                loader.showLoading()
    /*            val file = Utility.copyFile(activity, data.data!!)
                val fileList: MutableList<S3UploadFile> = mutableListOf()
                fileList.add(S3UploadFile(file!!, file.name))
                S3Utility.getInstance(activity!!)
                    .uploadFile(fileList,
                        {
                            profileImage = fileList[0].url ?: file.absolutePath!!
                            runOnUiThread { loader.dismmissLoading() }
                        }) {
                        runOnUiThread { loader.dismmissLoading() }
                    }*/
                loadImage(activity!!, img_document_front, data.data!!, { filePath ->
                    try {
                        val file: File = File(filePath)
                        val url = file.name + file.extension
                        val fileList: MutableList<S3UploadFile> = mutableListOf()
                        fileList.add(S3UploadFile(file, url))
                        S3Utility.getInstance(activity!!)
                            .uploadFile(fileList,
                                {
                                    profileImage = fileList[0].url ?: filePath
                                    runOnUiThread { loader.dismmissLoading() }
                                }) {
                                runOnUiThread { loader.dismmissLoading() }
                            }
                    } catch (e: Exception) {
                        runOnUiThread { loader.dismmissLoading() }
                        e.printStackTrace()
                        Crashlytics.log(e.message)

                    }
                })
            }
        }
    }

    private fun getFileName(uri: Uri?): String? {
        if (uri == null) return null
        var fileName: String? = null
        val path = uri.path
        val cut = path?.lastIndexOf('/')
        if (cut != -1) {
            fileName = path?.substring((cut ?: 0) + 1)
        }
        return fileName
    }

    private fun copyFile(uri: Uri): File? {
        val tempDirectory = File("${activity!!.filesDir}/temp")
        if (!tempDirectory.exists()) {
            tempDirectory.mkdirs()
        }
        val fileName = getFileName(uri) ?: return null
        val copiedFile: File = File(tempDirectory, fileName.replace(":", "")) ?: return null
        copiedFile.createNewFile()
        try {
            val BUFFER_SIZE = 1024 * 20
            val inputStream = activity!!.contentResolver.openInputStream(uri) ?: return null
            val outPutStream: OutputStream = FileOutputStream(copiedFile)
            val buffer = ByteArray(BUFFER_SIZE)
            val input = BufferedInputStream(inputStream, BUFFER_SIZE);
            val out = BufferedOutputStream(outPutStream, BUFFER_SIZE);
            var count = 0
            var n = input.read(buffer, 0, BUFFER_SIZE)
            while (n != -1) {
                out.write(buffer, 0, n)
                count += n
                n = input.read(buffer, 0, BUFFER_SIZE)
            }
            out.close()
            input.close()
            inputStream.close()
            outPutStream.close()
            return copiedFile
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Crashlytics.log(e.message)

        }
        return null
    }

    companion object {
        const val PICK_IMAGE = 123
        var profileImage: String = ""
    }
}
