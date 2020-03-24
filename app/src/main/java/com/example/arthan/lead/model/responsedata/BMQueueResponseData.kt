package com.example.arthan.lead.model.responsedata

import com.example.arthan.model.Customer

data class BMQueueResponseData(
    val myQueue: List<Customer>?,
    val timestamp: String?,
    val status: Int?,
    val error: String?,
    val message: String?,
    val path: String?
)