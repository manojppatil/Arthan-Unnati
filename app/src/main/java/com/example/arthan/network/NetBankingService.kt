package com.example.arthan.network

import com.example.arthan.model.BankindDocUploadRequest
import com.example.arthan.model.UploadBankingDocResponse
import retrofit2.Response
import retrofit2.http.*
import java.util.*

interface NetBankingService {

    @GET("getReport")
    suspend fun generateReport(@Query("reqId") reqId: String): Response<Objects>

    @Headers("Content-Type:application/json")
    @POST("upload")
    suspend fun uploadReport(@Body request: BankindDocUploadRequest): Response<UploadBankingDocResponse>

}