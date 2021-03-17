package com.example.arthan.model

data class BmRmStatusModel(
    val userId:String,
    val status:ArrayList<BMRMStatus>

) {
}
data class BMRMStatus(
    val rmId:String,
    val rmName:String,
    val leadCnt:String,
    val screeningCnt:String,
    val loginCnt:String
)