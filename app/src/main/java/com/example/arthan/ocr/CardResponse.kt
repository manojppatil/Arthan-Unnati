package com.example.arthan.ocr

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CardResponse(
    @SerializedName("request_id") val request_id: String?,
    @SerializedName("status") var status: String?,
    @SerializedName("image_id") val imageId: String?,
    @SerializedName("reason") val reason: String?,
    @SerializedName("results") val results: List<RequestCardResult>?,
    @Transient var cardFrontUrl: String? = "",
    @Transient var cardBackUrl: String? = ""
) : Parcelable

@Parcelize
data class RequestCardResult(
    @SerializedName("card_info") val cardInfo: CardInfo?,
    @SerializedName("card_type") val cardType: String?,
    @SerializedName("card_side") val cardSide: String?
) : Parcelable

@Parcelize
data class CardInfo(
    @SerializedName("card_no") val cardNo: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("father_name") val fatherName: String?,
    @SerializedName("date_info") val dateInfo: String?,
    @SerializedName("date_type") val dateType: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("address_line_one") val addressLineOne: String?,
    @SerializedName("address_line_two") val addressLineTwo: String?,
    @SerializedName("care_of") val careOf: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("daughter_of") val daughterOf: String?,
    @SerializedName("husband_of") val husbandOf: String?,
    @SerializedName("pin") val pin: String?,
    @SerializedName("son_of") val sonOf: String?,
    @SerializedName("state") val state: String?,
    @SerializedName("district") val district: String?,
    @SerializedName("landmark") val landmark: String?,
    @SerializedName("areaName") val areaName: String?,
    @SerializedName("vid") val vid: String?,
    @SerializedName("wife_of") val wifeOf: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("mother_name") val motherName: String?,
    @SerializedName("phone_number") val phoneNumber: String?,
    @SerializedName("voter_id") val voterId: String?,
    @SerializedName("dob") val dob: String?,
    @SerializedName("age") val age: String?,
    @SerializedName("age_as_on") val ageAsOn: String?,
    @SerializedName("date") val date: String?
) : Parcelable
