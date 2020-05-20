package com.example.arthan.ocr

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface OcrService {

    @Headers(
        "Content-Type:application/json",
        "cache-control:no-cache",
        "Postman-Token:0324ddd2-8bae-4f34-bf20-8d9dc781ac20")
//    @Multipart
    @POST("indian_card")
    suspend fun getIndianCardInfo(@Body ocrRequest: OcrRequest): Response<CardResponse>

    @Headers(
        "Content-Type:application/json",
        "cache-control:no-cache",
        "Postman-Token:0324ddd2-8bae-4f34-bf20-8d9dc781ac20")
//    @Multipart
    @POST("indian_voterid")
    suspend fun getVoterIdInfo(@Body ocrRequest: OcrRequest): Response<CardResponse>
//    suspend fun getPANInfo(@Part file: MultipartBody.Part): Response<ResponseBody>

}