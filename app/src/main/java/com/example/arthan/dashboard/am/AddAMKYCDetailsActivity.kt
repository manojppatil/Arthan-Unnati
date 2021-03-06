package com.example.arthan.dashboard.am

import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.arthan.R
import com.example.arthan.dashboard.bm.ShowPDFActivity
import com.example.arthan.global.AppPreferences
import com.example.arthan.global.ArthanApp
import com.example.arthan.global.Crashlytics
import com.example.arthan.global.DOC_TYPE
import com.example.arthan.lead.UploadDocumentActivity
import com.example.arthan.lead.model.postdata.KYCPostData
import com.example.arthan.lead.model.responsedata.BaseResponseData
import com.example.arthan.lead.model.responsedata.DocDetailsAM
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.ocr.CardInfo
import com.example.arthan.ocr.CardResponse
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.utils.RequestCode
import com.example.arthan.views.activities.BaseActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_am_kycdetails.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class AddAMKYCDetailsActivity : BaseActivity(), View.OnClickListener, CoroutineScope {
    override fun contentView() = R.layout.activity_am_kycdetails

    var mKYCPostData: KYCPostData? = null
        var amId: String? = ""
    var custId: String? = ""
    var customerId: String? = ""


    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    private fun getCustomerId() {

        val map=HashMap<String, String>()
        var loanId=""
        map["loanId"]="AM"+ArthanApp.getAppInstance().loginUser
        map["applicantType"]="AM"
        CoroutineScope(Dispatchers.IO).launch {
            val response=RetrofitFactory.getApiService().getCustomerId(map)
            if(response.body()!=null&&response.body()?.loanId!=null)
            {

                loanId= response.body()!!.loanId!!
                customerId=response.body()!!.customerId!!
                if(intent.getStringExtra("task")==null) {
                    ArthanApp.getAppInstance().currentCustomerId = custId
                }
                AppPreferences.getInstance().addString(
                    AppPreferences.Key.CustomerId,
                    response.body()!!.customerId!!
                )
                AppPreferences.getInstance().addString(
                    AppPreferences.Key.LoanId,
                    response.body()!!.loanId!!
                )
            }
        }
    }

    override fun init() {
        Log.d("TAG", "In AddAMDetailsActivity")

        getCustomerId()

        if(intent.extras!=null&& intent!!.getStringExtra("task")=="AMRejected" )
        {
            val progress= ProgrssLoader(this)
            progress.showLoading()
            val map=HashMap<String, String?>()
            map["screen"]=intent.getStringExtra("screen")
            map["amId"]=intent.getStringExtra("amId")
            CoroutineScope(Dispatchers.IO).launch {
                val res= RetrofitFactory.getApiService().getAMScreenData(map)
                if(res?.body()!=null)
                {
                    withContext(Dispatchers.Main)
                    {
                        progress.dismmissLoading()
                        updateData(res.body()!!.docDetails)
                    }
                }
                else{
                    withContext(Dispatchers.Main){
                        Toast.makeText(
                            this@AddAMKYCDetailsActivity,
                            "Try again later",
                            Toast.LENGTH_LONG
                        ).show()
                        progress.dismmissLoading()
                    }
                }
            }
        }
        txt_am_pan_card.setOnClickListener(this)
        txt_am_aadhar_card.setOnClickListener(this)
        txt_am_voter_id.setOnClickListener(this)
        txt_am_appl_photo.setOnClickListener(this)
        btn_am_next.setOnClickListener(this)
        val value=resources.getString(R.string.code_conduct)
        val tca=resources.getString(R.string.terms_agreement)
        val ss = SpannableString(value)
        val ss2 = SpannableString(tca)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
//                startActivity(Intent(this, NextActivity::class.java))
                CoroutineScope(Dispatchers.IO).launch {
                    val response=RetrofitFactory.getApiService().getCoc()
                    if(response.isSuccessful)
                    {
                        if(response?.body()!!.cocUrl!=null)
                        {
                            startActivity(Intent(this@AddAMKYCDetailsActivity,ShowPDFActivity::class.java).apply {
                                putExtra("pdf_url",response.body()!!.cocUrl)
                                putExtra("title","COde of Conduct")
                            })
                        }
                    }
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }
        }
        val clickableSpan2: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
//                startActivity(Intent(this, NextActivity::class.java))
                CoroutineScope(Dispatchers.IO).launch {
                    val response=RetrofitFactory.getApiService().getTCA()
                    if(response.isSuccessful)
                    {
                        if(response?.body()!!.tcaUrl!=null)
                        {
                            startActivity(Intent(this@AddAMKYCDetailsActivity,ShowPDFActivity::class.java).apply {
                                putExtra("pdf_url",response.body()!!.tcaUrl)
                                putExtra("title","Terms of Agreement")
                            })
                        }
                    }
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }
        }
        ss.setSpan(clickableSpan,22, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss2.setSpan(clickableSpan2, 43, 63, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv_codeOfConduct.text = ss
        tv_terms.text = ss2
        tv_codeOfConduct.movementMethod=LinkMovementMethod.getInstance()
        tv_terms.movementMethod=LinkMovementMethod.getInstance()


    }

    private fun updateData(docDetails: DocDetailsAM) {

        mKYCPostData=KYCPostData()
        mKYCPostData?.panUrl=docDetails.panUrl
        mKYCPostData?.aadharFrontUrl=docDetails.aadharFrontUrl
        mKYCPostData?.aadharBackUrl=docDetails.aadharBackUrl
        mKYCPostData?.voterUrl=docDetails.voterUrl
        mKYCPostData?.paApplicantPhoto=docDetails.paApplicantPhoto


    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.txt_am_pan_card -> {
                startActivityForResult(Intent(this, UploadDocumentActivity::class.java).apply {
                    putExtra(DOC_TYPE, RequestCode.PanCard)
                    putExtra("show", mKYCPostData?.panUrl)
                }, RequestCode.PanCard)
            }
            R.id.txt_am_aadhar_card -> {
                startActivityForResult(Intent(this, UploadDocumentActivity::class.java).apply {
                    putExtra(DOC_TYPE, RequestCode.AadharCard)
                    putExtra("show", mKYCPostData?.aadharFrontUrl)
                    putExtra("show2", mKYCPostData?.aadharBackUrl)

                }, RequestCode.AadharCard)
                /*startActivityForResult(Intent(this, UploadDocumentActivity::class.java).apply {
                    putExtra(DOC_TYPE, RequestCode.AadharCard)
                }, RequestCode.AadharCard)*/
            }
            R.id.txt_am_voter_id -> {
                startActivityForResult(Intent(this, UploadDocumentActivity::class.java).apply {
                    putExtra(DOC_TYPE, RequestCode.VoterCard)
                    putExtra("show", mKYCPostData?.voterUrl)

                }, RequestCode.VoterCard)
            }
            R.id.txt_am_appl_photo -> {
                startActivityForResult(Intent(this, UploadDocumentActivity::class.java).apply {
                    putExtra(DOC_TYPE, RequestCode.ApplicantPhoto)
                    putExtra("show", mKYCPostData?.paApplicantPhoto)
                }, RequestCode.ApplicantPhoto)
            }
            R.id.btn_am_next -> {
                saveKycDetail()
            }
        }

    }

    override fun onToolbarBackPressed() = finish()

    override fun screenTitle() = "KYC details"
    var applicantPhoto: String = ""
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        checkForProceed()
//        loanId = intent.getStringExtra("loanId")
     //   custId = intent.getStringExtra("custId")
//        mKYCPostData?.loanId = loanId

        when (requestCode) {
            RequestCode.PanCard -> {
                data?.let {
                    val panCardData: CardResponse? =
                        it.getParcelableExtra(ArgumentKey.PanDetails) as? CardResponse
                    if (mKYCPostData == null) {
                        mKYCPostData = KYCPostData(
                            amId = panCardData?.amId,
                            customerId = panCardData?.customerId
                        )
                    }
                    mKYCPostData?.loanId = panCardData?.loanId
                    mKYCPostData?.customerId = panCardData?.customerId
                    mKYCPostData?.panDob = panCardData?.results?.get(0)?.cardInfo?.dateInfo
                    mKYCPostData?.panFathername = panCardData?.results?.get(0)?.cardInfo?.fatherName
                    mKYCPostData?.panFirstname = panCardData?.results?.get(0)?.cardInfo?.name
                    mKYCPostData?.panLastname = panCardData?.results?.get(0)?.cardInfo?.dob
                    mKYCPostData?.panId = panCardData?.results?.get(0)?.cardInfo?.cardNo
                    mKYCPostData?.panVerified = panCardData?.status
                    mKYCPostData?.panUrl = panCardData?.cardFrontUrl
                    txt_am_pan_card.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_document_attached,
                        0,
                        0,
                        0
                    )


                    pan_am_accepted.visibility = View.VISIBLE
                    txt_am_pan_card.setTextColor(ContextCompat.getColor(this, R.color.black))
                    checkForProceed()
                }
            }
            RequestCode.AadharCard -> {
                data?.let {
                    if (mKYCPostData == null) {
                        mKYCPostData = KYCPostData(
                            loanId = intent.getStringExtra("loanId"),
                            customerId = intent.getStringExtra("custId")
                        )
                    }
                    val aadharCardData =
                        it.getParcelableExtra<CardResponse>(ArgumentKey.AadharDetails)
                    val aadharCardDataBack =
                        it.getParcelableExtra<CardResponse>(ArgumentKey.AadharDetailsBack)

                    /* AppPreferences.getInstance().also { ap ->
                        ap.addString(
                            AppPreferences.Key.AadharId,
                            aadharCardData?.results?.get(0)?.cardInfo?.cardNo.toString()
                        )
                    }*/
                    mKYCPostData?.aadharAddress =
                        aadharCardDataBack?.results?.get(0)?.cardInfo?.address
                    mKYCPostData?.aadharId = aadharCardData?.results?.get(0)?.cardInfo?.cardNo
                    mKYCPostData?.aadharFrontUrl = aadharCardData?.cardFrontUrl
                    mKYCPostData?.aadharBackUrl = aadharCardDataBack?.cardBackUrl
                    mKYCPostData?.aadharVerified = aadharCardData?.status
                    var aadharCardBack: CardInfo? = null
                    for (index in 0 until (aadharCardData?.results?.size ?: 0)) {
                        if (aadharCardDataBack?.results?.get(index)?.cardSide?.equals(
                                "back",
                                ignoreCase = true
                            ) == true
                        ) {
                            aadharCardBack = aadharCardDataBack?.results?.get(index)?.cardInfo
                        }
                    }
                    Log.i("AADhar front", "Aadhar" + aadharCardData)
                    Log.i("AADhar Back", "Aadhar" + aadharCardBack)

                    AppPreferences.getInstance().also { ap ->
                        ap.addString(AppPreferences.Key.Pincode, aadharCardBack?.pin)
                        ap.addString(
                            AppPreferences.Key.AddressLine1,
                            aadharCardBack?.addressLineOne
                        )
                        ap.addString(
                            AppPreferences.Key.AddressLine2,
                            aadharCardBack?.addressLineTwo
                        )
                        ap.addString(AppPreferences.Key.City, aadharCardBack?.city)
                        ap.addString(AppPreferences.Key.State, aadharCardBack?.state)
                    }
                    mKYCPostData?.pincode = aadharCardBack?.pin
                    mKYCPostData?.state = aadharCardBack?.state
                    mKYCPostData?.city = aadharCardBack?.city
                    mKYCPostData?.district = aadharCardBack?.district
                    mKYCPostData?.address_line1 = aadharCardBack?.addressLineOne
                    mKYCPostData?.address_line2 = aadharCardBack?.addressLineTwo
                    mKYCPostData?.landmark = aadharCardBack?.landmark
                    mKYCPostData?.areaName = aadharCardBack?.areaName


                    txt_am_aadhar_card.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_document_attached,
                        0,
                        0,
                        0
                    )
//                       adhar_accepted.visibility=View.VISIBLE

                    txt_am_aadhar_card.setTextColor(ContextCompat.getColor(this, R.color.black))
                    checkForProceed()
                }
            }
            RequestCode.VoterCard -> {
                data?.let {
                    if (mKYCPostData == null) {
                        mKYCPostData = KYCPostData(
                            loanId = intent.getStringExtra("loanId"),
                            customerId = intent.getStringExtra("custId")
                        )
                    }

                    val voterCardData: CardResponse? =
                        it.getParcelableExtra(ArgumentKey.VoterDetails) as? CardResponse
                    mKYCPostData?.voterId = voterCardData?.results?.get(0)?.cardInfo?.voterId
                    mKYCPostData?.voterUrl = voterCardData?.cardFrontUrl
                    mKYCPostData?.voterVerified = voterCardData?.results?.get(0)?.cardInfo?.voterId
                    txt_am_voter_id.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_document_attached,
                        0,
                        0,
                        0
                    )
                    voter_am_accepted.visibility = View.VISIBLE
                    txt_am_voter_id.setTextColor(ContextCompat.getColor(this, R.color.black))
                    checkForProceed()
                }
            }
            RequestCode.ApplicantPhoto -> {

                data?.let {
                    val applicantData: CardResponse? =
                        it.getParcelableExtra(ArgumentKey.ApplicantPhoto) as? CardResponse

                    applicantPhoto = applicantData?.cardFrontUrl!!
                    mKYCPostData?.paApplicantPhoto = applicantPhoto
                    txt_am_appl_photo.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_document_attached,
                        0,
                        0,
                        0
                    )
                    aphoto_am_accepted.visibility = View.VISIBLE
                    txt_am_appl_photo.setTextColor(ContextCompat.getColor(this, R.color.black))
                    checkForProceed()
                }


            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun checkForProceed() {
        btn_am_next.isEnabled = true
        btn_am_next.setBackgroundResource(R.drawable.ic_next_enabled)
        this?.let { btn_am_next.setTextColor(ContextCompat.getColor(it, R.color.white)) }
    }


    private fun saveKycDetail() {
        val progressBar: ProgrssLoader? = if (this != null) ProgrssLoader(this!!) else null
        progressBar?.showLoading()
        CoroutineScope(ioContext).launch {
            try {
               // mKYCPostData?.applicantType = intent.getStringExtra("type") ?: "pa"
                val map=HashMap<String, String>()
                map["amId"]=mKYCPostData?.loanId!!
                map["customerId"]=customerId!!
                map["applicantType"]="AM"
                map["paApplicantPhoto"]=mKYCPostData?.paApplicantPhoto!!
//
             //   mKYCPostData?.loanId = "L111"
                mKYCPostData?.applicantType = "AM"
                mKYCPostData?.amId = ArthanApp.getAppInstance().loginUser
                val response = RetrofitFactory.getApiService().saveAMKycDetail(mKYCPostData)
                if (response?.isSuccessful == true) {
                    val result = response.body()
                    withContext(Dispatchers.Main) {
                        if (result?.apiCode == "200") {
                            progressBar?.dismmissLoading()
                            startActivity(
                                Intent(
                                    this@AddAMKYCDetailsActivity,
                                    AMPersonalDetailsActivity::class.java
                                ).also {
                                    // custId = result.customerId
//                                    loanId = result.loanId
                                    it.putExtra("amMobNo", result.amMobNo);
                                    AppPreferences.getInstance().addString(
                                        "amMobNo",
                                        result.amMobNo
                                    );
                                    it.putExtra("PAN_DATA", mKYCPostData)
                                    it.putExtra("AADHAR_NO", mKYCPostData?.aadharId)
                                    custId = result.customerId
                                    mKYCPostData?.aadharId = result.applicantAadharNo
                                    amId = result.loanId
                                    mKYCPostData?.pincode = result.pincode
                                    mKYCPostData?.state = result.state
                                    mKYCPostData?.city = result.city
                                    mKYCPostData?.address_line1 = result.addressLine1
                                    mKYCPostData?.address_line2 = result.addressLine2
                                    mKYCPostData?.customerName = result.customerName
                                    mKYCPostData?.panDob = result.customerDob
                                    mKYCPostData?.panFathername = result.fatherName
                                    mKYCPostData?.panId = result.panNo
                                    /* it.putExtra("custId", result.customerId)
                                    it.putExtra("PAN_DATA", mKYCPostData)
                                    it.putExtra("loanId", result.loanId)
                                    it.putExtra("type", intent.getStringExtra("type"))*/

                                }
                            )
                        }
                    }
                } else {
                    try {
                        val result: BaseResponseData? = Gson().fromJson(
                            response?.errorBody()?.string(),
                            BaseResponseData::class.java
                        )
                        stopLoading(
                            progressBar!!,
                            "Something went wrong with api!!!"/*result?.message*/
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Crashlytics.log(e.message)

                        stopLoading(progressBar!!, "Something went wrong. Please try later!")
                    }
                }
            } catch (e: Exception) {
                stopLoading(progressBar!!, "Something went wrong. Please try later!")
                e.printStackTrace()
            }
        }
    }

    private suspend fun stopLoading(progressBar: ProgrssLoader, message: String) {
        withContext(uiContext) {
            progressBar.dismmissLoading()
            message.let {
                Toast.makeText(this@AddAMKYCDetailsActivity, it, Toast.LENGTH_LONG).show()
            }
        }
    }


}
