package com.example.arthan.lead.model.responsedata

import android.os.Parcelable
import com.example.arthan.dashboard.bm.model.BureauDetails
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cust360ResponseData(
    val bankDetailsVO: String?,
    val collateralVO: CollateralVO?,
    val gstDetailsVO: GstDetailsVO?,
    val ipAddressVO: ipAddressVO?,
    val pdVO: PdX?,
    val scVO: SCVO?,
    val pd1DetailsVO: Pd1?,
    val asset: Asset?,
    val pd23DetailsVO: Pd23?,
    val pdCust: PdCust?,
    val bureauData: BureauDetails?

) : Parcelable

@Parcelize
data class PdCust(

    val applicantName: String?,
    val qualification: String?,
    val businessName: String?,
    val vintage: String?,
    val noOfEmployees: String?,
    val businessOwnership: String?,
    val noOfDeviations: String?,
    val purposeOfLoan: String?,
    val bankingWith: String?,
    val incomeConsidered: String?,
    val risk: String?,
    val strength: String?,
    val sanctionConditions: String?,
    val remarks: String?
) : Parcelable

@Parcelize
data class Asset(
    val loanId: String,
    val assets: ArrayList<Assets?>
) : Parcelable

@Parcelize
data class Assets(
    val assetType: String?,
    val ownerName: String?,
    val purchaseYear: String?,
    val purchaseValue: String?,
    val currentValue: String?,
    val assetAddress: String?
) : Parcelable
