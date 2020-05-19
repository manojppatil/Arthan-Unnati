package com.example.arthan.lead


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.crashlytics.android.Crashlytics

import com.example.arthan.R
import com.example.arthan.dashboard.bm.BMScreeningReportActivity
import com.example.arthan.global.AppPreferences
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.lead.model.postdata.*
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.PendingCustomersActivity
import kotlinx.android.synthetic.main.collateral_section.*
import kotlinx.android.synthetic.main.fragment_other_details.*
import kotlinx.android.synthetic.main.liquid_type_layout.*
import kotlinx.android.synthetic.main.movable_type_layout.*
import kotlinx.android.synthetic.main.movable_type_layout.rb_individual
import kotlinx.coroutines.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.coroutines.CoroutineContext

/**
 * A simple [Fragment] subclass.
 */
class OtherDetailsFragment : Fragment(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    private var mLoanId: String? = null
    private var mCustomerId: String? = null
    var collaterals: ArrayList<CollateralData> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                   // enableInCome()
                    val navController: NavController? = if (activity is AddLeadActivity) Navigation.findNavController(
                        activity!!,
                        R.id.frag_container
                    ) else null
                    var b=Bundle()
                    b.putString("from","rmIncome")
                    navController?.navigate(R.id.frag_income_info,b)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        return inflater.inflate(R.layout.fragment_other_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLoanId = AppPreferences.getInstance().getString(AppPreferences.Key.LoanId)
        mCustomerId = AppPreferences.getInstance().getString(AppPreferences.Key.CustomerId)

        loadInitialData()
        if (activity?.intent?.extras?.getString("FROM")
                .equals("BCM") || activity?.intent?.extras?.getString("FROM").equals("BM")
        ) {
            bcmCheckBoxes.visibility = View.VISIBLE
        } else {
            bcmCheckBoxes.visibility = View.GONE
            if (activity?.intent?.getStringExtra("loanType")
                    .equals("unsecure", ignoreCase = true)
            ) {
                ll_collateral.visibility = View.GONE
            }

        }

        trade_reference_1_years_working_with_count?.tag = 0
        trade_reference_1_years_working_with_plus_button?.setOnClickListener {
            updateCount(
                IncomeInformationFragment.UpdateCountType.Increment,
                trade_reference_1_years_working_with_count
            )
        }
        trade_reference_1_years_working_with_minus_button?.setOnClickListener {
            updateCount(
                IncomeInformationFragment.UpdateCountType.Decrement,
                trade_reference_1_years_working_with_count
            )
        }

        trade_reference_2_years_working_with_count?.tag = 0
        trade_reference_2_years_working_with_plus_button?.setOnClickListener {
            updateCount(
                IncomeInformationFragment.UpdateCountType.Increment,
                trade_reference_2_years_working_with_count
            )
        }
        trade_reference_2_years_working_with_minus_button?.setOnClickListener {
            updateCount(
                IncomeInformationFragment.UpdateCountType.Decrement,
                trade_reference_2_years_working_with_count
            )
        }

        no_of_rented_tenants_count?.tag = 0
        no_of_rented_tenants_plus_button?.setOnClickListener {
            updateCount(
                IncomeInformationFragment.UpdateCountType.Increment,
                no_of_rented_tenants_count
            )
        }
        no_of_rented_tenants_minus_button?.setOnClickListener {
            updateCount(
                IncomeInformationFragment.UpdateCountType.Decrement,
                no_of_rented_tenants_count
            )
        }


        val securitySpinner: AdapterView.OnItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    parent?.getItemAtPosition(position)?.let {

                        var list =
                            (sp_security?.adapter as? DataSpinnerAdapter)?.list

                        //fetchmstrIdsubSecurity(list?.get(position)?.description!!.toLowerCase())
                        if (list?.get(position)?.description?.toLowerCase() == "movable") {
                            security_section_movable.visibility = View.VISIBLE
                        } else {
                            security_section_movable.visibility = View.GONE

                        }
                        if (list?.get(position)?.description?.toLowerCase() == "immovable" || list?.get(
                                position
                            )?.description?.toLowerCase() == "Negative Lien".toLowerCase()
                        ) {
                            immovable_section.visibility = View.VISIBLE
                            CoroutineScope(Dispatchers.IO).launch {
                                fetchAndUpdateCollateralNatureAsync().await()
                                fetchRelationshipAsync()
                                fetchOwnerShip()
                            }

                        } else {
                            immovable_section.visibility = View.GONE

                        }
                    }
                }
            }
        val subSecuritySpinner: AdapterView.OnItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    parent?.getItemAtPosition(position)?.let {
                        var list =
                            (sp_security_subType?.adapter as? DataSpinnerAdapter)?.list
                        if (list?.get(position)?.description == "liquid") {
                            liquid_section.visibility = View.VISIBLE
                            others_section.visibility = View.GONE
                        } else if (list?.get(position)?.description == "others") {
                            liquid_section.visibility = View.GONE
                            others_section.visibility = View.VISIBLE
                        }
                    }
                }
            }
        /*  val immovableSecurity: AdapterView.OnItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    parent?.getItemAtPosition(position)?.let {
                        var list =
                            (sp_immovable_security?.adapter as? DataSpinnerAdapter)?.list
                        ll_plotType.visibility = View.GONE
                        ll_namuna.visibility = View.GONE
                        if (list?.get(position)?.description == "NA Plot(with boundary)") {
                            ll_plotType.visibility = View.VISIBLE
                        } else if (list?.get(position)?.description == "Namuna 8 A Property") {
                            ll_namuna.visibility = View.VISIBLE

                        }
                    }
                }
            }*/
        sp_security.onItemSelectedListener = securitySpinner
        sp_security_subType.onItemSelectedListener = subSecuritySpinner
        //    sp_immovable_security.onItemSelectedListener = immovableSecurity

        val navController: NavController? =
            if (activity is LeadInfoCaptureActivity) Navigation.findNavController(
                activity!!,
                R.id.frag_container
            ) else null

        yearsCount?.tag = 0
        plusYears?.setOnClickListener {
            var years = yearsCount?.tag as? Int ?: 0
            years++
            //   if(years<=7) {
            yearsCount?.text = "$years yrs"
            yearsCount?.tag = years
            /* }else
             {
                 Toast.makeText(this,"maximum tenure is 7 years",Toast.LENGTH_LONG).show()
             }*/
        }

        minusYears?.setOnClickListener {
            var years = yearsCount.tag as? Int ?: 0
            years--
            if (years >= 0) {
                yearsCount?.text = "$years yrs"
                yearsCount?.tag = years
            }/*else
            {
                Toast.makeText(this,"miniumum tenure is 1 year",Toast.LENGTH_LONG).show()

            }*/
        }
        monthsCount?.tag = 0
        plusMonths?.setOnClickListener {
            var years = monthsCount?.tag as? Int ?: 0
            years++
            //   if(years<=7) {
            monthsCount?.text = "$years months"
            monthsCount?.tag = years
            /* }else
             {
                 Toast.makeText(this,"maximum tenure is 7 years",Toast.LENGTH_LONG).show()
             }*/
        }

        minusMonths?.setOnClickListener {
            var months = monthsCount.tag as? Int ?: 0
            months--
            if (months >= 0) {
                monthsCount?.text = "$months months"
                monthsCount?.tag = months
            }/*else
            {
                Toast.makeText(this,"miniumum tenure is 1 year",Toast.LENGTH_LONG).show()

            }*/
        }
        /* issueDateLiq.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(
                activity!!,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val date = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year


                    var timenow= Calendar.getInstance().time
                    var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("")
                    var formatDate:String=simpleDateFormat.format(timenow)
                    issueDateLiq.setText(date)

                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        validityDateLiq.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(
                activity!!,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val date = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year


                    var timenow = Calendar.getInstance().time
                    var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("")
                    var formatDate: String = simpleDateFormat.format(timenow)
                    validityDateLiq.setText(date)
*//*
                    if(SimpleDateFormat("dd-MM-yyyy").parse(date).after(Date())){

                    }else
                    {
                        Toast.makeText(activity,"Later should be greater than current date",Toast.LENGTH_LONG).show()
                    }*//*
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
            ).show()
        }*/
        btn_save_continue?.setOnClickListener {

            if (activity?.intent?.getStringExtra("FROM") == "BM") {
                var dialog = AlertDialog.Builder(activity)
                var view: View? = activity?.layoutInflater?.inflate(R.layout.remarks_popup, null)
                dialog.setView(view)
                var et_remarks = view?.findViewById<EditText>(R.id.et_remarks)
                var btn_submit_remark = view?.findViewById<Button>(R.id.btn_submit)
                var btn_cancel = view?.findViewById<Button>(R.id.btn_cancel)

                var alert = dialog.create() as AlertDialog
                btn_cancel?.setOnClickListener {
                    alert.dismiss()
                }
                btn_submit_remark?.setOnClickListener {
                    var map = HashMap<String, String>()
                    map["loanId"] = mLoanId!!
                    map["custId"] = mCustomerId!!
                    map["remarks"] = et_remarks?.text.toString()
                    map["rltWOValue"] = "" + rltWOCheckBox.isChecked
                    map["rltWFeeValue"] = "" + rltWFeeCheckBox.isChecked
                    map["userId"] = activity?.intent?.getStringExtra("FROM") + ""

                    CoroutineScope(Dispatchers.IO).launch {
                        val respo = RetrofitFactory.getApiService().updateOtherDetails(
                            map
                        )

                        val result = respo.body()
                        if (respo.isSuccessful && respo.body() != null && result?.apiCode == "200") {

                            if (result?.discrepancy.equals("y", ignoreCase = true)) {
                                startActivity(
                                    Intent(
                                        activity,
                                        PendingCustomersActivity::class.java
                                    ).apply {
                                        putExtra("FROM", "BM")
                                    })
                            } else {
                                val intent = Intent(activity, BMScreeningReportActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }

                        } else {
                            Toast.makeText(activity, "Please try again later", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }


                alert.show()
            } else {
                if (navController != null) {
                    val progressLoader: ProgrssLoader? =
                        if (context != null) ProgrssLoader(context!!) else null
                    progressLoader?.showLoading()
                    launch(ioContext) {
                        val isTradeReferenceSaved = saveTradeReferenceDataAsync().await()
//                        val isNeighbourReferenceSaved = saveNeighbourReferenceAsync().await()
                        val isCollateralSaved = saveCollateralDataAsync().await()
                        if (isCollateralSaved && isTradeReferenceSaved) {
                            withContext(uiContext) {
                                progressLoader?.dismmissLoading()
                                val intent = Intent(activity, DocumentActivity::class.java)
                                intent.putExtra("loanId", mLoanId)
                                intent.putExtra("custId", mCustomerId)
                                startActivity(intent)

                            }

                        } else {
                            progressLoader?.dismmissLoading()

                        }
                    }
                }
            }
        }
    }

    private fun fetchRelationshipAsync() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitFactory.getMasterApiService().getRelationship()
                if (response?.isSuccessful == true) {
                    withContext(Dispatchers.Main) {
                        try {
                            sp_relaionShipApplicant?.adapter = DataSpinnerAdapter(
                                context!!,
                                response.body()?.data?.toMutableList() ?: mutableListOf()
                            ).also {
                                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Crashlytics.log(e.message)

                        }
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Crashlytics.log(e.message)

            }
        }
    }

    private fun fetchOwnerShip() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitFactory.getApiService().getCollateralOwnership()
                if (response?.isSuccessful == true) {
                    withContext(Dispatchers.Main) {
                        try {
                            sp_ownerShip?.adapter = DataSpinnerAdapter(
                                context!!,
                                response.body()?.data?.toMutableList() ?: mutableListOf()
                            ).also {
                                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Crashlytics.log(e.message)

                        }
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Crashlytics.log(e.message)

            }
        }
    }
    private fun fetchAndUpdateCollateralNatureAsync(): Deferred<Boolean> =
        async(context = ioContext) {
            try {
                val response = RetrofitFactory.getMasterApiService().getCollateralNature()
                if (response?.isSuccessful == true) {
                    withContext(Dispatchers.Main) {
                        try {
                            sp_collateral_type?.adapter = DataSpinnerAdapter(
                                context!!,
                                response.body()?.data?.toMutableList() ?: mutableListOf()
                            ).also {
                                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Crashlytics.log(e.message)

                        }
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Crashlytics.log(e.message)

            }
            return@async true
        }

        private fun fetchmstrIdImmovable(value: String) {

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response =
                        RetrofitFactory.getApiService().getCollateralMstr("immovable_sub_type")
                    if (response?.body()?.errorCode == "200") {

                        withContext(Dispatchers.Main) {
                            "sp_immovable_security.adapter = getAdapter(response.body()?.data)"
                        }
                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    Crashlytics.log(e.message)

                }

            }
        }


        private fun fetchmstrIdsubSecurity(str: String) {

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    var temp = str
                    if (temp == "immovable") {
                        temp = "immovable_type"
                    }
                    val response = RetrofitFactory.getApiService().getCollateralMstr(temp)
                    if (response?.body()?.errorCode == "200") {

                        withContext(Dispatchers.Main) {
                            sp_security_subType.adapter = null
                            sp_security_subType.adapter = getAdapter(response.body()?.data)
                        }
                    } else {
                        withContext(Dispatchers.Main)
                        {
                            sp_security_subType.adapter = null

                        }
                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    Crashlytics.log(e.message)

                }

            }
        }

        private fun updateCount(
            updateType: IncomeInformationFragment.UpdateCountType,
            countText: TextView?
        ) = when (updateType) {
            is IncomeInformationFragment.UpdateCountType.Increment -> performIncrement(
                countText?.tag as? Int ?: 0
            )
            is IncomeInformationFragment.UpdateCountType.Decrement -> performDecrement(
                countText?.tag as? Int ?: 0
            )
        }.also {
            countText?.text = "$it"
            countText?.tag = it
        }

        private fun performIncrement(initialCount: Int) = initialCount + 1
        private fun performDecrement(initialCount: Int): Int =
            if (initialCount - 1 < 0) 0 else initialCount - 1

        private suspend fun stopLoading(progressBar: ProgrssLoader, message: String?) {
            withContext(uiContext) {
                progressBar.dismmissLoading()
                message?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                }
            }
        }

        private fun saveTradeReferenceDataAsync(): Deferred<Boolean> = async(ioContext) {
            try {
                //trade_reference_1_product_purchase_sale_input?.text?.toString(),
                val postBody = TradeReferencePostData(
                    mutableListOf(
                        TradeRefDetail(
                            loanId = mLoanId,
                            firmName = trade_reference_1_firm_name_input?.text?.toString(),
                            nameofPersonDealingWith = trade_reference_1_person_name_dealing_with_input?.text?.toString(),
                            rshipWithApplicant = (trade_reference_1_relationship_with_applicant_spinner?.selectedItem as? Data)?.value,
                            contactDetails = trade_reference_1_contact_details_input?.text?.toString(),
                            noOfYrsWorkingWith = (trade_reference_1_years_working_with_count?.tag as? Int)?.toString(),
                            productPurchaseSale = "",
                            customerId = mCustomerId
                        ),
                        TradeRefDetail(
                            loanId = mLoanId,
                            firmName = trade_reference_1_firm_name_input?.text?.toString(),
                            nameofPersonDealingWith = trade_reference_2_person_name_dealing_with_input?.text?.toString(),
                            rshipWithApplicant = (trade_reference_2_relationship_with_applicant_spinner?.selectedItem as? Data)?.value,
                            contactDetails = trade_reference_2_contact_details_input?.text?.toString(),
                            noOfYrsWorkingWith = (trade_reference_2_years_working_with_count?.tag as? Int)?.toString(),
                            productPurchaseSale = trade_reference_2_product_purchase_sale_input?.text?.toString(),
                            customerId = mCustomerId
                        )
                    )
                )
                val response = RetrofitFactory.getApiService().saveTradeReference(postBody)
                return@async if (response?.isSuccessful == true) {
                    val result = response?.body()
                    result?.apiCode == "200"
                } else {
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Crashlytics.log(e.message)

                return@async false
            }
            return@async true
        }

        private fun loadInitialData() {
            val progressLoader: ProgrssLoader? =
                if (context != null) ProgrssLoader(context!!) else null
            progressLoader?.showLoading()
            launch(ioContext) {
                val natureOfProperty = fetchAndUpdateNatureOfPropertyAsync().await()
                val propertyJurisdiction = fetchAndUpdatePropertyJurisdictionAsync().await()
                val propertyType = fetchAndUpdatePropertyTypeAsync().await()
                val relationshipWitApplicant =
                    fetchAndUpdateRelationshipWithApplicantAsync().await()

                fetchmstrId()
                fetchDocNature()
                fetchmDocType()
                fetchoccupiedBy()
                if (natureOfProperty && propertyJurisdiction && propertyType && relationshipWitApplicant) {
                    withContext(uiContext) {
                        progressLoader?.dismmissLoading()
                        if (arguments?.getString("task").equals("RM_AssignList")) {
                            getOthersDataForRm()
                        }
                    }
                }
            }
        }

        private fun getOthersDataForRm() {


            try {
                val progressLoader: ProgrssLoader? =
                    if (context != null) ProgrssLoader(context!!) else null
                progressLoader?.showLoading()
                CoroutineScope(Dispatchers.IO).launch {
                    val res =
                        RetrofitFactory.getApiService().getOtherData(arguments?.getString("loanId"))
                    withContext(uiContext) {
                        if (res!!.isSuccessful) {
                            val responseBody = res.body()
                            updateData(
                                responseBody?.neighborRefDetails,
                                responseBody?.tradeRefDetails,
                                responseBody?.collateralDetails,
                                arguments?.getString("loanId")
                            )
                            progressLoader?.dismmissLoading()
                        }
                    }
                }

            } catch (e: java.lang.Exception) {
                Crashlytics.log(e.message)

            }

        }

        private fun fetchAndUpdateNatureOfPropertyAsync(): Deferred<Boolean> =
            async(context = ioContext) {
                try {
                    val response = RetrofitFactory.getMasterApiService().getNatureOfProperty()
                    if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                        try {
                            withContext(uiContext) {
                                nature_of_property_spinner?.adapter =
                                    getAdapter(response.body()?.data)
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

        private fun fetchAndUpdatePropertyJurisdictionAsync(): Deferred<Boolean> =
            async(context = ioContext) {
                try {
                    val response = RetrofitFactory.getMasterApiService().getPropertyJurisdiction()
                    if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                        try {
                            withContext(uiContext) {
                                property_jurisdiction_spinner?.adapter =
                                    getAdapter(response.body()?.data)
                                sp_jurisdictionType?.adapter=getAdapter(response.body()?.data)

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

        private fun fetchAndUpdatePropertyTypeAsync(): Deferred<Boolean> =
            async(context = ioContext) {
                try {
                    val response = RetrofitFactory.getMasterApiService().getPropertyType()
                    if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                        try {
                            withContext(uiContext) {
                                property_type_spinner?.adapter = getAdapter(response.body()?.data)
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

        private fun fetchmstrId() {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response =
                        RetrofitFactory.getApiService().getCollateralMstr("security_type")
                    if (response?.body()?.errorCode == "200") {

                        withContext(Dispatchers.Main) {
                            sp_security.adapter = getAdapter(response.body()?.data)
                            if (response.body() != null && (response.body()!!.data[0].description.toLowerCase() == "Negative Lien")) {
                                immovable_section.visibility = View.VISIBLE
                            }
                        }
                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    Crashlytics.log(e.message)

                }

            }
        }

        private fun fetchoccupiedBy() {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitFactory.getApiService().getCollateralMstr("occupied_by")
                    if (response?.body()?.errorCode == "200") {

                        withContext(Dispatchers.Main) {
                            //sp_occupiedBy.adapter = getAdapter(response.body()?.data)
                        }
                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    Crashlytics.log(e.message)

                }

            }
        }

        private fun fetchDocNature() {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitFactory.getApiService().getCollateralMstr("doc_nature")
                    if (response?.body()?.errorCode == "200") {

                        withContext(Dispatchers.Main) {
                         //   sp_NatureOfDo.adapter = getAdapter(response.body()?.data)
                        }
                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    Crashlytics.log(e.message)

                }

            }
        }

        private fun fetchmDocType() {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitFactory.getApiService().getCollateralMstr("doc_type")
                    if (response?.body()?.errorCode == "200") {

                        withContext(Dispatchers.Main) {
                            //sp_typeOfDoc.adapter = getAdapter(response.body()?.data)
                        }
                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    Crashlytics.log(e.message)

                }

            }
        }

        private fun fetchAndUpdateRelationshipWithApplicantAsync(): Deferred<Boolean> =
            async(context = ioContext) {
                try {
                    val response =
                        RetrofitFactory.getMasterApiService().getRelationshipWithApplicant()
                    if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                        withContext(uiContext) {
                            trade_reference_1_relationship_with_applicant_spinner?.adapter =
                                getAdapter(response.body()?.data)
                            trade_reference_2_relationship_with_applicant_spinner?.adapter =
                                getAdapter(response.body()?.data)
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    Crashlytics.log(e.message)

                }
                return@async true
            }

        private fun getAdapter(list: List<Data>?): DataSpinnerAdapter? =
            if (context != null)
                DataSpinnerAdapter(context!!, list?.toMutableList() ?: mutableListOf()).also {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                } else null

        private fun saveNeighbourReferenceAsync(): Deferred<Boolean> =
            async(context = ioContext) {
                try {
                    val list: MutableList<NeighborReference> = mutableListOf(
                        NeighborReference(
                            name = neighbour_reference_1_name_input?.text?.toString(),
                            customerId = mCustomerId,
                            loanId = mLoanId,
                            knownSince = neighbour_reference_1_known_since_input?.text?.toString(),
                            mobileNo = neighbour_reference_1_mobile_input?.text?.toString(),
                            rshipWithApplicant = neighbour_reference_1_relationship_with_applicant_spinner?.selectedItem as? String
                        ),
                        NeighborReference(
                            name = neighbour_reference_2_name_input?.text?.toString(),
                            customerId = mCustomerId,
                            loanId = mLoanId,
                            knownSince = neighbour_reference_2_known_since_input?.text?.toString(),
                            mobileNo = neighbour_reference_2_mobile_input?.text?.toString(),
                            rshipWithApplicant = neighbour_reference_2_relationship_with_applicant_spinner?.selectedItem as? String
                        )
                    )
                    val postBody = NeighborReferencePostData(
                        loanId = mLoanId,
                        customerId = mCustomerId,
                        neighborRef = list
                    )
                    val response = RetrofitFactory.getApiService().saveNeighborReference(postBody)
                    return@async if (response?.isSuccessful == true) {
                        val result = response?.body()
                        result?.apiCode == "200"
                    } else {
                        false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Crashlytics.log(e.message)

                    return@async false
                }
                return@async true
            }

        private fun saveCollateralDataAsync(): Deferred<Boolean> = async(ioContext) {
            try {
                collaterals = ArrayList()

                var addressType=""
                if(rb_ResidentType.isChecked)
                {
                    addressType=rb_ResidentType.text.toString()
                }else if(rb_Business.isChecked)
                {
                    addressType=rb_Business.text.toString()
                }else
                {
                    addressType="Others"
                }
                collaterals.add(
                    CollateralData(
                        securityType = (sp_security.selectedItem as Data).description.toString(),
                        liquidDetails = LiquidDetails(
                            ownerName  = et_coOwnerName.text.toString(),
                            policyNo = et_COpolicyNo.text.toString(),
                            surrenderValue = et_cosurrenderValue.text.toString()
                        ),
                        otherDetails = MovableDetails(
                            ownerName  = et_coOthersOwnerName.text.toString(),
                            policyNo = et_COOtherspolicyNo.text.toString(),
                            marketValue = et_marketValueCo.text.toString(),
                            derivedValue = et_derivedValueCO.text.toString()
                        ),
                        immovableDetails = ImmovableDetails(
                            ownerName =et_COOwnerNameImm.text.toString() ,
                            address = et_address.text.toString(),
                            addressType = addressType,
                            collateralType = (sp_collateral_type?.selectedItem as Data).value.toString(),
                            jurisdiction = (sp_jurisdictionType.selectedItem as Data).value.toString(),
                            marketValue = et_MarketValueImm.text.toString()
                            , rshipWithApplicant = (sp_relaionShipApplicant.selectedItem as Data).description.toString(),
                            ownership = (sp_ownerShip.selectedItem as Data).description.toString()
                        )

                    )
                )
                val postBody = CollateralDetailsPostData(
                    loanId = AppPreferences.getInstance().getString(AppPreferences.Key.LoanId),
                    custId = AppPreferences.getInstance().getString(AppPreferences.Key.CustomerId),
                    collaterals = collaterals
                )

                /*  securityType = sp_security.selectedItem.toString(),
                  securitySubType = sp_security_subType?.selectedItem?.toString(),
                  immovableSubType = sp_immovable_security.selectedItem?.toString(),
                  plotType = when(rb_boundary.isChecked){
                      true->"Boundary"
                      false->"No Boundary"
                  },namunaType = when(rb_online.isChecked){
                      true->"Online"
                      false->"Offline"
                  },
                  occupiedBy = sp_occupiedBy.selectedItem.toString(),
                  considerCFA =  cfa_cb.isChecked,
                  natureOfDoc = sp_NatureOfDo.selectedItem.toString(),
                  typeOfDoc = sp_typeOfDoc.selectedItem?.toString(),
                  docDesc = et_docDesc.text.toString(),
                  docStatus = when(rb_received.isChecked)
                  {
                      true->"Received"
                      false->"Not Received"
                  }*/
                val response = RetrofitFactory.getApiService().saveCollateralDetail(postBody)
                return@async if (response?.isSuccessful == true) {
                    val result = response?.body()
                    result?.apiCode == "200"
                } else {
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Crashlytics.log(e.message)

                return@async false
            }
            return@async true
        }

        fun updateData(
            neighborReference: List<NeighborReference>?,
            tradeRefDetails: List<TradeRefDetail>?,
            collateralDetails: CollateralDetails?,
            loanId: String?
        ) {
            if (collateralDetails?.loanType.equals("unsecure", ignoreCase = true)) {
                ll_collateral.visibility = View.GONE
            }
            mLoanId = loanId
            if ((neighborReference?.size ?: 0) > 0) {
                mCustomerId = neighborReference?.get(0)?.customerId
                neighbour_reference_1_name_input?.setText(neighborReference?.get(0)?.name)
                neighbour_reference_1_mobile_input?.setText(neighborReference?.get(0)?.mobileNo)
                neighbour_reference_1_known_since_input?.setText(neighborReference?.get(0)?.knownSince)
            }
            if ((neighborReference?.size ?: 0) > 1) {
                neighbour_reference_2_name_input?.setText(neighborReference?.get(1)?.name)
                neighbour_reference_2_mobile_input?.setText(neighborReference?.get(1)?.mobileNo)
                neighbour_reference_2_known_since_input?.setText(neighborReference?.get(1)?.knownSince)
            }
            if ((tradeRefDetails?.size ?: 0) > 0) {
                if (mCustomerId == null) {
                    mCustomerId = neighborReference?.get(0)?.customerId
                }
                trade_reference_1_firm_name_input?.setText(tradeRefDetails?.get(0)?.firmName)
                trade_reference_1_person_name_dealing_with_input?.setText(tradeRefDetails?.get(0)?.nameofPersonDealingWith)
                var position = -1
                val list =
                    (trade_reference_1_relationship_with_applicant_spinner?.adapter as? DataSpinnerAdapter)?.list
                for (index in 0 until (list?.size ?: 0)) {
                    if (list?.get(index)?.value == tradeRefDetails?.get(0)?.rshipWithApplicant) {
                        position = index
                    }
                }
                if (position != -1) {
                    trade_reference_1_relationship_with_applicant_spinner?.setSelection(position)
                }
                trade_reference_1_contact_details_input?.setText(tradeRefDetails?.get(0)?.contactDetails)
                //   trade_reference_1_product_purchase_sale_input?.setText(tradeRefDetails?.get(0)?.productPurchaseSale)
                try {
                    trade_reference_1_years_working_with_count?.tag =
                        tradeRefDetails?.get(0)?.noOfYrsWorkingWith?.toInt()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Crashlytics.log(e.message)

                }
                trade_reference_1_years_working_with_count?.text =
                    "${tradeRefDetails?.get(0)?.noOfYrsWorkingWith}"
            }
            if ((tradeRefDetails?.size ?: 0) > 1) {
                trade_reference_2_firm_name_input?.setText(tradeRefDetails?.get(1)?.firmName)
                trade_reference_2_person_name_dealing_with_input?.setText(tradeRefDetails?.get(1)?.nameofPersonDealingWith)
                var position = -1
                val list =
                    (trade_reference_2_relationship_with_applicant_spinner?.adapter as? DataSpinnerAdapter)?.list
                for (index in 0 until (list?.size ?: 0)) {
                    if (list?.get(index)?.value == tradeRefDetails?.get(1)?.rshipWithApplicant) {
                        position = index
                    }
                }
                if (position != -1) {
                    trade_reference_2_relationship_with_applicant_spinner?.setSelection(position)
                }
                trade_reference_2_contact_details_input?.setText(tradeRefDetails?.get(1)?.contactDetails)
                trade_reference_2_product_purchase_sale_input?.setText(tradeRefDetails?.get(1)?.productPurchaseSale)
                try {
                    trade_reference_2_years_working_with_count?.tag =
                        tradeRefDetails?.get(1)?.noOfYrsWorkingWith?.toInt()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Crashlytics.log(e.message)

                }
                trade_reference_2_years_working_with_count?.text =
                    "${tradeRefDetails?.get(1)?.noOfYrsWorkingWith}"
            }
            var position = -1
            var list =
                (nature_of_property_spinner?.adapter as? DataSpinnerAdapter)?.list
            for (index in 0 until (list?.size ?: 0)) {
                if (list?.get(index)?.value == collateralDetails?.natureofProperty) {
                    position = index
                }
            }
            if (position != -1) {
                nature_of_property_spinner?.setSelection(position)
            }
            no_of_rented_tenants_count?.text = collateralDetails?.noOfTenants
            try {
                no_of_rented_tenants_count?.tag = collateralDetails?.noOfTenants?.toInt()
            } catch (e: Exception) {
                e.printStackTrace()
                Crashlytics.log(e.message)

            }
            try {
                property_location_address_input?.setText(collateralDetails?.addressline1)
                land_area_input?.setText(collateralDetails?.landArea)
                construction_area_input?.setText(collateralDetails?.constructionArea)
                market_value_input?.setText(collateralDetails?.marketValue)
                position = -1
                if ((property_type_spinner?.adapter as? DataSpinnerAdapter) != null) {
                    list = (property_type_spinner?.adapter as? DataSpinnerAdapter)?.list
                    if (list != null) {
                        for (index in 0 until (list.size ?: 0)) {
                            if (list[index].value == collateralDetails?.propertyType) {
                                position = index
                            }
                        }
                        if (position != -1) {
                            property_type_spinner?.setSelection(position)
                        }
                    }
                }
            } catch (e: Exception) {
                Crashlytics.log(e.message)

            }

        }

}
