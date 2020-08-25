package com.example.arthan.model

data class EXceptionReportResponse(
    val propertyOwner: CommonReport,
    val area: CommonReport,
    val east: CommonReport,
    val west: CommonReport,
    val north: CommonReport,
    val south: CommonReport,
    val loanId: String
) {

}
data class CommonReport(
    val legal :String,
    val tech:String
)