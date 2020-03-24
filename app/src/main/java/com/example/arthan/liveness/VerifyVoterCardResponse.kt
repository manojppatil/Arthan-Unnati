package com.example.arthan.liveness

import com.google.gson.annotations.SerializedName

data class VerifyVoterCardResponse(
    @SerializedName("request_id") val requestIdd: String?,
    @SerializedName("result_code") val resultCode: Int?,
    @SerializedName("result_message") var resultMessage: String?,
    var status: String?,
    var reason: String?,
    var result: VerifyVoterCardResult?
)

data class VerifyVoterCardResult(
    val id: String?,
    val name: String?,
    val name_v1: String?,
    val name_v2: String?,
    val name_v3: String?,
    val dob: String?,
    val gender: String?,
    val age: String?,
    val rln_name: String?,
    val rln_name_v1: String?,
    val rln_name_v2: String?,
    val rln_name_v3: String?,
    val rln_type: String?,
    val house_no: String?,
    val part_no: String?,
    val part_name: String?,
    val section_no: String?,
    val epic_no: String?,
    val last_update: String?,
    val st_code: String?,
    val state: String?,
    val district: String?,
    val ac_no: String?,
    val ac_name: String?,
    val ps_name: String?,
    val ps_lat_long: String?,
    val pc_name: String?,
    val slno_inpart: String?
)