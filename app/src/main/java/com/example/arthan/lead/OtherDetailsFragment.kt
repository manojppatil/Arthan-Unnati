package com.example.arthan.lead


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.arthan.R
import com.example.arthan.dashboard.bcm.AddTradeRefActivity
import com.example.arthan.dashboard.bcm.TradeRefDetailsAdapter
import com.example.arthan.dashboard.bm.BMDocumentVerificationActivity
import com.example.arthan.dashboard.bm.BMScreeningReportActivity
import com.example.arthan.dashboard.rm.RMScreeningNavigationActivity
import com.example.arthan.dashboard.rm.ReUsableFragmentSpace
import com.example.arthan.global.ArthanApp
import com.example.arthan.global.Crashlytics
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.lead.model.postdata.*
import com.example.arthan.lead.model.responsedata.DetailsResponseData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.PendingCustomersActivity
import kotlinx.android.synthetic.main.collateral_section.*
import kotlinx.android.synthetic.main.fragment_other_details.*
import kotlinx.android.synthetic.main.movable_type_layout.*
import kotlinx.coroutines.*
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
    var tradeAdapterResponse: DetailsResponseData?=null
    var   tradeRefDetails: ArrayList<TradeRefDetail>?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLoanId = activity?.intent?.getStringExtra("loanId")
        mCustomerId = activity?.intent?.getStringExtra("custId")
        et_marketValueCo.setText(activity?.intent?.getStringExtra("apMarketValue"))
        et_MarketValueImm.setText(activity?.intent?.getStringExtra("apMarketValue"))
        //  mLoanId = AppPreferences.getInstance().getString(AppPreferences.Key.LoanId)
        // mCustomerId = AppPreferences.getInstance().getString(AppPreferences.Key.CustomerId)

        loadInitialData()
        if (ArthanApp.getAppInstance().loginRole == "BCM" || ArthanApp.getAppInstance().loginRole == "BM"
        ) {
            //bcmCheckBoxes.visibility = View.VISIBLE
            ll_collateral.visibility=View.GONE
            addNewTrade.visibility=View.VISIBLE
            txt_input_pincode_tl.visibility=View.VISIBLE
            addNewTrade.setOnClickListener {

              startActivityForResult(Intent(context,AddTradeRefActivity::class.java).apply {
                   putExtra("loanId",mLoanId)
                   putExtra("custId",mCustomerId)
               },100)
            }

        } else {
//            ll_collateral.visibility=View.VISIBLE //for p2 visible is gone
            ll_collateral.visibility=View.GONE
            bcmCheckBoxes.visibility = View.GONE
            addNewTrade.visibility=View.GONE

            if (activity?.intent?.getStringExtra("loanType")!=null&&activity?.intent?.getStringExtra("loanType")
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

        /* no_of_rented_tenants_count?.tag = 0
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
        }*/
        et_address.visibility=View.GONE


        rb_Others.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked)
            {
                et_address.visibility=View.VISIBLE
            }else
            {
                et_address.visibility=View.GONE

            }
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
                            input_pincode.visibility=View.GONE

                            others_section.visibility=View.GONE
                            CoroutineScope(Dispatchers.IO).launch {

                                fetchmstrIdsubSecurity(list[position].description!!.toLowerCase())
                            }

                        } else {
                            security_section_movable.visibility = View.GONE

                        }
                        if (list?.get(position)?.description?.toLowerCase() == "immovable" || list?.get(
                                position
                            )?.description?.toLowerCase() == "Negative Lien".toLowerCase()
                        ) {
                            immovable_section.visibility = View.VISIBLE
                            input_pincode.visibility=View.VISIBLE
                            CoroutineScope(Dispatchers.IO).launch {
                                fetchAndUpdateCollateralNatureAsync("").await()
                                fetchRelationshipAsync("")
                                fetchOwnerShip("")
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

            if(trade_reference_1_firm_name_input.length()==0||trade_reference_1_person_name_dealing_with_input.length()==0||trade_reference_1_contact_details_input.length()==0 ||trade_reference_1_years_working_with_count?.tag==0
                ||(trade_reference_1_relationship_with_applicant_spinner.selectedItem as Data).value.contains("Choose Relationship")
                ||(trade_reference_2_relationship_with_applicant_spinner.selectedItem as Data).value.contains("Choose Relationship")
                ||(sp_collateral_type_liq.selectedItem as Data).value.contains("Select")
                || trade_reference_2_firm_name_input.length()==0||trade_reference_2_person_name_dealing_with_input.length()==0||trade_reference_2_contact_details_input.length()==0||trade_reference_2_years_working_with_count?.tag==0)
            {
                Toast.makeText(context!!,"All details are mandatory",Toast.LENGTH_LONG).show()

                return@setOnClickListener
            }
            if(ArthanApp.getAppInstance().loginRole=="RM"&&(sp_security.selectedItem as Data).description.toLowerCase() == "immovable")
            {
                if(et_COOwnerNameImm.length()==0||et_MarketValueImm.length()==0){
                    Toast.makeText(context!!,"All details are mandatory",Toast.LENGTH_LONG).show()

                    return@setOnClickListener
                }

            }
            if(mLoanId==null||mLoanId=="")
            {
                mLoanId=activity?.intent?.getStringExtra("loanId")
                mCustomerId=activity?.intent?.getStringExtra("custId")
                if(arguments?.getString("task").equals("RM_AssignList",ignoreCase = true)||
                    arguments?.getString("task").equals("RMreJourney",ignoreCase = true))
                {
                    mLoanId= arguments?.getString("loanId")
                    mCustomerId= arguments?.getString("custId")
                }
            }

            if(rb_Others.isChecked)
            {
                if(et_address.text.isEmpty()&&ArthanApp.getAppInstance().loginRole=="RM")
                {

                    Toast.makeText(context!!,"Collateral address is mandatory",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }
            if (ArthanApp.getAppInstance().loginRole == "BM"||ArthanApp.getAppInstance().loginRole == "BCM") {
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
                    alert.dismiss()
                    var map = HashMap<String, String>()


                    map["loanId"] = mLoanId!!
                    map["custId"] = mCustomerId!!
                    map["remarks"] = et_remarks?.text.toString()
                    map["rltWOValue"] = "" + rltWOCheckBox.isChecked
                    map["rltWFeeValue"] = "" + rltWFeeCheckBox.isChecked
                    map["userId"] = ArthanApp.getAppInstance().loginUser + ""

                    var post=getPostBody()
                    post.remarks=et_remarks?.text.toString()
                    CoroutineScope(Dispatchers.IO).launch {
                        val respo = RetrofitFactory.getApiService().updateOtherDetails(
                            post
                        )


                        val result = respo.body()
                        if (respo.isSuccessful && respo.body() != null && result?.apiCode == "200") {
                            withContext(Dispatchers.Main)
                            {
                                if(ArthanApp.getAppInstance().loginRole == "BM" && et_remarks?.text.toString().isNotEmpty())
                                {
                                    //Toast.makeText(activity,"Case ReAssigned to RM",Toast.LENGTH_LONG).show()
                                }
                            }

                            if (result?.discrepancy.equals("y", ignoreCase = true)) {
                                startActivity(
                                    Intent(
                                        activity,
                                        PendingCustomersActivity::class.java
                                    ).apply {
                                        putExtra("FROM", "BM")
                                    })
                                activity?.finish()

                            } else {

                                if (ArthanApp.getAppInstance().loginRole == "BM") {

                                    if(activity is BMDocumentVerificationActivity) {
                                        withContext(Dispatchers.Main) {

                                            (activity as BMDocumentVerificationActivity).moveVPinDataFragment(
                                                4
                                            )
                                        }
                                    }else
                                    {

                                    }
                                    /*startActivity(
                                        Intent(
                                            activity,
                                            PendingCustomersActivity::class.java
                                        ).apply {
                                            putExtra("FROM", "BM")
                                        })
                                    activity?.finish()*/
                                } else {
                                    val intent =
                                        Intent(activity, BMScreeningReportActivity::class.java)
                                    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    intent.putExtra("loanId", mLoanId)
                                    intent.putExtra(
                                        "indSeg",
                                        activity?.intent?.getStringExtra("indSeg")
                                    )
                                    intent.putExtra(
                                        "loginDate",
                                        activity?.intent?.getStringExtra("loginDate")
                                    )
                                    intent.putExtra(
                                        "loanId",
                                        activity?.intent?.getStringExtra("loanId")
                                    )
                                    intent.putExtra(
                                        "loanAmt",
                                        activity?.intent?.getStringExtra("loanAmt")
                                    )
                                    intent.putExtra(
                                        "cname",
                                        activity?.intent?.getStringExtra("cname")
                                    )
                                    intent.putExtra(
                                        "custId",
                                        activity?.intent?.getStringExtra("custId")
                                    )
                                    intent.putExtra(
                                        "loanType",
                                        activity?.intent?.getStringExtra("loanType")
                                    )

                                    startActivity(intent)

                                    activity?.finish()
                                }
                            }

                        } else {
                            Toast.makeText(activity, "Please try again later", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }


                alert.show()
            }
            else if(ArthanApp.getAppInstance().loginRole=="RM"||ArthanApp.getAppInstance().loginRole=="AM"){
//                if (navController != null) {
                    val progressLoader: ProgrssLoader? =
                        if (context != null) ProgrssLoader(context!!) else null
                    progressLoader?.showLoading()
                    launch(ioContext) {
                        val isTradeReferenceSaved = saveTradeReferenceDataAsync().await()
//                        val isNeighbourReferenceSaved = saveNeighbourReferenceAsync().await()
                        var isCollateralSaved=false
                        if(activity?.intent?.getStringExtra("loanType").equals("Secure",ignoreCase = true)) {


                            if((sp_security.selectedItem as Data).description == "Movable") {
                                if (et_coOwnerName.length() == 0 || et_COpolicyNo.length() == 0 || et_cosurrenderValue.length() == 0)
//                                &&et_coOthersOwnerName.length()>0&&et_COOtherspolicyNo.length()>0&&et_marketValueCo.length()>0)
//                                &&et_derivedValueCO.length()>0)
                                {
                                    withContext(Dispatchers.Main)
                                    {
                                        Toast.makeText(
                                            context!!,
                                            "Please fill all details",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        progressLoader?.dismmissLoading()
                                    }
                                    return@launch
                                } else {


                                }
                            }
                         /*     immovableDetails = ImmovableDetails(
                                ownerName = et_COOwnerNameImm.text.toString(),
                                address = et_address.text.toString(),
                                addressType = addressType,
                                collateralType = (sp_collateral_type_liq?.selectedItem as Data).value.toString(),
                                jurisdiction = (sp_jurisdictionType.selectedItem as Data).value.toString(),
                                marketValue = et_MarketValueImm.text.toString()*/
                           // isCollateralSaved = saveCollateralDataAsync().await() //commented for p2
                       }

                        if (/*isCollateralSaved &&*/ isTradeReferenceSaved) { //commented for p2
                            withContext(uiContext) {
                                progressLoader?.dismmissLoading()
                                if (arguments?.getString("task")
                                        .equals("RMreJourney", ignoreCase = true)
                                ) {
                                    withContext(Dispatchers.Main)
                                    {

                                        startActivity(
                                            Intent(
                                                activity,
                                                RMScreeningNavigationActivity::class.java
                                            ).apply {
                                                putExtra("loanId",mLoanId)
                                            }
                                        )
                                        activity?.finish()
                                    }
                                }else {

                                    if (context is ReUsableFragmentSpace) {
                                        activity?.finish()
                                    } else {
                                        val intent = Intent(activity, DocumentActivity::class.java)
                                        intent.putExtra("loanId", mLoanId)
                                        intent.putExtra("custId", mCustomerId)
                                        startActivity(intent)
//                                    activity?.finish()

                                    }
                                }
                            }

                        } else if(isTradeReferenceSaved)
                        {
                            withContext(Dispatchers.Main) {
                                val intent = Intent(activity, DocumentActivity::class.java)
                                intent.putExtra("loanId", mLoanId)
                                intent.putExtra("custId", mCustomerId)
                                startActivity(intent)
                            }
                        }else {
                            withContext(Dispatchers.Main)
                            {
                                progressLoader?.dismmissLoading()
                                if (context is ReUsableFragmentSpace) {
                                    activity?.finish()
                                }
                            }

                        }
                    }
//                }
            }
        }
    }

    private fun fetchRelationshipAsync(value: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitFactory.getMasterApiService().getRelationship()
                if (response?.isSuccessful == true) {
                    withContext(Dispatchers.Main) {
                        try {
                            if (sp_relaionShipApplicant?.adapter == null) {
                                sp_relaionShipApplicant?.adapter = DataSpinnerAdapter(
                                    context!!,
                                    response.body()?.data?.toMutableList() ?: mutableListOf()
                                ).also {
                                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                }
                            }
                            if (value != null && value.isNotEmpty()) {
                                var sp_relaionShipApplicantList =
                                    (sp_relaionShipApplicant?.adapter as? DataSpinnerAdapter)?.list
                                if (sp_relaionShipApplicantList != null) {
                                    for (i in 0 until sp_relaionShipApplicantList.size) {

                                        if (sp_relaionShipApplicantList[i].value == value) {
                                            sp_relaionShipApplicant.setSelection(i)
                                        }
                                    }
                                }
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

    private fun fetchOwnerShip(value: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitFactory.getApiService().getCollateralOwnership()
                if (response?.isSuccessful == true) {
                    withContext(Dispatchers.Main) {
                        try {
                            if (sp_ownerShip?.adapter == null) {
                                sp_ownerShip?.adapter = DataSpinnerAdapter(
                                    context!!,
                                    response.body()?.data?.toMutableList() ?: mutableListOf()
                                ).also {
                                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                }
                            }
                            if (value != null && value.isNotEmpty()) {
                                var sp_ownerShipList =
                                    (sp_ownerShip?.adapter as? DataSpinnerAdapter)?.list

                                if (sp_ownerShipList != null) {
                                    for (i in 0 until sp_ownerShipList.size) {

                                        if (sp_ownerShipList[i].value == value) {
                                            sp_ownerShip.setSelection(i)
                                        }
                                    }
                                }
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

    private fun fetchAndUpdateCollateralNatureAsync(value: String?): Deferred<Boolean> =
        async(context = ioContext) {
            try {
                val response = RetrofitFactory.getMasterApiService().getCollateralNature()
                if (response?.isSuccessful == true) {
                    withContext(Dispatchers.Main) {
                        try {
                            if (sp_collateral_type_liq?.adapter == null) {
                                sp_collateral_type_liq?.adapter = DataSpinnerAdapter(
                                    context!!,
                                    response.body()?.data?.toMutableList() ?: mutableListOf()
                                ).also {
                                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                }
                            }
                            if (value != null && value.isNotEmpty()) {
                                var colType =
                                    (sp_collateral_type_liq?.adapter as? DataSpinnerAdapter)?.list
                                if (colType != null) {
                                    for (i in 0 until colType.size) {

                                        if (colType[i].value == value) {
                                            sp_collateral_type_liq.setSelection(i)
                                        }
                                    }
                                }
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
//                       sp_immovable_security.adapter = getAdapter(response.body()?.data)
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

    private fun  getPostBody():TradeReferencePostData
    {
        val postBody = TradeReferencePostData(
            resubmit = null,
            userId = ArthanApp.getAppInstance().loginUser,
            tradeRef = mutableListOf(
                TradeRefDetail(
                    loanId = mLoanId,
                    firmName = trade_reference_1_firm_name_input?.text?.toString(),
                    nameofPersonDealingWith = trade_reference_1_person_name_dealing_with_input?.text?.toString(),
                    rshipWithApplicant = (trade_reference_1_relationship_with_applicant_spinner?.selectedItem as? Data)?.value,
                    contactDetails = trade_reference_1_contact_details_input?.text?.toString(),
                    noOfYrsWorkingWith = (trade_reference_1_years_working_with_count?.tag as? Int)?.toString(),
                    productPurchaseSale = "",
                    customerId = mCustomerId,
                    tradeRefId = tradeRefDetails?.get(0)!!.tradeRefId
                ),
                TradeRefDetail(
                    loanId = mLoanId,
                    firmName = trade_reference_1_firm_name_input?.text?.toString(),
                    nameofPersonDealingWith = trade_reference_2_person_name_dealing_with_input?.text?.toString(),
                    rshipWithApplicant = (trade_reference_2_relationship_with_applicant_spinner?.selectedItem as? Data)?.value,
                    contactDetails = trade_reference_2_contact_details_input?.text?.toString(),
                    noOfYrsWorkingWith = (trade_reference_2_years_working_with_count?.tag as? Int)?.toString(),
                    productPurchaseSale = trade_reference_2_product_purchase_sale_input?.text?.toString(),
                    customerId = mCustomerId,
                    tradeRefId = tradeRefDetails?.get(1)!!.tradeRefId
                )
            )
        )
        return postBody
    }
    private fun saveTradeReferenceDataAsync(): Deferred<Boolean> = async(ioContext) {
        try {
            //trade_reference_1_product_purchase_sale_input?.text?.toString(),
            val postBody = TradeReferencePostData(
                resubmit = null,
                tradeRef = mutableListOf(
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
                        firmName = trade_reference_2_firm_name_input?.text?.toString(),
                        nameofPersonDealingWith = trade_reference_2_person_name_dealing_with_input?.text?.toString(),
                        rshipWithApplicant = (trade_reference_2_relationship_with_applicant_spinner?.selectedItem as? Data)?.value,
                        contactDetails = trade_reference_2_contact_details_input?.text?.toString(),
                        noOfYrsWorkingWith = (trade_reference_2_years_working_with_count?.tag as? Int)?.toString(),
                        productPurchaseSale = trade_reference_2_product_purchase_sale_input?.text?.toString(),
                        customerId = mCustomerId
                    )
                )
            )
            if (arguments?.getString("task").equals("RM_AssignList")) {

                postBody.resubmit = "yes"
                postBody.reassign="Y"
            }
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
            val natureOfProperty = true/*fetchAndUpdateNatureOfPropertyAsync().await()*/
            val propertyJurisdiction = fetchAndUpdatePropertyJurisdictionAsync("").await()
            val propertyType = fetchAndUpdatePropertyTypeAsync().await()
            val relationshipWitApplicant =
                fetchAndUpdateRelationshipWithApplicantAsync().await()

            fetchmstrId("")
            fetchDocNature()
            fetchmDocType()
            fetchoccupiedBy()
            if (natureOfProperty && propertyJurisdiction && propertyType && relationshipWitApplicant) {
                withContext(uiContext) {
                    progressLoader?.dismmissLoading()
                    if (arguments?.getString("task").equals("RM_AssignList")||arguments?.getString("task").equals("RMreJourney")||(activity?.intent?.getStringExtra("task")!=null&&activity!!.intent.getStringExtra("task")=="RMContinue")) {
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
                /* val res =
                     RetrofitFactory.getApiService().getOtherData(arguments?.getString("loanId"))*/

                var map = HashMap<String, String>()
                var loanId=arguments?.getString("loanId")
                if(loanId==null)
                {
                    loanId=activity?.intent?.getStringExtra("loanId")
                }
                map["loanId"]=loanId!!
                if(arguments?.getString("screen")!=null)
                {
                    map["screen"]=arguments?.getString("screen")!!
                }else
                map["screen"] = "OTHERS_TRADE"
                val res =
                    RetrofitFactory.getApiService().getScreenData(map)
                withContext(uiContext) {
                    if (res!!.isSuccessful) {
                        val responseBody = res.body()
                        activity?.intent?.putExtra("loanType",responseBody?.loanType)
                        updateData(
                            responseBody?.neighborRefDetails,
                            responseBody?.tradeRefDetails,
                            responseBody?.collateralDetails,
                            arguments?.getString("loanId"),
                            responseBody?.collateralDetails?.custId,responseBody?.otherComments,responseBody?.collateralComments
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
                            /* nature_of_property_spinner?.adapter =
                                 getAdapter(response.body()?.data)*/
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

    private fun fetchAndUpdatePropertyJurisdictionAsync(value: String?): Deferred<Boolean> =
        async(context = ioContext) {
            try {
                val response = RetrofitFactory.getMasterApiService().getPropertyJurisdiction()
                if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                    try {
                        withContext(uiContext) {
                            /*  property_jurisdiction_spinner?.adapter =
                                  getAdapter(response.body()?.data)*/
                            if (sp_jurisdictionType?.adapter == null) {
                                sp_jurisdictionType?.adapter = getAdapter(response.body()?.data)
                            }

                            if (value != null && value.isNotEmpty()) {
                                var sp_jurisdictionTypeList =
                                    (sp_jurisdictionType?.adapter as? DataSpinnerAdapter)?.list



                                if (sp_jurisdictionTypeList != null) {
                                    for (i in 0 until sp_jurisdictionTypeList.size) {

                                        if (sp_jurisdictionTypeList[i].value == value) {
                                            sp_jurisdictionType.setSelection(i)
                                        }
                                    }
                                }

                            }
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
                            //  property_type_spinner?.adapter = getAdapter(response.body()?.data)
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

    private fun fetchmstrId(value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    RetrofitFactory.getApiService().getCollateralMstr("security_type")
                if (response?.body()?.errorCode == "200") {

                    withContext(Dispatchers.Main) {
                        if (sp_security.adapter == null) {
                            sp_security.adapter = getAdapter(response.body()?.data)
                            if (value.isNotEmpty()) {
                                var listSecurity =
                                    (sp_security?.adapter as? DataSpinnerAdapter)?.list
                                if (listSecurity != null) {
                                    for (i in 0 until listSecurity.size) {

                                        if (listSecurity[i].value == value) {
                                            sp_security.setSelection(i)
                                        }
                                    }
                                }
                            }

                            if (response.body() != null && (response.body()!!.data[0].description.toLowerCase() == "Negative Lien")) {
                                immovable_section.visibility = View.VISIBLE
                            }
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
                        tradeAdapterResponse=response.body()
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

            var addressType = ""
            if (rb_ResidentType.isChecked) {
                addressType = rb_ResidentType.text.toString()
            } else if (rb_Business.isChecked) {
                addressType = rb_Business.text.toString()
            } else {
                addressType = "Others"
            }

            collaterals.add(
                CollateralData(
                    securityType = (sp_security.selectedItem as Data).description.toString(),
                    pincode = input_pincode.text.toString(),
                    liquidDetails = LiquidDetails(
                        ownerName = et_coOwnerName.text.toString(),
                        policyNo = et_COpolicyNo.text.toString(),
                        surrenderValue = et_cosurrenderValue.text.toString()
                    ),
                    otherDetails = MovableDetails(
                        ownerName = et_coOthersOwnerName.text.toString(),
                        policyNo = et_COOtherspolicyNo.text.toString(),
                        marketValue = et_marketValueCo.text.toString().replace("₹",""),
                        propertyArea = et_propertyAreaValueCo.text.toString(),
                        derivedValue = et_derivedValueCO.text.toString()
                    ),
                    immovableDetails = ImmovableDetails(
                        ownerName = et_COOwnerNameImm.text.toString(),
                        address = et_address.text.toString(),
                        addressType = addressType,
                        collateralType = (sp_collateral_type_liq?.selectedItem as Data).value.toString(),
                        jurisdiction = (sp_jurisdictionType.selectedItem as Data).value.toString(),
                        marketValue = et_MarketValueImm.text.toString().replace("₹",""),
                         propertyArea = et_propertyAreaValueImm.text.toString().replace("₹",""),
                        rshipWithApplicant = (sp_relaionShipApplicant.selectedItem as Data).description.toString(),
                        ownership = (sp_ownerShip.selectedItem as Data).description.toString()
                    )

                )
            )
            val postBody = CollateralDetailsPostData(
                resubmit = "",
                loanId = activity?.intent?.getStringExtra("loanId"),
                custId = activity?.intent?.getStringExtra("custId"),
                collaterals = collaterals
            )

            if (arguments?.getString("task").equals("RM_AssignList")) {

                postBody.resubmit = "yes"
                postBody.reassign="Y"
            }

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
        tradeRefDetails: ArrayList<TradeRefDetail>?,
        collateralDetails: CollateralDetailsPostData?,
        loanId: String?,
        loanType: String?,
        comment: String?,
        collateralComment: String?
    ) {

        this.tradeRefDetails=tradeRefDetails
        if (activity is ReUsableFragmentSpace) {
            (activity as ReUsableFragmentSpace).setCommentsToField(comment.toString()+"")
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


        CoroutineScope(Dispatchers.IO).launch {


            val relationshipWitApplicant =
                fetchAndUpdateRelationshipWithApplicantAsync().await()
            withContext(Dispatchers.Main)
            {
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
                            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                                trade_reference_1_relationship_with_applicant_spinner.setSelection(position)

                            },1000)


                        }
                    }


//                        if (position != -1) {

//                            }

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
                        Handler(Looper.getMainLooper()).postDelayed(Runnable {

                            trade_reference_2_relationship_with_applicant_spinner?.setSelection(
                                position
                            )
                        },1000)
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

                if((tradeRefDetails?.size?:0) > 2&&(ArthanApp.getAppInstance().loginRole == "BCM" || ArthanApp.getAppInstance().loginRole == "BM"))
                {
                    llTradeRefDetails.visibility=View.VISIBLE
                  val newTradeRef=ArrayList<TradeRefDetail>()
                    for(i in 2 until tradeRefDetails!!.size )
                    {

                        newTradeRef.add(tradeRefDetails[i])
                    }

                    rvTradeRefList.adapter=TradeRefDetailsAdapter(context!!,newTradeRef,"",tradeAdapterResponse!!)

                }else
                {
                    llTradeRefDetails.visibility=View.GONE
                }

            }
        }





        if (ArthanApp.getAppInstance().loginRole.contains("RM")) {
            if (activity?.intent?.getStringExtra("loanType")!=null&&activity?.intent?.getStringExtra("loanType").equals(
                    "secure",
                    ignoreCase = true
                )
            ) {
//                ll_collateral.visibility = View.VISIBLE
            } else {
                ll_collateral.visibility = View.GONE

            }
        }
        if (arguments?.getString("task").equals("RM_AssignList")) {
            if (loanType.equals(
                    "secure",
                    ignoreCase = true
                )
            ) {
//                ll_collateral.visibility = View.VISIBLE
            } else {
                ll_collateral.visibility = View.GONE

            }
        }
        if (ArthanApp.getAppInstance().loginRole.contains("RM")&&arguments?.getString("screen")!=null&&arguments?.getString("screen")=="OTHERS_SECURITY") {

            tradeDetailsLL.visibility=View.GONE
//            ll_collateral.visibility=View.VISIBLE
            if (activity is ReUsableFragmentSpace) {
                (activity as ReUsableFragmentSpace).setCommentsToField(collateralComment.toString()+"")
            }

        }


        if (collateralDetails != null && collateralDetails.collaterals.size > 0) {


            CoroutineScope(Dispatchers.IO).launch {
                fetchmstrId(collateralDetails.collaterals[0].securityType)
                fetchAndUpdateCollateralNatureAsync(collateralDetails.collaterals[0].immovableDetails.collateralType).await()
                fetchRelationshipAsync(collateralDetails.collaterals[0].immovableDetails.rshipWithApplicant)
                fetchAndUpdatePropertyJurisdictionAsync(collateralDetails.collaterals[0].immovableDetails.jurisdiction).await()
                fetchOwnerShip(collateralDetails.collaterals[0].immovableDetails.ownership)

            }
            //   Handler(Looper.getMainLooper()).postDelayed(Runnable {
            //       progressBar.dismmissLoading()
            var list = collateralDetails.collaterals
            if (list[0].securityType!=null&&list[0].securityType.toLowerCase() == "movable") {
                security_section_movable.visibility = View.VISIBLE
                input_pincode.visibility=View.GONE
            } else {
                security_section_movable.visibility = View.GONE
                input_pincode.visibility=View.VISIBLE


            }
            if (list[0].securityType!=null&&(list.get(0).securityType.toLowerCase() == "immovable" || list.get(
                    0
                ).securityType?.toLowerCase() == "Negative Lien".toLowerCase())
            ) {
                immovable_section.visibility = View.VISIBLE
                input_pincode.visibility = View.VISIBLE
            } else {
                immovable_section.visibility = View.GONE
                input_pincode.visibility = View.GONE

            }

            liquid_section.visibility = View.VISIBLE
            others_section.visibility = View.VISIBLE
            /* var listmm =
                 (sp_security_subType?.adapter as? DataSpinnerAdapter)?.list
             if (list?.get(0)?.securityType == "liquid") {
                 liquid_section.visibility = View.VISIBLE
                 others_section.visibility = View.GONE
             } else if (list?.get(position)?.description == "others") {
                 liquid_section.visibility = View.GONE
                 others_section.visibility = View.VISIBLE
             }
 */



            when (collateralDetails!!.collaterals[0].immovableDetails.addressType) {

                "Residential" -> {
                    rb_ResidentType.isChecked = true
                }
                "Business" -> {
                    rb_Business.isChecked = true
                }
                "Others" -> {
                    rb_Others.isChecked = true
                }
                else -> {

                }

            }
            et_coOwnerName.setText(collateralDetails!!.collaterals[0].liquidDetails.ownerName)
            et_COpolicyNo.setText(collateralDetails!!.collaterals[0].liquidDetails.policyNo)
            et_cosurrenderValue.setText(collateralDetails!!.collaterals[0].liquidDetails.surrenderValue)


            et_coOthersOwnerName.setText(collateralDetails!!.collaterals[0].otherDetails.ownerName)
            et_COOtherspolicyNo.setText(collateralDetails!!.collaterals[0].otherDetails.policyNo)
          //  et_marketValueCo.setText(collateralDetails!!.collaterals[0].otherDetails.marketValue)
            et_derivedValueCO.setText(collateralDetails!!.collaterals[0].otherDetails.derivedValue)



            et_MarketValueImm.setText(collateralDetails!!.collaterals[0].immovableDetails.marketValue)
            et_address.setText(collateralDetails!!.collaterals[0].immovableDetails.address)
            et_COOwnerNameImm.setText(collateralDetails!!.collaterals[0].immovableDetails.ownerName)


            //   }, 7000)

        }

        /*var position = -1
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
            }*/

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            100->{
                data?.let {
                    llTradeRefDetails.visibility=View.VISIBLE

                    val newTrade: TradeReferencePostData? =
                        it.extras?.getParcelable<TradeReferencePostData>("TradeRefDetail")
                  val  tradeRefDetails=ArrayList<TradeRefDetail>()
                  tradeRefDetails?.add(newTrade!!.tradeRef?.get(0)!!)
                   /* val dummyTradeRef=this@OtherDetailsFragment.tradeRefDetails
                    dummyTradeRef?.removeAt(0)
                    dummyTradeRef?.removeAt(1)*/

                    rvTradeRefList.adapter=TradeRefDetailsAdapter(context!!,tradeRefDetails,"",tradeAdapterResponse!!)
                }
            }
        }
    }
}
