package com.example.arthan.lead

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.arthan.R
import com.example.arthan.global.AppPreferences
import com.example.arthan.global.ArthanApp
import com.example.arthan.global.Crashlytics
import com.example.arthan.global.DOC_TYPE
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
import com.example.arthan.views.activities.FrontCameraActivity
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import kotlinx.android.synthetic.main.activity_upload_document.*
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.*
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
    private var mCardDataBack: CardResponse? = null
    private var mFrontImagePath: String? = null
    private var mBackImagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_document)

        loadImageIfExists()

        val code = intent.getIntExtra(DOC_TYPE, 0)
        /*    if(code==RequestCode.AadharCard)
            {
                btn_next.text="Save Aadhaar Back"
            }*/

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
       /* btn_next_front.setOnClickListener {
            val progressLoader = ProgrssLoader(this)
            progressLoader.showLoading()
            CoroutineScope(ioContext).launch {
                try {
                    var uploadFront: Deferred<Unit>? = null
                    if (intent?.getStringExtra("skip") != null) {
                        when (intent?.getIntExtra(DOC_TYPE, 0)) {

                            RequestCode.PanCard -> {
                                if (uploadFront == null && mFrontImagePath != null && RequestCode.PanCard == intent?.getIntExtra(
                                        DOC_TYPE,
                                        0
                                    )
                                )
                                    uploadToS3(mFrontImagePath!!, CardType.PANCard)

                            }
                            RequestCode.VoterCard -> {
                                if (uploadFront == null && mFrontImagePath != null && RequestCode.VoterCard == intent?.getIntExtra(
                                        DOC_TYPE,
                                        0
                                    )
                                )
                                    uploadToS3(mFrontImagePath!!, CardType.VoterIdCard)
                            }
                            RequestCode.AadharCard -> {
                                if (uploadFront == null && mFrontImagePath != null && RequestCode.AadharCard == intent?.getIntExtra(
                                        DOC_TYPE,
                                        0
                                    )
                                )
                                    uploadToS3(mFrontImagePath!!, CardType.AadharCardFront)
                            }
                            RequestCode.AadharCardBack -> {
                                if (uploadFront == null && mFrontImagePath != null && RequestCode.AadharCardBack == intent?.getIntExtra(
                                        DOC_TYPE,
                                        0
                                    )
                                )
                                    uploadToS3(mFrontImagePath!!, CardType.AadharCardBack)
                            }
                            RequestCode.PFP -> {
                                if (uploadFront == null && mFrontImagePath != null && RequestCode.PFP == intent?.getIntExtra(
                                        DOC_TYPE,
                                        0
                                    )
                                )
                                    uploadToS3(mFrontImagePath!!, CardType.PFP)
                            }
                            else -> {
                                null
                            }
                        }
                    } else {
                        uploadFront = mFrontImagePath?.let {
                            when (intent?.getIntExtra(DOC_TYPE, 0)) {

                                RequestCode.AadharCard -> {
                                    captureCardInfoAsync(it, CardType.AadharCardFront)
                                }
                                else -> {
                                    null
                                }
                            }
                        }
                    }
                    uploadFront?.await()
//

                    try {

                        withContext(uiContext) {
                            var continueResult = false
                            if (mCardData != null) {

                                when (intent?.getIntExtra(DOC_TYPE, 0)) {
                                    RequestCode.PanCard, RequestCode.AadharBackCard, RequestCode.AadharCardBack, RequestCode.AadharFrontCard, RequestCode.AadharCard, RequestCode.VoterCard -> {
                                        if (mCardData!!.status == "OK") {

                                            continueResult = true
                                        }
                                        if (intent?.getStringExtra("skip") != null) {
                                            continueResult = true
                                        }
                                    }
                                    else -> {
                                        continueResult = intent?.getStringExtra("skip") == null
                                    }
                                }
                            }

                            if (continueResult && (mCardData?.cardFrontUrl != null)) {
//                                finishActivity(progressLoader)
                                progressLoader.dismmissLoading()
                            } else {
                                progressLoader.dismmissLoading()

                            }

                        }
                    } catch (e: Exception) {
                        progressLoader.dismmissLoading()
                        Crashlytics.log(e.message)

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Crashlytics.log(e.message)

                    withContext(uiContext) { progressLoader.dismmissLoading() }
                }
            }
        }*/
        btn_next.setOnClickListener {
            val progressLoader = ProgrssLoader(this)
            progressLoader.showLoading()
            CoroutineScope(ioContext).launch {
                try {
                    var uploadFront: Deferred<Unit>? = null
                    if (intent?.getStringExtra("skip") != null) {
                        when (intent?.getIntExtra(DOC_TYPE, 0)) {

                            RequestCode.PanCard -> {
                                if (uploadFront == null && mFrontImagePath != null && RequestCode.PanCard == intent?.getIntExtra(
                                        DOC_TYPE,
                                        0
                                    )
                                )
                                    uploadToS3(mFrontImagePath!!, CardType.PANCard)

                            }
                            RequestCode.VoterCard -> {
                                if (uploadFront == null && mFrontImagePath != null && RequestCode.VoterCard == intent?.getIntExtra(
                                        DOC_TYPE,
                                        0
                                    )
                                )
                                    uploadToS3(mFrontImagePath!!, CardType.VoterIdCard)
                            }
                            RequestCode.AadharCard -> {
                                if (uploadFront == null && mFrontImagePath != null && RequestCode.AadharCard == intent?.getIntExtra(
                                        DOC_TYPE,
                                        0
                                    )
                                )
                                    uploadToS3(mFrontImagePath!!, CardType.AadharCardFront)
                            }
                            RequestCode.AadharCardBack -> {
                                if (uploadFront == null && mFrontImagePath != null && RequestCode.AadharCardBack == intent?.getIntExtra(
                                        DOC_TYPE,
                                        0
                                    )
                                )
                                    uploadToS3(mFrontImagePath!!, CardType.AadharCardBack)
                            }
                            RequestCode.PFP -> {
                                if (uploadFront == null && mFrontImagePath != null && RequestCode.PFP == intent?.getIntExtra(
                                        DOC_TYPE,
                                        0
                                    )
                                )
                                    uploadToS3(mFrontImagePath!!, CardType.PFP)
                            }
                            else -> {
                                null
                            }
                        }
                    } else {
                        uploadFront = mFrontImagePath?.let {
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
                                RequestCode.PFP -> {
                                    captureCardInfoAsync(it, CardType.PFP)
                                }
                                else -> {
                                    null
                                }
                            }
                        }
                    }
                    if (intent.getStringExtra("skip") != null) {
//                        if (uploadFront == null && mBackImagePath != null && RequestCode.AadharBackCard == intent?.getIntExtra(
                        if (uploadFront == null && mBackImagePath != null && RequestCode.AadharCardBack == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        )
                            uploadToS3(mBackImagePath!!, CardType.AadharCardBack)

                    } else {
                        val uploadBack: Deferred<Unit>? = mBackImagePath?.let {
                            captureCardInfoAsync(it, CardType.AadharCardBack)
                        }
                        if (RequestCode.AadharCardBack == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
//                            uploadFront?.await()
                            uploadBack?.await()
                        } else {
                            uploadFront?.await()
                         //   uploadBack?.await()
                        }

                    }

                    try {
                        if (uploadFront == null && mFrontImagePath != null && RequestCode.ApplicantPhoto == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.ApplicantPhoto)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.CrossedCheque == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.CrossedCheque)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.Passport == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.Passport)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.DrivingLicense == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.driverLicense)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.electricityBill == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.ElectricityBillProof)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.waterBill == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.waterBill)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.Agreement == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.agreement)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.Coc == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.coc)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.telephonebill == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.telephoneBill)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.AadharCardAddrProof == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.AadharCardAddrProof)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.SalesTaxRegistration == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.SalesTaxRegistration)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.VatOrder == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.VatOrder)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.LicenseissuedunderShop == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.LicenseissuedunderShop)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.EstablishmentAct == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.EstablishmentAct)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.CST == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.CST)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.VAT == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.VAT)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.GSTCert == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.GSTCert)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.CurrentACofbankStmt == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.CurrentACofbankStmt)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.SSIcertificate == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.SSIcertificate)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.LatestTelephoneBill == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.LatestTelephoneBill)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.ElectricityBillOfcAdd == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.ElectricityBillOfcAdd)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.BankStatement == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.BankStatement)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.LeaveandLicenceagreement == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.LeaveandLicenceagreement)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.Last2yearsITR == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.Last2yearsITR)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.Auditedbalancesheet == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.Auditedbalancesheet)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.SaleDeed == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.SaleDeed)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.ChainDocument == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.ChainDocument)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.PropertyTaxReceipt == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.PropertyTaxReceipt)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.ROR == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.ROR)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.NOC == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.NOC)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode._7by12 == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType._7by12)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.Mutation == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.Mutation)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.ROR == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.ROR)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.FerfarCertificate == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.FerfarCertificate)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.Others == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.Others)
                        } else if (uploadFront == null && mFrontImagePath != null && RequestCode.LoanDoc == intent?.getIntExtra(
                                DOC_TYPE,
                                0
                            )
                        ) {
                            uploadToS3(mFrontImagePath!!, CardType.LoanDoc)
                        }
                        withContext(uiContext) {
                            var continueResult = false
                            if (mCardData != null) {

                                when (intent?.getIntExtra(DOC_TYPE, 0)) {
                                    RequestCode.PanCard, RequestCode.AadharBackCard, RequestCode.AadharFrontCard, RequestCode.AadharCard, RequestCode.VoterCard -> {
                                        if (mCardData!!.status == "OK") {

                                            continueResult = true
                                        }
                                        if (intent?.getStringExtra("skip") != null) {
                                            continueResult = true
                                        }
                                    }
                                    else -> {
                                        continueResult = intent?.getStringExtra("skip") == null
                                    }
                                }
                            }

                            if (continueResult && (mCardData?.cardFrontUrl != null)) {
                                finishActivity(progressLoader)
                                progressLoader.dismmissLoading()
                            } else {
                                progressLoader.dismmissLoading()

                            }

                        }
                    } catch (e: Exception) {
                        progressLoader.dismmissLoading()
                        Crashlytics.log(e.message)

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Crashlytics.log(e.message)

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
//                        getIntent()?.getIntExtra(DOC_TYPE, 0) ?: 0
                         if (getIntent()?.getIntExtra(DOC_TYPE, 0) == RequestCode.AadharCard) {
                                 RequestCode.AadharFrontCard
//                             } /*else {
//                                 RequestCode.AadharBackCard
//                             }*/
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
            btn_next_front.visibility = View.GONE
            btn_take_picture?.visibility = View.VISIBLE
            btn_attach_document?.isEnabled = true
            Glide.with(this).clear(img_document_front)
            img_document_front?.setImageBitmap(null)
            mFrontImagePath = null
        }

        img_clear_front.visibility = View.GONE
        img_clear_back.visibility = View.GONE

        /*if (intent.getIntExtra(DOC_TYPE, 0) == RequestCode.AadharCard)
            fl_document_back.visibility = View.VISIBLE
        else*/
        fl_document_back.visibility = View.GONE

        val docName = when (intent.getIntExtra(DOC_TYPE, 0)) {
            RequestCode.PanCard -> "PAN Card"

            RequestCode.AadharCard -> "Aadhar Card"
            RequestCode.AadharCardBack -> "Aadhar Card Back"
            RequestCode.VoterCard -> "Voter ID"
            RequestCode.ApplicantPhoto -> "Applicant Photo_${intent.getStringExtra("applicant_type") ?: "PA"}"
            RequestCode.CrossedCheque -> "Crossed Cheque"
            RequestCode.PFP -> "Profile firm and promoters"
            else -> ""
        }
        txt_msg.text =
            getString(R.string.capture_the_original_pan_card_within_the_provided_box, docName)
    }

    private fun loadImageIfExists() {


        Glide.with(this).load(intent?.getStringExtra("show")).into(img_document_front)
        if (intent?.getStringExtra("show2") != null) {
            fl_document_back.visibility = View.VISIBLE
            Glide.with(this).load(intent?.getStringExtra("show2")).into(img_document_back)

        }
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
                resultIntent.putExtra(ArgumentKey.AadharDetailsBack, mCardDataBack)
            }
            RequestCode.AadharCardBack -> {
                resultIntent.putExtra(ArgumentKey.AadharDetails, mCardData)
                resultIntent.putExtra(ArgumentKey.AadharDetailsBack, mCardDataBack)
            }
            RequestCode.VoterCard -> {
                resultIntent.putExtra(ArgumentKey.VoterDetails, mCardData)
            }
            RequestCode.Passport -> {
                resultIntent.putExtra(ArgumentKey.Passport, mCardData)
            }
            RequestCode.DrivingLicense -> {
                resultIntent.putExtra(ArgumentKey.DrivingLicense, mCardData)
            }
            RequestCode.ApplicantPhoto -> {
                resultIntent.putExtra(ArgumentKey.ApplicantPhoto, mCardData)
            }
            RequestCode.CrossedCheque -> {
                resultIntent.putExtra(ArgumentKey.CrossedCheque, mCardData)
            }
            RequestCode.PFP -> {
                resultIntent.putExtra(ArgumentKey.PFP, mCardData)
            }
            RequestCode.waterBill -> {
                resultIntent.putExtra(ArgumentKey.waterBill, mCardData)
            }
            RequestCode.Agreement -> {
                resultIntent.putExtra(ArgumentKey.Agreement, mCardData)
            }
            RequestCode.Coc -> {
                resultIntent.putExtra(ArgumentKey.Coc, mCardData)
            }
            RequestCode.electricityBill -> {
                resultIntent.putExtra(ArgumentKey.electricityBill, mCardData)
            }
            RequestCode.telephonebill -> {
                resultIntent.putExtra(ArgumentKey.telephoneBill, mCardData)
            }
            RequestCode.AadharCardAddrProof -> {
                resultIntent.putExtra(ArgumentKey.AadharCardAddrProof, mCardData)
            }

            RequestCode.SalesTaxRegistration -> {
                resultIntent.putExtra(ArgumentKey.SalesTaxRegistration, mCardData)
            }
            RequestCode.VatOrder -> {
                resultIntent.putExtra(ArgumentKey.VatOrder, mCardData)
            }
            RequestCode.LicenseissuedunderShop -> {
                resultIntent.putExtra(ArgumentKey.LicenseissuedunderShop, mCardData)
            }
            RequestCode.EstablishmentAct -> {
                resultIntent.putExtra(ArgumentKey.EstablishmentAct, mCardData)
            }
            RequestCode.CST -> {
                resultIntent.putExtra(ArgumentKey.CST, mCardData)
            }
            RequestCode.VAT -> {
                resultIntent.putExtra(ArgumentKey.VAT, mCardData)
            }
            RequestCode.GSTCert -> {
                resultIntent.putExtra(ArgumentKey.GSTCert, mCardData)
            }
            RequestCode.CurrentACofbankStmt -> {
                resultIntent.putExtra(ArgumentKey.CurrentACofbankStmt, mCardData)
            }
            RequestCode.SSIcertificate -> {
                resultIntent.putExtra(ArgumentKey.SSIcertificate, mCardData)
            }

            RequestCode.LatestTelephoneBill -> {
                resultIntent.putExtra(ArgumentKey.LatestTelephoneBill, mCardData)
            }
            RequestCode.ElectricityBillOfcAdd -> {
                resultIntent.putExtra(ArgumentKey.ElectricityBillOfcAdd, mCardData)
            }
            RequestCode.BankStatement -> {
                resultIntent.putExtra(ArgumentKey.BankStatement, mCardData)
            }
            RequestCode.LeaveandLicenceagreement -> {
                resultIntent.putExtra(ArgumentKey.LeaveandLicenceagreement, mCardData)
            }
            RequestCode.Last2yearsITR -> {
                resultIntent.putExtra(ArgumentKey.Last2yearsITR, mCardData)
            }
            RequestCode.Auditedbalancesheet -> {
                resultIntent.putExtra(ArgumentKey.Auditedbalancesheet, mCardData)
            }
            RequestCode.SaleDeed -> {
                resultIntent.putExtra(ArgumentKey.SaleDeed, mCardData)
            }
            RequestCode.ChainDocument -> {
                resultIntent.putExtra(ArgumentKey.ChainDocument, mCardData)
            }
            RequestCode.PropertyTaxReceipt -> {
                resultIntent.putExtra(ArgumentKey.PropertyTaxReceipt, mCardData)
            }
            RequestCode.ROR -> {
                resultIntent.putExtra(ArgumentKey.ROR, mCardData)
            }
            RequestCode.NOC -> {
                resultIntent.putExtra(ArgumentKey.NOC, mCardData)
            }
            RequestCode._7by12 -> {
                resultIntent.putExtra(ArgumentKey._7by12, mCardData)
            }
            RequestCode.Mutation -> {
                resultIntent.putExtra(ArgumentKey.Mutation, mCardData)
            }
            RequestCode.FerfarCertificate -> {
                resultIntent.putExtra(ArgumentKey.FerfarCertificate, mCardData)
            }
            RequestCode.Others -> {
                resultIntent.putExtra(ArgumentKey.Others, mCardData)
            }
            RequestCode.LoanDoc -> {
                resultIntent.putExtra(ArgumentKey.LoanDoc, mCardData)
            }

        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun navigateToCamera(reqCode: Int) {

        if (reqCode.equals(RequestCode.ApplicantPhoto) && ArthanApp.getAppInstance().loginRole == "AM" && ArthanApp.getAppInstance().onboarded.toLowerCase() != "yes") {
            startActivityForResult(Intent(this, FrontCameraActivity::class.java).apply {
                putExtra(DOC_TYPE, reqCode)
                val dir = File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "Arthan"
                )/* val dir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Arthan"
            )*/
                if (!dir.exists())
                    dir.mkdirs()
                putExtra(
                    ArgumentKey.FilePath, when (reqCode) {
                        RequestCode.PanCard -> "${dir.absolutePath}/IMG_PAN_${
                            intent.getStringExtra(
                                "applicant_type"
                            ) ?: "PA"
                        }.jpg"
                        RequestCode.Passport -> "${dir.absolutePath}/passport_${
                            intent.getStringExtra(
                                "applicant_type"
                            ) ?: "PA"
                        }.jpg"
                        RequestCode.AadharFrontCard -> "${dir.absolutePath}/IMG_AADHAR_FRONT_${
                            intent.getStringExtra(
                                "applicant_type"
                            ) ?: "PA"
                        }.jpg"
                        RequestCode.AadharBackCard -> "${dir.absolutePath}/IMG_AADHAR_REAR_${
                            intent.getStringExtra(
                                "applicant_type"
                            ) ?: "PA"
                        }.jpg"
                        RequestCode.PFP -> "${dir.absolutePath}/PFP.jpg"
                        RequestCode.DrivingLicense -> "${dir.absolutePath}/driving_license.jpg"
                        RequestCode.VoterCard -> "${dir.absolutePath}/voterId_${
                            intent.getStringExtra(
                                "applicant_type"
                            ) ?: "PA"
                        }.jpg"

                        RequestCode.electricityBill -> "${dir.absolutePath}/electricityBill.jpg"
                        RequestCode.waterBill -> "${dir.absolutePath}/waterBill.jpg"
                        RequestCode.Agreement -> "${dir.absolutePath}/agreement.jpg"
                        RequestCode.Coc -> "${dir.absolutePath}/coc.jpg"
                        RequestCode.telephonebill -> "${dir.absolutePath}/telephonebill.jpg"
                        RequestCode.AadharCardAddrProof -> "${dir.absolutePath}/AadharCardAddrProof.jpg"

                        RequestCode.SalesTaxRegistration -> "${dir.absolutePath}/SalesTaxRegistration.jpg"
                        RequestCode.VatOrder -> "${dir.absolutePath}/VatOrder.jpg"
                        RequestCode.LicenseissuedunderShop -> "${dir.absolutePath}/LicenseissuedunderShop.jpg"
                        RequestCode.EstablishmentAct -> "${dir.absolutePath}/EstablishmentAct.jpg"
                        RequestCode.CST -> "${dir.absolutePath}/CST.jpg"
                        RequestCode.VAT -> "${dir.absolutePath}/VAT.jpg"
                        RequestCode.GSTCert -> "${dir.absolutePath}/GSTCert.jpg"
                        RequestCode.CurrentACofbankStmt -> "${dir.absolutePath}/CurrentACofbankStmt.jpg"
                        RequestCode.SSIcertificate -> "${dir.absolutePath}/SSIcertificate.jpg"

                        RequestCode.LatestTelephoneBill -> "${dir.absolutePath}/LatestTelephoneBill.jpg"
                        RequestCode.ElectricityBillOfcAdd -> "${dir.absolutePath}/ElectricityBillOfcAdd.jpg"
                        RequestCode.BankStatement -> "${dir.absolutePath}/BankStatement.jpg"
                        RequestCode.LeaveandLicenceagreement -> "${dir.absolutePath}/LeaveandLicenceagreement.jpg"


                        RequestCode.Last2yearsITR -> "${dir.absolutePath}/Last2yearsITR.jpg"
                        RequestCode.Auditedbalancesheet -> "${dir.absolutePath}/Auditedbalancesheet.jpg"
                        RequestCode.SaleDeed -> "${dir.absolutePath}/SaleDeed.jpg"
                        RequestCode.ChainDocument -> "${dir.absolutePath}/ChainDocument.jpg"
                        RequestCode.PropertyTaxReceipt -> "${dir.absolutePath}/PropertyTaxReceipt.jpg"
                        RequestCode.ROR -> "${dir.absolutePath}/ROR.jpg"
                        RequestCode.NOC -> "${dir.absolutePath}/NOC.jpg"
                        RequestCode._7by12 -> "${dir.absolutePath}/_7by12.jpg"
                        RequestCode.Mutation -> "${dir.absolutePath}/Mutation.jpg"
                        RequestCode.FerfarCertificate -> "${dir.absolutePath}/FerfarCertificate.jpg"
                        RequestCode.Others -> "${dir.absolutePath}/Others.jpg"
                        RequestCode.LoanDoc -> "${dir.absolutePath}/LoanDoc.jpg"

                        else -> "${dir.absolutePath}/IMG_VOTER.jpg"
                    }
                )
            }, reqCode)
        } else {
            startActivityForResult(Intent(this, CustomerCameraActivity::class.java).apply {
                putExtra(DOC_TYPE, reqCode)
                val dir = File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "Arthan"
                )/* val dir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Arthan"
            )*/
                if (!dir.exists())
                    dir.mkdirs()
                putExtra(
                    ArgumentKey.FilePath, when (reqCode) {
                        RequestCode.PanCard -> "${dir.absolutePath}/IMG_PAN_${
                            intent.getStringExtra(
                                "applicant_type"
                            ) ?: "PA"
                        }.jpg"
                        RequestCode.Passport -> "${dir.absolutePath}/passport_${
                            intent.getStringExtra(
                                "applicant_type"
                            ) ?: "PA"
                        }.jpg"
                        RequestCode.AadharFrontCard -> "${dir.absolutePath}/IMG_AADHAR_FRONT_${
                            intent.getStringExtra(
                                "applicant_type"
                            ) ?: "PA"
                        }.jpg"
                        RequestCode.AadharBackCard -> "${dir.absolutePath}/IMG_AADHAR_REAR_${
                            intent.getStringExtra(
                                "applicant_type"
                            ) ?: "PA"
                        }.jpg"
                        RequestCode.AadharCardBack -> "${dir.absolutePath}/IMG_AADHAR_REAR_${
                            intent.getStringExtra(
                                "applicant_type"
                            ) ?: "PA"
                        }.jpg"
                        RequestCode.PFP -> "${dir.absolutePath}/PFP.jpg"
                        RequestCode.DrivingLicense -> "${dir.absolutePath}/driving_license.jpg"
                        RequestCode.VoterCard -> "${dir.absolutePath}/voterId_${
                            intent.getStringExtra(
                                "applicant_type"
                            ) ?: "PA"
                        }.jpg"

                        RequestCode.electricityBill -> "${dir.absolutePath}/electricityBill.jpg"
                        RequestCode.waterBill -> "${dir.absolutePath}/waterBill.jpg"
                        RequestCode.Agreement -> {
                            if (intent.getStringExtra("recordType") == "AM") {
                                "${dir.absolutePath}/agreement_${ArthanApp.getAppInstance().loginUser}.jpg"
                            } else {
                                "${dir.absolutePath}/agreement.jpg"
                            }
                        }
                        RequestCode.Coc -> "${dir.absolutePath}/coc.jpg"
                        RequestCode.telephonebill -> "${dir.absolutePath}/telephonebill.jpg"
                        RequestCode.AadharCardAddrProof -> "${dir.absolutePath}/AadharCardAddrProof.jpg"

                        RequestCode.SalesTaxRegistration -> "${dir.absolutePath}/SalesTaxRegistration.jpg"
                        RequestCode.VatOrder -> "${dir.absolutePath}/VatOrder.jpg"
                        RequestCode.LicenseissuedunderShop -> "${dir.absolutePath}/LicenseissuedunderShop.jpg"
                        RequestCode.EstablishmentAct -> "${dir.absolutePath}/EstablishmentAct.jpg"
                        RequestCode.CST -> "${dir.absolutePath}/CST.jpg"
                        RequestCode.VAT -> "${dir.absolutePath}/VAT.jpg"
                        RequestCode.GSTCert -> "${dir.absolutePath}/GSTCert.jpg"
                        RequestCode.CurrentACofbankStmt -> "${dir.absolutePath}/CurrentACofbankStmt.jpg"
                        RequestCode.SSIcertificate -> "${dir.absolutePath}/SSIcertificate.jpg"

                        RequestCode.LatestTelephoneBill -> "${dir.absolutePath}/LatestTelephoneBill.jpg"
                        RequestCode.ElectricityBillOfcAdd -> "${dir.absolutePath}/ElectricityBillOfcAdd.jpg"
                        RequestCode.BankStatement -> "${dir.absolutePath}/BankStatement.jpg"
                        RequestCode.LeaveandLicenceagreement -> "${dir.absolutePath}/LeaveandLicenceagreement.jpg"


                        RequestCode.Last2yearsITR -> "${dir.absolutePath}/Last2yearsITR.jpg"
                        RequestCode.Auditedbalancesheet -> "${dir.absolutePath}/Auditedbalancesheet.jpg"
                        RequestCode.SaleDeed -> "${dir.absolutePath}/SaleDeed.jpg"
                        RequestCode.ChainDocument -> "${dir.absolutePath}/ChainDocument.jpg"
                        RequestCode.PropertyTaxReceipt -> "${dir.absolutePath}/PropertyTaxReceipt.jpg"
                        RequestCode.ROR -> "${dir.absolutePath}/ROR.jpg"
                        RequestCode.NOC -> "${dir.absolutePath}/NOC.jpg"
                        RequestCode._7by12 -> "${dir.absolutePath}/_7by12.jpg"
                        RequestCode.Mutation -> "${dir.absolutePath}/Mutation.jpg"
                        RequestCode.FerfarCertificate -> "${dir.absolutePath}/FerfarCertificate.jpg"
                        RequestCode.Others -> "${dir.absolutePath}/Others.jpg"
                        RequestCode.LoanDoc -> "${dir.absolutePath}/LoanDoc.jpg"

                        else -> "${dir.absolutePath}/IMG_VOTER.jpg"
                    }
                )
            }, reqCode)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RequestCode.AadharCardBack -> {
                    if (data?.hasExtra(ArgumentKey.FilePath) == true) {
//                        loadImage(data?.getStringExtra(ArgumentKey.FilePath), img_document_back)
                        loadImage(data?.getStringExtra(ArgumentKey.FilePath), img_document_front)
                        //  img_clear_back?.visibility = View.VISIBLE
                        changeButtonVisibility()
                        mBackImagePath = data?.getStringExtra(ArgumentKey.FilePath)
                    } else {
                        data?.data?.let { uri ->
//                            loadImage(this, img_document_back, uri, {
                            loadImage(this, img_document_front, uri, {
                                mBackImagePath = it
                                //      img_clear_back?.visibility = View.VISIBLE
                                changeButtonVisibility()
                            })
                        }
                    }
                }
                MyProfileActivity.PICK_IMAGE, RequestCode.PanCard, RequestCode.PFP, RequestCode.VoterCard,
                RequestCode.ApplicantPhoto, RequestCode.CrossedCheque, RequestCode.DrivingLicense, RequestCode.Passport,
                RequestCode.electricityBill,
                RequestCode.waterBill,
                RequestCode.Agreement,
                RequestCode.Coc,
                RequestCode.telephonebill,

                RequestCode.SalesTaxRegistration,
                RequestCode.VatOrder,
                RequestCode.LicenseissuedunderShop,
                RequestCode.EstablishmentAct,
                RequestCode.CST,
                RequestCode.VAT,
                RequestCode.GSTCert,
                RequestCode.CurrentACofbankStmt,
                RequestCode.SSIcertificate,

                RequestCode.LatestTelephoneBill,
                RequestCode.ElectricityBillOfcAdd,
                RequestCode.BankStatement,
                RequestCode.LeaveandLicenceagreement,


                RequestCode.Last2yearsITR,
                RequestCode.Auditedbalancesheet,
                RequestCode.SaleDeed,
                RequestCode.ChainDocument,
                RequestCode.PropertyTaxReceipt,
                RequestCode.ROR,
                RequestCode.NOC,
                RequestCode._7by12,
                RequestCode.Mutation,
                RequestCode.FerfarCertificate,
                RequestCode.Others,
                RequestCode.AadharCardAddrProof,
                RequestCode.LoanDoc,

                RequestCode.AadharFrontCard -> {
                    if (data?.hasExtra(ArgumentKey.FilePath) == true) {
                        mFrontImagePath = data?.getStringExtra(ArgumentKey.FilePath)
                        compressImage(
                            FileProvider.getUriForFile(
                                this, this.applicationContext?.packageName + ".provider",
                                File(mFrontImagePath)
                            )
                        )
                        loadImage(mFrontImagePath, img_document_front)
                        img_clear_front?.visibility = View.VISIBLE
                        changeButtonVisibility()
                        /* if (requestCode == RequestCode.AadharFrontCard) {
                            btn_next_front.visibility=View.VISIBLE
                            btn_next_front.text="Save Aadhaar Front"

                        }else if(requestCode==RequestCode.AadharBackCard)
                        {
                            changeButtonVisibility()
                            btn_next_front.text="Save Aadhaar Back"

                        }else if(requestCode!=RequestCode.AadharFrontCard)
                        {
                            changeButtonVisibility()


                        }*/
                    } else {
                        data?.data?.let { uri ->
                            compressImage(uri)
                            loadImage(this, img_document_front, uri, {
                                mFrontImagePath = it
                                img_clear_front?.visibility = View.VISIBLE
                                changeButtonVisibility()

                                /* if (requestCode == RequestCode.AadharFrontCard) {
                                    btn_next_front.visibility=View.VISIBLE

                                }
                                else if(requestCode==RequestCode.AadharBackCard)
                                {
                                    changeButtonVisibility()
                                    btn_next_front.text="Save Aadhaar Back"

                                }
                                else if(requestCode!=RequestCode.AadharFrontCard)
                                {
                                    changeButtonVisibility()

                                }*/
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
//            val apiService = RetrofitFactory.getMasterApiService()
                val apiService = RetrofitFactory.getApiService()
                try {
                    //val stream=  contentResolver?.openInputStream(shopUri!!)
                    val bm = BitmapFactory.decodeStream(FileInputStream(File(filePath)))
                    val base64 = BitmapUtils.getBase64(bm)
                    val file = File(filePath)
                    val requestBody: RequestBody =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file)
                    val multiPartBody =
                        MultipartBody.Part.createFormData("file", file.name, requestBody)
                    val map = HashMap<String, String>()
                    if (ArthanApp.getAppInstance().loginRole == "AM" && ArthanApp.getAppInstance().onboarded.toLowerCase() != "yes") {
                        map["amId"] = ArthanApp.getAppInstance().loginUser
                        map["loanId"] = ArthanApp.getAppInstance().loginUser
                    } else {
                        map["loanId"] =
                            AppPreferences.getInstance().getString(AppPreferences.Key.LoanId) ?: ""
                    }
                    map["customerId"] =
                        AppPreferences.getInstance().getString(AppPreferences.Key.CustomerId) ?: ""
                    map["userId"] = ArthanApp.getAppInstance().loginUser  //Logged in userId
                    map["imageBase64"] = base64
                    when (cardType) {
                        CardType.PANCard -> {
                            map["idType"] = "PAN"    //PAN/AF/AB/VOTER/ApplicantPhoto

//                        apiService.getPANCardInfo(OcrRequest(base64))
                        }
                        CardType.AadharCardFront -> {
                            map["idType"] = "AF"    //PAN/AF/AB/VOTER/ApplicantPhoto

                        }
                        CardType.AadharCardBack -> {
                            map["idType"] = "AB"    //PAN/AF/AB/VOTER/ApplicantPhoto

                        }
                        CardType.VoterIdCard -> {
                            map["idType"] = "VOTER"    //PAN/AF/AB/VOTER/ApplicantPhoto

                        }
                        else -> {
                            ""
                        }
                    }
                    map["applicantType"] =
                        intent.getStringExtra("applicant_type") ?: "PA"  //PA/CA/G
                    val response = apiService.getVerifyKYCDocs(map)


                    /*when (cardType) {
                        CardType.PANCard -> {
                            apiService.getVerifyKYCDocs(map)
    //                        apiService.getPANCardInfo(OcrRequest(base64))
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
                    }*/
                    if (response != null && response.isSuccessful && response.body()?.kycStatus != "InValid") {
                        if (cardType == CardType.PANCard && ArthanApp.getAppInstance().loginRole == "RM") {
                            AppPreferences.getInstance()
                                .addString(AppPreferences.Key.LoanId, response.body()?.loanId)
                            AppPreferences.getInstance().addString(
                                AppPreferences.Key.CustomerId,
                                response.body()?.customerId
                            )
                        }
                        if (ArthanApp.getAppInstance().loginRole == "AM") {
                            AppPreferences.getInstance()
                                .addString(AppPreferences.Key.LoanId, response.body()?.amId)
                            AppPreferences.getInstance().addString(
                                AppPreferences.Key.CustomerId,
                                response.body()?.customerId
                            )
                        }
                        if (cardType == CardType.AadharCardBack) {
                            mCardDataBack = response.body()

                        } else {
                            mCardData = response.body()
                        }
                        withContext(Dispatchers.Main) {
                            if (mCardData != null || mCardDataBack != null) {
                                uploadToS3(filePath, cardType)
                                /*  if (cardType == CardType.PANCard) {
                                      verifyCardDataAsync(filePath, cardType).await()
                                  } else {
                                      uploadToS3(filePath, cardType)
                                  }*/
                            } else
                                Toast.makeText(
                                    this@UploadDocumentActivity,
                                    "Please capture valid KYC",
                                    Toast.LENGTH_SHORT
                                ).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            if (response.body()?.kycStatus == "InValid") {
                                Toast.makeText(
                                    this@UploadDocumentActivity,
                                    response.body()?.apiMsg,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@UploadDocumentActivity,
                                    "Please Try again...",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } catch (e: HttpException) {
                    e.printStackTrace()
                    Crashlytics.log(e.message)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@UploadDocumentActivity,
                            "Please Try again...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Crashlytics.log(e.message)

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
                        CardType.AadharCardFront -> {
                            apiService.verifyAAdharCardInfo(mCardData?.results?.get(0)?.cardInfo?.cardNo)
                        }
                        CardType.AadharCardBack -> {
                            apiService.verifyAAdharCardInfo(mCardDataBack?.results?.get(0)?.cardInfo?.cardNo)
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
                                } else {
                                    if (cardType == CardType.PANCard) {

                                        Toast.makeText(
                                            this@UploadDocumentActivity,
                                            "Capture Valid PAN Card",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } else
                                        Toast.makeText(
                                            this@UploadDocumentActivity,
                                            "Please capture valid KYC",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    /*if (cardType == CardType.PANCard) {

                                        Toast.makeText(
                                            this@UploadDocumentActivity,
                                            "Case is rejected, since PAN is not Valid",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(Intent(this@UploadDocumentActivity,RMDashboardActivity::class.java))
                                       finish()
                                    } else
                                        Toast.makeText(
                                            this@UploadDocumentActivity,
                                            "Please Try again...",
                                            Toast.LENGTH_SHORT
                                        ).show()
    */
                                }
                            } else
                                Toast.makeText(
                                    this@UploadDocumentActivity,
                                    "Please Try again...",
                                    Toast.LENGTH_SHORT
                                ).show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Crashlytics.log(e.message)

                        }
                    }
                } catch (e: HttpException) {
                    e.printStackTrace()
                    Crashlytics.log(e.message)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@UploadDocumentActivity,
                            "Please Try again...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Crashlytics.log(e.message)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@UploadDocumentActivity,
                            "Please Try again...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        fun compressImage(imageUri: Uri): Uri? {


            val file = copyFile(this, imageUri)
            var path = ""
            if (file != null) {
                path = file.absolutePath
            }
//        val filePath = getRealPathFromURI(path)
            var scaledBitmap: Bitmap? = null
            val options = BitmapFactory.Options()
            //      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true
            var bmp = BitmapFactory.decodeFile(path, options)
            var actualHeight = options.outHeight
            var actualWidth = options.outWidth
            //      max Height and width values of the compressed image is taken as 816x612
            val maxHeight = 816.0f
            val maxWidth = 612.0f
            var imgRatio = actualWidth / actualHeight.toFloat()
            val maxRatio = maxWidth / maxHeight
            //      width and height values are set maintaining the aspect ratio of the image
            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight
                    actualWidth = (imgRatio * actualWidth).toInt()
                    actualHeight = maxHeight.toInt()
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth
                    actualHeight = (imgRatio * actualHeight).toInt()
                    actualWidth = maxWidth.toInt()
                } else {
                    actualHeight = maxHeight.toInt()
                    actualWidth = maxWidth.toInt()
                }
            }
            //      setting inSampleSize value allows to load a scaled down version of the original image
            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
            //      inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false
            //      this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true
            options.inInputShareable = true
            options.inTempStorage = ByteArray(16 * 1024)
            try { //          load the bitmap from its path
                bmp = BitmapFactory.decodeFile(path, options)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }
            try {
                scaledBitmap =
                    Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }
            val ratioX = actualWidth / options.outWidth.toFloat()
            val ratioY = actualHeight / options.outHeight.toFloat()
            val middleX = actualWidth / 2.0f
            val middleY = actualHeight / 2.0f
            val scaleMatrix = Matrix()
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
            val canvas = Canvas(scaledBitmap!!)
            canvas.setMatrix(scaleMatrix)
            canvas.drawBitmap(
                bmp,
                middleX - bmp.width / 2,
                middleY - bmp.height / 2,
                Paint(Paint.FILTER_BITMAP_FLAG)
            )
            //      check the rotation of the image and display it properly
            val exif: ExifInterface
            try {
                exif = ExifInterface(path)
                val orientation: Int = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0
                )
                Log.d("EXIF", "Exif: $orientation")
                val matrix = Matrix()
                if (orientation == 6) {
                    matrix.postRotate(90f)
                    Log.d("EXIF", "Exif: $orientation")
                } else if (orientation == 3) {
                    matrix.postRotate(180f)
                    Log.d("EXIF", "Exif: $orientation")
                } else if (orientation == 8) {
                    matrix.postRotate(270f)
                    Log.d("EXIF", "Exif: $orientation")
                }
                scaledBitmap = Bitmap.createBitmap(
                    scaledBitmap!!, 0, 0,
                    scaledBitmap.width, scaledBitmap.height, matrix,
                    true
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
            var out: FileOutputStream? = null
            val filename = file?.canonicalFile?.name
            try {
                out = FileOutputStream(filename);

                //          write the compressed bitmap at the destination specified by filename.
                scaledBitmap?.compress(Bitmap.CompressFormat.JPEG, 80, out);
            } catch (e: FileNotFoundException) {

            }

            /* val uri=FileProvider.getUriForFile(
                 this, this.applicationContext?.packageName + ".provider",
                 File(filename)
             )
     */
            /* val fileName = getOutputMediaFile()
             try {
                 if(fileName.exists())
                 {
                     fileName.delete()
                     fileName.createNewFile()
                 }
                 out = FileOutputStream(fileName)
                 //          write the compressed bitmap at the destination specified by filename.
                 scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
             } catch (e: FileNotFoundException) {
                 e.printStackTrace()
             }
             return FileProvider.getUriForFile(
                 this, this?.applicationContext?.packageName + ".provider",
                 fileName
             )*/

            return getUriFromBitmap(scaledBitmap, filename!!)
        }

        fun getUriFromBitmap(scaledBitmap: Bitmap?, fileName: String): Uri? {
            var out: FileOutputStream? = null
//        val fileName = fileName
            try {
                out = FileOutputStream(fileName, false)
                //          write the compressed bitmap at the destination specified by filename.
                scaledBitmap?.compress(Bitmap.CompressFormat.JPEG, 80, out)
//            imageview.setImageBitmap(scaledBitmap)
                val udi = FileProvider.getUriForFile(
                    this, this?.applicationContext?.packageName + ".provider",
                    File(fileName)
                )
//            Glide.with(this).load(udi).into(imageview)

                return udi
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            return null
        }

        fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int
        ): Int {
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                val heightRatio =
                    Math.round(height.toFloat() / reqHeight.toFloat())
                val widthRatio =
                    Math.round(width.toFloat() / reqWidth.toFloat())
                inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
            }
            val totalPixels = width * height.toFloat()
            val totalReqPixelsCap = reqWidth * reqHeight * 2.toFloat()
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }
            return inSampleSize
        }

        private suspend fun uploadToS3(
            filePath: String,
            cardType: CardType
        ) = suspendCoroutine<Unit> { continuation ->
            val fileList = mutableListOf(
                S3UploadFile(
                    File(filePath),
                    "${
                        if (AppPreferences.getInstance()
                                .getString(AppPreferences.Key.LoanId) != null && ArthanApp.getAppInstance().loginRole != "AM" && intent.getStringExtra(
                                "recordType"
                            ) != "AM"
                        ) {
                            AppPreferences.getInstance()
                                .getString(AppPreferences.Key.LoanId) + "_" + AppPreferences.getInstance()
                                .getString(AppPreferences.Key.CustomerId)
                        } else if (ArthanApp.getAppInstance().loginRole == "AM" && ArthanApp.getAppInstance().onboarded.toLowerCase() == "yes") {

                            AppPreferences.getInstance()
                                .getString(AppPreferences.Key.LoanId) + "_" + AppPreferences.getInstance()
                                .getString(AppPreferences.Key.CustomerId)
                        } else {
                            if (intent.getStringExtra("recordType") == "AM" || ArthanApp.getAppInstance().loginRole != "AM") {
                                ArthanApp.getAppInstance().loginUser
                            } else {
                                ArthanApp.getAppInstance().loginUser
                            }
                        }
                    }${
                        when (cardType) {


                            CardType.PANCard -> {
                                /* if( ArthanApp.getAppInstance().loginRole=="AM"){
                                     "_PAN"
          
                                 }else {*/
                                "_${intent.getStringExtra("applicant_type") ?: "PA"}_PAN"
//                       }
                            }
                            CardType.AadharCardFront -> {
                                /*  if( ArthanApp.getAppInstance().loginRole=="AM"){
                                      "_AADHAR_FRONT"
          
                                  }else {*/
                                "_${intent.getStringExtra("applicant_type") ?: "PA"}_AADHAR_FRONT"
//                        }
                            }
                            CardType.AadharCardBack -> {
                                /*   if( ArthanApp.getAppInstance().loginRole=="AM"){
                                       "_AADHAR_BACK"
           
                                   }else {*/
                                "_${intent.getStringExtra("applicant_type") ?: "PA"}_AADHAR_BACK"
                                //    }
                            }
                            CardType.VoterIdCard -> {
                                /*  if( ArthanApp.getAppInstance().loginRole=="AM"){
                                      "_VOTER"
          
                                  }else {*/
                                "_${intent.getStringExtra("applicant_type") ?: "PA"}_VOTER"
//                        }
                            }
                            CardType.ApplicantPhoto -> {
                                /*   if( ArthanApp.getAppInstance().loginRole=="AM"&&ArthanApp.getAppInstance().onboarded.toLowerCase() == "yes") {
                                       
                                       "_PHOTO"
           
                                   }else {*/
                                "_${intent.getStringExtra("applicant_type") ?: "PA"}_PHOTO"
//                        }
                            }
                            CardType.CrossedCheque -> {
                                "_CrossedCheque"
                            }
                            CardType.PFP -> {
                                "_PFP"
                            }
                            CardType.Passport -> {
                                "_PASSPORT"
                            }
                            CardType.driverLicense -> {
                                "_DrivingLicense"
                            }
                            CardType.telephoneBill -> {
                                "_Telephone_Bill"
                            }
                            CardType.AadharCardAddrProof -> {
                                "_AadharCardAddrProof"
                            }
                            CardType.waterBill -> {
                                "_Water_Bill"
                            }
                            CardType.agreement -> {

                                if (intent.getStringExtra("recordType") == "AM") {
                                    "Agreement" + "_" + intent.getStringExtra("amId")
                                } else {
                                    "_Agreement" + "_" + intent.getStringExtra("amId")
                                }
                            }
                            CardType.coc -> {
                                if (intent.getStringExtra("recordType") == "AM") {
                                    "Coc" + "_" + intent.getStringExtra("amId")
                                } else {
                                    "_Coc"
                                }
                            }
                            CardType.ElectricityBillProof -> {
                                "_Electricity_Bill"
                            }
                            CardType.SalesTaxRegistration -> {
                                "_SalesTaxRegistration"
                            }
                            CardType.VatOrder -> {
                                "_VatOrder"
                            }
                            CardType.LicenseissuedunderShop -> {
                                "_LicenseissuedunderShop"
                            }
                            CardType.EstablishmentAct -> {
                                "_EstablishmentAct"
                            }
                            CardType.CST -> {
                                "_CST"
                            }
                            CardType.VAT -> {
                                "_VAT"
                            }
                            CardType.GSTCert -> {
                                "_GSTCert"
                            }
                            CardType.CurrentACofbankStmt -> {
                                "_CurrentACofbankStmt"
                            }
                            CardType.SSIcertificate -> {
                                "_SSIcertificate"
                            }

                            CardType.LatestTelephoneBill -> {
                                "_LatestTelephoneBill"
                            }
                            CardType.ElectricityBillOfcAdd -> {
                                "_ElectricityBillOfcAdd"
                            }
                            CardType.BankStatement -> {
                                "_BankStatement"
                            }
                            CardType.LeaveandLicenceagreement -> {
                                "_LeaveandLicenceagreement"
                            }

                            CardType.Last2yearsITR -> {
                                "_Last2yearsITR"
                            }
                            CardType.Auditedbalancesheet -> {
                                "_Auditedbalancesheet"
                            }
                            CardType.SaleDeed -> {
                                "_SaleDeed"
                            }
                            CardType.ChainDocument -> {
                                "_ChainDocument"
                            }
                            CardType.PropertyTaxReceipt -> {
                                "_PropertyTaxReceipt"
                            }
                            CardType.ROR -> {
                                "_ROR"
                            }
                            CardType.NOC -> {
                                "_NOC"
                            }
                            CardType._7by12 -> {
                                "__7by12"
                            }
                            CardType.Mutation -> {
                                "_Mutation"
                            }
                            CardType.FerfarCertificate -> {
                                "_FerfarCertificate"
                            }
                            CardType.Others -> {
                                "_Others"
                            }
                            CardType.LoanDoc -> {
                                "_LoanDoc"
                            }
                        }
                    }.${File(filePath).extension}"
                )
            )
            S3Utility.getInstance()
                .uploadFile(fileList,
                    {
                        if (mCardData == null) {
                            mCardData = CardResponse("", "OK", "", "", null)
                        }
                        if (cardType != CardType.AadharCardBack) {
                            mCardData?.cardFrontUrl = fileList[0].url
                        } else {
                            mCardDataBack?.cardBackUrl = fileList[0].url
                        }

                        Log.e("URL", ":::: ${fileList[0].url}")

                        CoroutineScope(uiContext).launch {
                            btn_next.visibility = View.VISIBLE
                            mCardData!!.status = "OK"
                            /* if (cardType == CardType.PANCard) {
                                 if (mCardData?.status?.equals(
                                         ConstantValue.CardStatus.Ok,
                                         true
                                     ) == true
                                 )
                                     btn_next.visibility = View.VISIBLE
                                 else
                                     if (intent.getStringExtra("skip") == null)
                                         Toast.makeText(
                                             this@UploadDocumentActivity,
                                             "Please capture valid PAN Card",
                                             Toast.LENGTH_SHORT
                                         ).show()
                             } else */if (cardType == CardType.ApplicantPhoto) {
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
        object CrossedCheque : CardType()
        object PFP : CardType()
        object Passport : CardType()
        object driverLicense : CardType()
        object waterBill : CardType()
        object agreement : CardType()
        object coc : CardType()
        object ElectricityBillProof : CardType()
        object telephoneBill : CardType()
        object AadharCardAddrProof : CardType()
        object SalesTaxRegistration : CardType()
        object VatOrder : CardType()
        object LicenseissuedunderShop : CardType()
        object EstablishmentAct : CardType()
        object CST : CardType()
        object VAT : CardType()
        object GSTCert : CardType()
        object CurrentACofbankStmt : CardType()
        object SSIcertificate : CardType()

        object LatestTelephoneBill : CardType()
        object ElectricityBillOfcAdd : CardType()
        object BankStatement : CardType()
        object LeaveandLicenceagreement : CardType()

        object Last2yearsITR : CardType()
        object Auditedbalancesheet : CardType()
        object SaleDeed : CardType()
        object ChainDocument : CardType()
        object PropertyTaxReceipt : CardType()
        object ROR : CardType()
        object NOC : CardType()
        object _7by12 : CardType()
        object Mutation : CardType()
        object FerfarCertificate : CardType()
        object Others : CardType()
        object LoanDoc : CardType()

    }