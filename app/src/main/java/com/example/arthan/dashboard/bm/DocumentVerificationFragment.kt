package com.example.arthan.dashboard.bm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.arthan.R
import com.example.arthan.lead.PaymentSuccessActivity
import com.example.arthan.lead.model.postdata.DocScreeningStatusPost
import com.example.arthan.lead.model.postdata.DocumentsData
import com.example.arthan.lead.model.postdata.NeighborReferencePostData
import com.example.arthan.lead.model.responsedata.DocDetails
import com.example.arthan.model.PaymentRequest
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.activity_application_fee.*
import kotlinx.android.synthetic.main.fragment_document_verification.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DocumentVerificationFragment : BaseFragment() {

    override fun contentView() = R.layout.fragment_document_verification

    private var isPanApproved=false
    private var isAdhaarFrontApproved=false
    private var isAdhaarBackApproved=false
    private var isVoterApproved=false
    private var isApplicantpproved=false
    private var docDetails:DocDetails?=null
    private var mContext:Context?=null
    override fun init() {

        btn_aadhar_approve.setOnClickListener {
            isAdhaarFrontApproved = true
        }
        btn_aadhar_disapprove.setOnClickListener { isAdhaarFrontApproved = false }

        btn_aadhar_back_disapprove.setOnClickListener { isAdhaarBackApproved = false }
        btn_aadhar_back_approve.setOnClickListener { isAdhaarBackApproved = true }

        btn_pan_approve.setOnClickListener { isPanApproved = true }
        btn_pan_disapprove.setOnClickListener { isPanApproved = false }

        btn_voter_approve.setOnClickListener { isVoterApproved = true }
        btn_voter_disapprove.setOnClickListener { isVoterApproved = false }

        btn_applicant_approve.setOnClickListener { isApplicantpproved = true }
        btn_applicant_disapprove.setOnClickListener { isApplicantpproved = false }
        btn_next.setOnClickListener {

            val progressBar = ProgrssLoader(mContext!!)
            progressBar.showLoading()
            var actvity=context as BMDocumentVerificationActivity
            var mLoanId=actvity.intent.getStringExtra(ArgumentKey.LoanId)
            var mCustomerId=actvity.intent.getStringExtra(ArgumentKey.CustomerId)
            var list=ArrayList<DocumentsData>()

            list.add(DocumentsData("","aadhar_front",docUrl = docDetails?.aadharFrontUrl,docStatus = isAdhaarFrontApproved))
            list.add(DocumentsData("","aadhar_back",docDetails?.aadharBackUrl,isAdhaarBackApproved))
            list.add(DocumentsData("","voter",docDetails?.voterUrl,isVoterApproved))
            list.add(DocumentsData("","pan",docDetails?.panUrl,isPanApproved))
            list.add(DocumentsData("","paApplicantPhoto",docDetails?.paApplicantPhoto,isApplicantpproved))
            val postBody = DocScreeningStatusPost(
                loanId = mLoanId,
                custId = mCustomerId,
                documents = list
            )
            CoroutineScope(Dispatchers.IO).launch {
                try {
            val response =
                RetrofitFactory.getApiService().docScreeningStatus(
                   postBody
                )
            if (response.isSuccessful && response.body() != null) {

                if (response.body()?.apiCode == "200") {

                    actvity.moveToData()


                } else {

                    Toast.makeText(actvity,"Failed,try again later",Toast.LENGTH_LONG).show()
                }
            }
        }
                catch ( e:Exception)
                {

                }
            }}
    }

    fun updateData(
        docDetails: DocDetails?,
        bmDocumentVerificationActivity: BMDocumentVerificationActivity
    ) {

        this.docDetails=docDetails
        this.mContext=bmDocumentVerificationActivity
        context?.let {
            Glide.with(it)
                .load(docDetails?.panUrl)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Error in loading image ---> ${e?.message}")
                        return false
                    }


                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Success in loading image ")
                        return false
                    }
                })
                .into(img_pan)
            Glide.with(it)
                .load(docDetails?.aadharFrontUrl)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Error in loading image ---> ${e?.message}")
                        return false
                    }


                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Success in loading image ")
                        return false
                    }
                })
                .into(img_aadhaar)
            Glide.with(it)
                .load(docDetails?.aadharBackUrl)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Error in loading image ---> ${e?.message}")
                        return false
                    }


                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Success in loading image ")
                        return false
                    }
                })
                .into(img_aadhaar_back)
            Glide.with(it)
                .load(docDetails?.voterUrl)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Error in loading image ---> ${e?.message}")
                        return false
                    }


                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Success in loading image ")
                        return false
                    }
                })
                .into(img_voter)
            Glide.with(it)
                .load(docDetails?.paApplicantPhoto)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Error in loading image ---> ${e?.message}")
                        return false
                    }


                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Success in loading image ")
                        return false
                    }
                })
                .into(img_applicant)
        }
    }

    companion object {
        const val TAG = "DocumentVerifFragment"
    }
}