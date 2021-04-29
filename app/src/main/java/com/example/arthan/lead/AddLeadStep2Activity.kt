package com.example.arthan.lead

import android.content.Intent
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.arthan.R
import com.example.arthan.dashboard.bcm.BCMApprovedAddCoApplicant
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.dashboard.rm.RMScreeningNavigationActivity
import com.example.arthan.global.AppPreferences
import com.example.arthan.global.ArthanApp
import com.example.arthan.global.Crashlytics
import com.example.arthan.global.DOC_TYPE
import com.example.arthan.lead.model.postdata.KYCPostData
import com.example.arthan.lead.model.responsedata.BaseResponseData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.ocr.CardInfo
import com.example.arthan.ocr.CardResponse
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.utils.RequestCode
import com.example.arthan.views.activities.BaseActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add_lead_step2.*
import kotlinx.coroutines.*
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
    var loanId: String? = ""
    var custId: String? = ""
    var aplicantType:String=""


    override fun screenTitle() = "KYC Details"

    override fun onToolbarBackPressed() = onBackPressed()

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.txt_pan_card -> {
                startActivityForResult(Intent(this, UploadDocumentActivity::class.java).apply {
                    putExtra(DOC_TYPE, RequestCode.PanCard)
                    putExtra("show", mKYCPostData?.panUrl)
//                    putExtra("applicant_type", intent.getStringExtra("type") ?: "PA")
                    putExtra("applicant_type", aplicantType ?: "PA")
                }, RequestCode.PanCard)
            }
            R.id.txt_aadhar_card -> {
                startActivityForResult(Intent(this, UploadDocumentActivity::class.java).apply {
                    putExtra(DOC_TYPE, RequestCode.AadharCard)
                    putExtra("show", mKYCPostData?.aadharFrontUrl)
                    putExtra("show2", mKYCPostData?.aadharBackUrl)
                    putExtra("applicant_type", aplicantType ?: "PA")


                }, RequestCode.AadharCard)
            }
            R.id.txt_voter_id -> {
                startActivityForResult(Intent(this, UploadDocumentActivity::class.java).apply {
                    putExtra(DOC_TYPE, RequestCode.VoterCard)
                    putExtra("show", mKYCPostData?.voterUrl)
                    putExtra("applicant_type", aplicantType ?: "PA")


                }, RequestCode.VoterCard)
            }
            R.id.txt_applicant_phot -> {
                startActivityForResult(Intent(this, UploadDocumentActivity::class.java).apply {
                    putExtra(DOC_TYPE, RequestCode.ApplicantPhoto)
                    putExtra("show", mKYCPostData?.paApplicantPhoto)
                    putExtra("applicant_type", aplicantType ?: "PA")

                }, RequestCode.ApplicantPhoto)
            }
            R.id.btn_next -> {
                if(otp_consent.isChecked)
                saveKycDetail()
                else
                    Toast.makeText(this,"Click on checkbox to continue",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun contentView() = R.layout.activity_add_lead_step2

    override fun init() {

        txt_pan_card.setOnClickListener(this)
        txt_aadhar_card.setOnClickListener(this)
        txt_voter_id.setOnClickListener(this)
        txt_applicant_phot.setOnClickListener(this)

        var htmlString=resources.getString(R.string.kyc_consent,ArthanApp.getAppInstance().userName,ArthanApp.getAppInstance().loginUser)
        val ss = SpannableString(htmlString)
        val ss2 = SpannableString(htmlString)
        val boldSpan = StyleSpan(Typeface.BOLD)
        ss.setSpan(StyleSpan(android.graphics.Typeface.BOLD), 3, htmlString.indexOf("having"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(StyleSpan(android.graphics.Typeface.BOLD), htmlString.lastIndexOf("ID")+2, htmlString.indexOf("hereby"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        otp_consent.text=ss
        btn_next.setOnClickListener(this)
        if(ArthanApp.getAppInstance().currentCustomerId==null) {
            getCustomerId()
        }
        else if(intent.getStringExtra("task")!=null)
        {
            getCustomerId()
        }
    }

    private fun getCustomerId() {

        val map=HashMap<String,String>()
        var loanId=""
        if(intent.getStringExtra("loanId")!=null)
        {
            loanId=intent.getStringExtra("loanId")?:AppPreferences.getInstance().getString(AppPreferences.Key.LoanId) ?: ""
        }
        map["loanId"]=loanId
        map["applicantType"]=intent.getStringExtra("type") ?: "PA"
        CoroutineScope(Dispatchers.IO).launch {
            val response=RetrofitFactory.getApiService().getCustomerId(map)
            if(response.body()!=null)
            {
                loanId=response.body()!!.loanId!!
                custId=response.body()!!.customerId!!
                 if(intent.getStringExtra("task")==null) {
                     ArthanApp.getAppInstance().currentCustomerId = custId
                 }
                AppPreferences.getInstance().addString(AppPreferences.Key.CustomerId,response.body()!!.customerId!!)
                AppPreferences.getInstance().addString(AppPreferences.Key.LoanId,response.body()!!.loanId!!)
                aplicantType=response.body()!!.applicantType!!
            }
        }
    }

    var applicantPhoto: String = ""
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        checkForProceed()
        loanId =intent.getStringExtra("loanId")
//        custId = intent.getStringExtra("custId")
//        mKYCPostData?.loanId=loanId

        when (requestCode) {
            RequestCode.PanCard -> {
                data?.let {
                    val panCardData: CardResponse? =
                        it.getParcelableExtra(ArgumentKey.PanDetails) as? CardResponse
                    if (mKYCPostData == null) {
                        mKYCPostData = KYCPostData(
                            loanId =panCardData?.loanId,
                            customerId = panCardData?.customerId
                        )
                    }
                    mKYCPostData?.loanId=loanId
                    mKYCPostData?.customerId=custId
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


                    pan_accepted.visibility = View.VISIBLE
                    txt_pan_card.setTextColor(ContextCompat.getColor(this, R.color.black))
                    checkForProceed()
                }
            }
            RequestCode.AadharCard -> {
                data?.let {
                    if (mKYCPostData == null) {
                        mKYCPostData = KYCPostData(
                           /* loanId = intent.getStringExtra("loanId"),
                            customerId = intent.getStringExtra("custId")*/
                        loanId,
                            custId
                        )
                    }
                    val aadharCardData =
                        it.getParcelableExtra(ArgumentKey.AadharDetails) as CardResponse
                    val aadharCardDataBack =
                        it.getParcelableExtra(ArgumentKey.AadharDetailsBack) as CardResponse

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
                    mKYCPostData?.pincode=aadharCardBack?.pin
                    mKYCPostData?.state=aadharCardBack?.state
                    mKYCPostData?.city=aadharCardBack?.city
                    mKYCPostData?.district=aadharCardBack?.district
                    mKYCPostData?.address_line1=aadharCardBack?.addressLineOne
                    mKYCPostData?. address_line2=aadharCardBack?.addressLineTwo
                    mKYCPostData?.landmark=aadharCardBack?.landmark
                    mKYCPostData?.areaName=aadharCardBack?.areaName


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
            RequestCode.VoterCard ->{
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
            RequestCode.ApplicantPhoto -> {

                data?.let {

                    val applicantData: CardResponse? =
                        it.getParcelableExtra(ArgumentKey.ApplicantPhoto) as? CardResponse

                    applicantPhoto = applicantData?.cardFrontUrl!!
                    mKYCPostData?.paApplicantPhoto = applicantPhoto
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


    private suspend fun stopLoading(progressBar: ProgrssLoader, message: String) {
        withContext(uiContext) {
            progressBar.dismmissLoading()
            message.let {
                Toast.makeText(this@AddLeadStep2Activity, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {

//        menuInflater.inflate(R.menu.more, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.homeMenu -> {
                finish()

                if(ArthanApp.getAppInstance().loginRole=="RM")
                {
                    startActivity(Intent(this,RMDashboardActivity::class.java))
                }else if(ArthanApp.getAppInstance().loginRole=="BCM")
                {
                    startActivity(Intent(this, BCMDashboardActivity::class.java))

                }else if(ArthanApp.getAppInstance().loginRole=="BM")
                {
                    startActivity(Intent(this, BMDashboardActivity::class.java))

                }
            }
            R.id.logoutMenu -> {
                finish()
                startActivity(Intent(this, SplashActivity::class.java))
            }

        }
        return super.onOptionsItemSelected(item)
    }*/

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


    private fun saveKycDetail() {

        val progressBar: ProgrssLoader? = if (this != null) ProgrssLoader(this!!) else null
        progressBar?.showLoading()
        CoroutineScope(ioContext).launch {
            try {
                if(intent.getStringExtra("task")=="Add-CoApplicant"){
                    mKYCPostData?.userId=ArthanApp.getAppInstance().loginUser
                    mKYCPostData?.stage="BCM Approved"
                }
               // mKYCPostData?.customerId = custId
                //mKYCPostData?.loanId=loanId
                mKYCPostData?.applicantType = aplicantType
                val map=HashMap<String,String>()
                map["loanId"]=mKYCPostData?.loanId!!
                map["customerId"]=mKYCPostData?.customerId!!
                map["applicantType"]=aplicantType
                map["paApplicantPhoto"]=mKYCPostData?.paApplicantPhoto!!
//                val response = RetrofitFactory.getApiService().saveKycDetail(mKYCPostData)
                val response = RetrofitFactory.getApiService().saveKycDetail(map)
                if (response?.isSuccessful == true) {
                    val result = response.body()
                    withContext(Dispatchers.Main) {
                        if (result?.apiCode == "200") {

                            if (intent.getStringExtra("task") == "RMreJourney") {
                                withContext(Dispatchers.Main) {
                                    progressBar?.dismmissLoading()

                                    startActivity(
                                        Intent(
                                            this@AddLeadStep2Activity,
                                            RMScreeningNavigationActivity::class.java
                                        ).apply {
                                            putExtra("loanId", loanId)
                                        }
                                    )
                                    finish()
                                }
                            } else if(intent.getStringExtra("task")=="Add-CoApplicant")
                            {
                                withContext(Dispatchers.Main)
                                {
                                    BCMApprovedAddCoApplicant.custId=result.customerId

                                    finish()
                                }
                            }else {
                                progressBar?.dismmissLoading()

                                if (result.canNavigate.equals("N",ignoreCase = true)) {

                                    Toast.makeText(
                                        this@AddLeadStep2Activity,
                                        result.message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                    startActivity(
                                        Intent(
                                            this@AddLeadStep2Activity,
                                            RMDashboardActivity::class.java
                                        )
                                    )
                                    finish()
                                } else {
                                    startActivity(
                                        Intent(
                                            this@AddLeadStep2Activity,
                                            PersonalInformationActivity::class.java
                                        ).also {
                                            custId = result.customerId
                                            loanId = result.loanId
                                            mKYCPostData?.pincode=result.pincode
                                            mKYCPostData?.state=result.state
                                            mKYCPostData?.city=result.city
                                            mKYCPostData?.address_line1=result.addressLine1
                                            mKYCPostData?. address_line2=result.addressLine2
                                            mKYCPostData?.customerName=result.customerName
                                            mKYCPostData?.panDob=result.customerDob
                                            mKYCPostData?.panFathername=result.fatherName
                                            mKYCPostData?.panId=result.panNo
                                            it.putExtra("custId", result.customerId)
                                            it.putExtra("type", result.applicantType)
                                            it.putExtra("PAN_DATA", mKYCPostData)
                                            it.putExtra("loanId", result.loanId)
//                                            it.putExtra("type", intent.getStringExtra("type"))
                                            if( intent.getStringExtra("task")!=null)
                                            it.putExtra("task",intent.getStringExtra("task"))

                                            if( intent.getStringExtra("task")!=null&&intent.getStringExtra("task")=="RMContinue")
                                                it.putExtra("screen",intent.getStringExtra("screen"))

                                        })
                                    if(intent.getStringExtra("task")!=null&&intent.getStringExtra("task")=="RMAddCo"||
                                        intent.getStringExtra("task")!=null&&intent.getStringExtra("task")=="RMAddCoRe")
                                    {
                                        finish()
                                    }
//                            startActivity(
//                                Intent(
//                                    context,
//                                    PersonalInformationActivity::class.java
//                                ).apply {
//                                    putExtra("PAN_DATA", mPanCardData)
//                                })
                                }
                            }
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
                            "Smething went wrong with api!!!"/*result?.message*/
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
}