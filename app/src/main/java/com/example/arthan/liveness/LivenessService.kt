package com.example.arthan.liveness

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LivenessService {

    @Headers(
        "Content-Type:application/json",
        "cache-control:no-cache",
        "Postman-Token:0324ddd2-8bae-4f34-bf20-8d9dc781ac20"
    )
    @POST("anti_hack")
    suspend fun detectFace(@Body livenessRequest: LivenessRequest): Response<LivenessResponse>

    @Headers(
        "Content-Type:application/json",
        "cache-control:no-cache",
        "Postman-Token:0324ddd2-8bae-4f34-bf20-8d9dc781ac20"
    )
    @POST("indian_pan")
    suspend fun verifyPan(@Body verifyRequest: VerifyPanRequest): Response<VerifyCardResponse>

    @Headers(
        "Content-Type:application/json",
        "cache-control:no-cache",
        "Postman-Token:0324ddd2-8bae-4f34-bf20-8d9dc781ac20"
    )
    @POST("indian_pan")
    suspend fun verifyAdhaeCard(@Body verifyRequest: VerifyAadhareRequest): Response<VerifyCardResponse>

    @Headers(
        "Content-Type:application/json",
        "cache-control:no-cache",
        "Postman-Token:0324ddd2-8bae-4f34-bf20-8d9dc781ac20"
    )
    @POST("indian_pan")
    suspend fun verifyVoterIdCard(@Body verifyRequest: VerifyVoterIdRequest): Response<VerifyVoterCardResponse>
}

