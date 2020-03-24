package com.example.arthan.liveness

import com.google.gson.annotations.SerializedName

data class LivenessRequest(@SerializedName("image_base64") val imageBase64: String)