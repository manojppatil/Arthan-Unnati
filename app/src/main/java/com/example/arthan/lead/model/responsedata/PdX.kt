package com.example.arthan.lead.model.responsedata

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PdX(
    val comfortableEMICustomer: String? = "",
    val emiExistLoan: String? = "",
    val finalEligibleAmt: String? = "",
    val foir: String? = "",
    val foirNorms: String? = "",
    val householdExpensesPm: String? = "",
    val loanAmtApplied: String? = "",
    val loanAmtAsPerPropVal: String? = "",
    val loanAmtAsPerPropValComfEmi: String? = "",
    val loanAmtRecommended: String? = "",
    val ltv: String? = "",
    val ltvNorms: String? = "",
    val maxLoanAmountCashFlow: String? = "",
    val monthlyNetBusinessSurplus: String? = "",
    val monthlyNetSurplus : String? = "",
    val netMarginMaster: String? = "",
    val netMarginPD: String? = "",
    val netSurplus: String? = "",
    val otherIncomeForLoan: String? = "",
    val otherIncomePm: String? = "",
    val propertyValue: String? = "",
    val proposedEmi: String? = "",
    val proposedFoir: String? = "",
    val proposedLtv: String? = "",
    val proposedObligation: String? = "",
    val roi: String? = "",
    val tenure: String? = "",
    val turnover: String? = ""
): Parcelable