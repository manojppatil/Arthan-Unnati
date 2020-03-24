package com.example.arthan.lead

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.arthan.R
import com.example.arthan.global.AppPreferences
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.lead.model.postdata.LeadPostData
import com.example.arthan.lead.model.responsedata.LeadResponseData
import com.example.arthan.liveness.LivenessRequest
import com.example.arthan.liveness.LivenessResponse
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.BitmapUtils
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add_lead_step1.*
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.io.File
import java.util.*
import kotlin.coroutines.CoroutineContext


class AddLeadStep1Activity : BaseActivity(), TextWatcher, View.OnClickListener, CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    override fun afterTextChanged(p0: Editable?) {
        checkForProceed()
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun screenTitle() = "Add New Lead"

    private var shopUri: Uri? = null

    override fun contentView() = R.layout.activity_add_lead_step1

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        loadInitialData()

        btn_next.setOnClickListener(this)
        ll_upload_photo.setOnClickListener(this)
        et_date.setOnClickListener(this)

        chk_later.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                et_date.visibility = View.VISIBLE
            else
                et_date.visibility = View.GONE
        }

        switch_interested.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                switch_interested.text = getString(R.string.yes)
            else
                switch_interested.text = getString(R.string.no)
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

    private fun getOutputMediaFile(): File {
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
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
        shopUri = FileProvider.getUriForFile(
            this, applicationContext.packageName + ".provider",
            getOutputMediaFile()
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, shopUri)

        startActivityForResult(intent, 100)

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
                ll_upload_photo.visibility = View.GONE
                img_shop.visibility = View.VISIBLE
                Glide.with(this).load(shopUri).into(img_shop)
                checkForProceed()
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
                et_area_pincode.text?.isNotEmpty() == true
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
        val progressBar = ProgrssLoader(this)
        progressBar.showLoading()
        val postBody = LeadPostData(
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
            lat = "12.1",
            long = "15.2",
            createdBy = AppPreferences.getInstance().getString(AppPreferences.Key.LoginType)
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
                            AppPreferences.getInstance()
                                .addString(AppPreferences.Key.LeadId, result.leadId)
                            LoanDetailActivity.startMe(this@AddLeadStep1Activity, result.leadId)
                        }
                    } else {
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
            R.id.btn_next -> saveLead()
            R.id.ll_upload_photo -> {
                val request = permissionsBuilder(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).build()
                request.listeners {
                    onAccepted {
                        navigateToCamera()
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
            R.id.et_date -> {
                val c = Calendar.getInstance()
                DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        val date = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                        et_date.setText(date)
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }
    }

}