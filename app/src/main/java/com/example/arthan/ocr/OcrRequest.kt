package com.example.arthan.ocr

import com.google.gson.annotations.SerializedName
import java.io.File

data class OcrRequest (@SerializedName("imageBase64") val imageBase64: String /*var file : File*/)