package com.example.arthan.lead


import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
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
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.BitmapUtils
import com.example.arthan.utils.ProgrssLoader
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_add_new_lead.*
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.io.File
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * A simple [Fragment] subclass.
 */
class AddNewLeadFragment : NavHostFragment(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    private var mShopUri: Uri? = null
    private val nTextChanged = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) = checkForProceed()
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new_lead, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadInitialData()

        btn_next.setOnClickListener { saveLead() }
        ll_upload_photo.setOnClickListener {
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
        et_date.setOnClickListener { _ ->
            val c = Calendar.getInstance()
            context?.let {
                DatePickerDialog(
                    it,
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

        et_customer_name.addTextChangedListener(nTextChanged)
        et_mobile_number.addTextChangedListener(nTextChanged)
        et_establishment_name.addTextChangedListener(nTextChanged)
        et_area_pincode.addTextChangedListener(nTextChanged)

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
                    val progressLoader: ProgrssLoader? =
                        if (context != null) ProgrssLoader(context!!) else null
                    progressLoader?.showLoading()
                    launch(ioContext) {
                        val businessActivity = fetchAndUpdateBusinessActivityAsync(
                            (parent?.adapter?.getItem(position) as? Data)?.id ?: ""
                        ).await()
                        if (businessActivity) {
                            withContext(Dispatchers.Main) {
                                progressLoader?.dismmissLoading()
                            }
                        }
                    }
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            100 -> {
                ll_upload_photo.visibility = View.GONE
                img_shop.visibility = View.VISIBLE
                Glide.with(this).load(mShopUri).into(img_shop)
                checkForProceed()
                detectFace()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
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
        context?.let {
            mShopUri = FileProvider.getUriForFile(
                it, activity?.applicationContext?.packageName + ".provider",
                getOutputMediaFile()
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mShopUri)
        }

        startActivityForResult(intent, 100)
    }

    private fun detectFace() {
        if (mShopUri != null) {
            val faceDetectionService = RetrofitFactory.getLivenessService()
            //pb_approve.visibility= View.VISIBLE
            val loader: ProgrssLoader? = if (context != null) ProgrssLoader(context!!) else null
            loader?.showLoading()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val stream = context?.contentResolver?.openInputStream(mShopUri!!)
                    val bm = BitmapFactory.decodeStream(stream)
                    val base64 = BitmapUtils.getBase64(bm)

                    val response = faceDetectionService.detectFace(LivenessRequest(base64))
                    withContext(Dispatchers.Main) {
                        loader?.dismmissLoading()
                        if (response.isSuccessful) {
                            val result: LivenessResponse = response.body() as LivenessResponse

                            Log.e("SCORE", "::: ${result.score}")
                            if (result.score < 0.98) { // 0.98 is recommended threshold
                                Toast.makeText(
                                    context,
                                    "REAL Person",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
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
        if (et_customer_name.text.isNullOrBlank() ||
            et_mobile_number.text.isNullOrBlank() ||
            et_establishment_name.text.isNullOrBlank() ||
            et_mobile_number.text.isNullOrBlank() /*||
                img_shop.visibility == View.GONE*/) {
            btn_next.isEnabled = false
            btn_next.setBackgroundResource(R.drawable.ic_next_disable)
            context?.let { context ->
                btn_next.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.disable_text
                    )
                )
            }
        } else {
            btn_next.isEnabled = true
            btn_next.setBackgroundResource(R.drawable.ic_next_enabled)
            context?.let { context ->
                btn_next.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.white
                    )
                )
            }
        }

    }

    private fun loadInitialData() {
        val progressLoader: ProgrssLoader? = if (context != null) ProgrssLoader(context!!) else null
        progressLoader?.showLoading()
        launch(ioContext) {
            val industrySegment = fetchAndUpdateIndustrySegmentAsync().await()
            val industryType = fetchAndUpdateIndustryTypeAsync().await()
            if (industrySegment && industryType) {
                withContext(Dispatchers.Main) {
                    progressLoader?.dismmissLoading()
                }
            }
        }
    }

    private fun getAdapter(list: List<Data>?): DataSpinnerAdapter? =
        if (context != null) DataSpinnerAdapter(
            context!!,
            list?.toMutableList() ?: mutableListOf()
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        } else null

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

    private suspend fun stopLoading(progressBar: ProgrssLoader?, message: String?) {
        withContext(Dispatchers.Main) {
            progressBar?.dismmissLoading()
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveLead() {
        val progressBar: ProgrssLoader? = if (context != null) ProgrssLoader(context!!) else null
        progressBar?.showLoading()
        val postBody = LeadPostData(
            customerName = et_customer_name?.text?.toString() ?: "",
            mobileNo = et_mobile_number?.text?.toString() ?: "",
            establishmentName = et_establishment_name?.text?.toString() ?: "",
            segment = industry_segment_spinner?.selectedItem as? String ?: "",
            industryType = industry_type_spinner?.selectedItem as? String ?: "",
            businessActivity = business_industry_spinner?.selectedItem as? String ?: "",
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
                            progressBar?.dismmissLoading()
                            AppPreferences.getInstance()
                                .addString(AppPreferences.Key.LeadId, result.leadId)
                            findNavController(this@AddNewLeadFragment).navigate(
                                R.id.action_addNewLeadFragment_to_addLoanDetailsFragment,
                                Bundle().also {
                                    it.putString(ArgumentKey.LeadId, result.leadId)
                                })
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
}
