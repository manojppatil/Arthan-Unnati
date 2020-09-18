package com.example.arthan.model

data class BMReassignCaseData(

    val id:String,
    val reAssignedCases:ArrayList<BMCasesData>
) {

}
data class BMCasesData(
    val caseId: String,
    val customerName: String,
    val loanAmount: String,
    val assignedOn: String,
    val assignedBy: String,
    val loginDate: String,
    val remarks: String
)
