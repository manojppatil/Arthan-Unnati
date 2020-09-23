package com.example.arthan.dashboard.bcm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.crashlytics.android.Crashlytics
import com.example.arthan.R
import com.example.arthan.global.ArthanApp
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.lead.model.postdata.*
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.PendingCustomersActivity
import kotlinx.android.synthetic.main.activity_b_c_m_approved_collateral.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class BCMApprovedCollateral : BaseActivity(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    override fun contentView(): Int {
        return R.layout.activity_b_c_m_approved_collateral
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
    private fun loadInitialData() {
        val progressLoader: ProgrssLoader? =
            if (this != null) ProgrssLoader(this) else null
        progressLoader?.showLoading()
        launch(ioContext) {
            val natureOfProperty = fetchAndUpdateNatureOfPropertyAsync().await()
            val propertyJurisdiction = fetchAndUpdatePropertyJurisdictionAsync("").await()
            val propertyType = fetchAndUpdatePropertyTypeAsync().await()

            fetchmstrId("")
            fetchDocNature()
            fetchmDocType()
            fetchoccupiedBy()
            if (natureOfProperty && propertyJurisdiction && propertyType ) {
                withContext(uiContext) {
                    progressLoader?.dismmissLoading()

                }
            }
        }
    }

    override fun init() {
        var mLoanId = intent?.getStringExtra("loanId")
        var mCustomerId = intent?.getStringExtra("custId")
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

        sp_security.onItemSelectedListener = securitySpinner
        sp_security_subType.onItemSelectedListener = subSecuritySpinner
        loadInitialData()
        submitCollateral?.setOnClickListener {

            if (ArthanApp.getAppInstance().loginRole == "BM" || ArthanApp.getAppInstance().loginRole == "BCM") {

                var dialog = AlertDialog.Builder(this)
                var view: View? = layoutInflater?.inflate(R.layout.remarks_popup, null)
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
                                finish()
                            }


                        }
                    }



                }
                alert.show()

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
                                    this@BCMApprovedCollateral,
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
                                   this@BCMApprovedCollateral,
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
                                 this@BCMApprovedCollateral,
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

    private fun saveCollateralDataAsync() {
        try {
            var collaterals:ArrayList<CollateralData> = ArrayList()

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
                loanId = intent?.getStringExtra("loanId"),
                custId = intent?.getStringExtra("custId"),
                collaterals = collaterals
            )
            postBody.resubmit = "yes"
            postBody.reassign="Y"

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
            CoroutineScope(Dispatchers.IO).launch {
                val response = RetrofitFactory.getApiService().saveCollateralDetail(postBody)
              if (response?.isSuccessful == true) {
                    val result = response?.body()
                    result?.apiCode == "200"

                } else {
                    false
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Crashlytics.log(e.message)
        }
    }
    override fun onToolbarBackPressed() {
        finish()
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
    private fun getAdapter(list: List<Data>?): DataSpinnerAdapter? =
            DataSpinnerAdapter(this, list?.toMutableList() ?: mutableListOf()).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }


    override fun screenTitle(): String {
      return "Collateral Details"
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


}
