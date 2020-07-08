package com.example.arthan.network

import com.example.arthan.global.ArthanApp
import com.example.arthan.liveness.LivenessService
import com.example.arthan.ocr.OcrService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


object RetrofitFactory {


    private const val OCR_SERVICE_BASE_URL = "https://cloudapi.accuauth.com/ocr/"
    private const val FACE_DETECTION_SERVICE_BASE_URL = "https://cloudapi.accuauth.com/face/"
    private const val PAN_VERIFY_SERVICE_BASE_URL = "https://cloudapi.accuauth.com/verify/"
    private const val NET_BANKING_BASE_URL = "http://mycomp:8080/comp/api/"
    private const val SERVER_URL = "http://13.233.169.214:8080" //debug
    //    private const val SERVER_URL = "http://13.6.237.112:8080"//prod
//    private const val SERVER_URL = "https://arthanfin.com"//prod
//    private const val SERVER_URL = "https://www.glairo.com"
    private const val API_BASE_URL = "$SERVER_URL/artlos/"
    private const val MASTER_API_BASE_URL = "$SERVER_URL/JerseyDemos/"

    private const val RM_API= "$SERVER_URL/JerseyDemos/rest/GetData/"

    private const val AM_API= "$SERVER_URL/JerseyDemos/rest/"

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
        .client(getSSLHttpClient())
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
        .client(getSSLHttpClient())
        .build()
        .create(NetBankingService::class.java)

    fun getMasterApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl(MASTER_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getLoggingInterceptor())
            .client(getSSLHttpClient())
            .build().create(ApiService::class.java)
    }

    fun getApiService(): ApiService {


        return Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getLoggingInterceptor())
            .client(getSSLHttpClient())
            .build()
            .create(ApiService::class.java)
    }

    fun getSSLHttpClient(): OkHttpClient {
        val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
        val cert: InputStream = ArthanApp.getAppInstance()
            .applicationContext.resources.openRawResource(com.example.arthan.R.raw.certi)
        val ca: Certificate
        ca = cert.use { cert ->
            cf.generateCertificate(cert)
        }

        // creating a KeyStore containing our trusted CAs
        // creating a KeyStore containing our trusted CAs
        val keyStoreType: String = KeyStore.getDefaultType()
        val keyStore: KeyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", ca)

        // creating a TrustManager that trusts the CAs in our KeyStore
        // creating a TrustManager that trusts the CAs in our KeyStore
        val tmfAlgorithm: String = TrustManagerFactory.getDefaultAlgorithm()
        val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm)
        tmf.init(keyStore)

        // creating an SSLSocketFactory that uses our TrustManager
        // creating an SSLSocketFactory that uses our TrustManager
        val sslContext: SSLContext = SSLContext.getInstance("TLS")
        sslContext.init(null, tmf.trustManagers, null)

        // creating an OkHttpClient that uses our SSLSocketFactory
        // creating an OkHttpClient that uses our SSLSocketFactory
        val untrustedClient = OkHttpClient.Builder()
        untrustedClient.sslSocketFactory(
            sslContext.socketFactory,
            tmf.trustManagers[0] as X509TrustManager
        )

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        untrustedClient.addInterceptor(logging)
        untrustedClient.connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.MINUTES)
        return untrustedClient.build()

    }
    fun getRMServiceService():ApiService= Retrofit.Builder()
        .baseUrl(RM_API)
        .addConverterFactory(GsonConverterFactory.create())
        .client(getLoggingInterceptor())
        .client(getSSLHttpClient())
        .build().create(ApiService::class.java)


    /*AM service*/
    fun getAMService():ApiService= Retrofit.Builder()
        .baseUrl(AM_API)
        .addConverterFactory(GsonConverterFactory.create())
        .client(getLoggingInterceptor())
        .client(getSSLHttpClient())
        .build().create(ApiService::class.java)
}