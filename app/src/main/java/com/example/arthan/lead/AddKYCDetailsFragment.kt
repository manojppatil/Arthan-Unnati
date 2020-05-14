package com.example.arthan.lead


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.crashlytics.android.Crashlytics

import com.example.arthan.R
import com.example.arthan.global.*
import com.example.arthan.lead.model.postdata.KYCPostData
import com.example.arthan.lead.model.responsedata.BaseResponseData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.ocr.CardResponse
import com.example.arthan.utils.ProgrssLoader
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_add_kycdetails.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.RequestCode

/**
 * A simple [Fragment] subclass.
 */
class AddKYCDetailsFragment : NavHostFragment(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    var mPanCardData: CardResponse? = null
    var mKYCPostData: KYCPostData? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_kycdetails, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txt_pan_card.setOnClickListener {
            startActivityForResult(Intent(context, UploadDocumentActivity::class.java).apply {
                putExtra(DOC_TYPE, RequestCode.PanCard)
            }, RequestCode.PanCard)
        }
        txt_aadhar_card.setOnClickListener {
            startActivityForResult(Intent(context, UploadDocumentActivity::class.java).apply {
                putExtra(DOC_TYPE, RequestCode.AadharCard)
            }, RequestCode.AadharCard)
        }
        txt_voter_id.setOnClickListener {
            startActivityForResult(Intent(context, UploadDocumentActivity::class.java).apply {
                putExtra(DOC_TYPE, RequestCode.VoterCard)
            }, RequestCode.VoterCard)
        }

        btn_next.setOnClickListener {
            navController?.navigate(R.id.action_addKYCDetailsFragment_to_addPersonalDetailsFragment)
//            saveKycDetail()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCode.PanCard -> {
                data?.let {
                    val panCardData: CardResponse? =
                        it.getSerializableExtra(ArgumentKey.PanDetails) as CardResponse
                    if (mKYCPostData == null) {
                        mKYCPostData = KYCPostData(
                            loanId = AppPreferences.getInstance()
                                .getString(AppPreferences.Key.LoanId),
                            customerId = AppPreferences.getInstance()
                                .getString(AppPreferences.Key.CustomerId)
                        )
                    }
                    mKYCPostData?.panDob = panCardData?.results?.get(0)?.cardInfo?.dob
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
                    context?.let { context ->
                        txt_pan_card.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                    }
                    checkForProceed()
                }
            }
            RequestCode.AadharCard -> {
                data?.let {
                    if (mKYCPostData == null) {
                        mKYCPostData = KYCPostData(
                            loanId = AppPreferences.getInstance()
                                .getString(AppPreferences.Key.LoanId),
                            customerId = AppPreferences.getInstance()
                                .getString(AppPreferences.Key.CustomerId)
                        )
                    }
                    val aadharCardData =
                        it.getSerializableExtra(ArgumentKey.AadharDetails) as CardResponse
                    mKYCPostData?.aadharAddress =
                        aadharCardData?.results?.get(0)?.cardInfo?.address
                    mKYCPostData?.aadharId = aadharCardData?.results?.get(0)?.cardInfo?.cardNo
                    mKYCPostData?.aadharFrontUrl = aadharCardData?.cardFrontUrl
                    mKYCPostData?.aadharVerified = aadharCardData?.status
                    txt_aadhar_card.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_document_attached,
                        0,
                        0,
                        0
                    )
                    context?.let { context ->
                        txt_aadhar_card.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                    }
                    checkForProceed()
                }
            }
            RequestCode.VoterCard -> {
                data?.let {
                    if (mKYCPostData == null) {
                        mKYCPostData = KYCPostData(
                            loanId = AppPreferences.getInstance()
                                .getString(AppPreferences.Key.LoanId),
                            customerId = AppPreferences.getInstance()
                                .getString(AppPreferences.Key.CustomerId)
                        )
                    }

                    val voterCardData: CardResponse? =
                        it.getSerializableExtra(ArgumentKey.VoterDetails) as CardResponse
                    mKYCPostData?.voterId = voterCardData?.results?.get(0)?.cardInfo?.voterId
                    mKYCPostData?.voterUrl = voterCardData?.cardFrontUrl
                    mKYCPostData?.voterVerified = voterCardData?.results?.get(0)?.cardInfo?.voterId
                    txt_voter_id.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_document_attached,
                        0,
                        0,
                        0
                    )
                    context?.let { context ->
                        txt_voter_id.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                    }
                    checkForProceed()
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
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
        context?.let { btn_next.setTextColor(ContextCompat.getColor(it, R.color.white)) }
    }

    private suspend fun stopLoading(progressBar: ProgrssLoader?, message: String?) {
        withContext(uiContext) {
            progressBar?.dismmissLoading()
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveKycDetail() {
        val progressBar: ProgrssLoader? = if (context != null) ProgrssLoader(context!!) else null
        progressBar?.showLoading()
        CoroutineScope(ioContext).launch {
            try {
                val response = RetrofitFactory.getApiService().saveKycDetail(mKYCPostData)
                if (response?.isSuccessful == true) {
                    val result = response.body()
                    withContext(Dispatchers.Main) {
                        if (result?.apiCode == "200") {
                            progressBar?.dismmissLoading()
                            navController?.navigate(
                                R.id.action_addKYCDetailsFragment_to_addPersonalDetailsFragment,
                                Bundle().also {
                                    it.putParcelable("PAN_DATA", mPanCardData)
                                })
//                            startActivity(
//                                Intent(
//                                    context,
//                                    PersonalInformationActivity::class.java
//                                ).apply {
//                                    putExtra("PAN_DATA", mPanCardData)
//                                })
                        }
                    }
                } else {
                    try {
                        val result: BaseResponseData? = Gson().fromJson(
                            response?.errorBody()?.string(),
                            BaseResponseData::class.java
                        )
                        stopLoading(
                            progressBar,
                            "Smething went wrong with api!!!"/*result?.message*/
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Crashlytics.log(e.message)

                        stopLoading(progressBar, "Something went wrong. Please try later!")
                    }
                }
            } catch (e: Exception) {
                stopLoading(progressBar, "Something went wrong. Please try later!")
                e.printStackTrace()
            }
        }
    }
}
