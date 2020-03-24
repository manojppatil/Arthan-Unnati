package com.example.arthan.network

import com.example.arthan.liveness.LivenessService
import com.example.arthan.ocr.OcrService
import kotlinx.coroutines.withTimeout
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitFactory {

    private const val OCR_SERVICE_BASE_URL = "https://cloudapi.accuauth.com/ocr/"
    private const val FACE_DETECTION_SERVICE_BASE_URL = "https://cloudapi.accuauth.com/face/"
    private const val PAN_VERIFY_SERVICE_BASE_URL = "https://cloudapi.accuauth.com/verify/"
    private const val NET_BANKING_BASE_URL = "http://mycomp:8080/comp/api/"
    private const val SERVER_URL = "http://13.233.169.214:8080"
    private const val API_BASE_URL = "$SERVER_URL/artlos/"
    private const val MASTER_API_BASE_URL = "$SERVER_URL/JerseyDemos/"

    private const val RM_API= "$SERVER_URL/JerseyDemos/rest/GetData/"

    fun getLoggingInterceptor(): OkHttpClient = OkHttpClient.Builder().also {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        it.addInterceptor(logging)
        it.connectTimeout(120,TimeUnit.SECONDS)
    }.readTimeout(10, TimeUnit.MINUTES).build()

    fun getLivenessService(): LivenessService = Retrofit.Builder()
        .baseUrl(FACE_DETECTION_SERVICE_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(getLoggingInterceptor())
        .build()
        .create(LivenessService::class.java)

    fun getPanVerifyService(): LivenessService =
        Retrofit.Builder()
            .baseUrl(PAN_VERIFY_SERVICE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getLoggingInterceptor())
            .build()
            .create(LivenessService::class.java)

    fun getOCRService(): OcrService = Retrofit.Builder()
        .baseUrl(OCR_SERVICE_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(getLoggingInterceptor())
        .build()
        .create(OcrService::class.java)

    fun getNetBankingService(): NetBankingService = Retrofit.Builder()
        .baseUrl(NET_BANKING_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(getLoggingInterceptor())
        .build()
        .create(NetBankingService::class.java)

    fun getMasterApiService(): ApiService = Retrofit.Builder()
        .baseUrl(MASTER_API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(getLoggingInterceptor())
        .build().create(ApiService::class.java)

    fun getApiService(): ApiService = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(getLoggingInterceptor())
        .build().create(ApiService::class.java)

    fun getRMServiceService():ApiService= Retrofit.Builder()
        .baseUrl(RM_API)
        .addConverterFactory(GsonConverterFactory.create())
        .client(getLoggingInterceptor())
        .build().create(ApiService::class.java)
}