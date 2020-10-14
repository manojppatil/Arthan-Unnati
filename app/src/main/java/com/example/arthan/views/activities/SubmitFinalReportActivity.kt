package com.example.arthan.views.activities

import android.Manifest
import android.content.Intent
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.bumptech.glide.Glide
import com.example.arthan.R
import com.example.arthan.dashboard.bcm.BCMDashboardActivity
import com.example.arthan.dashboard.bm.BMDashboardActivity
import com.example.arthan.dashboard.bm.model.FinalReportPostData
import com.example.arthan.global.ArthanApp
import com.example.arthan.global.DOC_TYPE
import com.example.arthan.global.STATUS
import com.example.arthan.lead.UploadDocumentActivity
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.network.S3UploadFile
import com.example.arthan.network.S3Utility
import com.example.arthan.ocr.CardResponse
import com.example.arthan.utils.*
import com.example.arthan.views.adapters.DocumentAdapter
import com.example.arthan.views.adapters.SanctionAdapter
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import kotlinx.android.synthetic.main.activity_submit_final_report.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class SubmitFinalReportActivity : BaseActivity(), View.OnClickListener {

    val docList = mutableListOf<Uri>()
    var currentCapture: Uri? = null
    var mDocAdapter: DocumentAdapter? = null
    val fileList: MutableList<S3UploadFile> = mutableListOf()
    var sanctionList = ArrayList<String>()
    var docUrlList: String = ""
    var agreementUrl: String = ""
    var cocUrl: String = ""


    override fun contentView() = R.layout.activity_submit_final_report

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        btn_search.visibility = View.GONE
        btn_filter.visibility = View.GONE

        txt_change.setOnClickListener(this)
        btn_submit.setOnClickListener(this)
        ll_upload_document.setOnClickListener(this)

        txt_status.text = "Status: ${intent.getStringExtra(STATUS)}"
        txt_reason_msg.text = (resources.getString(
            R.string.state_the_reasons_for_the_approval_of_this_application,
            intent.getStringExtra(STATUS)
        ))
        tv_agreement.setOnClickListener(this)
        tv_coc.setOnClickListener(this)
        if (intent.getStringExtra(STATUS).contains("Recommended to CCM", ignoreCase = true)&&ArthanApp.getAppInstance().loginRole=="BM"&&
                intent.getStringExtra("recordType")=="AM") {

            tv_agreement.visibility = View.VISIBLE
            tv_coc.visibility = View.VISIBLE
        }else{
            tv_agreement.visibility = View.GONE
            tv_coc.visibility = View.GONE
        }

        if(ArthanApp.getAppInstance().loginRole=="BM"&&intent.getStringExtra("recordType")=="AM"&&intent.getStringExtra(STATUS).contains("Recommended to CCM", ignoreCase = true)) {
            val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                this.resources.getStringArray(R.array.recommendToCC)
            ) //selected item will look like a spinner set from XML

            rejectReason.adapter = spinnerArrayAdapter

        }
        if(ArthanApp.getAppInstance().loginRole=="BM"&&intent.getStringExtra("recordType")=="AM"&&intent.getStringExtra(STATUS).contains("Reject", ignoreCase = true))
        {

            CoroutineScope(Dispatchers.IO).launch {
                val res=RetrofitFactory.getApiService().bmAMReason()

                if(res?.body()!=null)
                {
                    withContext(Dispatchers.Main) {
                        rejectReason.adapter = getAdapter(res.body()?.data)
                    }
                }
            }
        }
        if (ArthanApp.getAppInstance().loginRole == "BCM") {
            sanctions.visibility = View.VISIBLE
        }
        addSanction.setOnClickListener {
            if (addSanction.text.toString() == "Add new") {
                addSanction.text = "Done"
                addNewSanctionField()
            } else {
                saveSanction()
            }
        }

        et_reason.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                checkForProceed()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        cb_sanction.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                addSanction.visibility = View.VISIBLE

            } else {
                addSanction.visibility = View.GONE
                sanctionList = ArrayList()
                sanctionRv.removeAllViews()
            }
        }


        // mDocAdapter = DocumentAdapter(this, docList,docUrlList)
        //   rv_docs.adapter=mDocAdapter

       /* if (intent.getStringExtra("recordType") == "AM") {
            tv_agreement.visibility = View.VISIBLE
            tv_coc.visibility = View.VISIBLE


        } else {
            tv_agreement.visibility = View.GONE
            tv_coc.visibility = View.GONE
        }*/
    }

    private fun saveSanction() {

        var view = sanctionRv.layoutManager?.findViewByPosition(sanctionList.size - 1)

        if (view != null) {
            var field = view?.findViewById<EditText>(R.id.field)
            if (field != null && field?.length()!! > 0) {
                sanctionList.removeAt(sanctionList.size - 1)
                sanctionList.add(field.text.toString())
                addSanction.text = "Add new"

            }
        }
    }
    private fun getAdapter(list: List<Data>?): DataSpinnerAdapter =
        DataSpinnerAdapter(this, list?.toMutableList() ?: mutableListOf()).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

    fun removeSanctionField(position: Int) {

        sanctionList
            .removeAt(position)

        sanctionRv.adapter?.notifyDataSetChanged()
    }

    private fun addNewSanctionField() {
        if (sanctionList.isEmpty()) {
            sanctionList.add("")
            sanctionRv.adapter = SanctionAdapter(this, sanctionList)
        } else {

            sanctionList.add("")
            var adapter: SanctionAdapter = sanctionRv.adapter as SanctionAdapter
            adapter.notifyDataSetChanged()

        }

    }

    override fun screenTitle() = "Final Report"

    private fun checkForProceed() {
        if (et_reason.text.isNullOrBlank()) {
            btn_submit.isEnabled = false
            btn_submit.setBackgroundResource(R.drawable.ic_next_disable)
            btn_submit.setTextColor(ContextCompat.getColor(this, R.color.disable_text))
        } else {
            btn_submit.isEnabled = true
            btn_submit.setBackgroundResource(R.drawable.ic_next_enabled)
            btn_submit.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.txt_change -> {
                finish()
            }

            R.id.btn_submit -> {
                if (intent.getStringExtra(STATUS).contains(
                        "Recommended to CCM",
                        ignoreCase = true
                    ) && intent.getStringExtra("recordType") == "AM" && ArthanApp.getAppInstance().loginRole == "BM" && (cocUrl.isEmpty() || agreementUrl.isEmpty() || docUrlList.isEmpty())
                ) {
                    Toast.makeText(
                        this,
                        "Document, Coc & Agreement are mandatory.",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
                val progressBar = ProgrssLoader(this)
                progressBar.showLoading()

                var decision = ""
                if (intent.getStringExtra(STATUS).contains("reject", ignoreCase = true)) {
                    rejectReason.visibility = View.VISIBLE
                    decision = rejectReason.selectedItem.toString()
                } else if (intent.getStringExtra(STATUS).contains("Approve", ignoreCase = true)) {
                    rejectReason.visibility = View.GONE
                    decision = "Approve"

                } else if (intent.getStringExtra(STATUS).contains("RM Reassigned")){

                    rejectReason.visibility = View.GONE
                    decision = "RM Reassigned"
                }
                var user = ""
                if (intent.getStringExtra("recordType") == "AM") {
                    user = intent.getStringExtra("amId")

                }

                var map = FinalReportPostData(
                    intent.getStringExtra("loanId"),
                    intent.getStringExtra("custId"),
                    intent.getStringExtra(STATUS),
                    decision,
                    et_reason.text.toString(),
                    docUrlList,
                    sanctionList, ArthanApp.getAppInstance().loginUser, user,
                    agreementUrl,
                    cocUrl
                )

                if (ArthanApp.getAppInstance().loginRole == "BM") {

                    CoroutineScope(Dispatchers.IO).launch {
                        var respo = if (intent.getStringExtra("recordType") == "AM") {
                            RetrofitFactory.getApiService().bmAmSubmit(
                                map
                            )
                        } else {
                            RetrofitFactory.getApiService().bmSubmit(
                                map
                            )
                        }

                        val result = respo.body()
                        if (respo.isSuccessful && respo.body() != null && result?.apiCode == "200") {

                            withContext(Dispatchers.Main) {
                                var msg =
                                    if (intent.getStringExtra(STATUS).contains(
                                            "reject",
                                            ignoreCase = true
                                        )
                                    ) {
                                        "Case is Rejected Successfully"
                                    } else {
                                        if(intent.getStringExtra("recordType") == "AM"){

                                            "Case successfully submitted to operations"
                                        }else {

                                            "Case is Successfully submitted to BCM"
                                        }
                                    }
                                Toast.makeText(
                                    this@SubmitFinalReportActivity,
                                    msg,
                                    Toast.LENGTH_LONG
                                ).show()


                                progressBar.dismmissLoading()
                                startActivity(Intent(
                                    this@SubmitFinalReportActivity,
                                    BMDashboardActivity::class.java
                                ).apply {
                                    putExtra("FROM", "BM")
                                })
                                finish()
                            }

                        } else {
                            withContext(Dispatchers.Main) {

                                progressBar.dismmissLoading()
                                Toast.makeText(
                                    this@SubmitFinalReportActivity,
                                    "Please try again later",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }

                } else if (ArthanApp.getAppInstance().loginRole == "BCM") {
                    CoroutineScope(Dispatchers.IO).launch {
                        val respo = RetrofitFactory.getApiService().bcmSubmit(
                            map
                        )

                        val result = respo.body()
                        withContext(Dispatchers.Main) {
                            progressBar.dismmissLoading()
                            if (respo.isSuccessful && respo.body() != null && result?.apiCode == "200") {

                                var msg =
                                    when {
                                        intent.getStringExtra(STATUS).contains(
                                            "Approve",
                                            ignoreCase = true
                                        ) -> {
                                            "Case is Approved Successfully"
                                        }
                                        intent.getStringExtra(STATUS).contains(
                                            "reject",
                                            ignoreCase = true
                                        ) -> {
                                            "Case is Rejected Successfully"
                                        }
                                        else -> {

                                            "Case is Successfully Submitted to AA"
                                        }
                                    }
                                Toast.makeText(
                                    this@SubmitFinalReportActivity,
                                    msg,
                                    Toast.LENGTH_LONG
                                ).show()


                                startActivity(Intent(
                                    this@SubmitFinalReportActivity,
                                    BCMDashboardActivity::class.java
                                ).apply {
                                    putExtra("FROM", "BCM")
                                })
                                finish()

                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@SubmitFinalReportActivity,
                                        "Please try again later",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                        }
                    }

                }

                /*  val intent = Intent(this, BMDashboardActivity::class.java)
                  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                  startActivity(intent)*/
            }

            R.id.ll_upload_document -> capture()

            R.id.tv_agreement ->
                startActivityForResult(
                    Intent(
                        this@SubmitFinalReportActivity,
                        UploadDocumentActivity::class.java
                    ).apply {
                        putExtra(DOC_TYPE, RequestCode.Agreement)
                    }, RequestCode.Agreement
                )
            R.id.tv_coc ->
                startActivityForResult(
                    Intent(
                        this@SubmitFinalReportActivity,
                        UploadDocumentActivity::class.java
                    ).apply {
                        putExtra(DOC_TYPE, RequestCode.Coc)
                    }, RequestCode.Coc
                )
        }
    }

    fun capture() {
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

    private fun getOutputMediaFile(): File {
        val dir = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "Arthan"
        )/* val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Arthan"
        )*/
        if (!dir.exists())
            dir.mkdirs()
        return File(
            dir.absolutePath + "/IMG_" + DateFormatUtil.currentTime("ddMMyyyy_HHmmssSS") + ".jpg"
        )
    }

    private fun navigateToCamera() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        currentCapture = FileProvider.getUriForFile(
            this, applicationContext.packageName + ".provider",
            getOutputMediaFile()
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentCapture)

        startActivityForResult(intent, 100)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            100 -> {
                ll_upload_document.visibility = View.GONE
                rv_docs.visibility = View.VISIBLE


                val loader = ProgrssLoader(context = this)
                loader.showLoading()

                val uriNew=compressImage(currentCapture!!)
                currentCapture= getUriFromBitmap(uriNew,rv_docs)
                loadImage(this, rv_docs, currentCapture!!, { filePath ->
                    try {
                        val file: File = File(filePath)
                        val url = file.name
                        val fileList: MutableList<S3UploadFile> = mutableListOf()
                        fileList.add(S3UploadFile(file, url))
                        S3Utility.getInstance(this)
                            .uploadFile(fileList,
                                {
                                    docUrlList = fileList[0].url ?: filePath
                                    println("docurlList-" + docUrlList)
                                    ThreadUtils.runOnUiThread { loader.dismmissLoading() }
                                }) {
                                ThreadUtils.runOnUiThread { loader.dismmissLoading() }
                            }
                    } catch (e: Exception) {
                        ThreadUtils.runOnUiThread { loader.dismmissLoading() }
                        e.printStackTrace()
                    }
                })
                //  docList.add(currentCapture!!)//used

                //      loader.showLoading()
                /*docList.add(currentCapture!!)
           if(mDocAdapter?.itemCount==0)
           {
               rv_docs.adapter=mDocAdapter
           }*/
//                mDocAdapter?.notifyDataSetChanged()
                //  mDocAdapter?.addNewDoc(currentCapture!!)//used
//               mDocAdapter?.notifyDataSetChanged()
//                capture.setImageURI(currentCapture)


/*

                val file = File(currentCapture!!.path)
                val url = file.name + file.extension
                fileList.add(S3UploadFile(file, url))
                S3Utility.getInstance(this)
                    .uploadFile(fileList,
                        {
                            //  MyProfileActivity.profileImage = fileList[0].url ?: filePath
                            ThreadUtils.runOnUiThread {
                                loader.dismmissLoading() }
                        }) {
                        ThreadUtils.runOnUiThread { loader.dismmissLoading() }
                    }
//                     Log.e("DOC SIZE","::: ${docList.size}")
*/


            }
            RequestCode.Agreement -> {
                data?.let {
                    tv_agreement.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_document_attached,
                        0,
                        0,
                        0
                    )

                    val applicantData: CardResponse? =
                        it.getParcelableExtra(ArgumentKey.Agreement) as? CardResponse

                    agreementUrl =
                        applicantData?.cardFrontUrl!!
                }
            }

            RequestCode.Coc -> {
                data?.let {
                    tv_coc.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_document_attached,
                        0,
                        0,
                        0
                    )
                    val applicantData: CardResponse? =
                        it.getParcelableExtra(ArgumentKey.Coc) as? CardResponse

                    cocUrl =  applicantData?.cardFrontUrl!!
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }

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

    fun getUriFromBitmap(scaledBitmap: Bitmap?, imageview: ImageView):Uri?
    {
        var out: FileOutputStream? = null
        val fileName = getOutputMediaFile()
        try {
            out = FileOutputStream(fileName,false)
            //          write the compressed bitmap at the destination specified by filename.
            scaledBitmap?.compress(Bitmap.CompressFormat.JPEG, 80, out)
//            imageview.setImageBitmap(scaledBitmap)
            val udi=FileProvider.getUriForFile(
                this, this?.applicationContext?.packageName + ".provider",
                fileName
            )
         //   Glide.with(this).load(udi).into(imageview)

            return udi
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }
}