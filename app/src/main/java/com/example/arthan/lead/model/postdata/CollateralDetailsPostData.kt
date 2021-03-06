package com.example.arthan.lead.model.postdata

data class CollateralDetailsPostData(
    var resubmit:String="",
    var reassign:String="",
    var loanId: String? ="",
    var collaterals: ArrayList<CollateralData>,
    var custId: String? ="",
    var remarks:String?="",
    var lat:String?="",
    var lng:String?="",
    var docUrl:String?=""
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
/*
*  "collaterals": [
    {
      "securityType": "",
      "liquidDetails": {
        "ownerName": "",
        "policyNo": "",
        "surrenderValue": ""
      },
      "otherDetails": {
        "ownerName": "",
        "policyNo": "",
        "marketValue": "",
        "derivedValue": ""
      },
      "immovableDetails": {
        "ownerName":"",
        "address":"",
        "collateralType": "",
        "jurisdiction": "",
        "marketValue": "",
        "rshipWithApplicant": "",
        "ownership": ""
      }
    }
  ]*/

)

data class CollateralData(
    val securityType: String,
    val pincode: String?,
    val liquidDetails: LiquidDetails,
    val otherDetails: MovableDetails,
    val immovableDetails: ImmovableDetails,
    var collateralId:String?=""

    )

data class LiquidDetails(
    val ownerName: String?,
    val policyNo: String?,
    val surrenderValue: String?
)

data class MovableDetails(
    val ownerName: String?,
    val policyNo: String?,
    val marketValue: String?,
    val propertyArea: String?,
    val derivedValue: String?
)

data class ImmovableDetails(
    val ownerName: String?,
    val address: String?,
    val addressType: String?,
    val collateralType: String?="",
    val jurisdiction: String?,
    val marketValue: String?,
    val propertyArea: String?,
    val rshipWithApplicant: String?,
    val ownership: String?
)