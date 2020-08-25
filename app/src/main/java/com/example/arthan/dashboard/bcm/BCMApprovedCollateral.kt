package com.example.arthan.dashboard.bcm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.crashlytics.android.Crashlytics
import com.example.arthan.R
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.lead.model.postdata.*
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_b_c_m_approved_collateral.*
import kotlinx.coroutines.*

class BCMApprovedCollateral : BaseActivity() {
    override fun contentView(): Int {
        return R.layout.activity_b_c_m_approved_collateral
    }

    override fun init() {
        var type=intent.getStringExtra("loanType")

     /*   CoroutineScope(Dispatchers.IO).launch {
            fetchmstrId(collateralDetails.collaterals[0].securityType)
            fetchAndUpdateCollateralNatureAsync(collateralDetails.collaterals[0].immovableDetails.collateralType).await()
            fetchRelationshipAsync(collateralDetails.collaterals[0].immovableDetails.rshipWithApplicant)
            fetchAndUpdatePropertyJurisdictionAsync(collateralDetails.collaterals[0].immovableDetails.jurisdiction).await()
            fetchOwnerShip(collateralDetails.collaterals[0].immovableDetails.ownership)

        }*/
        if (type.toLowerCase() == "movable") {
            security_section_movable.visibility = View.VISIBLE
        } else {
            security_section_movable.visibility = View.GONE

        }
        if (type.toLowerCase() == "immovable" || type.toLowerCase() == "Negative Lien".toLowerCase()
        ) {
            immovable_section.visibility = View.VISIBLE
        } else {
            immovable_section.visibility = View.GONE

        }

        liquid_section.visibility = View.VISIBLE
        others_section.visibility = View.VISIBLE

        submitCollateral.setOnClickListener {
           // saveCollateralDataAsync()
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
}
