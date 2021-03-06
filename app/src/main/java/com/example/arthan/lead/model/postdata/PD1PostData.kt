package com.example.arthan.lead.model.postdata

data class PD1PostData(
    var productData: List<ProductData>? = mutableListOf(ProductData()),
    var highPerDayAmount: String? = "",
    var highPerWeekCount: String? = "",
    var lowPerDayAmount: String? = "",
    var lowPerWeekCount: String? = "",
    var mediumPerDayAmount: String? = "",
    var mediumPerWeekCount: String? = "",
    var grossMarginVerifiedByBcm: String? = "",
    var otherIncome: String? = "",
    var otherIncomeAgriculture: String? = "",
    var otherIncomeRent: String? = "",
    var otherIncomeSalaries: String? = "",
    var rent: String? = "",
    var salaries: String? = "",
    var utilities: String? = "",
    var transportation: String? = "",
    var licenseRenewal: String? = "",
    var loanId: String? = "",
    var customerId: String? = "",
    var clothing: String? = "",
    var education: String? = "",
    var financialExpenses: String? = "",
    var food: String? = "",
    var healthcare: String? = "",
    var loanamountrecommended: String? = "",
    var methodUsed: String? = "",
    var otherOpex: String? = "",
    var otherhhExpnse: String? = "",
    var personalDebt: String? = "",
    var incomeRemarks: String? = "",
    var comfEmi: String? = ""
)