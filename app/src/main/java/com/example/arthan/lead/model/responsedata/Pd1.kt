package com.example.arthan.lead.model.responsedata

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pd1(
    var clothing: String?,
    var costpriceUnit: String?,
    var education: String?,
    var financialExpenses: String?,
    var food: String?,
    var grossMarginVerifiedByBcm: String?,
    var healthcare: String?,
    var highPerDayAmount: String?,
    var highPerWeekCount: String?,
    val pd1_id: String?,
    var licenseRenewal: String?,
    var loanamountrecommended: String?,
    var lowPerDayAmount: String?,
    var lowPerWeekCount: String?,
    var mediumPerDayAmount: String?,
    var mediumPerWeekCount: String?,
    var methodused: String?,
    var otherIncome: String?,
    var otherIncomeAgriculture: String?,
    var otherIncomeRent: String?,
    var otherIncomeSalaries: String?,
    var otherOpex: String?,
    var otherhhExpnse: String?,
    var personalDebt: String?,
    var product: String?,
    var rent: String?,
    var salaries: String?,
    var salepriceUnit: String?,
    var transportation: String?,
    var unitsSold: String?,
    var incomeRemarks: String?,
    var comfEmi: String?,
    var utilities: String?
) : Parcelable