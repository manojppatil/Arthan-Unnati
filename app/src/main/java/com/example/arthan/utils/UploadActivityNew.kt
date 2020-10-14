package com.example.arthan.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.bumptech.glide.Glide
import com.crashlytics.android.Crashlytics
import com.example.arthan.R
import com.example.arthan.network.S3UploadFile
import com.example.arthan.network.S3Utility
import com.example.arthan.ocr.CardResponse
import com.example.arthan.views.activities.BaseActivity
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import kotlinx.android.synthetic.main.activity_upload_new.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
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

     fun reduceBitmapSize(uri: Uri?,imageview:ImageView)
    {
        val bitmapImage = BitmapFactory.decodeFile(uri?.path)
        val nh = (bitmapImage.height * (512.0 / bitmapImage.width)).toInt()
        val scaled = Bitmap.createScaledBitmap(bitmapImage, 512, nh, true)
        imageview.setImageBitmap(scaled)
    }
    fun getUriFromBitmap(scaledBitmap:Bitmap?,imageview:ImageView):Uri?
    {
        var out: FileOutputStream? = null
        val fileName = getFilename()
        try {
            out = FileOutputStream(fileName,false)
            //          write the compressed bitmap at the destination specified by filename.
            scaledBitmap?.compress(Bitmap.CompressFormat.JPEG, 80, out)
//            imageview.setImageBitmap(scaledBitmap)
            val udi=FileProvider.getUriForFile(
                this, this?.applicationContext?.packageName + ".provider",
                fileName
            )
            Glide.with(this).load(udi).into(imageview)

            return udi
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            100 -> {
                img_document_front.visibility = View.VISIBLE
                mUri=data?.data
                btn_next.visibility=View.VISIBLE
//                reduceBitmapSize(mUri,img_document_front)
                val uriNew=compressImage(mUri!!)
//                mUri=compressImage(mUri!!)
               mUri= getUriFromBitmap(uriNew,img_document_front)
                //  Glide.with(this).load(uriNew).into(img_document_front)

            }
            101 -> {
                img_document_front.visibility = View.VISIBLE
//                reduceBitmapSize(mUri,img_document_front)
            //    Glide.with(this).load(mUri).into(img_document_front)
//                mUri=compressImage(mUri!!)
                val uriNew=compressImage(mUri!!)
                mUri= getUriFromBitmap(uriNew,img_document_front)

//                Glide.with(this).load(uriNew).into(img_document_front)

//                Glide.with(this).load(mUri).into(img_document_front)

                btn_next.visibility=View.VISIBLE

            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
    override fun screenTitle(): String {
       return "Upload Documents"
    }


    fun compressImage(imageUri: Uri): Bitmap? {


        val file = copyFile(this, imageUri)
        var path = ""
        if (file != null) {
            path = file.absolutePath
        }
//        val filePath = getRealPathFromURI(path)
        var scaledBitmap: Bitmap? = null
        val options = BitmapFactory.Options()
        //      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(path, options)
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth
        //      max Height and width values of the compressed image is taken as 816x612
        val maxHeight = 816.0f
        val maxWidth = 612.0f
        var imgRatio = actualWidth / actualHeight.toFloat()
        val maxRatio = maxWidth / maxHeight
        //      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }
        //      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
        //      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false
        //      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try { //          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(path, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bmp,
            middleX - bmp.width / 2,
            middleY - bmp.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )
        //      check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            exif = ExifInterface(path)
            val orientation: Int = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0
            )
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 3) {
                matrix.postRotate(180f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 8) {
                matrix.postRotate(270f)
                Log.d("EXIF", "Exif: $orientation")
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap!!, 0, 0,
                scaledBitmap.width, scaledBitmap.height, matrix,
                true
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var out: FileOutputStream? = null
       /* val filename=getFilename()
        try{
            out =FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap?.compress(Bitmap.CompressFormat.JPEG, 80, out);
        }
        catch (e:FileNotFoundException)
        {

        }

        return  FileProvider.getUriForFile(
            this, this?.applicationContext?.packageName + ".provider",
            File(filename)
        )*/
       /* val fileName = getOutputMediaFile()
        try {
            if(fileName.exists())
            {
                fileName.delete()
                fileName.createNewFile()
            }
            out = FileOutputStream(fileName)
            //          write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return FileProvider.getUriForFile(
            this, this?.applicationContext?.packageName + ".provider",
            fileName
        )*/

        return scaledBitmap

    }

    fun getFilename(): File {
        val dir = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "Arthan"
        )

        if (!dir.exists())
            dir.mkdirs()
        val file=File(
            dir.absolutePath + "/"+intent.getSerializableExtra("loanId")+"_"+intent.getSerializableExtra("docName")+"_"+(0..100).random()+".jpg"
//            dir.absolutePath + "/"+intent.getSerializableExtra("loanId")+"_"+intent.getSerializableExtra("docName")+".jpg"
        )
        if(file.length()>0)
        {
            file.delete()
            file.createNewFile()
        }

        return file
    }

    private fun getRealPathFromURI(contentURI: String): String? {
        val contentUri = Uri.parse(contentURI)
        val cursor: Cursor? = contentResolver.query(contentUri, null, null, null, null)
        return if (cursor == null) {
            contentUri.path
        } else {
            cursor.moveToFirst()
            val index: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(index)
        }
    }

    fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio =
                Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio =
                Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = width * height.toFloat()
        val totalReqPixelsCap = reqWidth * reqHeight * 2.toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }
}
