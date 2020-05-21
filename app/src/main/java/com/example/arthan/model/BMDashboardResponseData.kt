package com.example.arthan.model

data class BMDashboardResponseData(
val empId:String?,
val empName:String?,
val empBranch:String?,
val since:String?,
val empImage:String?,
val leads:Leads,
val myQueue:MyQueue,
val rmReview:RmReview,
val approved:Approved,
val rejected:Rejected,
val reassignBy:ReassignBy,
val reassignTo:ReassignTo)


data class Leads(
    val count:String?,
    val total:String?,
    val label:String?
)data class MyQueue(
    val count:String?,
    val total:String?,
    val label:String?
)data class ReassignBy(
    val count:String?,
    val total:String?,
    val label:String?
)data class ReassignTo(
    val count:String?,
    val total:String?,
    val label:String?
)data class Rejected(
    val count:String?,
    val total:String?,
    val label:String?
)data class Approved(
    val count:String?,
    val total:String?,
    val label:String?
)data class RmReview(
    val count:String?,
    val total:String?,
    val label:String?
)