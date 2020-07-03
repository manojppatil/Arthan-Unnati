package com.example.arthan.lead.model.responsedata

import com.example.arthan.lead.model.postdata.*

data class CustomerDocumentAndDataResponseData(
    val businessDetails: BusinessDetails?,
    val collateralDetails: CollateralDetails?,
    val incomeDetails: IncomeDetails?,
    val personalDetails: PersonalDetails?,
    val tradeRefDetails: List<TradeRefDetail>?,
    val neighborRefDetails: List<NeighborReference>?,
    val docDetails: DocDetails?,
    val timestamp: String?,
    val status: Int?,
    val error: String?,
    val message: String?,
    val path: String?
)

data class DocDetails(
    val panUrl: String?,
    val aadharFrontUrl: String?,
    val aadharBackUrl: String?,
    val voterUrl: String?,
    val paApplicantPhoto: String?
)