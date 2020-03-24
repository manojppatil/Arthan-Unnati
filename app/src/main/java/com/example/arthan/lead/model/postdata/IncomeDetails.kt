package com.example.arthan.lead.model.postdata

data class IncomeDetails(
    val anyOtherSourceofIncome: String = "",
    val customerId: String = "",
    val businessTurnover: String = "",
    val expenditures: List<Expenditure> = mutableListOf(),
    val incomeId: String = "",
    val incomes: List<IncomeX> = mutableListOf(),
    val liabilities: List<LiabilityX> = mutableListOf(),
    val loanId: String = "",
    val monthlyFamilyIncome: String = "",
    val monthlyhouseholdexpenditures: String = "",
    val netMarginaspercustomer: String = "",
    val noofEarningFamilyMembers: String = "",
    val numberofFamilyMembers: String = ""
)