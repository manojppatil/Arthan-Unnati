package com.example.arthan.network

import android.content.Context
import android.util.Log
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.example.arthan.global.ArthanApp
import com.example.arthan.global.Crashlytics
import com.example.arthan.utils.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.CountDownLatch

class S3Utility {

    private var mS3Client: AmazonS3Client? = null
    private var mS3CredentialsProvider: CognitoCachingCredentialsProvider? = null
    private var mTransferUtility: TransferUtility? = null

    fun getTransferUtility(context: Context): TransferUtility? {
        val appContext = context.applicationContext
        if (mTransferUtility == null) {
            mTransferUtility =
                TransferUtility.builder().s3Client(getS3Client(appContext)).context(appContext)
                    .build()
        }
        return mTransferUtility
    }

    private fun getS3Client(context: Context): AmazonS3Client {
        val appContext = context.applicationContext
        if (mS3Client == null) {
            mS3Client =
                AmazonS3Client(getCredProvider(appContext), Region.getRegion(Regions.AP_SOUTH_1))
        }
        return mS3Client!!
    }

    private fun getCredProvider(context: Context): CognitoCachingCredentialsProvider {
        if (mS3CredentialsProvider == null) {
            mS3CredentialsProvider = CognitoCachingCredentialsProvider(
                context.applicationContext,
                "us-east-2:6182f6ea-79cd-4f6c-a747-beb8186cf602",
                Regions.US_EAST_2
            )
        }
        return mS3CredentialsProvider!!
    }

    fun uploadFile(
        fileList: MutableList<S3UploadFile>,
        onFileUploaded: (() -> Unit)? = null,
        onUploadError: ((message: String?) -> Unit)? = null
    ) {
        if (fileList.isEmpty()) {
            onFileUploaded?.invoke()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                for (file in fileList) {
                    uploadFileAsync(file, {
                        Log.i(TAG, "S3 url ----> $it")
                        file.url = it
                        onFileUploaded?.invoke()
                    }) {
                        Log.i(TAG, "S3 url upload error ----> $it")
                        onUploadError?.invoke(it)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Crashlytics.log(e.message)

            }
        }
    }

    private fun uploadFileAsync(
        uploadFile: S3UploadFile,
        onFileUploaded: ((url: String) -> Unit)? = null,
        onUploadError: ((error: String?) -> Unit)? = null
    ) {
        try {
            mTransferUtility?.upload(
                Constant.BUCKET_NAME,
                uploadFile.url,
                uploadFile.file
            )
                ?.setTransferListener(object : TransferListener {
                    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                        Log.i(TAG, "s# upload progress ----> ${bytesCurrent / bytesTotal}")
                    }

                    override fun onStateChanged(id: Int, state: TransferState?) {
                        if (state == TransferState.COMPLETED) {
                            var url =
                                mS3Client?.getUrl(
                                    Constant.BUCKET_NAME,
                                    uploadFile.url
                                )
                            onFileUploaded?.invoke(url?.toString() ?: "")
                        }
                    }

                    override fun onError(id: Int, ex: Exception?) {
                        ex?.printStackTrace()
                        onUploadError?.invoke(ex?.message)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            Crashlytics.log(e.message)

        }
    }

    companion object {

        private const val TAG = "S3Utility"
        private var sInstance: S3Utility? = null

        fun getInstance(context: Context): S3Utility {
            if (sInstance == null) {
                sInstance = S3Utility()
                sInstance?.getTransferUtility(context)
            }
            return sInstance!!
        }

        fun getInstance(): S3Utility {
            if (sInstance == null) {
                sInstance = S3Utility()
                sInstance?.getTransferUtility(ArthanApp.getAppInstance().baseContext)
            }
            return sInstance!!
        }
    }
}

data class S3UploadFile(val file: File, var url: String? = null)