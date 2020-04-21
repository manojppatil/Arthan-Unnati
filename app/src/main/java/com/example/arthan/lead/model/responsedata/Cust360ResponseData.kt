package com.example.arthan.lead.model.responsedata

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cust360ResponseData(
    val bankDetailsVO: String?,
    val collateralVO: CollateralVO?,
    val gstDetailsVO: GstDetailsVO?,
    val ipAddressVO: IpAddressVO?,
    val pdVO: PdX?,
    val scVO: SCVO?,
    val pd1DetailsVO: Pd1?,
    val pd23DetailsVO: Pd23?
) : Parcelable