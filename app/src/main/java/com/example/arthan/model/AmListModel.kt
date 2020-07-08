package com.example.arthan.model


data class AmListResponse(
    val response:List<AmListModel>
)
data class AmListModel(
    val name:String,
    val submittedDate:String,
    val status:String
) {
}