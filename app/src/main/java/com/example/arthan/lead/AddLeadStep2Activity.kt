package com.example.arthan.lead

import android.R.attr.scaleHeight
import android.R.attr.scaleWidth
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.ScaleDrawable
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.arthan.R
import com.example.arthan.global.AppPreferences
import com.example.arthan.global.DOC_TYPE
import com.example.arthan.lead.model.postdata.KYCPostData
import com.example.arthan.ocr.CardInfo
import com.example.arthan.ocr.CardResponse
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.utils.RequestCode
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_add_lead_step2.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext


class AddLeadStep2Activity : BaseActivity(), View.OnClickListener, CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    var mPanCardData: CardResponse? = null
    var mKYCPostData: KYCPostData? = null

    override fun screenTitle() = "KYC Details"

    override fun onToolbarBackPressed() = onBackPressed()

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.txt_pan_card -> {
                startActivityForResult(Intent(this, UploadDocumentActivity::class.java).apply {
                    putExtra(DOC_TYPE, RequestCode.PanCard)
                }, RequestCode.PanCard)
            }
            R.id.txt_aadhar_card -> {
                startActivityForResult(Intent(this, UploadDocumentActivity::class.java).apply {
                    putExtra(DOC_TYPE, RequestCode.AadharCard)
                }, RequestCode.AadharCard)
            }
            R.id.txt_voter_id -> {
                startActivityForResult(Intent(this, UploadDocumentActivity::class.java).apply {
                    putExtra(DOC_TYPE, RequestCode.VoterCard)
                }, RequestCode.VoterCard)
            }
            R.id.txt_applicant_phot -> {
                startActivityForResult(Intent(this, UploadDocumentActivity::class.java).apply {
                    putExtra(DOC_TYPE, RequestCode.ApplicantPhoto)
                }, RequestCode.ApplicantPhoto)
            }
            R.id.btn_next -> {
                startActivity(Intent(this, PersonalInformationActivity::class.java).also {
                    it.putExtra("PAN_DATA", mKYCPostData)
                    it.putExtra("APPLICANT_PHOTO",applicantPhoto)
                })
            }
        }
    }

    override fun contentView() = R.layout.activity_add_lead_step2

    override fun init() {

        txt_pan_card.setOnClickListener(this)
        txt_aadhar_card.setOnClickListener(this)
        txt_voter_id.setOnClickListener(this)
        txt_applicant_phot.setOnClickListener(this)

        btn_next.setOnClickListener(this)
    }

    var applicantPhoto: String= ""
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        checkForProceed()
        when (requestCode) {
            RequestCode.PanCard -> {
                data?.let {
                    val panCardData: CardResponse? =
                        it.getParcelableExtra(ArgumentKey.PanDetails) as? CardResponse
                    if (mKYCPostData == null) {
                        mKYCPostData = KYCPostData(
                            loanId = AppPreferences.getInstance().getString(AppPreferences.Key.LoanId),
                            customerId = AppPreferences.getInstance().getString(AppPreferences.Key.CustomerId)
                        )
                    }
                    mKYCPostData?.panDob = panCardData?.results?.get(0)?.cardInfo?.dateInfo
                    mKYCPostData?.panFathername = panCardData?.results?.get(0)?.cardInfo?.fatherName
                    mKYCPostData?.panFirstname = panCardData?.results?.get(0)?.cardInfo?.name
                    mKYCPostData?.panLastname = panCardData?.results?.get(0)?.cardInfo?.dob
                    mKYCPostData?.panId = panCardData?.results?.get(0)?.cardInfo?.cardNo
                    mKYCPostData?.panVerified = panCardData?.status
                    mKYCPostData?.panUrl = panCardData?.cardFrontUrl
                    txt_pan_card.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_document_attached,
                        0,
                        0,
                        0
                    )


                    pan_accepted.visibility=View.VISIBLE
                    txt_pan_card.setTextColor(ContextCompat.getColor(this, R.color.black))
                    checkForProceed()
                }
            }
            RequestCode.AadharCard -> {
                data?.let {
                    if (mKYCPostData == null) {
                        mKYCPostData = KYCPostData(
                            loanId = AppPreferences.getInstance().getString(AppPreferences.Key.LoanId),
                            customerId = AppPreferences.getInstance().getString(AppPreferences.Key.CustomerId)
                        )
                    }
                    val aadharCardData =
                        it.getParcelableExtra(ArgumentKey.AadharDetails) as CardResponse
                    mKYCPostData?.aadharAddress =
                        aadharCardData?.results?.get(0)?.cardInfo?.address
                    mKYCPostData?.aadharId = aadharCardData?.results?.get(0)?.cardInfo?.cardNo
                    mKYCPostData?.aadharFrontUrl = aadharCardData?.cardFrontUrl
                    mKYCPostData?.aadharBackUrl = aadharCardData?.cardBackUrl
                    mKYCPostData?.aadharVerified = aadharCardData?.status
                    var aadharCardBack: CardInfo? = null
                    for (index in 0 until (aadharCardData?.results?.size ?: 0)) {
                        if(aadharCardData?.results?.get(index)?.cardSide?.equals("back", ignoreCase = true) == true){
                            aadharCardBack = aadharCardData?.results?.get(index)?.cardInfo
                        }
                    }
                    AppPreferences.getInstance().also {ap ->
                        ap.addString(AppPreferences.Key.Pincode, aadharCardBack?.pin)
                        ap.addString(AppPreferences.Key.AddressLine1, aadharCardBack?.addressLineOne)
                        ap.addString(AppPreferences.Key.AddressLine2, aadharCardBack?.addressLineTwo)
                        ap.addString(AppPreferences.Key.City, aadharCardBack?.city)
                        ap.addString(AppPreferences.Key.State, aadharCardBack?.state)
                    }
                    txt_aadhar_card.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_document_attached,
                        0,
                        0,
                        0
                    )
                 //   adhar_accepted.visibility=View.VISIBLE

                    txt_aadhar_card.setTextColor(ContextCompat.getColor(this, R.color.black))
                    checkForProceed()
                }
            }
            RequestCode.VoterCard -> {
                data?.let {
                    if (mKYCPostData == null) {
                        mKYCPostData = KYCPostData(
                            loanId = AppPreferences.getInstance().getString(AppPreferences.Key.LoanId),
                            customerId = AppPreferences.getInstance().getString(AppPreferences.Key.CustomerId)
                        )
                    }

                    val voterCardData: CardResponse? =
                        it.getParcelableExtra(ArgumentKey.VoterDetails) as? CardResponse
                    mKYCPostData?.voterId = voterCardData?.results?.get(0)?.cardInfo?.voterId
                    mKYCPostData?.voterUrl = voterCardData?.cardFrontUrl
                    mKYCPostData?.voterVerified = voterCardData?.results?.get(0)?.cardInfo?.voterId
                    txt_voter_id.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_document_attached,
                        0,
                        0,
                        0
                    )
                   // voter_accepted.visibility=View.VISIBLE

                    txt_voter_id.setTextColor(ContextCompat.getColor(this, R.color.black))
                    checkForProceed()
                }
            }
            RequestCode.ApplicantPhoto ->  {

                data?.let {

                    val applicantData: CardResponse? =
                        it.getParcelableExtra(ArgumentKey.ApplicantPhoto) as? CardResponse

                    applicantPhoto= applicantData?.cardFrontUrl!!
                    txt_applicant_phot.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_document_attached,
                        0,
                        0,
                        0
                    )
                   // applicant_accepted.visibility=View.VISIBLE

                    txt_applicant_phot.setTextColor(ContextCompat.getColor(this, R.color.black))
                    checkForProceed()
                }


            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private suspend fun stopLoading(progressBar: ProgrssLoader, message: String?) {
        withContext(uiContext) {
            progressBar.dismmissLoading()
            message?.let {
                Toast.makeText(this@AddLeadStep2Activity, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkForProceed() {
        /* if((txt_pan_card.tag != null && !(txt_pan_card.tag as String).isBlank()) &&
             (txt_voter_id.tag != null && !(txt_voter_id.tag as String).isBlank()) &&
             (txt_aadhar_card.tag != null && !(txt_aadhar_card.tag as String).isBlank())){
             btn_next.isEnabled= true
             btn_next.setBackgroundResource(R.drawable.ic_next_enabled)
             btn_next.setTextColor(ContextCompat.getColor(this,R.color.white))
         } else {
             btn_next.isEnabled= false
             btn_next.setBackgroundResource(R.drawable.ic_next_disable)
             btn_next.setTextColor(ContextCompat.getColor(this,R.color.disable_text))
         }*/
        btn_next.isEnabled = true
        btn_next.setBackgroundResource(R.drawable.ic_next_enabled)
        btn_next.setTextColor(ContextCompat.getColor(this, R.color.white))
    }
}