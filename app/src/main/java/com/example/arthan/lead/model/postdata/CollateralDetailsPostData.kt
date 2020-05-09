package com.example.arthan.lead.model.postdata

data class CollateralDetailsPostData(
    var loanId: String? ="",
    var collaterals: ArrayList<CollateralData>,
    var custId: String? =""
   /* var securityType: String? =,
    var securitySubType: String? =,
    var immovableSubType: String? =,
    var plotType: String? =,
    var namunaType: String? =,
    var occupiedBy: String? =,
    var considerCFA: Boolean?,
    var natureOfDoc: String? =,
    var typeOfDoc: String? =,
    var docDesc: String? =,
    var docStatus: String? =*/


)

data class CollateralData(
    val securityType: String,
    val liquidDetails: LiquidDetails,
    val movableDetails: MovableDetails,
    val immovableDetails: ImmovableDetails

    )

data class LiquidDetails(
    val liqOwnership: String?,
    val policyNo: String?,
    val issueDate: String?,
    val validDate: String?,
    val currentValue: String?,
    val remarks: String?
)

data class MovableDetails(
    val movOwnership: String?,
    val name: String?,
    val months: String?,
    val years: String?,
    val identification: String?,
    val description: String?,
    val currentValue: String?,
    val derivedValue: String?
)

data class ImmovableDetails(
    val ownerName: String?,
    val address: String?,
    val securitySubType: String?,
    val immovableSubType: String?,
    val plotType: String?,
    val namunaType: String?,
    val occupiedBy: String?
)