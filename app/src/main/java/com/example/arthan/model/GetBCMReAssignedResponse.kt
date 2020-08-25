package com.example.arthan.model

data class GetBCMReAssignedResponse(

    val userId:String,
    val reAssignedData:ArrayList<ReAssignedData>)
    {

}
data class ReAssignedData(
    val customerName: String,
    val loanId: String,
    val loanAmount: String,
    val loginDate: String,
    val assignedDate: String,
    val assignedBy: String
)
