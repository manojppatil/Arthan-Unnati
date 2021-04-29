package com.example.arthan.lead.model.responsedata

import com.example.arthan.lead.model.postdata.*

data class CustomerDocumentAndDataResponseData(
    val businessDetails: BusinessDetails?,
    val inPrincipleAmt: String?,
    val roi: String?,
    val tenure: String?,
    val gst: String?,
    val total: String?,
    val appFee: String?,
    val leadId: String?,
    val mobNo: String?,
    val loanAmt: String?,
    val collateralDetails: CollateralDetailsPostData?,
    val incomeDetails: IncomeDetails?,
    val personalDetails: List<PersonalDetails>?,
    val tradeRefDetails: ArrayList<TradeRefDetail>?,
    val neighborRefDetails: List<NeighborReference>?,
    val docDetails: DocDetails?,
    val timestamp: String?,
    val status: Int?,
    val error: String?,
    val message: String?,
    val path: String?,
    val loanType:String?,
    val consentText:String?,
    val businessComments:String?,
    val otherComments:String?,
    val collateralComments:String?,
    val incomeComments:String?,
    val businessDocs:ArrayList<RequireDocs> = ArrayList(),
    val kycDocs:ArrayList<RequireDocs> = ArrayList(),
    val coAppKycDocs:ArrayList<RequireDocs> = ArrayList(),
    val residentialDocs:ArrayList<RequireDocs> = ArrayList(),
    val bussPremisesDocs:ArrayList<RequireDocs> = ArrayList(),
    val loanDetails:LoanPostData?,
    val pd1: Pd1,
    val pd23: PD23PostData

)
data class RequireDocs(

    val loanId:String,
    val docId:String,
    val docName:String,
    val docUrl:String,
    val docStatus:String="false",
    val panUrl: String?=null,
    val aadharFrontUrl: String?=null,
    val aadharBackUrl: String?=null,
    val voterUrl: String?=null,
    val paApplicantPhoto: String?=null,
    val businessProof: String?=null,
    val businessAddrProof: String?=null,
    val incomeProof: String?=null,
    val chequeUrl: String?=null
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