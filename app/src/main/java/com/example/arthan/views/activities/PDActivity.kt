package com.example.arthan.views.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import com.example.arthan.R
import com.example.arthan.dashboard.bm.Customer360Activity
import com.example.arthan.lead.model.responsedata.PdX
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.getRupeeSymbol
import kotlinx.android.synthetic.main.activity_pd.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*

class PDActivity : BaseActivity() {

    private var mPDData: PdX? = null

    override fun contentView() = R.layout.activity_pd

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {
        if (intent?.hasExtra(ArgumentKey.PDData) == true) {
            mPDData = intent?.getParcelableExtra(ArgumentKey.PDData) as? PdX
        }
        btn_filter.visibility = View.GONE
        btn_search.visibility = View.GONE
        comfortable_emi_text_view?.text = mPDData?.comfortableEMICustomer
        existing_emi_text_view?.text = mPDData?.emiExistLoan
        final_eligible_amount_text_view?.text = mPDData?.finalEligibleAmt
        foir_text_view?.text = mPDData?.foir
        foir_norms_text_view?.text = mPDData?.foirNorms
        household_expense_pm_text_view?.text = mPDData?.householdExpensesPm
        loan_requested_text_view?.text = mPDData?.loanAmtApplied
        loan_amount_eligible_text_view?.text = mPDData?.loanAmtAsPerPropVal
        comfortable_loan_amount_text_view?.text = mPDData?.loanAmtAsPerPropValComfEmi
        recommended_loan_amount_text_view?.text = mPDData?.loanAmtRecommended
        ltv_text_view?.text = mPDData?.ltv
        ltv_norms_text_view?.text = mPDData?.ltvNorms
        max_loan_text_view?.text = mPDData?.maxLoanAmountCashFlow
        monthly_net_business_surplus_text_view?.text = mPDData?.monthlyNetBusinessSurplus
        net_surplus_pm_text_view?.text = mPDData?.monthlyNetSurplus
        net_margin_master_text_view?.text = mPDData?.netMarginMaster
        net_margin_pd_text_view?.text = mPDData?.netMarginPD
        net_surplus_text_view?.text = mPDData?.netSurplus
        other_income_for_loan_text_view?.text = mPDData?.otherIncomeForLoan
        other_income_pm_text_view?.text = mPDData?.otherIncomePm
        property_value_text_view?.text = mPDData?.propertyValue
        proposed_emi_text_view?.text = mPDData?.proposedEmi
        proposed_foir_text_view?.text = mPDData?.proposedFoir
        proposed_ltv_text_view?.text = mPDData?.proposedLtv
        proposed_obligations_emi_text_view?.text = mPDData?.proposedObligation
        roi_text_view?.text = mPDData?.roi
        tenure_text_view?.text = mPDData?.tenure
        turnover_text_view?.text = mPDData?.turnover

        getRupeeSymbol(
            this,
            comfortable_emi_text_view?.textSize ?: 14f,
            comfortable_emi_text_view?.currentTextColor ?: Color.BLACK
        )?.let {
            comfortable_emi_text_view?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            existing_emi_text_view?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            net_surplus_text_view?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            monthly_net_business_surplus_text_view?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            other_income_pm_label?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            other_income_for_loan_text_view?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            household_expense_pm_text_view?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            net_surplus_pm_text_view?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            comfortable_loan_amount_text_view?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            loan_requested_text_view?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            proposed_obligations_emi_text_view?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            max_loan_text_view?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            property_value_text_view?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            loan_amount_eligible_text_view?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            final_eligible_amount_text_view?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            recommended_loan_amount_text_view?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            proposed_emi_text_view?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            turnover_text_view?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
        }
    }

    override fun screenTitle() = "PD * Customer Full Name_1"

    companion object {
        fun startMe(context: Context?, pdData: PdX?) =
            context?.startActivity(Intent(context, PDActivity::class.java).apply {
                putExtra(ArgumentKey.PDData, pdData)
            })
    }
}