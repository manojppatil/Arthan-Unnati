package com.example.arthan.liveness

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LivenessService {

    @Headers(
        "X-DF-API-ID:9ae9987221bd4eaab5daa2101f712fb0",
        "X-DF-API-SECRET:a3802951077149c68a474fc22b766db8",
        "Content-Type:application/json",
        "cache-control:no-cache",
        "Postman-Token:0324ddd2-8bae-4f34-bf20-8d9dc781ac20"
    )
    @POST("anti_hack")
    suspend fun detectFace(@Body livenessRequest: LivenessRequest): Response<LivenessResponse>

    @Headers(
        "X-DF-API-ID:9ae9987221bd4eaab5daa2101f712fb0",
        "X-DF-API-SECRET:a3802951077149c68a474fc22b766db8",
        "Content-Type:application/json",
        "cache-control:no-cache",
        "Postman-Token:0324ddd2-8bae-4f34-bf20-8d9dc781ac20"
    )
    @POST("indian_pan")
    suspend fun verifyPan(@Body verifyRequest: VerifyPanRequest): Response<VerifyCardResponse>

    @Headers(
        "X-DF-API-ID:9ae9987221bd4eaab5daa2101f712fb0",
        "X-DF-API-SECRET:a3802951077149c68a474fc22b766db8",
        "Content-Type:application/json",
        "cache-control:no-cache",
        "Postman-Token:0324ddd2-8bae-4f34-bf20-8d9dc781ac20"
    )
    @POST("indian_pan")
    suspend fun verifyAdhaeCard(@Body verifyRequest: VerifyAadhareRequest): Response<VerifyCardResponse>

    @Headers(
        "X-DF-API-ID:9ae9987221bd4eaab5daa2101f712fb0",
        "X-DF-API-SECRET:a3802951077149c68a474fc22b766db8",
        "Content-Type:application/json",
        "cache-control:no-cache",
        "Postman-Token:0324ddd2-8bae-4f34-bf20-8d9dc781ac20"
    )
    @POST("indian_pan")
    suspend fun verifyVoterIdCard(@Body verifyRequest: VerifyVoterIdRequest): Response<VerifyVoterCardResponse>
}

