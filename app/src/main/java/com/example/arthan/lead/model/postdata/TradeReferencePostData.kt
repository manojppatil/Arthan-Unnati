package com.example.arthan.lead.model.postdata

data class TradeReferencePostData(
    var resubmit:String?=null,
    var reassign:String?=null,
    val tradeRef: List<TradeRefDetail>? = null
)