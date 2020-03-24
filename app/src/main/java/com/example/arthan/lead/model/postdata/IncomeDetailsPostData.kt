package com.example.arthan.lead.model.postdata

data class IncomeDetailsPostData(
    var loanId: String? = "",
    var customerId: String? = "",
    var anyOtherSourceofIncome: String? = "",
    var incomes: List<Income> = mutableListOf(),
    var numberofFamilyMembers: String? = "",
    var noofEarningFamilyMembers: String? = "",
    var monthlyFamilyIncome: String? = "",
    var businessTurnover: String? = "",
    var monthlyhouseholdexpenditures: String? = "",
    var monthlybusinessexpenditures: String? = "",
    var expenditures: List<Expenditure> = mutableListOf(),
    var liabilities: List<Liability> = mutableListOf(),
    var netMarginaspercustomer: String? = "",
    var methodUsed: String? = ""
)