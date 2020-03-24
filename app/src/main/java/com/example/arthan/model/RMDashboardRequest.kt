package com.example.arthan.model

const val SCREENING_SECTION="screening"
const val LEAD_SECTION= "leads"
const val REASSIGN_LEAD_SECTION= "reAssign"
const val APPROVED_SECTION="rmApproved"
const val REJECTED_SECTION="rmRejected"
const val TODISBURSED_SECTION="rmToDisburse"
const val RM_REVIEW_SECTION="rmReview"

data class RMDashboardRequest(val rmId: String,
val section: String)