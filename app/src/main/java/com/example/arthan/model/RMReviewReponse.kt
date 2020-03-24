package com.example.arthan.model

data class RMReviewReponse(val id: String,
val rmReviewList: List<RMReviewData>)

data class RMReviewData(val rmName: String,
val employeeId: String,
val leadCount: String,
val screeningCount: String,
val wip: String,
val approvedCount: String,
val rejectedCount: String,
val disbursedCount: String)

