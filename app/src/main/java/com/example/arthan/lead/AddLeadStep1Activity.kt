package com.example.arthan.lead

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.bumptech.glide.Glide
import com.example.arthan.AppLocationProvider
import com.example.arthan.R
import com.example.arthan.dashboard.bcm.BCMDashboardActivity
import com.example.arthan.dashboard.bm.BMDashboardActivity
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.global.AppPreferences
import com.example.arthan.global.ArthanApp
import com.example.arthan.global.Crashlytics
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.lead.model.postdata.LeadPostData
import com.example.arthan.lead.model.responsedata.LeadResponseData
import com.example.arthan.liveness.LivenessRequest
import com.example.arthan.liveness.LivenessResponse
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.network.S3UploadFile
import com.example.arthan.network.S3Utility
import com.example.arthan.ocr.CardResponse
import com.example.arthan.profile.MyProfileActivity
import com.example.arthan.utils.*
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.SplashActivity
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add_lead_step1.*
import kotlinx.android.synthetic.main.activity_add_lead_step1.btn_next
import kotlinx.android.synthetic.main.activity_add_lead_step1.business_industry_spinner
import kotlinx.android.synthetic.main.activity_add_lead_step1.chk_later
import kotlinx.android.synthetic.main.activity_add_lead_step1.et_area_pincode
import kotlinx.android.synthetic.main.activity_add_lead_step1.et_customer_name
import kotlinx.android.synthetic.main.activity_add_lead_step1.et_date
import kotlinx.android.synthetic.main.activity_add_lead_step1.et_establishment_name
import kotlinx.android.synthetic.main.activity_add_lead_step1.et_mobile_number
import kotlinx.android.synthetic.main.activity_add_lead_step1.img_shop
import kotlinx.android.synthetic.main.activity_add_lead_step1.industry_segment_spinner
import kotlinx.android.synthetic.main.activity_add_lead_step1.industry_type_spinner
import kotlinx.android.synthetic.main.activity_add_lead_step1.ll_upload_photo
import kotlinx.android.synthetic.main.activity_add_lead_step1.switch_interested
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.fragment_add_new_lead.*
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext


open class AddLeadStep1Activity : BaseActivity(), TextWatcher, View.OnClickListener, CoroutineScope,LocationListener {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main


    private var shopUrl: String? = ""
    private var shop1Url: String? = ""
    private var shop2Url: String? = ""
    private  var later:String?="";
    override fun afterTextChanged(p0: Editable?) {
        checkForProceed()
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun screenTitle() = "Add New Lead"

    private var shopUri: Uri? = null
    private var shop1Uri: Uri? = null
    private var shop2Uri: Uri? = null

    override fun contentView() = R.layout.activity_add_lead_step1

    override fun onToolbarBackPressed() = onBackPressed()
    private var lat: String? = null
    private var lng: String? = null
    private var leadId: String = ""
    var locationListener: LocationListener = this
    var locationManager: LocationManager? = null
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {


        menuInflater.inflate(R.menu.more, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.homeMenu -> {
                finish()
                if (ArthanApp.getAppInstance().loginRole == "RM") {
                    startActivity(Intent(this, RMDashboardActivity::class.java).apply {
                        flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
                    })
                } else if (ArthanApp.getAppInstance().loginRole == "BCM") {
                    startActivity(Intent(this, BCMDashboardActivity::class.java).apply {
                        flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
                    })

                } else if (ArthanApp.getAppInstance().loginRole == "BM") {
                    startActivity(Intent(this, BMDashboardActivity::class.java).apply {
                        flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
                    })

                }
            }
            R.id.logoutMenu -> {
                finish()
                startActivity(Intent(this, SplashActivity::class.java))
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun init() {

        loadInitialData()

        btn_next.setOnClickListener(this)
        ll_upload_photo.setOnClickListener(this)
        shop1.setOnClickListener(this)
        shop2.setOnClickListener(this)
        et_date.setOnClickListener(this)

        if(switch_interested.isChecked)
        {
            later="No"
        }
        chk_later.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                et_date.visibility = View.VISIBLE
                later = "Yes"
            }
            else {
                et_date.visibility = View.GONE
                later = "No"
            }
        }

        switch_interested.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switch_interested.text = getString(R.string.yes)
                sp_notinterestReason.visibility = View.GONE
                later="No"

            } else {
                switch_interested.text = getString(R.string.no)
                sp_notinterestReason.visibility = View.VISIBLE
                later="Yes"
            }
        }
        btn_geolocator.setOnClickListener {
            fetchLocation(3)
        }

        et_customer_name.addTextChangedListener(this)
        et_mobile_number.addTextChangedListener(this)
        et_establishment_name.addTextChangedListener(this)
        et_area_pincode.addTextChangedListener(this)

        industry_segment_spinner?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val progressLoader = ProgrssLoader(this@AddLeadStep1Activity)
                    progressLoader.showLoading()
                    launch(ioContext) {
                        val businessActivity = fetchAndUpdateBusinessActivityAsync(
                            (parent?.adapter?.getItem(position) as? Data)?.id ?: ""
                        ).await()
                        if (businessActivity) {
                            withContext(Dispatchers.Main) {
                                progressLoader.dismmissLoading()
                            }
                        }
                    }
                }
            }
    }

    private fun getOutputMediaFile(to:Int): File {
        /* val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Arthan"
        )*/
        val dir = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "Arthan"
        )
        if (!dir.exists())
            dir.mkdirs()
/*
        var file:File=File(
            dir.absolutePath+ "/"+(0..10000).random() +"/IMG_shop.jpg"
        )*/


        /* file.delete()
            file=File(
                dir.absolutePath +"/IMG_shop.jpg"
            )*/
        var fileName=""
        fileName = if(to==0||to==1) {
            "shopImg1"
        }else {
            "shopImg2"
        }
        return File(
            dir.absolutePath + "/${et_mobile_number.text.toString()+"_"+System.currentTimeMillis()}_${fileName}.jpg"
        )
    }

    private fun navigateToCamera(from: Int) {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)


        var req = 0
        when (from) {
            0 -> {
                req = 100
                shopUri = FileProvider.getUriForFile(
                    this, applicationContext.packageName + ".provider",
                    getOutputMediaFile(0)
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, shopUri)
            }
            1 -> {
                req = 1001
                shop1Uri = FileProvider.getUriForFile(
                    this, applicationContext.packageName + ".provider",
                    getOutputMediaFile(1)
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, shop1Uri)
            }
            2 -> {
                req = 1002
                shop2Uri = FileProvider.getUriForFile(
                    this, applicationContext.packageName + ".provider",
                    getOutputMediaFile(2)
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, shop2Uri)
            }
        }
        startActivityForResult(intent, req)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            /* KEY_TO_DETECT_REQUEST_CODE -> {

                 val mResult=( ArthanApp.getAppInstance() as DFTransferResultInterface).result

                 //val mResult = (application as DFTransferResultInterface).result

                 ///get key frame
                 val imageResultArr= mResult.livenessImageResults
                 if (imageResultArr != null) {
                     val size = imageResultArr.size
                     if (!imageResultArr.isNullOrEmpty()) {
                         val imageResult= imageResultArr[0]
                         val imageBitmap= BitmapFactory.decodeByteArray(imageResult.image, 0, imageResult.image.size)
                     }
                 }

                 // the encrypt buffer which is used to send to anti-hack API
                 val livenessEncryptResult= mResult.livenessEncryptResult

             }*/
            100 -> {
//                if(File(shopUri?.path).length()>0) {
                ll_upload_photo.visibility = View.GONE
//                img_shop.visibility = View.VISIBLE
                val uriNew = compressImage(shopUri!!)
                shopUri = getUriFromBitmap(uriNew, img_shop,0)
                //  Glide.with(this).load(shopUri).error(R.mipmap.ic_launcher).into(img_shop)
                checkForProceed()


                val loader = ProgrssLoader(this)
                loader.showLoading()
                loadImage(this, img_shop, shopUri!!, { filePath ->
                    try {
                        val file: File = File(filePath)
                        val url = file.name
                        val fileList: MutableList<S3UploadFile> = mutableListOf()
                        fileList.add(S3UploadFile(file, url))
                        S3Utility.getInstance(this)
                            .uploadFile(fileList,
                                {
                                    shopUrl = fileList[0].url ?: filePath
                                    ThreadUtils.runOnUiThread { loader.dismmissLoading() }
                                }) {
                                ThreadUtils.runOnUiThread { loader.dismmissLoading() }
                            }
                    } catch (e: Exception) {
                        Crashlytics.log(e.message)
                        ThreadUtils.runOnUiThread { loader.dismmissLoading() }
                        e.printStackTrace()
                    }
                })
                //    }


                // detectFace()
            }
            1001 -> {
//                if(File(shopUri?.path).length()>0) {
                ll_upload_photo.visibility = View.GONE
                img_shop.visibility = View.VISIBLE
                val uriNew = compressImage(shop1Uri!!)
                shop1Uri = getUriFromBitmap(uriNew, shop1,1)
                checkForProceed()


                val loader = ProgrssLoader(this)
                loader.showLoading()
                loadImage(this, shop1, shop1Uri!!, { filePath ->
                    try {
                        val file: File = File(filePath)
                        val url = file.name
                        val fileList: MutableList<S3UploadFile> = mutableListOf()
                        fileList.add(S3UploadFile(file, url))
                        S3Utility.getInstance(this)
                            .uploadFile(fileList,
                                {
                                    shop1Url = fileList[0].url ?: filePath
                                    ThreadUtils.runOnUiThread {
                                        Glide.with(this).load(shop1Uri).error(R.mipmap.ic_launcher)
                                            .into(shop1)
                                        loader.dismmissLoading()
                                    }
                                }) {
                                ThreadUtils.runOnUiThread { loader.dismmissLoading() }
                            }
                    } catch (e: Exception) {
                        Crashlytics.log(e.message)
                        ThreadUtils.runOnUiThread { loader.dismmissLoading() }
                        e.printStackTrace()
                    }
                })
                //    }


                // detectFace()
            }
            1002 -> {
//                if(File(shopUri?.path).length()>0) {
                ll_upload_photo.visibility = View.GONE
//                img_shop.visibility = View.VISIBLE
                val uriNew = compressImage(shop2Uri!!)
                shop2Uri = getUriFromBitmap(uriNew, shop2,2)
                checkForProceed()


                val loader = ProgrssLoader(this)
                loader.showLoading()
                loadImage(this, shop2, shop2Uri!!, { filePath ->
                    try {
                        val file: File = File(filePath)
                        val url = file.name
                        val fileList: MutableList<S3UploadFile> = mutableListOf()
                        fileList.add(S3UploadFile(file, url))
                        S3Utility.getInstance(this)
                            .uploadFile(fileList,
                                {

                                    shop2Url = fileList[0].url ?: filePath

                                    ThreadUtils.runOnUiThread {
                                        Glide.with(this).load(shop2Uri).error(R.mipmap.ic_launcher)
                                            .into(shop2)

                                        loader.dismmissLoading()
                                    }
                                }) {
                                ThreadUtils.runOnUiThread { loader.dismmissLoading() }
                            }
                    } catch (e: Exception) {
                        Crashlytics.log(e.message)
                        ThreadUtils.runOnUiThread { loader.dismmissLoading() }
                        e.printStackTrace()
                    }
                })
                //    }


                // detectFace()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun detectFace() {
        if (shopUri != null) {
            val faceDetectionService = RetrofitFactory.getLivenessService()
            //pb_approve.visibility= View.VISIBLE
            val loader = ProgrssLoader(this)
            loader.showLoading()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val stream = contentResolver?.openInputStream(shopUri!!)
                    val bm = BitmapFactory.decodeStream(stream)
                    val base64 = BitmapUtils.getBase64(bm)


                    val response = faceDetectionService.detectFace(LivenessRequest(base64))
                    withContext(Dispatchers.Main) {
                        loader.dismmissLoading()
                        if (response.isSuccessful) {
                            val result: LivenessResponse = response.body() as LivenessResponse

                            Log.e("SCORE", "::: ${result.score}")
                            if (result.score < 0.98) { // 0.98 is recommended threshold
                                Toast.makeText(
                                    this@AddLeadStep1Activity,
                                    "REAL Person",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@AddLeadStep1Activity,
                                    "HACK",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } catch (e: HttpException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun checkForProceed() {
        btn_next.isEnabled = et_customer_name.text?.isNotEmpty() == true &&
                et_mobile_number.text?.isNotEmpty() == true &&
                et_establishment_name.text?.isNotEmpty() == true &&
                et_area_pincode.text?.isNotEmpty() == true &&
                et_area_pincode.length() == 6

    }

    private fun loadInitialData() {
        val progressLoader = ProgrssLoader(this)
        progressLoader.showLoading()
        launch(ioContext) {
            val industrySegment = fetchAndUpdateIndustrySegmentAsync().await()
            val industryType = fetchAndUpdateIndustryTypeAsync().await()
            if (industrySegment && industryType) {
                withContext(Dispatchers.Main) {
                    progressLoader.dismmissLoading()
                }
            }
        }
    }

    private fun getAdapter(list: List<Data>?): DataSpinnerAdapter =
        DataSpinnerAdapter(this, list?.toMutableList() ?: mutableListOf()).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

    private fun fetchAndUpdateBusinessActivityAsync(industryType: String): Deferred<Boolean> =
        async(context = ioContext) {
            try {
                val response =
                    RetrofitFactory.getMasterApiService()
                        .getBusinessActivity(industryType)
                if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                    withContext(uiContext) {
                        business_industry_spinner?.adapter = getAdapter(response.body()?.data)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@async true
        }

    private fun fetchAndUpdateIndustryTypeAsync(): Deferred<Boolean> =
        async(context = ioContext) {
            try {
                val response =
                    RetrofitFactory.getMasterApiService()
                        .getIndustryType()
                if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                    withContext(uiContext) {
                        industry_type_spinner?.adapter = getAdapter(response.body()?.data)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@async true
        }

    private fun fetchAndUpdateIndustrySegmentAsync(): Deferred<Boolean> =
        async(context = ioContext) {
            try {
                val response = RetrofitFactory.getMasterApiService().getIndustrySegment()
                if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                    withContext(uiContext) {
                        industry_segment_spinner?.adapter = getAdapter(response.body()?.data)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@async fetchAndUpdateBusinessActivityAsync("1").await()
        }

    private suspend fun stopLoading(progressBar: ProgrssLoader, message: String?) {
        withContext(Dispatchers.Main) {
            progressBar.dismmissLoading()
            message?.let {
                Toast.makeText(this@AddLeadStep1Activity, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveLead() {
        if (lat == null) {
            Toast.makeText(this,"Location is empty. Please wait fetching your current location",Toast.LENGTH_LONG).show()
            fetchLocation(5)
            return
        }
        val progressBar = ProgrssLoader(this)
        progressBar.showLoading()
        val postBody = LeadPostData(
            leadId = leadId,
            customerName = et_customer_name?.text?.toString() ?: "",
            mobileNo = et_mobile_number?.text?.toString() ?: "",
            establishmentName = et_establishment_name?.text?.toString() ?: "",
            segment = (industry_segment_spinner?.selectedItem as? Data)?.value ?: "",
            industryType = (industry_type_spinner?.selectedItem as? Data)?.value ?: "",
            businessActivity = (business_industry_spinner?.selectedItem as? Data)?.value ?: "",
            areaPincode = et_area_pincode?.text?.toString() ?: "",
            interested = if (switch_interested?.isChecked == true) "Yes" else "No",
            later = if (chk_later?.isChecked == true) "Yes" else "No",
            laterDate = et_date?.text?.toString() ?: "",
            lat = lat.toString(),
            lng = lng.toString(),
            shopPicUrl =  shop1Url,
            shopPicUrl2 = shop2Url,
            reason = when(switch_interested.isChecked){
                true->""
                false->sp_notinterestReason.selectedItem.toString()
            },
            createdBy = ArthanApp.getAppInstance().loginUser
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitFactory.getApiService().saveLead(postBody)
                if (response?.isSuccessful == true) {
                    val result = response.body()
                    AppPreferences.getInstance().remove(AppPreferences.Key.LeadId)
                    AppPreferences.getInstance().remove(AppPreferences.Key.LoanId)
                    AppPreferences.getInstance().remove(AppPreferences.Key.CustomerId)
                    AppPreferences.getInstance().remove(AppPreferences.Key.PrincipleLoanAmount)
                    AppPreferences.getInstance().remove(AppPreferences.Key.BusinessId)
                    if (result?.apiCode == "200") {
                        withContext(Dispatchers.Main) {
                            progressBar.dismmissLoading()
                            if (result.canNavigate == "N") {

                                Toast.makeText(this@AddLeadStep1Activity,"Customer Already exists",Toast.LENGTH_LONG).show()
                                finish()
                            } else {
                                if (chk_later.isChecked || !switch_interested.isChecked) {

                                    finish()
                                } else {
                                    AppPreferences.getInstance()
                                        .addString(AppPreferences.Key.LeadId, result.leadId)
                                    leadId = result.leadId!!
                                    LoanDetailActivity.startMe(
                                        this@AddLeadStep1Activity,
                                        result.leadId
                                    )
                                    //  finish()
                                }
                            }
                        }
                    }else {
                        stopLoading(progressBar, result?.apiDesc)
                    }
                } else {
                    try {
                        val result: LeadResponseData? = Gson().fromJson(
                            response?.errorBody()?.string(),
                            LeadResponseData::class.java
                        )
                        stopLoading(progressBar, result?.message)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        stopLoading(progressBar, "Something went wrong. Please try later!")
                    }
                }
            } catch (e: Exception) {
                stopLoading(progressBar, "Something went wrong. Please try later!")
                e.printStackTrace()
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_next ->{
           if(later.equals("yes",ignoreCase = true)&& et_date?.text?.trim()?.length==0) {

               Toast.makeText(this,"Date is mandatory when later is selected",Toast.LENGTH_LONG)
                   .show()
           }
          else {
               saveLead()
           }
        }
            R.id.ll_upload_photo -> {
                val request = permissionsBuilder(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ).build()
                request.listeners {
                    onAccepted {
                        fetchLocation(0)

                        /* val bundle = Bundle()

                         val intent = Intent().apply {
                             setClass(this@AddLeadStep1Activity, DFSilentLivenessActivity::class.java)
                             putExtras(bundle)
                             putExtra(DFSilentLivenessActivity.KEY_DETECT_IMAGE_RESULT, true)
                             putExtra(DFSilentLivenessActivity.KEY_HINT_MESSAGE_HAS_FACE, "Please hold still")
                             putExtra(DFSilentLivenessActivity.KEY_HINT_MESSAGE_NO_FACE, "Please place your face inside the circle")
                             putExtra(DFSilentLivenessActivity.KEY_HINT_MESSAGE_FACE_NOT_VALID, "Please move away from the screen")
                         }
                         startActivityForResult(intent, KEY_TO_DETECT_REQUEST_CODE)*/
                    }
                    onDenied {
                    }
                    onPermanentlyDenied {
                    }
                }
                request.send()
            }
            R.id.shop1 -> {
                if (shop1Url!!.isNotEmpty()) {


                    showPreview(1)

                } else {
                    val request = permissionsBuilder(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ).build()
                    request.listeners {
                        onAccepted {
                            navigateToCamera(1)
//                            fetchLocation(1)

                            /* val bundle = Bundle()

                         val intent = Intent().apply {
                             setClass(this@AddLeadStep1Activity, DFSilentLivenessActivity::class.java)
                             putExtras(bundle)
                             putExtra(DFSilentLivenessActivity.KEY_DETECT_IMAGE_RESULT, true)
                             putExtra(DFSilentLivenessActivity.KEY_HINT_MESSAGE_HAS_FACE, "Please hold still")
                             putExtra(DFSilentLivenessActivity.KEY_HINT_MESSAGE_NO_FACE, "Please place your face inside the circle")
                             putExtra(DFSilentLivenessActivity.KEY_HINT_MESSAGE_FACE_NOT_VALID, "Please move away from the screen")
                         }
                         startActivityForResult(intent, KEY_TO_DETECT_REQUEST_CODE)*/
                        }
                        onDenied {
                        }
                        onPermanentlyDenied {
                        }
                    }
                    request.send()
                }
            }
            R.id.shop2 -> {

                if (shop2Url.toString().isNotEmpty()) {

                    showPreview(2)
                } else {
                    val request = permissionsBuilder(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ).build()
                    request.listeners {
                        onAccepted {
//                            fetchLocation(2)
                            navigateToCamera(2)

                            /* val bundle = Bundle()

                         val intent = Intent().apply {
                             setClass(this@AddLeadStep1Activity, DFSilentLivenessActivity::class.java)
                             putExtras(bundle)
                             putExtra(DFSilentLivenessActivity.KEY_DETECT_IMAGE_RESULT, true)
                             putExtra(DFSilentLivenessActivity.KEY_HINT_MESSAGE_HAS_FACE, "Please hold still")
                             putExtra(DFSilentLivenessActivity.KEY_HINT_MESSAGE_NO_FACE, "Please place your face inside the circle")
                             putExtra(DFSilentLivenessActivity.KEY_HINT_MESSAGE_FACE_NOT_VALID, "Please move away from the screen")
                         }
                         startActivityForResult(intent, KEY_TO_DETECT_REQUEST_CODE)*/
                        }
                        onDenied {
                        }
                        onPermanentlyDenied {
                        }
                    }
                    request.send()
                }
            }
            R.id.et_date -> {
                val c = Calendar.getInstance()
                DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        val date = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year


                        var timenow = Calendar.getInstance().time
                        var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("")
                        var formatDate: String = simpleDateFormat.format(timenow)
                        if (SimpleDateFormat("dd-MM-yyyy").parse(date).after(Date())) {
                            et_date.setText(date)

                        } else {
                            Toast.makeText(
                                this,
                                "Later should be greater than current date",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }
    }

    private fun fetchLocation(from: Int) {

       when {
           ContextCompat.checkSelfPermission(
               this,
               Manifest.permission.ACCESS_COARSE_LOCATION
           ) == PackageManager.PERMISSION_GRANTED -> {
               // You can use the API that requires the permission.
               AppLocationProvider().getLocation(
                   this,
                   object : AppLocationProvider.LocationCallBack {
                       override fun locationResult(location: Location?) {

                           lat = location?.latitude.toString()
                           lng = location?.longitude.toString()
                           Handler(Looper.getMainLooper()).post( Runnable {
                             //  btn_geolocator.isEnabled=false
                               Toast.makeText(this@AddLeadStep1Activity,
                                   "location received-{$lat} / {$lng}",Toast.LENGTH_LONG).show()
                           })

                           Log.d("latlng", lng.toString())
                           if (from != 3&&from!=5) {
                               navigateToCamera(from)

                           }
                           else if(from==5)
                           {
                               saveLead()
                           }
                           AppLocationProvider().stopLocation()

                           // use location, this might get called in a different thread if a location is a last known location. In that case, you can post location on main thread
                       }

                   })

           }
           else -> {
               val request = permissionsBuilder(
                   Manifest.permission.ACCESS_COARSE_LOCATION,
                   Manifest.permission.ACCESS_FINE_LOCATION
               ).build()
               request.listeners {
                   onAccepted {
                       fetchLocation(from)

                   }
                   onDenied {
                   }
                   onPermanentlyDenied {
                   }
               }
               request.send()
           }

       }

/*
        var mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager;
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mLocationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 0,
            0f, this);
   */ }

    fun showPreview(from: Int)
    {
        var dialog=AlertDialog.Builder(this)
        var view=layoutInflater.inflate(R.layout.shop_img_preview_dialog,null)
        var close=view.findViewById<ImageView>(R.id.closePreview)
        var edit=view.findViewById<ImageView>(R.id.edit)
        var preview=view.findViewById<ImageView>(R.id.preview)
        when(from)
        {
            0->{
//                Glide.with(this).load(shopurl).error(R.mipmap.ic_launcher).into(image)
            }
            1->{
                Glide.with(this).load(shop1Url).error(R.mipmap.ic_launcher).into(preview)
            }
            2->{
                Glide.with(this).load(shop2Url).error(R.mipmap.ic_launcher).into(preview)
            }
        }

        dialog.setView(view)
        var d=dialog.create()
        d.show()
        close.setOnClickListener { d.dismiss() }
        edit.setOnClickListener {
            when(from)
            {
                0->{
//                Glide.with(this).load(shopurl).error(R.mipmap.ic_launcher).into(image)
                }
                1->{
                  shop1Url=""
                  shop1Uri=null
                    shop1.performClick()
                    d.dismiss()
                }
                2->{
                  shop2Url=""
                  shop2Uri=null
                    shop2.performClick()
                    d.dismiss()

                }
            }

        }
    }
    override fun onLocationChanged(location: Location?) {


    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
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

    fun getUriFromBitmap(scaledBitmap:Bitmap?,imageview:ImageView,to:Int):Uri?
    {
        var out: FileOutputStream? = null
        val fileName = getOutputMediaFile(to)
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
}


