package com.example.arthan.lead.model.postdata

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TradeReferencePostData(
    var resubmit:String?=null,
    var reassign:String?=null,
    val tradeRef: List<TradeRefDetail>? = null,
    var remarks:String?=null,
    var userId:String?=null
):Parcelable