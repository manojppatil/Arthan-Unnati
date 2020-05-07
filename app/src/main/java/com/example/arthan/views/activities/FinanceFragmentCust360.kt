package com.example.arthan.views.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import com.example.arthan.R
import com.example.arthan.lead.model.responsedata.DataPD
import com.example.arthan.lead.model.responsedata.PdX
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.getRupeeSymbol
import kotlinx.android.synthetic.main.activity_finance360.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*

class FinanceFragmentCust360 : BaseActivity() {

    private var mPDData: DataPD? = null
    private var mPDData2: DataPD? = null

    override fun contentView() = R.layout.activity_finance360

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {
        if (intent?.hasExtra(ArgumentKey.PDData) == true) {
           val mPDDataFull = intent?.getParcelableExtra(ArgumentKey.PDData) as? PdX
            mPDData=mPDDataFull?.data1
            mPDData2=mPDDataFull?.data2
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

        comfortable_emi_text_view2?.text = mPDData2?.comfortableEMICustomer
        existing_emi_text_view2?.text = mPDData2?.emiExistLoan
        final_eligible_amount_text_view2?.text = mPDData2?.finalEligibleAmt
        foir_text_view2?.text = mPDData2?.foir
        foir_norms_text_view2?.text = mPDData2?.foirNorms
        household_expense_pm_text_view2?.text = mPDData2?.householdExpensesPm
        loan_requested_text_view2?.text = mPDData2?.loanAmtApplied
        loan_amount_eligible_text_view2?.text = mPDData2?.loanAmtAsPerPropVal
        comfortable_loan_amount_text_view2?.text = mPDData2?.loanAmtAsPerPropValComfEmi
        recommended_loan_amount_text_view2?.text = mPDData2?.loanAmtRecommended
        ltv_text_view2?.text = mPDData2?.ltv
        ltv_norms_text_view2?.text = mPDData2?.ltvNorms
        max_loan_text_view2?.text = mPDData2?.maxLoanAmountCashFlow
        monthly_net_business_surplus_text_view2?.text = mPDData2?.monthlyNetBusinessSurplus
        net_surplus_pm_text_view2?.text = mPDData2?.monthlyNetSurplus
        net_margin_master_text_view2?.text = mPDData2?.netMarginMaster
        net_margin_pd_text_view2?.text = mPDData2?.netMarginPD
        net_surplus_text_view2?.text = mPDData2?.netSurplus
        other_income_for_loan_text_view2?.text = mPDData2?.otherIncomeForLoan
        other_income_pm_text_view2?.text = mPDData2?.otherIncomePm
        property_value_text_view2?.text = mPDData2?.propertyValue
        proposed_emi_text_view2?.text = mPDData2?.proposedEmi
        proposed_foir_text_view2?.text = mPDData2?.proposedFoir
        proposed_ltv_text_view2?.text = mPDData2?.proposedLtv
        proposed_obligations_emi_text_view2?.text = mPDData2?.proposedObligation
        roi_text_view2?.text = mPDData2?.roi
        tenure_text_view2?.text = mPDData2?.tenure
        turnover_text_view2?.text = mPDData2?.turnover

        getRupeeSymbol(
            this,
            comfortable_emi_text_view?.textSize ?: 14f,
            comfortable_emi_text_view?.currentTextColor ?: Color.BLACK
        )?.let {
            comfortable_emi_text_view?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            existing_emi_text_view?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            net_surplus_text_view?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            monthly_net_business_surplus_text_view?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            other_income_pm_text_view?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
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

    override fun screenTitle() = "Financials"

    companion object {
        fun startMe(
            context: Context?,
            pdData: PdX?,
            stringExtra: String?
        ) =
            context?.startActivity(Intent(context, FinanceFragmentCust360::class.java).apply {
                putExtra(ArgumentKey.PDData, pdData)
            })
    }
}