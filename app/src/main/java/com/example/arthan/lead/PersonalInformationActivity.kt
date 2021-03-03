package com.example.arthan.lead

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import com.example.arthan.R
import com.example.arthan.dashboard.bcm.BCMApprovedAddCoApplicant
import com.example.arthan.dashboard.bcm.BCMApprovedLegalStatusActivity
import com.example.arthan.dashboard.bcm.BCMDashboardActivity
import com.example.arthan.dashboard.bm.BMDashboardActivity
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.dashboard.rm.RMScreeningNavigationActivity
import com.example.arthan.global.AppPreferences
import com.example.arthan.global.ArthanApp
import com.example.arthan.global.Crashlytics
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.lead.model.postdata.KYCPostData
import com.example.arthan.lead.model.postdata.PersonalDetails
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
import java.util.HashMap
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
    var loanId:String?=""
    var custId:String?=""

    override fun init() {


        loadInitialData()

        if (intent.hasExtra("PAN_DATA")) {
            mKYCPostData = intent.getParcelableExtra("PAN_DATA") as? KYCPostData
            et_name.setText(mKYCPostData?.customerName)
            panNoEt.setText(mKYCPostData?.panId)
            et_father_name.setText(mKYCPostData?.panFathername)
            et_dob.setText(mKYCPostData?.panDob?.replace("/","-"))
           /* AppPreferences.getInstance()?.also {
                address_line1_input?.setText(it.getString(AppPreferences.Key.AddressLine1))
                address_line2_input?.setText(it.getString(AppPreferences.Key.AddressLine2))
                city_input?.setText(it.getString(AppPreferences.Key.City))

                setValueToState(tl_state,AppPreferences.Key.State)
//                state_input?.setText(it.getString(AppPreferences.Key.State))
                pincode_input?.setText(it.getString(AppPreferences.Key.Pincode))
            }*/
            address_line1_input?.setText(mKYCPostData?.address_line1)
            address_line2_input?.setText(mKYCPostData?.address_line2)
            city_input?.setText(mKYCPostData?.city)

            setValueToState(tl_state,mKYCPostData?.state)
//                state_input?.setText(it.getString(AppPreferences.Key.State))
            pincode_input?.setText(mKYCPostData?.pincode)
            cb_sameAddress.setOnCheckedChangeListener { buttonView, isChecked ->
                if(!isChecked)
                {
                    sameAsAddressLL.visibility=View.VISIBLE
                }else
                {
                    sameAsAddressLL.visibility=View.GONE

                }
            }
        }

        if(intent.hasExtra("APPLICANT_PHOTO")){
            applicantPhoto= intent.getStringExtra("APPLICANT_PHOTO")
        }


//        ll_partners?.findViewById<View?>(R.id.remove_button)?.visibility = View.GONE
        btn_next.setOnClickListener {

            if (et_name.length() > 0 && panNoEt.length() > 0 && et_father_name.length() > 0 && et_dob.length() > 0
                && contact_number_input.length() > 0 && email_id_input.length() > 0 /*&& gross_annual_income_spinner.length() > 0*/ &&
                address_line1_input.length() > 0 && address_line2_input.length() > 0 && pincode_input.length() > 0 && city_input.length() > 0
                && district_input.length() > 0
            ) {
                if (intent.getStringExtra("type") == null) {
                    savePersonalData("PA")
                } else {
                    savePersonalData(intent.getStringExtra("type"))

                }
            }else
            {
                Toast.makeText(this,"All fields are mandatory",Toast.LENGTH_LONG).show()
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
                    var list =
                        (spnr_occupation_type?.adapter as? DataSpinnerAdapter)?.list

                    //fetchmstrIdsubSecurity(list?.get(position)?.description!!.toLowerCase())
                    if (list?.get(position)?.description?.toLowerCase() == "Self Employed Professional".toLowerCase()) {
                        spnr_occupation_name.visibility = View.VISIBLE

                    }else
                    {
                        spnr_occupation_name.visibility=View.GONE

                    }

                }
            }
        sp_religion?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {


                    //fetchmstrIdsubSecurity(list?.get(position)?.description!!.toLowerCase())
                    if (sp_religion.selectedItem.toString().toLowerCase() == "Others".toLowerCase()) {
                        other_religion.visibility=View.VISIBLE


                    }else
                    {
                        others_rel.visibility=View.GONE

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

        var reassign="N"
        if(intent?.getStringExtra("task").equals("RM_AssignList",ignoreCase = true)){
            reassign="Y"
        }

        val category=when(cat_General.isChecked){
            true->"General"
            else->{
                when(cat_OBC.isChecked)
                {
                    true->"OBC"
                    else->{
                        when(cat_SC.isChecked){
                            true->"SC"
                            else->""
                        }
                    }
                }
            }


        }
        val progressBar = ProgrssLoader(this)
        progressBar.showLoading()
        var stage=""
        if(intent.getStringExtra("task")=="Add-CoApplicant"){
         stage ="BCM Approved"
        }
        val postBody = PersonalPostData(
            loanId = loanId,
            custId = custId,
            applicantPanNo=mKYCPostData?.panId,
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
//            state = state_input?.text?.toString() ?: "",
            state = (tl_state.selectedItem as Data).value ?: "",
            addrFlag = cb_sameAddress.isChecked,
            addressLine1p = address1_line1_input?.text?.toString() ?: "",
            addressLine2p = address1_line2_input?.text?.toString() ?: "",
            landmarkp = landmark1_input?.text?.toString() ?: "",
            pinCodep = pincode1_input?.text?.toString() ?: "",
            areaNamep = area_name1_input?.text?.toString() ?: "",
            cityp = city1_input?.text?.toString() ?: "",
            districtp = district_input1?.text?.toString() ?: "",
            statep = (tl_state1.selectedItem as Data).value ?: "",
            applicantType = applicantType,
            category = category,
            religion = when(sp_religion.selectedItem.toString())
            {
                "Others"->other_religion.text.toString()
                else->sp_religion.selectedItem.toString()
            }
            /*when(Rel_hindu.isChecked){
                true->"Hindu"
                else->"Muslim"
            },*/,
            maritalStatus = when(rb_married.isChecked)
            {
                true->"Married"
                else->"UnMarried"
            },
            userId = ArthanApp.getAppInstance().loginUser,
            stage = stage
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

                            if (intent.getStringExtra("task") == "RMreJourney") {
                                startActivity(
                                    Intent(
                                        this@PersonalInformationActivity,
                                        RMScreeningNavigationActivity::class.java
                                    ).apply {
                                        putExtra("loanId", loanId)
                                    }
                                )
                                finish()
                                return@withContext
                            }
                           else if (intent.getStringExtra("task") == "RMContinue") {
                                startActivity(
                                    Intent(
                                        this@PersonalInformationActivity,
                                        RMScreeningNavigationActivity::class.java
                                    ).apply {
                                        putExtra("loanId", loanId)
                                    }
                                )
                                finish()
                                return@withContext
                            } else if(intent.getStringExtra("task")=="Add-CoApplicant")
                            {
                                withContext(Dispatchers.Main)
                                {
                                    finish()

                                }
                            }else {
                                AppPreferences.getInstance()
                                    .addString(
                                        AppPreferences.Key.CustomerId,
                                        result.customerId
                                    )
                                mKYCPostData?.customerId = result.customerId
                                custId = result.customerId
                                loanId = result.loanId
                                mKYCPostData?.paApplicantPhoto = applicantPhoto
                                AppPreferences.getInstance().addString(
                                    AppPreferences.Key.PrincipleLoanAmount,
                                    result.inPrincipleLnAmt
                                )
                                /*                   val kycResponse =
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
                                        }*/
                                progressBar.dismmissLoading()
                            //    if (applicantType == "PA") {
                                    var alert =
                                        AlertDialog.Builder(this@PersonalInformationActivity)
                                    alert.setMessage("Do you want to add co-applicant or Guarantor?")
                                    alert.setPositiveButton(
                                        "Co-Applicant",
                                        DialogInterface.OnClickListener { dialog, which ->
                                            dialog.dismiss()

                                            startActivity(
                                                Intent(
                                                    this@PersonalInformationActivity,
                                                    AddLeadStep2Activity::class.java
                                                ).apply {
                                                    putExtra("type", "CA")
                                                    putExtra("loanId", loanId)
                                                })
                                            finish()

                                        })
                                alert.setNegativeButton("Guarantor", DialogInterface.OnClickListener { dialog, which ->
                                    dialog.dismiss()
                                    startActivity(
                                        Intent(
                                            this@PersonalInformationActivity,
                                            AddLeadStep2Activity::class.java
                                        ).apply {
                                            putExtra("type", "G")
                                            putExtra("loanId", loanId)
                                        })
                                    finish()

                                })
                                    alert.setNeutralButton(
                                        "Cancel",
                                        DialogInterface.OnClickListener { dialog, which ->
                                            ConsentActivity.startMe(
                                                this@PersonalInformationActivity,
                                                result.inPrincipleLnAmt,
                                                response.body()!!.customerId,
                                                response.body()!!.loanId
                                            )
                                            dialog.dismiss()
                                        })
                                    alert.show()

                      //          }
                             /*   if (applicantType == "CA") {
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
                                                    putExtra("loanId", loanId)
                                                })
                                            finish()

                                        })
                                    alert.setNegativeButton(
                                        "No",
                                        DialogInterface.OnClickListener { dialog, which ->
                                            ConsentActivity.startMe(
                                                this@PersonalInformationActivity,
                                                result.inPrincipleLnAmt,
                                                response.body()!!.customerId,
                                                response.body()!!.loanId

                                            )
                                            dialog.dismiss()
                                        })

                                    alert.show()
                                }
                                if (applicantType == "G") {

                                    ConsentActivity.startMe(
                                        this@PersonalInformationActivity,
                                        result.inPrincipleLnAmt,
                                        response.body()!!.customerId,
                                        response.body()!!.loanId


                                    )
                                }*/


                                /*} else {
                                try {
                                    val result: BaseResponseData? = Gson().fromJson(
                                        kycResponse?.errorBody()?.string(),
                                        BaseResponseData::class.java
                                    )
                                    stopLoading(
                                        progressBar,
                                        "Something went wrong with api!!!"*//*result?.message*//*
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Crashlytics.log(e.message)

                                    stopLoading(
                                        progressBar,
                                        "Something went wrong. Please try later!"
                                    )
                                }
                            }
                        }*/
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
                        Crashlytics.log(e.message)

                        stopLoading(
                            progressBar,
                            "Something went wrong. Please try later!"
                        )
                    }
                }
            } catch (e: Exception) {
                stopLoading(progressBar, "Something went wrong. Please try later!")
                e.printStackTrace()
                Crashlytics.log(e.message)

            }
        }
    }

    private fun loadInitialData() {
        val progressLoader = ProgrssLoader(this)
        progressLoader.showLoading()
        loanId=intent.getStringExtra("loanId")
        custId=intent.getStringExtra("custId")
        launch(ioContext) {
            val title = fetchAndUpdateServerTitleAsync().await()
            val state = fetchAndUpdateStateNameAsync().await()
            val nationality = fetchAndUpdateNationalityAsync().await()
            val education = fetchAndUpdateEducationAsync().await()
            val occupationType = fetchAndUpdateOccupationTypeAsync().await()
            val occupationName = fetchAndUpdateOccupationNameAsync().await()
            val sorceOfIncome = fetchAndUpdateSourceOfIncomeAsync().await()
            val anualIncom = fetchAndUpdateGrossAnnuanlIncomeAsync().await()


            if (title && nationality && education && occupationType && occupationName && sorceOfIncome && anualIncom) {
                withContext(uiContext) {
                    progressLoader.dismmissLoading()

                    if(intent.getStringExtra("task")=="RMreJourney"||intent.getStringExtra("task")=="RMContinue")
                    {
                        var map= HashMap<String,String>()
                        map["loanId"]=loanId!!
                        map["screen"]=intent.getStringExtra("screen")
                        map["CustomerId"]=intent.getStringExtra("custId")
                        map["customerId"]=intent.getStringExtra("custId")
                        map["applicantType"]=intent.getStringExtra("screen")

                        val response =
                            RetrofitFactory.getApiService().getScreenData(map)
                        withContext(Dispatchers.Main) {
                            setDataToFields(response.body()?.personalDetails)
                        }
                    }
                   else if(intent.getStringExtra("task")=="Add-CoApplicant")
                    {
                        var map= HashMap<String,String>()
                        map["loanId"]=loanId!!
//                        map["screen"]="PERSONAL_CA"
                        map["screen"]=intent.getStringExtra("screen")

                        val response =
                            RetrofitFactory.getApiService().getScreenData(map)
                        withContext(Dispatchers.Main) {
                            if(response.body()?.personalDetails!=null)
                            setDataToFields(response.body()?.personalDetails)
                        }
                    }
                }
            }
        }
    }

    private fun setDataToFields(personalDetails: List<PersonalDetails>?) {

            var index=-1;
          for (i in 0 until personalDetails?.size!!){

              if(personalDetails[i].applicantType=="PA"&&intent.getStringExtra("screen").endsWith("_PA"))
              {
                  setDataForPA(personalDetails[i])
              }
              if(personalDetails[i].applicantType=="CA"&&intent.getStringExtra("screen").contains("_CA"))
              {
                  setDataForPA(personalDetails[i])
              }
              if(personalDetails[i].applicantType=="G"&&intent.getStringExtra("screen").endsWith("_G"))
              {
                  setDataForPA(personalDetails[i])
              }
        }
    }

    private fun setDataForPA(personalDetails: PersonalDetails) {

        et_name.setText(personalDetails.fullName)
        et_father_name.setText(personalDetails.fatherOrSpousename)
        et_mother_name.setText(personalDetails.motherName)
        et_dob.setText(personalDetails.dob)
        contact_number_input.setText(personalDetails.contactNo)
        email_id_input.setText(personalDetails.email)
        when(personalDetails.gender?.toLowerCase())
        {
            "male","m"->{
                male_radio_button.isChecked=true
            }
            "female","f"->{
                female_radio_button.isChecked=true
            }
            "transgender"->{
                transgender_radio_button.isChecked=true
            }
        }

        setDataToSpinner(spnr_nationality,personalDetails.nationality)
        setDataToSpinner(spnr_eduction,personalDetails.educationlevel)
        setDataToSpinner(spnr_occupation_name,personalDetails.occupation)
        setDataToSpinner(spnr_occupation_type,personalDetails.occupationType)
        setDataToSpinner(source_of_income_spinner,personalDetails.sourceofIncome)
        gross_annual_income_spinner.setText(personalDetails.grossannualIncome)
        address_line1_input.setText(personalDetails.addressLine1)
        address_line2_input.setText(personalDetails.addressLine2)
        landmark_input.setText(personalDetails.landmark)
        pincode_input.setText(personalDetails.pinCode)
        city_input.setText(personalDetails.city)
        district_input.setText(personalDetails.district)

//        state_input.setText(personalDetails.state)
        setDataToSpinner(tl_state,personalDetails.state)
        panNoEt.setText(personalDetails.applicantPanNo)

        when(personalDetails.addrFlag)
        {
            true->{
                sameAsAddressLL.visibility-View.VISIBLE
                address1_line1_input.setText(personalDetails.addressLine1p)
                address1_line2_input.setText(personalDetails.addressLine2p)
                landmark1_input.setText(personalDetails.landmarkp)
                pincode1_input.setText(personalDetails.pinCodep)
                city1_input.setText(personalDetails.cityp)
                district_input1.setText(personalDetails.districtp)
                setDataToSpinner(tl_state1,personalDetails.state)

//                state_input1.setText(personalDetails.statep)

            }

        }
        if(personalDetails.category!=null) {
            when (personalDetails.category) {
                "SC" -> cat_SC.isChecked = true
                "OBC" -> cat_OBC.isChecked = true
                "General" -> cat_General.isChecked = true
            }
        }
        if(personalDetails.religion!=null) {
            when (personalDetails.religion.toLowerCase()) {
                "Hindu".toLowerCase() -> sp_religion.setSelection(0)
                "Muslim".toLowerCase() -> sp_religion.setSelection(1)
                "Christian".toLowerCase() -> sp_religion.setSelection(2)
                "Others".toLowerCase() ->{
                    other_religion.setText(personalDetails.religion.toString())
                    other_religion.visibility=View.VISIBLE
                    sp_religion.setSelection(3)
                }
                else ->{
                    sp_religion.setSelection(3)
                }

            }
        }
        /*when(personalDetails.m)
        {
            "SC"->cat_SC.isChecked=true
            "OBC"->cat_OBC.isChecked=true
            "General"->cat_General.isChecked=true
        }
        */


    }

    private fun setDataToSpinner(spinner:Spinner,value:String?)
    {
        val list=  (spinner.adapter as? DataSpinnerAdapter)?.list
        if(list!=null) {
            for (i in 0 until list!!.size)
            {
                if(list[i].value==value)
                {
                    spinner.setSelection(i)
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
                    Crashlytics.log(e.message)

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Crashlytics.log(e.message)

        }
        return@async true
    }

    private fun fetchAndUpdateNationalityAsync(): Deferred<Boolean> = async(context = ioContext) {
        try {
            val response = RetrofitFactory.getMasterApiService().getNationality()
            if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                withContext(uiContext) {
                    spnr_nationality?.adapter = getAdapter(response.body()?.data)
                    spnr_nationality.isEnabled=false;
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Crashlytics.log(e.message)

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
            Crashlytics.log(e.message)

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
                        var list =
                            (spnr_occupation_type?.adapter as? DataSpinnerAdapter)?.list

                        if (list?.get(0)?.description?.toLowerCase() == "Self Employed Professional".toLowerCase()) {
                            spnr_occupation_name.visibility = View.VISIBLE

                        }else
                        {
                            spnr_occupation_name.visibility=View.GONE

                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Crashlytics.log(e.message)

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
                Crashlytics.log(e.message)

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
                Crashlytics.log(e.message)

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
                Crashlytics.log(e.message)

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
                if(ArthanApp.getAppInstance().loginRole=="RM")
                {
                    startActivity(Intent(this, RMDashboardActivity::class.java))
                }else if(ArthanApp.getAppInstance().loginRole=="BCM")
                {
                    startActivity(Intent(this, BCMDashboardActivity::class.java))

                }else if(ArthanApp.getAppInstance().loginRole=="BM")
                {
                    startActivity(Intent(this, BMDashboardActivity::class.java))

                }
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

    private fun fetchAndUpdateStateNameAsync(): Deferred<Boolean> =
        async(context = ioContext) {
            try {
                val response = RetrofitFactory.getMasterApiService().getamStates()
                if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                    withContext(uiContext) {
                        tl_state?.adapter = getAdapter(response.body()?.data)
                        tl_state1?.adapter=getAdapter(response.body()?.data)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Crashlytics.log(e.message)

            }
            return@async true
        }

    fun setValueToState(sp:Spinner,value: String?) {

        if (sp.adapter != null) {
            val list = (sp.adapter as DataSpinnerAdapter).list
            for (i in 0 until list.size) {
                if (list[i].value.toLowerCase() == value?.toLowerCase()) {
                    sp.setSelection(i)
                }
            }

        }
    }
}