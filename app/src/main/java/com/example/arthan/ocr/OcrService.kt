package com.example.arthan.ocr

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface OcrService {

    @Headers("X-DF-API-ID:9ae9987221bd4eaab5daa2101f712fb0",
        "X-DF-API-SECRET:a3802951077149c68a474fc22b766db8",
        "Content-Type:application/json",
        "cache-control:no-cache",
        "Postman-Token:0324ddd2-8bae-4f34-bf20-8d9dc781ac20")
//    @Multipart
    @POST("indian_card")
    suspend fun getIndianCardInfo(@Body ocrRequest: OcrRequest): Response<CardResponse>

    @Headers("X-DF-API-ID:9ae9987221bd4eaab5daa2101f712fb0",
        "X-DF-API-SECRET:a3802951077149c68a474fc22b766db8",
        "Content-Type:application/json",
        "cache-control:no-cache",
        "Postman-Token:0324ddd2-8bae-4f34-bf20-8d9dc781ac20")
//    @Multipart
    @POST("indian_voterid")
    suspend fun getVoterIdInfo(@Body ocrRequest: OcrRequest): Response<CardResponse>
//    suspend fun getPANInfo(@Part file: MultipartBody.Part): Response<ResponseBody>

}