package com.example.arthan.lead

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.crashlytics.android.Crashlytics
import com.example.arthan.R
import com.example.arthan.dashboard.bm.BMDocumentVerificationActivity
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.dashboard.rm.RMReAssignListingActivity
import com.example.arthan.dashboard.rm.RMScreeningNavigationActivity
import com.example.arthan.dashboard.rm.ReUsableFragmentSpace
import com.example.arthan.global.AppPreferences
import com.example.arthan.global.ArthanApp
import com.example.arthan.global.BUSINESS
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.lead.model.postdata.BusinessDetails
import com.example.arthan.lead.model.postdata.BusinessDetailsPostData
import com.example.arthan.lead.model.postdata.Partner
import com.example.arthan.lead.model.responsedata.BusinessDetailsResponseData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.PendingCustomersActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_business_information.*
import kotlinx.android.synthetic.main.layout_partner_details.*
import kotlinx.coroutines.*
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.CoroutineContext


/**
 * This is being used as a common fragment. if this is invoked from RMAssignList ,
 * checking for task from intent extras and getting related data
 * Rushi Ayyappa
 */

class BusinessInformationFragment : Fragment(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main
    private var spinnerData:List<Data>?=null
    private  var businessData:BusinessDetails?= null
    private var loanId:String?=""
    private var custId:String?=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_business_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadInitialData()
        annual_turnover_current_year_input.setText(activity?.intent?.getStringExtra("annualturnover"))
        firm_name_input.setText(activity?.intent?.getStringExtra("businessName"))


        ll_partners?.findViewById<View?>(R.id.remove_button)?.visibility = View.GONE
        btn_save_continue.setOnClickListener {
//            saveBusinessData() //comment temporarily
            if(ArthanApp.getAppInstance().loginRole=="BM") {

                updateBusinessDetails()
            }else{
                saveBusinessData()
            }
//            navController?.navigate(R.id.action_business_to_income)
        }

        et_date_of_incorporation.setOnClickListener {
            dateSelection(et_date_of_incorporation)
        }

        any_associate_firm_switch?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                text_input_name_of_associate_firm?.visibility = View.VISIBLE
                text_input_firm_address?.visibility = View.VISIBLE
                constitution_spinner?.visibility = View.VISIBLE
                presently_banking_with_spinner?.visibility = View.VISIBLE
                any_associate_firm_switch?.text = "Yes"
            } else {
                text_input_name_of_associate_firm?.visibility = View.GONE
                text_input_firm_address?.visibility = View.GONE
                constitution_spinner?.visibility = View.GONE
                presently_banking_with_spinner?.visibility = View.GONE
                any_associate_firm_switch?.text = "No"
            }
        }

        switch_partners?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ll_partners?.visibility = View.VISIBLE
                btn_add_partner?.visibility = View.VISIBLE
                switch_partners?.text = "Yes"
            } else {
                ll_partners?.visibility = View.GONE
                btn_add_partner?.visibility = View.GONE
                switch_partners?.text = "No"
            }
        }
        no_of_employee_count?.tag = 0
        no_of_employee_plus_button?.setOnClickListener {
            var count = no_of_employee_count?.tag as? Int ?: 0
            count++
            no_of_employee_count?.text = "$count"
            no_of_employee_count?.tag = count
        }
        no_of_employee_minus_button?.setOnClickListener {
            var count = no_of_employee_count?.tag as? Int ?: 0
            count--
            if (count < 0) {
                count = 0
            }
            no_of_employee_count?.text = "$count"
            no_of_employee_count?.tag = count
        }
        ll_partners?.findViewById<EditText>(R.id.et_dob)?.let { view ->
            view.setOnClickListener {
                val c = Calendar.getInstance()
                DatePickerDialog(
                    activity!!,
                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        val date =
                            dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                        view.setText(date)
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }
//        sb_no_of_employees?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                seek_bar_current_value?.text = if (progress > 0) {
//                    "$progress"
//                } else {
//                    ""
//                }
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {
//            }
//
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//            }
//        })
    }

    private fun updateBusinessDetails() {

        if(ArthanApp.getAppInstance().loginRole=="BM"||ArthanApp.getAppInstance().loginRole=="BCM") {
            var dialog = AlertDialog.Builder(activity)
            var view: View? = activity?.layoutInflater?.inflate(R.layout.remarks_popup, null)
            dialog.setView(view)
            var et_remarks = view?.findViewById<EditText>(R.id.et_remarks)
            var btn_submit_remark = view?.findViewById<Button>(R.id.btn_submit)
            var btn_cancel = view?.findViewById<Button>(R.id.btn_cancel)

            var alert= dialog.create() as AlertDialog
            btn_cancel?.setOnClickListener {
                alert.dismiss()
            }
            btn_submit_remark?.setOnClickListener {
                alert.dismiss()
                val progressBar = ProgrssLoader(this.context!!)
                progressBar.showLoading()
                var map = HashMap<String, String>()
                map["loanId"] = activity?.intent?.getStringExtra(ArgumentKey.LoanId)!!
                map["remarks"] = et_remarks?.text.toString()
                map["userId"] = ArthanApp.getAppInstance().loginUser+""

                CoroutineScope(Dispatchers.IO).launch {
                    val respo = RetrofitFactory.getApiService().updateBusinessDetails(
                        map
                    )

                    val result = respo?.body()
                    if (respo?.body() != null && result?.apiCode == "200") {

                        withContext(Dispatchers.Main) {
                            if(ArthanApp.getAppInstance().loginRole == "BM" && et_remarks?.text.toString().isNotEmpty())
                            {
                                Toast.makeText(activity,"Case ReAssigned to RM",Toast.LENGTH_LONG).show()
                            }
                            /*  AppPreferences.getInstance()
                                .addString(AppPreferences.Key.BusinessId, result.businessId)*/
                            progressBar.dismmissLoading()

                            if (respo.body()!!.discrepancy?.toLowerCase() == "y") {

                                if (ArthanApp.getAppInstance().loginRole == "BM" || ArthanApp.getAppInstance().loginRole == "BCM") {
                                    progressBar.dismmissLoading()

                                    val navController: NavController? =
                                        if (activity is LeadInfoCaptureActivity) Navigation.findNavController(
                                            activity!!,
                                            R.id.frag_container
                                        ) else null
                                    navController?.navigate(R.id.action_business_to_income)

                                    if (activity is LeadInfoCaptureActivity) {
                                        (activity as LeadInfoCaptureActivity).enableInCome()
                                        (activity as LeadInfoCaptureActivity).infoCompleteState(
                                            BUSINESS
                                        )
                                    } else{
                                    if(activity is BMDocumentVerificationActivity) {

                                        (activity as BMDocumentVerificationActivity).moveVPinDataFragment(2)
                                    }else
                                    {

                                    }
                                    }
                                  /*  startActivity(
                                        Intent(
                                            activity,
                                            PendingCustomersActivity::class.java
                                        )
                                    )
                                    activity?.finish()
*/
                                } else {
                                    startActivity(Intent(activity, RMDashboardActivity::class.java))
                                    activity?.finish()
                                }

                            }
                            else {
                                progressBar.dismmissLoading()

                                val navController: NavController? =
                                    if (activity is LeadInfoCaptureActivity) Navigation.findNavController(
                                        activity!!,
                                        R.id.frag_container
                                    ) else null
                                navController?.navigate(R.id.action_business_to_income)

                                if (activity is LeadInfoCaptureActivity) {
                                    (activity as LeadInfoCaptureActivity).enableInCome()
                                    (activity as LeadInfoCaptureActivity).infoCompleteState(
                                        BUSINESS
                                    )
                                } else {

                                }
                                }
                        }
                    }
                        else  {
                        withContext(Dispatchers.Main) {

                            progressBar.dismmissLoading()
                            Toast.makeText(activity, "something went wrong", Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                }
            }


           alert.show()
        }
    }

    private suspend fun stopLoading(progressBar: ProgrssLoader, message: String?) {
        withContext(Dispatchers.Main) {
            progressBar.dismmissLoading()
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveBusinessData() {
        if(loanId==null||loanId==""||custId==null||custId=="")
        {
            loanId=activity?.intent?.getStringExtra("loanId")
            custId=activity?.intent?.getStringExtra("custId")
            if(arguments?.getString("task").equals("RM_AssignList",ignoreCase = true)||
                arguments?.getString("task").equals("RMreJourney",ignoreCase = true))
            {
                loanId= arguments?.getString("loanId")
                custId= arguments?.getString("custId")
            }
        }
        val progressBar = ProgrssLoader(context ?: return)
        progressBar.showLoading()

        val partners: MutableList<Partner> = mutableListOf()
        if (partners_name_input?.text?.isNotEmpty() == true &&
            partners_designation_input?.text?.isNotEmpty() == true &&
            partners_designation_input?.text?.isNotEmpty() == true
        ) {
            partners.add(
                Partner(
                    partnername = partners_name_input?.text?.toString(),
                    partnerdesignation = partners_designation_input?.text?.toString()
                )
            )

            if ((ll_partners?.childCount ?: 0) > 1) {
                for (childCount in 1 until (ll_partners?.childCount ?: 0)) {
                    val partnerView = ll_partners?.getChildAt(childCount)
                    partners.add(
                        Partner(
                            partnername = partnerView?.findViewById<TextInputEditText?>(R.id.partners_name_input)?.text?.toString(),
                            partnerdesignation = partnerView?.findViewById<TextInputEditText?>(R.id.partners_designation_input)?.text?.toString()
                        )
                    )
                }
            }
        }
            val postBody = BusinessDetailsPostData(
              resubmit = "",
            bname = firm_name_input?.text?.toString(),
            loanId = loanId,
            customerId =custId,
            dateofincorporation = et_date_of_incorporation?.text?.toString(),
            firmpan = firm_pan_input?.text?.toString(),
            form6061 = if (switch_form_61?.isChecked == true) "Yes" else "No",
            constitution = (spnr_constitution?.selectedItem as? Data)?.value,
            udhyogaadhar = udhyog_aadhar_id_input?.text?.toString(),
            gstcode = gstin_number_input?.text?.toString(),
            noofemployees = no_of_employee_count?.tag as Int,
            ssiregistrationno = ssi_registration_input?.text?.toString(),
            partners = partners,
            noOfyearsincurrentoffice = no_of_year_in_office_input?.text?.toString(),
            contactpersonname = contact_person_name_input?.text?.toString(),
            natureofassociation = (nature_of_association_spinner?.selectedItem as? Data)?.value,
            landlineMobile = et_mobile_number?.text?.toString(),
            whatsappno = whats_app_number_input?.text?.toString(),
            emailid = email_id_input?.text?.toString(),
            annualturnoverofcurrentfinancialyearLastfinancialyear = annual_turnover_current_year_input?.text?.toString(),
            annualturnover = annual_turnover_input?.text?.toString(),
            annualturnoverofpreviousfinancialyear = annual_turnover_previous_year_input?.text?.toString(),
            projectedturnover = projected_turnover_input?.text?.toString(),
            operatingbusinessaddress = operating_business_address_input?.text?.toString(),
            registeredbusinessaddress = registered_business_address_input?.text?.toString()
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                var response: Response<BusinessDetailsResponseData>?=null
                response = if(arguments?.getString("from")=="rmbusiness"||arguments?.getString("task").equals("RM_AssignList",ignoreCase = true)){

                    postBody.resubmit="yes"
//                    RetrofitFactory.getApiService().rmResubmitBusiness(postBody)
                    RetrofitFactory.getApiService().saveBusinessDetail(postBody)


                }else{
                    RetrofitFactory.getApiService().saveBusinessDetail(postBody)

                }
                if(response?.isSuccessful==true&&arguments?.getString("from")=="rmbusiness")
                {
                    withContext(Dispatchers.Main) {
                        if (arguments?.getString("task").equals("RMreJourney", ignoreCase = true)) {
                            withContext(Dispatchers.IO)
                            {
                                progressBar.dismmissLoading()

                                startActivity(Intent(
                                    activity,
                                    RMScreeningNavigationActivity::class.java
                                )
                                    .apply {
                                        putExtra("loanId", loanId)
                                    })
                                activity?.finish()
                            }
                        } else {
                            withContext(Dispatchers.IO)
                            {
                                if (context is RMReAssignListingActivity) {
                                    var con = context as RMReAssignListingActivity
                                    con.showAssignListFragment()
                                } else if (context is ReUsableFragmentSpace) {
                                    activity?.finish()
                                }
                                progressBar.dismmissLoading()
                            }
                        }
                    }


                }
                 else if (response?.isSuccessful == true) {
                    val result = response.body()
                    if (result?.apiCode == "200") {
                        withContext(Dispatchers.Main) {
                            AppPreferences.getInstance()
                                .addString(AppPreferences.Key.BusinessId, result.businessId)
                            progressBar.dismmissLoading()
                            val navController: NavController? =
                                if (activity is LeadInfoCaptureActivity) Navigation.findNavController(
                                    activity!!,
                                    R.id.frag_container
                                ) else null

                            navController?.navigate(R.id.action_business_to_income)

                            if (activity is LeadInfoCaptureActivity) {
                                (activity as LeadInfoCaptureActivity).enableInCome()
                                (activity as LeadInfoCaptureActivity).infoCompleteState(BUSINESS)
                            }
                        }
                    } else {
                        stopLoading(progressBar, result?.apiDesc)
                    }
                } else {
                    try {
                        val result: BusinessDetailsResponseData? = Gson().fromJson(
                            response?.errorBody()?.string(),
                            BusinessDetailsResponseData::class.java
                        )
                        stopLoading(progressBar, result?.message)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Crashlytics.log(e.message)

                        stopLoading(progressBar, "Something went wrong. Please try later!")
                    }
                }
            } catch (e: Exception) {
                Crashlytics.log(e.message)

                stopLoading(progressBar, "Something went wrong. Please try later!")
                e.printStackTrace()
            }
        }
    }

    private fun dateSelection(editText: EditText) {
        val c = Calendar.getInstance()
        DatePickerDialog(
            activity!!,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val date = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                editText.setText(date)
            },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun loadInitialData() {
        val progressLoader = ProgrssLoader(context!!)
        progressLoader.showLoading()
        launch(ioContext) {
            val constitution = fetchAndUpdateConstitutionAsync().await()
//            val industry = fetchAndUpdateIndustryAsync().await()
            if (/*industry && */constitution) {
                withContext(uiContext) {
                    progressLoader.dismmissLoading()
                    if(arguments?.getString("task").equals("RM_AssignList",ignoreCase = true)||arguments?.getString("task").equals("RMreJourney",ignoreCase = true))
                    {
                        loadDataFromRMAssignList()
                    }
//                    else if(activity.intent.getStringExtra("RMreJourneyScreen"))
                }
            }
        }
    }

    private fun loadDataFromRMAssignList() {


        try {

            var map=HashMap<String,String>()
            map["loanId"]=arguments?.getString("loanId")!!
            map["screen"]="BUSINESS"
            CoroutineScope(Dispatchers.IO).launch {
                /*val respo = RetrofitFactory.getApiService()
                    .getBusinessData(arguments?.getString("loanId"))*/
                val respo = RetrofitFactory.getApiService()
                    .getScreenData(map)
                if (respo != null) {
                    if (respo.isSuccessful && respo.body() != null) {
                        withContext(Dispatchers.Main) {
                            updateData(respo.body()!!.businessDetails,"")
                            updateSpinnerData(respo.body()!!.businessDetails)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            //response = null
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception){
           // response = null
            Crashlytics.log(e.message)

        }


    }

    private fun getAdapter(listlive: List<Data>?): DataSpinnerAdapter =
        DataSpinnerAdapter(activity!!, listlive?.toMutableList() ?: mutableListOf()).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
//
//    private fun fetchAndUpdateBusinessActivityAsync(industryType: String): Deferred<Boolean> =
//        async(context = ioContext) {
//            try {
//                val response =
//                    RetrofitFactory.getMasterApiService().getBusinessActivity(industryType)
//                if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
//                    withContext(uiContext) {
//                        spnr_business_activity?.adapter = getAdapter(response.body()?.data)
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            return@async true
//        }
//
//    private fun fetchAndUpdateIndustryAsync(): Deferred<Boolean> =
//        async(context = ioContext) {
//            try {
//                val response = RetrofitFactory.getMasterApiService().getIndustry()
//                if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
//                    withContext(uiContext) {
//                        spnr_industry?.adapter = getAdapter(response.body()?.data)
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            return@async fetchAndUpdateBusinessActivityAsync(
//                spnr_industry?.selectedItem as? String ?: ""
//            ).await()
//        }

    private fun fetchAndUpdateConstitutionAsync(): Deferred<Boolean> =
        async(context = ioContext) {
            try {
                val response = RetrofitFactory.getMasterApiService().getConstitution()
                if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                    withContext(uiContext) {
                        spnr_constitution?.adapter = getAdapter(response.body()?.data)
                        nature_of_association_spinner?.adapter = getAdapter(response.body()?.data)
                        constitution_spinner?.adapter=getAdapter(response.body()?.data)
                        spinnerData=response.body()?.data
                        if(businessData!=null)
                        updateSpinnerData(businessData)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Crashlytics.log(e.message)

            }
            return@async true
        }

    private fun updateSpinnerData(businessDetails:BusinessDetails?)
    {
        var  list2 = spinnerData

        for (index in 0 until (list2?.size ?: 0)) {
            if (list2?.get(index)?.value == businessDetails?.constitution) {
                spnr_constitution?.setSelection(index)
                break
            }
        }

        for (index in 0 until(list2?.size?:0))
        {
            if(list2?.get(index)?.value==businessDetails?.associateFirms?.get(0)?.constitution)
            {
                constitution_spinner?.setSelection(index)
            }
        }

        for (index in 0 until (list2?.size ?: 0)) {
            if (list2?.get(index)?.value == businessDetails?.constitution) {
                nature_of_association_spinner?.setSelection(index)
                break
            }
        }
    }

    fun updateData(businessDetails: BusinessDetails?,comment:String?) {
        this@BusinessInformationFragment.businessData=businessDetails
        firm_name_input?.setText(businessDetails?.bname)
        et_date_of_incorporation?.setText(businessDetails?.dateofincorporation)
        firm_pan_input?.setText(businessDetails?.firmpan)
        switch_form_61?.isChecked = businessDetails?.form60_61 == "Yes"
        var list = (spnr_constitution?.adapter as? DataSpinnerAdapter)?.list

        for (index in 0 until (list?.size ?: 0)) {
            if (list?.get(index)?.value == businessDetails?.constitution) {
                spnr_constitution?.setSelection(index)
                break
            }
        }
        if (activity is ReUsableFragmentSpace) {
            (activity as ReUsableFragmentSpace).setCommentsToField(comment.toString()+"")
        }
        udhyog_aadhar_id_input?.setText(businessDetails?.udhyogaadhar)
        gstin_number_input?.setText(businessDetails?.gstcode)
        no_of_employee_count?.setText(businessDetails?.noofemployees)
        ssi_registration_input?.setText(businessDetails?.ssiregistrationno)
        contact_person_name_input?.setText(businessDetails?.contactpersonname)
        et_mobile_number?.setText(businessDetails?.landlineMobile)
        no_of_year_in_office_input?.setText(businessDetails?.noOfyearsincurrentoffice)
        whats_app_number_input?.setText(businessDetails?.whatsappno)
        email_id_input?.setText(businessDetails?.emailid)
        annual_turnover_current_year_input?.setText(businessDetails?.annualturnoverofcurrentfinancialyearLastfinancialyear)
        annual_turnover_input?.setText(businessDetails?.annualturnover)
        annual_turnover_previous_year_input?.setText(businessDetails?.annualturnoverofpreviousfinancialyear)
        projected_turnover_input?.setText(businessDetails?.projectedturnover)
        operating_business_address_input?.setText(businessDetails?.operatingbusinessaddress)
        registered_business_address_input?.setText(businessDetails?.registeredbusinessaddress)
        businessDetails?.partners?.let {
            if (it.isNotEmpty()) {
                val partner = it[0]
                partners_name_input?.setText(partner.partnername)
                partners_designation_input?.setText(partner.partnerdesignation)
                et_dob.setText(partner.partnerdob)

                switch_partners.isChecked=true
            }
            if (it.size > 1) {
                for (index in 1 until it.size) {
                    val partnerView = LayoutInflater.from(context)
                        .inflate(R.layout.layout_partner_details, null, false)
                    partnerView?.findViewById<TextInputEditText?>(R.id.partners_name_input)
                        ?.setText(it[index].partnername)
                    partnerView?.findViewById<TextInputEditText?>(R.id.partners_designation_input)
                        ?.setText(it[index].partnerdesignation)
                    partnerView?.findViewById<EditText?>(R.id.et_dob)?.setText(it[index].partnerdob)
                    partnerView?.findViewById<View?>(R.id.remove_button)?.let { view ->

                        view.visibility = View.VISIBLE
                        view.setOnClickListener {
                            ll_partners?.removeView(partnerView)
                        }
                    }
                    partnerView?.findViewById<EditText>(R.id.et_dob)?.let { view ->
                        view.setOnClickListener {
                            val c = Calendar.getInstance()
                            DatePickerDialog(
                                activity!!,
                                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                                    val date =
                                        dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                                    view.setText(date)
                                },
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                    }

                    ll_partners?.addView(partnerView)
                }
            }else {
                val partnerView = LayoutInflater.from(context)
                    .inflate(R.layout.layout_partner_details, null, false)
                partnerView?.findViewById<View?>(R.id.remove_button)?.let { view ->

                    view.visibility = View.GONE
                    view.setOnClickListener {
                        ll_partners?.removeView(partnerView)
                    }
                }
                partnerView?.findViewById<EditText>(R.id.et_dob)?.let { view ->
                    view.setOnClickListener {
                        val c = Calendar.getInstance()
                        DatePickerDialog(
                            activity!!,
                            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                                val date =
                                    dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                                view.setText(date)
                            },
                            c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH),
                            c.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
                }

                ll_partners?.addView(partnerView)
            }
        }

        businessDetails?.associateFirms?.let {
            if (it.isNotEmpty()) {
                val associateFirm = it[0]
                name_of_associate_firm_input?.setText(associateFirm.assoFirmName)
                firm_address_input?.setText(associateFirm.addressLine1+"\n"+associateFirm.addressLine2+"\n"+associateFirm.landmark+","+associateFirm.pinCode+","+associateFirm.areaName+","
                +associateFirm.city+","+associateFirm.district+","+associateFirm.state)


                any_associate_firm_switch.isChecked=true
            }
          /*  if (it.size > 1) {
                for (index in 1 until it.size) {
                    val partnerView = LayoutInflater.from(context)
                        .inflate(R.layout.layout_partner_details, null, false)
                    partnerView?.findViewById<TextInputEditText?>(R.id.partners_name_input)
                        ?.setText(it[index].partnername)
                    partnerView?.findViewById<TextInputEditText?>(R.id.partners_designation_input)
                        ?.setText(it[index].partnerdesignation)
                    partnerView?.findViewById<EditText?>(R.id.et_dob)?.setText(it[index].partnerdob)
                    partnerView?.findViewById<View?>(R.id.remove_button)?.let { view ->

                        view.visibility = View.VISIBLE
                        view.setOnClickListener {
                            ll_partners?.removeView(partnerView)
                        }
                    }
                    partnerView?.findViewById<EditText>(R.id.et_dob)?.let { view ->
                        view.setOnClickListener {
                            val c = Calendar.getInstance()
                            DatePickerDialog(
                                activity!!,
                                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                                    val date =
                                        dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                                    view.setText(date)
                                },
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                    }

                    ll_partners?.addView(partnerView)
                }
            }*/
        }



       var  list2 = spinnerData

        for (index in 0 until(list2?.size?:0))
        {
            if(list2?.get(index)?.value==businessDetails?.associateFirms?.get(0)?.constitution)
            {
                constitution_spinner?.setSelection(index)
            }
        }

        for (index in 0 until (list?.size ?: 0)) {
            if (list?.get(index)?.value == businessDetails?.constitution) {
                nature_of_association_spinner?.setSelection(index)
                break
            }
        }
    }
}