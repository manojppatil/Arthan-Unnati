package com.example.arthan.dashboard.bcm

import android.content.Context
import android.content.Intent
import android.view.View
import com.example.arthan.R
import com.example.arthan.lead.model.responsedata.PdCust
import com.example.arthan.lead.model.responsedata.PdX
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_pd.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class PDActivity : BaseActivity(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main
    private val custName:String?=""
    private var mPDData: PdCust? = null

    override fun contentView()=R.layout.activity_pd

    override fun init() {

        if (intent?.hasExtra(ArgumentKey.PDData) == true) {
            mPDData = intent?.getParcelableExtra(ArgumentKey.PDData) as? PdCust
        }
        txt_appl_name.text="Name of Applicant:${mPDData?.applicantName}"
        txt_qualification.text="Qualification:${mPDData?.qualification}"
        txt_name_of_business.text="Name of Business:${mPDData?.businessName}"
        txt_vintage_of_bus.text="Vintage of Business:${mPDData?.vintage}"
        txt_no_of_wrokers.text="Number of employees/workers:${mPDData?.noOfEmployees}"
        txt_busines_ownership.text="Business Ownership:${mPDData?.businessOwnership}"
        txt_no_of_deviations.text="No of Deviations:${mPDData?.noOfDeviations}"
        txt_purpose_of_loan.text="Purpose of Loan:${mPDData?.purposeOfLoan}"
        txt_banking_with.text="Banking with:${mPDData?.bankingWith}"
        txt_income_considered.text="Income Considered:${mPDData?.incomeConsidered}"
        txt_risk.text="Risk/Concern of Proposal:${mPDData?.risk}"
        txt_strengths.text="Strengths/Mitigates of proposal:${mPDData?.strength}"
        txt_sanction_conditions.text="Sanction Conditions:${mPDData?.sanctionConditions}"
        txt_overall_remarks.text="Overall Remarks:${mPDData?.remarks}"
        btn_search.visibility = View.GONE
        btn_filter.visibility = View.GONE
    }

    override fun onToolbarBackPressed() {
        onBackPressed()
    }


    override fun screenTitle()="PD * "+intent.getStringExtra("cname")
    companion object {
        fun startMe(
            context: Context?,
            pdData: PdCust?,
            custName: String?
        ) {
            custName
            context?.startActivity(Intent(context, PDActivity::class.java).apply {
                putExtra(ArgumentKey.PDData, pdData)
                putExtra("cname",custName)
            })
        }
    }
}

