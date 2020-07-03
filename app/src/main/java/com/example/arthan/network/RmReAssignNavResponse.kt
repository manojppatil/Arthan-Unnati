package com.example.arthan.network

data class RmReAssignNavResponse(

    val loanId: String,
    val customerId: String,
    val custId: String,
    val completedScreens: ArrayList<String>
)


