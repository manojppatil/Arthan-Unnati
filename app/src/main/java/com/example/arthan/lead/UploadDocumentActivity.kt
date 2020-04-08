package com.example.arthan.lead

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.arthan.R
import com.example.arthan.global.*
import com.example.arthan.liveness.VerifyCardResponse
import com.example.arthan.liveness.VerifyVoterCardResponse
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.network.S3UploadFile
import com.example.arthan.network.S3Utility
import com.example.arthan.ocr.CardResponse
import com.example.arthan.ocr.OcrRequest
import com.example.arthan.profile.MyProfileActivity
import com.example.arthan.utils.*
import com.example.arthan.views.activities.CustomerCameraActivity
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import kotlinx.android.synthetic.main.activity_upload_document.*
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileInputStream
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UploadDocumentActivity : AppCompatActivity(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private val ioContext: CoroutineContext
        get() = Dispatchers.IO
    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    private var mCardData: CardResponse? = null
    private var mFrontImagePath: String? = null
    private var mBackImagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_document)

        btn_take_picture.setOnClickListener {
            val request = permissionsBuilder(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).build()
            request.listeners {
                onAccepted {
                    val code = intent.getIntExtra(DOC_TYPE, 0)
                    navigateToCamera(
                        if (code == RequestCode.AadharCard) {
                            if (mFrontImagePath == null) RequestCode.AadharFrontCard
                            else RequestCode.AadharBackCard
                        } else {
                            code
                        }
                    )
                }
                onDenied {
                }
                onPermanentlyDenied {
                }
            }
            request.send()
        }
        btn_next.setOnClickListener {

            val progressLoader = ProgrssLoader(this)
            progressLoader.showLoading()
            CoroutineScope(ioContext).launch {
                try {
                    val uploadFront: Deferred<Unit>? = mFrontImagePath?.let {
                        when (intent?.getIntExtra(DOC_TYPE, 0)) {
                            RequestCode.PanCard -> {
                                captureCardInfoAsync(it, CardType.PANCard)
                            }
                            RequestCode.VoterCard -> {
                                captureCardInfoAsync(it, CardType.VoterIdCard)
                            }
                            RequestCode.AadharCard -> {
                                captureCardInfoAsync(it, CardType.AadharCardFront)
                            }
                            else -> {
                                null
                            }
                        }
                    }
                    val uploadBack: Deferred<Unit>? = mBackImagePath?.let {
                        captureCardInfoAsync(it, CardType.AadharCardBack)
                    }
                    uploadFront?.await()
                    uploadBack?.await()
                    if (uploadFront == null && mFrontImagePath != null) {
                        uploadToS3(mFrontImagePath!!, CardType.ApplicantPhoto)
                    }
                    withContext(uiContext) {
                        finishActivity(progressLoader)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(uiContext) { progressLoader.dismmissLoading() }
                }
            }
        }
        btn_attach_document.setOnClickListener {
            val request = permissionsBuilder(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).build()
            request.listeners {
                onAccepted {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(
                        Intent.createChooser(intent, "Select Picture"),
                        if (getIntent()?.getIntExtra(DOC_TYPE, 0) == RequestCode.AadharCard) {
                            if (mFrontImagePath == null) {
                                RequestCode.AadharFrontCard
                            } else {
                                RequestCode.AadharBackCard
                            }
                        } else {
                            getIntent()?.getIntExtra(DOC_TYPE, 0) ?: 0
                        }
                    )
                }
                onDenied {
                    btn_take_picture?.isEnabled = false
                }
                onPermanentlyDenied {
                }
            }
            request.send()
        }

        img_clear_back.setOnClickListener {
            img_clear_back?.visibility = View.GONE
            btn_next?.visibility = View.GONE
            btn_take_picture?.visibility = View.VISIBLE
            btn_attach_document?.isEnabled = true
            Glide.with(this).clear(img_document_front)
            img_document_back?.setImageBitmap(null)
            mBackImagePath = null
        }
        img_clear_front.setOnClickListener {
            img_clear_front?.visibility = View.GONE
            btn_next?.visibility = View.GONE
            btn_take_picture?.visibility = View.VISIBLE
            btn_attach_document?.isEnabled = true
            Glide.with(this).clear(img_document_front)
            img_document_front?.setImageBitmap(null)
            mFrontImagePath = null
        }

        img_clear_front.visibility = View.GONE
        img_clear_back.visibility = View.GONE

        if (intent.getIntExtra(DOC_TYPE, 0) == RequestCode.AadharCard)
            fl_document_back.visibility = View.VISIBLE
        else
            fl_document_back.visibility = View.GONE

        val docName = when (intent.getIntExtra(DOC_TYPE, 0)) {
            RequestCode.PanCard -> "PAN Card"
            RequestCode.AadharCard -> "Aadhar Card"
            RequestCode.VoterCard -> "Voter ID"
            RequestCode.ApplicantPhoto -> "Applicant Photo"
            else -> ""
        }
        txt_msg.text =
            getString(R.string.capture_the_original_pan_card_within_the_provided_box, docName)
    }

    private fun finishActivity(progressLoader: ProgrssLoader?) {
        progressLoader?.dismmissLoading()
        val resultIntent = Intent()
        //resultIntent.putExtra("document_uri",shopUri)
        when (intent?.getIntExtra(DOC_TYPE, 0)) {
            RequestCode.PanCard -> {
                resultIntent.putExtra(ArgumentKey.PanDetails, mCardData)
            }
            RequestCode.AadharCard -> {
                resultIntent.putExtra(ArgumentKey.AadharDetails, mCardData)
            }
            RequestCode.VoterCard -> {
                resultIntent.putExtra(ArgumentKey.VoterDetails, mCardData)
            }
            RequestCode.ApplicantPhoto -> {
                resultIntent.putExtra(ArgumentKey.ApplicantPhoto, mCardData)
            }
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun navigateToCamera(reqCode: Int) {
        startActivityForResult(Intent(this, CustomerCameraActivity::class.java).apply {
            putExtra(DOC_TYPE, reqCode)
            val dir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Arthan"
            )
            if (!dir.exists())
                dir.mkdirs()
            putExtra(
                ArgumentKey.FilePath, when (reqCode) {
                    RequestCode.PanCard -> "${dir.absolutePath}/IMG_PAN.jpg"
                    RequestCode.AadharFrontCard -> "${dir.absolutePath}/IMG_AADHAR_FRONT.jpg"
                    RequestCode.AadharBackCard -> "${dir.absolutePath}/IMG_AADHAR_REAR.jpg"
                    PFP -> "${dir.absolutePath}/PFP.jpg"
                    else -> "${dir.absolutePath}/IMG_VOTER.jpg"
                }
            )
        }, reqCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                MyProfileActivity.PICK_IMAGE, RequestCode.PanCard, RequestCode.VoterCard, RequestCode.ApplicantPhoto, RequestCode.AadharFrontCard -> {
                    if (data?.hasExtra(ArgumentKey.FilePath) == true) {
                        mFrontImagePath = data?.getStringExtra(ArgumentKey.FilePath)
                        loadImage(mFrontImagePath, img_document_front)
                        img_clear_front?.visibility = View.VISIBLE
                        if (requestCode != RequestCode.AadharFrontCard) {
                            changeButtonVisibility()
                        }
                    } else {
                        data?.data?.let { uri ->
                            loadImage(this, img_document_front, uri, {
                                mFrontImagePath = it
                                img_clear_front?.visibility = View.VISIBLE
                                if (requestCode != RequestCode.AadharFrontCard) {
                                    changeButtonVisibility()
                                }
                            })
                        }

                    }
                }
                RequestCode.AadharBackCard -> {
                    if (data?.hasExtra(ArgumentKey.FilePath) == true) {
                        loadImage(data?.getStringExtra(ArgumentKey.FilePath), img_document_back)
                        img_clear_back?.visibility = View.VISIBLE
                        changeButtonVisibility()
                        mBackImagePath = data?.getStringExtra(ArgumentKey.FilePath)
                    } else {
                        data?.data?.let { uri ->
                            loadImage(this, img_document_back, uri, {
                                mBackImagePath = it
                                img_clear_back?.visibility = View.VISIBLE
                                changeButtonVisibility()
                            })
                        }
                    }
                }
            }
        }
    }

    private fun loadImage(filePath: String?, imageView: ImageView) {
        Glide.with(this)
            .load(filePath)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(imageView)
    }

    private fun changeButtonVisibility() {
        btn_take_picture?.visibility = View.GONE
        btn_attach_document?.isEnabled = false
        btn_next?.visibility = View.VISIBLE
    }

    private fun captureCardInfoAsync(
        filePath: String,
        cardType: CardType
    ): Deferred<Unit> =
        async(ioContext) {
            //  if(shopUri != null) {
            val apiService = RetrofitFactory.getMasterApiService()
            try {
                //val stream=  contentResolver?.openInputStream(shopUri!!)
                val bm = BitmapFactory.decodeStream(FileInputStream(File(filePath)))
                val base64 = BitmapUtils.getBase64(bm)
                val file = File(filePath)
                val requestBody: RequestBody =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file)
                val multiPartBody =
                    MultipartBody.Part.createFormData("file", file.name, requestBody)
                val response = when (cardType) {
                    CardType.PANCard -> {
                        apiService.getPANCardInfo(OcrRequest(base64))
                    }
                    CardType.AadharCardFront, CardType.AadharCardBack -> {
                        apiService.getAadharCardInfo(OcrRequest(base64))
                    }
                    CardType.VoterIdCard -> {
                        apiService.getVoterCardInfo(OcrRequest(base64))
                    }
                    else -> {
                        null
                    }
                }
                if (response != null && response.isSuccessful) {
                    mCardData = response.body()
                    if (mCardData != null) {
                        if (cardType == CardType.PANCard) {
                            verifyCardDataAsync(filePath, cardType).await()
                        } else {
                            uploadToS3(filePath, cardType)
                        }
                    } else
                        Toast.makeText(
                            this@UploadDocumentActivity,
                            "Please Try again...",
                            Toast.LENGTH_SHORT
                        ).show()
                } else
                    Toast.makeText(
                        this@UploadDocumentActivity,
                        "Please Try again...",
                        Toast.LENGTH_SHORT
                    ).show()
            } catch (e: HttpException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@UploadDocumentActivity,
                        "Please Try again...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@UploadDocumentActivity,
                        "Please Try again...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    private fun verifyCardDataAsync(
        filePath: String,
        cardType: CardType
    ): Deferred<Unit> =
        async(ioContext) {
            val apiService = RetrofitFactory.getMasterApiService()
            try {
                val response = when (cardType) {
                    CardType.PANCard -> {
                        apiService.verifyPANCardInfo(hashMapOf<String, String?>().apply {
                            put("pan", mCardData?.results?.get(0)?.cardInfo?.cardNo)
                        })
                    }
                    CardType.AadharCardFront, CardType.AadharCardBack -> {
                        apiService.verifyAAdharCardInfo(mCardData?.results?.get(0)?.cardInfo?.cardNo)
                    }
                    CardType.VoterIdCard -> {
                        apiService.verifyVoterIdCardInfo(mCardData?.results?.get(0)?.cardInfo?.cardNo)
                    }
                    else -> {
                        null
                    }
                }
                withContext(Dispatchers.Main) {
                    try {
                        if (response != null && response.isSuccessful) {
                            val verifyResult = when (cardType) {
                                CardType.VoterIdCard -> {
                                    response.body()
                                }
                                else -> {
                                    response.body()
                                }
                            }
                            val result = (verifyResult as? VerifyVoterCardResponse)?.result
                            if ((verifyResult is VerifyVoterCardResponse && verifyResult.result != null) ||
                                (verifyResult is VerifyCardResponse && verifyResult.result != null)
                            ) {

                                uploadToS3(filePath, cardType)

                            } else
                                Toast.makeText(
                                    this@UploadDocumentActivity,
                                    "Please Try again...",
                                    Toast.LENGTH_SHORT
                                ).show()
                        } else
                            Toast.makeText(
                                this@UploadDocumentActivity,
                                "Please Try again...",
                                Toast.LENGTH_SHORT
                            ).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: HttpException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@UploadDocumentActivity,
                        "Please Try again...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@UploadDocumentActivity,
                        "Please Try again...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    private suspend fun uploadToS3(
        filePath: String,
        cardType: CardType
    ) = suspendCoroutine<Unit> { continuation ->

        val fileList = mutableListOf(
            S3UploadFile(
                File(filePath),
                "${AppPreferences.getInstance()
                    .getString(AppPreferences.Key.LoanId)}${when (cardType) {
                    CardType.PANCard -> {
                        //  "_PA_PAN"
                        "_PAN"
                    }
                    CardType.AadharCardFront -> {
                        "_AADHAR_FRONT"
                    }
                    CardType.AadharCardBack -> {
                        "_AADHAR_BACK"
                    }
                    CardType.VoterIdCard -> {
                        "_VOTER"
                    }
                    CardType.ApplicantPhoto -> {
                        "_PHOTO"
                    }
                }
                }.${File(filePath).extension}"
            )
        )
        S3Utility.getInstance()
            .uploadFile(fileList,
                {
                    if (mCardData == null) {
                        mCardData = CardResponse("", "", "", "", null)
                    }
                    if (cardType != CardType.AadharCardBack) {
                        mCardData?.cardFrontUrl = fileList[0].url
                    } else {
                        mCardData?.cardBackUrl = fileList[0].url
                    }

                    Log.e("URL", ":::: ${fileList[0].url}")

                    CoroutineScope(uiContext).launch {
                        if (cardType == CardType.PANCard) {
                            if (mCardData?.status?.equals(
                                    ConstantValue.CardStatus.Ok,
                                    true
                                ) == true
                            )
                                btn_next.visibility = View.VISIBLE
                            else
                                Toast.makeText(
                                    this@UploadDocumentActivity,
                                    "Please capture valid PAN Card",
                                    Toast.LENGTH_SHORT
                                ).show()
                        } else if (cardType == CardType.ApplicantPhoto) {
                            btn_next.visibility = View.VISIBLE
                        } else {
                            btn_next.visibility = View.VISIBLE
                        }
                    }
                    continuation.resume(Unit)
                }
            ) {
                Toast.makeText(
                    this@UploadDocumentActivity,
                    "$it",
                    Toast.LENGTH_SHORT
                ).show()
                continuation.resumeWithException(Exception(it))
            }
    }
}

sealed class CardType {
    object PANCard : CardType()
    object AadharCardFront : CardType()
    object AadharCardBack : CardType()
    object VoterIdCard : CardType()
    object ApplicantPhoto : CardType()
}