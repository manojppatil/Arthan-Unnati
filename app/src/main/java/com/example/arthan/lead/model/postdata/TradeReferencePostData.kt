package com.example.arthan.lead.model.postdata

data class TradeReferencePostData(
    var resubmit:String?=null,
    val tradeRef: List<TradeRefDetail>? = null
)