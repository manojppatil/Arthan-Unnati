package com.example.arthan.lead

import android.view.View
import com.example.arthan.R
import com.example.arthan.global.BUSINESS
import com.example.arthan.global.DOCUMENT
import com.example.arthan.global.INCOME
import com.example.arthan.global.PERSONAL
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_lead_info_capture.*

class LeadInfoCaptureActivity: BaseActivity() {

    var panData: String= ""

    override fun screenTitle()= "Complete Details"

    override fun contentView()= R.layout.activity_lead_info_capture

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {
        if(intent.hasExtra("PAN_DATA"))
            panData= intent.getStringExtra("PAN_DATA")
    }

    fun infoCompleteState(type: String){
        when (type) {
            DOCUMENT -> {
                txt_doc.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_info_completed,0,0)
            }
            BUSINESS -> {
                txt_Business.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_info_completed,0,0)
            }
            INCOME -> {
                txt_income.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_info_completed,0,0)
            }
        }
    }

    fun enableBusiness(){
        vw_dim_business.visibility= View.GONE
    }

    fun enableInCome(){
        vw_dim_income.visibility= View.GONE
    }

    fun enableDoc(){
        vw_dim_doc.visibility= View.GONE
    }

}