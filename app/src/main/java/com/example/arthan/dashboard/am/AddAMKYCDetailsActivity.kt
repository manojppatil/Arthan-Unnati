package com.example.arthan.dashboard.am

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.crashlytics.android.Crashlytics
import com.example.arthan.R
import com.example.arthan.global.AppPreferences
import com.example.arthan.global.ArthanApp
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
    //    var loanId: String? = ""
    var custId: String? = ""


    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main


    override fun init() {
        Log.d("TAG", "In AddAMDetailsActivity")
        if(intent.extras!=null&& intent!!.getStringExtra("task")=="AMRejected" )
        {
            val progress= ProgrssLoader(this)
            progress.showLoading()
            val map=HashMap<String,String?>()
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
                        Toast.makeText(this@AddAMKYCDetailsActivity,"Try again later", Toast.LENGTH_LONG).show()
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
        custId = intent.getStringExtra("custId")
//        mKYCPostData?.loanId = loanId

        when (requestCode) {
            RequestCode.PanCard -> {
                data?.let {
                    val panCardData: CardResponse? =
                        it.getParcelableExtra(ArgumentKey.PanDetails) as? CardResponse
                    if (mKYCPostData == null) {
                        mKYCPostData = KYCPostData(
//                            loanId = loanId,
                            customerId = custId
                        )
                    }
                    mKYCPostData?.panDob =
                        panCardData?.results?.get(0)?.cardInfo?.dateInfo.toString()
                    mKYCPostData?.panFathername =
                        panCardData?.results?.get(0)?.cardInfo?.fatherName.toString()
                    mKYCPostData?.panFirstname =
                        panCardData?.results?.get(0)?.cardInfo?.name.toString()
                    mKYCPostData?.panId = panCardData?.results?.get(0)?.cardInfo?.cardNo.toString()
                    mKYCPostData?.panVerified = panCardData?.status.toString()
                    mKYCPostData?.panUrl = panCardData?.cardFrontUrl.toString()
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
                        it.getParcelableExtra(ArgumentKey.AadharDetails) as CardResponse

                    val aadharCardDataBack =
                        it.getParcelableExtra(ArgumentKey.AadharDetailsBack) as CardResponse

                    AppPreferences.getInstance().also { ap ->
                        ap.addString(
                            AppPreferences.Key.AadharId,
                            aadharCardData?.results?.get(0)?.cardInfo?.cardNo.toString()
                        )
                    }
                    mKYCPostData?.aadharAddress =
                        aadharCardDataBack?.results?.get(0)?.cardInfo?.address.toString()
                    mKYCPostData?.aadharId =
                        aadharCardData?.results?.get(0)?.cardInfo?.cardNo.toString()
                    mKYCPostData?.aadharFrontUrl = aadharCardData?.cardFrontUrl.toString()
                    mKYCPostData?.aadharBackUrl = aadharCardDataBack?.cardBackUrl.toString()
                    mKYCPostData?.aadharVerified = aadharCardData?.status.toString()
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
                    Log.i("AADhar front", "Aadhar"+aadharCardData)
                    Log.i("AADhar Back", "Aadhar"+aadharCardBack)

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
                    txt_am_aadhar_card.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_document_attached,
                        0,
                        0,
                        0
                    )
                    aadhar_am_accepted.visibility = View.VISIBLE
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
                    mKYCPostData?.voterId =
                        voterCardData?.results?.get(0)?.cardInfo?.voterId.toString()
                    mKYCPostData?.voterUrl = voterCardData?.cardFrontUrl.toString()
                    mKYCPostData?.voterVerified =
                        voterCardData?.results?.get(0)?.cardInfo?.voterId.toString()
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
                mKYCPostData?.loanId = "L111"
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
                                    custId = result.customerId
//                                    loanId = result.loanId
                                    it.putExtra("amMobNo",result.amMobNo);
                                    AppPreferences.getInstance().addString("amMobNo",result.amMobNo);
                                    it.putExtra("PAN_DATA", mKYCPostData)
                                    it.putExtra("AADHAR_NO", mKYCPostData?.aadharId)
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
