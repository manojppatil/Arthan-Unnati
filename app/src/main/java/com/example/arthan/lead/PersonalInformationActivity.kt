package com.example.arthan.lead

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.example.arthan.R
import com.example.arthan.global.AppPreferences
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.lead.model.postdata.KYCPostData
import com.example.arthan.lead.model.postdata.PersonalPostData
import com.example.arthan.lead.model.responsedata.BaseResponseData
import com.example.arthan.lead.model.responsedata.PersonalResponseData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.utils.dateSelection
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.SplashActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_personal_information.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class PersonalInformationActivity : BaseActivity(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    override fun contentView() = R.layout.activity_personal_information

    override fun onToolbarBackPressed() = onBackPressed()

    var mKYCPostData: KYCPostData? = null
    var applicantPhoto: String?= ""

    override fun init() {

        if (intent.hasExtra("PAN_DATA")) {
            mKYCPostData = intent.getParcelableExtra("PAN_DATA") as? KYCPostData
            et_name.setText(mKYCPostData?.panFirstname)
            panNoEt.setText(mKYCPostData?.panId)
            et_father_name.setText(mKYCPostData?.panFathername)
            et_dob.setText(mKYCPostData?.panDob?.replace("/","-"))
            AppPreferences.getInstance()?.also {
                address_line1_input?.setText(it.getString(AppPreferences.Key.AddressLine1))
                address_line2_input?.setText(it.getString(AppPreferences.Key.AddressLine2))
                city_input?.setText(it.getString(AppPreferences.Key.City))
                state_input?.setText(it.getString(AppPreferences.Key.State))
                pincode_input?.setText(it.getString(AppPreferences.Key.Pincode))
            }
        }

        if(intent.hasExtra("APPLICANT_PHOTO")){
            applicantPhoto= intent.getStringExtra("APPLICANT_PHOTO")
        }

        loadInitialData()

//        ll_partners?.findViewById<View?>(R.id.remove_button)?.visibility = View.GONE
        btn_next.setOnClickListener {
            if(intent.getStringExtra("type")==null) {
                savePersonalData("PA")
            }else
            {
                savePersonalData(intent.getStringExtra("type"))

            }
        }

        et_dob.setOnClickListener {
            dateSelection(this, et_dob)
        }

        spnr_occupation_type?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if((parent?.getItemAtPosition(position) as Data).description == "Self Employed Professional")
                    {
                        spnr_occupation_name.visibility=View.VISIBLE

                    }else
                    {
                        spnr_occupation_name.visibility=View.GONE

                    }
                }
            }

//        switch_partners.setOnCheckedChangeListener { buttonView, isChecked ->
//            ll_partners?.findViewById<View?>(R.id.remove_button)?.visibility = View.GONE
//            if (isChecked) {
//                ll_partners.visibility = View.VISIBLE
//                btn_add_partner.visibility = View.VISIBLE
//                buttonView?.text = "Yes"
//            } else {
//                buttonView?.text = "No"
//                ll_partners.visibility = View.GONE
//                btn_add_partner.visibility = View.GONE
//            }
//        }

//        btn_add_partner.setOnClickListener {
//            val partnerView =
//                LayoutInflater.from(this).inflate(R.layout.layout_partner_details, null, false)
//            partnerView?.findViewById<View?>(R.id.remove_button)?.setOnClickListener {
//                ll_partners?.removeView(partnerView)
//            }
//            ll_partners.addView(partnerView)
//        }

    }

    private suspend fun stopLoading(progressBar: ProgrssLoader, message: String?) {
        withContext(uiContext) {
            progressBar.dismmissLoading()
            message?.let {
                Toast.makeText(this@PersonalInformationActivity, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun savePersonalData( applicantType:String) {

        val progressBar = ProgrssLoader(this)
        progressBar.showLoading()
        val postBody = PersonalPostData(
            loanId = AppPreferences.getInstance().getString(AppPreferences.Key.LoanId),
            customeId = AppPreferences.getInstance().getString(AppPreferences.Key.CustomerId),
            title = (spnr_title?.selectedItem as? Data)?.value ?: "",
            fullName = et_name?.text?.toString() ?: "",
            fatherOrSpousename = et_father_name?.text?.toString() ?: "",
            motherName = et_mother_name?.text?.toString() ?: "",
            dob = et_dob?.text?.toString() ?: "",
            contactNo = contact_number_input?.text?.toString() ?: "",
            email = email_id_input?.text?.toString() ?: "",
            gender = when (rgrp_gender?.checkedRadioButtonId) {
                R.id.male_radio_button -> male_radio_button?.text?.toString() ?: "Male"
                R.id.female_radio_button -> female_radio_button?.text?.toString() ?: "Female"
                R.id.transgender_radio_button -> transgender_radio_button?.text?.toString()
                    ?: "Transgender"
                else -> ""
            },
            nationality = (spnr_nationality?.selectedItem as? Data)?.value ?: "",
            educationlevel = (spnr_eduction?.selectedItem as? Data)?.value ?: "",
            occupationType = (spnr_occupation_type?.selectedItem as? Data)?.value ?: "",
            occupation = (spnr_occupation_name?.selectedItem as? Data)?.value ?: "",
            sourceofIncome = (source_of_income_spinner?.selectedItem as? Data)?.value ?: "",
            grossannualIncome = gross_annual_income_spinner?.text.toString(),
            addressLine1 = address_line1_input?.text?.toString() ?: "",
            addressLine2 = address_line2_input?.text?.toString() ?: "",
            landmark = landmark_input?.text?.toString() ?: "",
            pinCode = pincode_input?.text?.toString() ?: "",
            areaName = area_name_input?.text?.toString() ?: "",
            city = city_input?.text?.toString() ?: "",
            district = district_input?.text?.toString() ?: "",
            state = state_input?.text?.toString() ?: "",
            applicantType = applicantType
        )

        CoroutineScope(ioContext).launch {
            try {
                val response =
                    RetrofitFactory.getApiService().savePersonalDetail(postBody)
                if (response?.isSuccessful == true) {
                    val result = response.body()
                    if (result?.apiCode == "200") {
                        withContext(uiContext) {
                            progressBar.dismmissLoading()
                            AppPreferences.getInstance()
                                .addString(
                                    AppPreferences.Key.CustomerId,
                                    result.customerId
                                )
                            mKYCPostData?.customerId = result.customerId
                            mKYCPostData?.paApplicantPhoto= applicantPhoto
                            AppPreferences.getInstance().addString(
                                AppPreferences.Key.PrincipleLoanAmount,
                                result.inPrincipleLnAmt
                            )
                            val kycResponse =
                                RetrofitFactory.getApiService().saveKycDetail(mKYCPostData)
                            if (kycResponse?.isSuccessful == true) {
                                val kycResult = kycResponse.body()
                                withContext(Dispatchers.Main) {
                                    if (kycResult?.apiCode == "200") {
                                        AppPreferences.getInstance().also {
                                            it.remove(AppPreferences.Key.AddressLine1)
                                            it.remove(AppPreferences.Key.AddressLine2)
                                            it.remove(AppPreferences.Key.City)
                                            it.remove(AppPreferences.Key.State)
                                            it.remove(AppPreferences.Key.Pincode)
                                        }
                                        progressBar.dismmissLoading()
                                        if (applicantType == "PA") {
                                            var alert =
                                                AlertDialog.Builder(this@PersonalInformationActivity)
                                            alert.setMessage("Do you want to add co-applicant ?")
                                            alert.setPositiveButton(
                                                "Yes",
                                                DialogInterface.OnClickListener { dialog, which ->
                                                    dialog.dismiss()

                                                    startActivity(
                                                        Intent(
                                                            this@PersonalInformationActivity,
                                                            AddLeadStep2Activity::class.java
                                                        ).apply {
                                                            putExtra("type", "CA")
                                                        })
                                                    finish()

                                                })
                                            alert.setNegativeButton(
                                                "No",
                                                DialogInterface.OnClickListener { dialog, which ->
                                                    ConsentActivity.startMe(
                                                        this@PersonalInformationActivity,
                                                        result.inPrincipleLnAmt
                                                    )
                                                    dialog.dismiss()
                                                })
                                            alert.show()

                                        }
                                        if (applicantType == "CA") {
                                            var alert =
                                                AlertDialog.Builder(this@PersonalInformationActivity)
                                            alert.setMessage("Do you want to add Guarantor ?")
                                            alert.setPositiveButton(
                                                "Yes",
                                                DialogInterface.OnClickListener { dialog, which ->
                                                    dialog.dismiss()

                                                    startActivity(
                                                        Intent(
                                                            this@PersonalInformationActivity,
                                                            AddLeadStep2Activity::class.java
                                                        ).apply {
                                                            putExtra("type", "G")
                                                        })
                                                    finish()

                                                })
                                            alert.setNegativeButton(
                                                "No",
                                                DialogInterface.OnClickListener { dialog, which ->
                                                    ConsentActivity.startMe(
                                                        this@PersonalInformationActivity,
                                                        result.inPrincipleLnAmt
                                                    )
                                                    dialog.dismiss()
                                                })

                                            alert.show()
                                        }
                                    }
                                    if (applicantType == "G") {

                                        ConsentActivity.startMe(
                                            this@PersonalInformationActivity,
                                            result.inPrincipleLnAmt
                                        )
                                    }
                                }
                            } else {
                                try {
                                    val result: BaseResponseData? = Gson().fromJson(
                                        kycResponse?.errorBody()?.string(),
                                        BaseResponseData::class.java
                                    )
                                    stopLoading(
                                        progressBar,
                                        "Something went wrong with api!!!"/*result?.message*/
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    stopLoading(
                                        progressBar,
                                        "Something went wrong. Please try later!"
                                    )
                                }
                            }
                        }
                    } else {
                        stopLoading(progressBar, result?.apiDesc)
                    }
                } else {
                    try {
                        val result: PersonalResponseData? = Gson().fromJson(
                            response?.errorBody()?.toString(),
                            PersonalResponseData::class.java
                        )
                        stopLoading(progressBar, result?.message)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        stopLoading(
                            progressBar,
                            "Something went wrong. Please try later!"
                        )
                    }
                }
            } catch (e: Exception) {
                stopLoading(progressBar, "Something went wrong. Please try later!")
                e.printStackTrace()
            }
        }
    }

    private fun loadInitialData() {
        val progressLoader = ProgrssLoader(this)
        progressLoader.showLoading()
        launch(ioContext) {
            val title = fetchAndUpdateServerTitleAsync().await()
            val nationality = fetchAndUpdateNationalityAsync().await()
            val education = fetchAndUpdateEducationAsync().await()
            val occupationType = fetchAndUpdateOccupationTypeAsync().await()
            val occupationName = fetchAndUpdateOccupationNameAsync().await()
            val sorceOfIncome = fetchAndUpdateSourceOfIncomeAsync().await()
            val anualIncom = fetchAndUpdateGrossAnnuanlIncomeAsync().await()

            if (title && nationality && education && occupationType && occupationName && sorceOfIncome && anualIncom) {
                withContext(uiContext) {
                    progressLoader.dismmissLoading()
                }
            }
        }
    }

    private fun getAdapter(list: List<Data>?): DataSpinnerAdapter =
        DataSpinnerAdapter(this, list?.toMutableList() ?: mutableListOf()).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

    private fun fetchAndUpdateServerTitleAsync(): Deferred<Boolean> = async(context = ioContext) {
        try {
            val response = RetrofitFactory.getMasterApiService().getTitle()
            if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                try {
                    withContext(uiContext) {
                        spnr_title?.adapter = getAdapter(response.body()?.data)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@async true
    }

    private fun fetchAndUpdateNationalityAsync(): Deferred<Boolean> = async(context = ioContext) {
        try {
            val response = RetrofitFactory.getMasterApiService().getNationality()
            if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                withContext(uiContext) {
                    spnr_nationality?.adapter = getAdapter(response.body()?.data)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@async true
    }

    private fun fetchAndUpdateEducationAsync(): Deferred<Boolean> = async(context = ioContext) {
        try {
            val response = RetrofitFactory.getMasterApiService().getEducation()
            if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                withContext(uiContext) {
                    spnr_eduction?.adapter = getAdapter(response.body()?.data)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@async true
    }

    private fun fetchAndUpdateOccupationTypeAsync(): Deferred<Boolean> =
        async(context = ioContext) {
            try {
                val response = RetrofitFactory.getMasterApiService().getOccupationType()
                if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                    withContext(uiContext) {
                        spnr_occupation_type?.adapter = getAdapter(response.body()?.data)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@async true
        }

    private fun fetchAndUpdateOccupationNameAsync(): Deferred<Boolean> =
        async(context = ioContext) {
            try {
                val response = RetrofitFactory.getMasterApiService().getOccupationName()
                if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                    withContext(uiContext) {
                        spnr_occupation_name?.adapter = getAdapter(response.body()?.data)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@async true
        }

    private fun fetchAndUpdateSourceOfIncomeAsync(): Deferred<Boolean> =
        async(context = ioContext) {
            try {
                val response = RetrofitFactory.getMasterApiService().getIncomeSource()
                if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                    withContext(uiContext) {
                        source_of_income_spinner?.adapter = getAdapter(response.body()?.data)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@async true
        }

    private fun fetchAndUpdateGrossAnnuanlIncomeAsync(): Deferred<Boolean> =
        async(context = ioContext) {
            try {
                val response = RetrofitFactory.getMasterApiService().getGrossAnnualIncome()
                if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                    withContext(uiContext) {
                        //gross_annual_income_spinner?.adapter = getAdapter(response.body()?.data)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@async true
        }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {


        menuInflater.inflate(R.menu.more,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.homeMenu->{
                finish()

            }
            R.id.logoutMenu->
            {
                finish()
                startActivity(Intent(this, SplashActivity::class.java))
            }

        }
        return super.onOptionsItemSelected(item)
    }
    override fun screenTitle() =
        when(intent.getStringExtra("type"))
        {
            null-> "Personal Details"
            "CA"->"Co-Applicant Details"
            "G"->"Guarantor Details"
            else->"Personal Details"

        }


}