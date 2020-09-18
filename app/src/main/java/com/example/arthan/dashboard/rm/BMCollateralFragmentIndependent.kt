package com.example.arthan.dashboard.rm

import android.app.AlertDialog
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.example.arthan.R
import com.example.arthan.dashboard.bm.BMScreeningReportActivity
import com.example.arthan.global.ArthanApp
import com.example.arthan.lead.DocumentActivity
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.lead.model.postdata.*
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.PendingCustomersActivity
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.collateral_section_indepdnt.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class BMCollateralFragmentIndependent: BaseFragment(),CoroutineScope {

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
    override fun contentView(): Int {
        return R.layout.collateral_section_indepdnt
    }

    override fun init() {
        mLoanId = activity?.intent?.getStringExtra("loanId")
        mCustomerId = activity?.intent?.getStringExtra("custId")
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
                            CoroutineScope(Dispatchers.IO).launch {
                                fetchAndUpdateCollateralNatureAsync("").await()
                                //fetchRelationshipAsync("")
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

        sp_security.onItemSelectedListener = securitySpinner
        sp_security_subType.onItemSelectedListener = subSecuritySpinner
        loadInitialData()
        btn_save_continue?.setOnClickListener {
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
                    map["userId"] = ArthanApp.getAppInstance().loginUser + ""

                    CoroutineScope(Dispatchers.IO).launch {
                        val respo = RetrofitFactory.getApiService().updateCollateralDetails(
                            map
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

                                    startActivity(
                                        Intent(
                                            activity,
                                            PendingCustomersActivity::class.java
                                        ).apply {
                                            putExtra("FROM", "BM")
                                        })
                                    activity?.finish()
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
            } else if(ArthanApp.getAppInstance().loginRole=="RM"||ArthanApp.getAppInstance().loginRole=="AM"){
//                if (navController != null) {
                val progressLoader: ProgrssLoader? =
                    if (context != null) ProgrssLoader(context!!) else null
                progressLoader?.showLoading()
                launch(ioContext) {
//                        val isNeighbourReferenceSaved = saveNeighbourReferenceAsync().await()
                    var isCollateralSaved=true
                    if(activity?.intent?.getStringExtra("loanType").equals("Secure",ignoreCase = true)) {
                        isCollateralSaved = saveCollateralDataAsync().await()
                    }

                    if (isCollateralSaved ) {
                        withContext(uiContext) {
                            progressLoader?.dismmissLoading()
                            if (arguments?.getString("task")
                                    .equals("RMreJourney", ignoreCase = true)
                            ) {
                                withContext(Dispatchers.IO)
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

                    } else {
                        withContext(Dispatchers.IO)
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
                    liquidDetails = LiquidDetails(
                        ownerName = et_coOwnerName.text.toString(),
                        policyNo = et_COpolicyNo.text.toString(),
                        surrenderValue = et_cosurrenderValue.text.toString()
                    ),
                    otherDetails = MovableDetails(
                        ownerName = et_coOthersOwnerName.text.toString(),
                        policyNo = et_COOtherspolicyNo.text.toString(),
                        marketValue = et_marketValueCo.text.toString(),
                        derivedValue = et_derivedValueCO.text.toString()
                    ),
                    immovableDetails = ImmovableDetails(
                        ownerName = et_COOwnerNameImm.text.toString(),
                        address = et_address.text.toString(),
                        addressType = addressType,
                        collateralType = (sp_collateral_type_liq?.selectedItem as Data).value.toString(),
                        jurisdiction = (sp_jurisdictionType.selectedItem as Data).value.toString(),
                        marketValue = et_MarketValueImm.text.toString()
                        ,
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


    private fun loadInitialData() {
        val progressLoader: ProgrssLoader? =
            if (context != null) ProgrssLoader(context!!) else null
        progressLoader?.showLoading()
        launch(ioContext) {
            val natureOfProperty = fetchAndUpdateNatureOfPropertyAsync().await()
            val propertyJurisdiction = fetchAndUpdatePropertyJurisdictionAsync("").await()
            val propertyType = fetchAndUpdatePropertyTypeAsync().await()

            fetchmstrId("")
            fetchDocNature()
            fetchmDocType()
            fetchoccupiedBy()
            if (natureOfProperty && propertyJurisdiction && propertyType) {
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
                /* val res =
                     RetrofitFactory.getApiService().getOtherData(arguments?.getString("loanId"))*/

                var map = HashMap<String, String>()
                map["loanId"] = arguments?.getString("loanId")!!
                map["screen"] = "OTHERS_TRADE"
                val res =
                    RetrofitFactory.getApiService().getScreenData(map)
                withContext(uiContext) {
                    if (res!!.isSuccessful) {
                        val responseBody = res.body()
                        updateData(
                            responseBody?.collateralDetails,
                            arguments?.getString("loanId"),
                            responseBody?.collateralDetails?.custId,""
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




    private fun getAdapter(list: List<Data>?): DataSpinnerAdapter? =
        if (context != null)
            DataSpinnerAdapter(context!!, list?.toMutableList() ?: mutableListOf()).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            } else null


    fun updateData(
        collateralDetails: CollateralDetailsPostData?,
        loanId: String?,
        loanType: String?,
        comment: String?
    ) {

        if (activity is ReUsableFragmentSpace) {
            (activity as ReUsableFragmentSpace).setCommentsToField(comment.toString()+"")
        }
        mLoanId = loanId


  /*      if (ArthanApp.getAppInstance().loginRole.contains("RM")) {
            if (activity?.intent?.getStringExtra("loanType").equals(
                    "secure",
                    ignoreCase = true
                )
            )
        }
        if (arguments?.getString("task").equals("RM_AssignList")) {
            if (loanType.equals(
                    "secure",
                    ignoreCase = true
                )
            ) {
                ll_collateral.visibility = View.VISIBLE
            } else {
                ll_collateral.visibility = View.GONE

            }
        }
*/
        if (collateralDetails != null && collateralDetails.collaterals.size > 0) {


            CoroutineScope(Dispatchers.IO).launch {
                fetchmstrId(collateralDetails.collaterals[0].securityType)
                fetchAndUpdateCollateralNatureAsync(collateralDetails.collaterals[0].immovableDetails.collateralType).await()
//                fetchRelationshipAsync(collateralDetails.collaterals[0].immovableDetails.rshipWithApplicant)
                fetchAndUpdatePropertyJurisdictionAsync(collateralDetails.collaterals[0].immovableDetails.jurisdiction).await()
                fetchOwnerShip(collateralDetails.collaterals[0].immovableDetails.ownership)

            }
            //   Handler(Looper.getMainLooper()).postDelayed(Runnable {
            //       progressBar.dismmissLoading()
            var list = collateralDetails.collaterals
            if (list[0].securityType.toLowerCase() == "movable") {
                security_section_movable.visibility = View.VISIBLE
            } else {
                security_section_movable.visibility = View.GONE

            }
            if (list.get(0).securityType.toLowerCase() == "immovable" || list.get(
                    0
                ).securityType?.toLowerCase() == "Negative Lien".toLowerCase()
            ) {
                immovable_section.visibility = View.VISIBLE
            } else {
                immovable_section.visibility = View.GONE

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
            et_marketValueCo.setText(collateralDetails!!.collaterals[0].otherDetails.marketValue)
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
}