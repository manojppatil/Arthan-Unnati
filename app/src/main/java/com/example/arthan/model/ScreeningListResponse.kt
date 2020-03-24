package com.example.arthan.model

data class ScreeningListResponse (val rmId: String,
val screeningList:List<ScreeningData>)

data class ScreeningData(val segment: String,
val leadDate: String,
val name: String,
val id: String,
val loanId: String,
val loanAmt: String,
val branch: String,
val mobileNo: String)
