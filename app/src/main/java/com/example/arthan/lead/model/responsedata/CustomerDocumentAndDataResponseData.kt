package com.example.arthan.lead.model.responsedata

import com.example.arthan.lead.model.postdata.*

data class CustomerDocumentAndDataResponseData(
    val businessDetails: BusinessDetails?,
    val inPrincipleAmt: String?,
    val roi: String?,
    val tenure: String?,
    val loanAmt: String?,
    val collateralDetails: CollateralDetailsPostData?,
    val incomeDetails: IncomeDetails?,
    val personalDetails: List<PersonalDetails>?,
    val tradeRefDetails: List<TradeRefDetail>?,
    val neighborRefDetails: List<NeighborReference>?,
    val docDetails: DocDetails?,
    val timestamp: String?,
    val status: Int?,
    val error: String?,
    val message: String?,
    val path: String?,
    val loanType:String?,
    val businessComments:String?,
    val otherComments:String?,
    val incomeComments:String?,
    val businessDocs:ArrayList<RequireDocs> = ArrayList(),
    val kycDocs:ArrayList<RequireDocs> = ArrayList(),
    val residentialDocs:ArrayList<RequireDocs> = ArrayList(),
    val bussPremisesDocs:ArrayList<RequireDocs> = ArrayList(),
    val loanDetails:LoanPostData?

)
data class RequireDocs(

    val loanId:String,
    val docId:String,
    val docName:String,
    val docUrl:String,
    val docStatus:String="false"
)

data class DocDetails(
    val panUrl: String?,
    val aadharFrontUrl: String?,
    val aadharBackUrl: String?,
    val voterUrl: String?,
    val paApplicantPhoto: String?,
    val businessProof: String?,
    val businessAddrProof: String?,
    val incomeProof: String?,
    val chequeUrl: String?
)