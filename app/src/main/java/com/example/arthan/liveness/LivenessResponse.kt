package com.example.arthan.liveness

import com.google.gson.annotations.SerializedName

data class LivenessResponse(@SerializedName("request_id") val requestId: String,
                            @SerializedName("status") val status: String,
                            @SerializedName("score") val score: Float)