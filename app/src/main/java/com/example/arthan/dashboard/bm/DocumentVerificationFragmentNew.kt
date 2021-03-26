package com.example.arthan.dashboard.bm

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.arthan.R
import com.example.arthan.dashboard.bm.adapter.*
import com.example.arthan.global.ArthanApp
import com.example.arthan.lead.model.postdata.DocScreeningStatusPost
import com.example.arthan.lead.model.postdata.DocScreeningStatusPostNew
import com.example.arthan.lead.model.postdata.DocumentsData
import com.example.arthan.lead.model.responsedata.DocDetails
import com.example.arthan.lead.model.responsedata.DocDetailsAM
import com.example.arthan.lead.model.responsedata.RequireDocs
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.PendingCustomersActivity
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.document_verification_fragment_new.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DocumentVerificationFragmentNew : BaseFragment() {

    override fun contentView() = R.layout.document_verification_fragment_new

    private var isPanApproved=false
    private var isAdhaarFrontApproved=false
    private var isAdhaarBackApproved=false
    private var isVoterApproved=false
    private var isApplicantpproved=false
    private var isBusinessProof=false
    private var isOfficeAddressProof=false
    private var isIncomeProof=false
    private var isChequeProof=false
    private var docDetails:DocDetails?=null
    private var docDetailsam:DocDetailsAM?=null
    private var mContext:Context?=null
    private var textColor:Int=Color.parseColor("#09327a")
    override fun init() {

     /*   if (activity?.intent?.getStringExtra("recordType") == "AM") {
            otherproofs.visibility=View.GONE
            btn_chq_disapprove.visibility=View.VISIBLE
            btn_chq_approve.visibility=View.VISIBLE
            img_chq.visibility=View.VISIBLE
            txt_chqPhoto.visibility=View.VISIBLE

        }
*/
        primaryApp.setTextColor(Color.WHITE)
        primaryApp.setBackgroundResource(R.color.colorPrimary)
        coapp.setTextColor(Color.BLACK)
        coapp.setBackgroundResource(R.color.disable_text)
        primaryKycLL.visibility=View.VISIBLE
        rvCoAppsKyc.visibility=View.GONE
        primaryApp.setOnClickListener {
            primaryApp.setTextColor(Color.WHITE)
            primaryApp.setBackgroundResource(R.color.colorPrimary)
            coapp.setTextColor(Color.BLACK)
            coapp.setBackgroundResource(R.color.disable_text)
            primaryKycLL.visibility=View.VISIBLE
            rvCoAppsKyc.visibility=View.GONE
        }
        coapp.setOnClickListener {
            primaryApp.setTextColor(Color.BLACK)
            primaryApp.setBackgroundResource(R.color.disable_text)
            coapp.setTextColor(Color.WHITE)
            coapp.setBackgroundResource(R.color.colorPrimary)
            primaryKycLL.visibility=View.GONE
            rvCoAppsKyc.visibility=View.VISIBLE
        }



        DocumentVerificationFragmentNew.context=activity!!

        btn_next.setOnClickListener {

            val progressBar = ProgrssLoader(mContext!!)
            progressBar.showLoading()
            var actvity=context as BMDocumentVerificationActivity
            var mLoanId=actvity.intent.getStringExtra(ArgumentKey.LoanId)
            var mCustomerId=actvity.intent.getStringExtra(ArgumentKey.CustomerId)

//            docDetails=(context as BMDocumentVerificationActivity).list

            //businessProof/businessAddrProof/incomeProof)
         /*   list.add(DocumentsData("","aadhar_front",docUrl = docDetails?.aadharFrontUrl,docStatus = isAdhaarFrontApproved.toString()))
            list.add(DocumentsData("","aadhar_back",docDetails?.aadharBackUrl,isAdhaarBackApproved.toString()))
            list.add(DocumentsData("","voter",docDetails?.voterUrl,isVoterApproved.toString()))
            list.add(DocumentsData("","pan",docDetails?.panUrl,isPanApproved.toString()))
            list.add(DocumentsData("","paApplicantPhoto",docDetails?.paApplicantPhoto,isApplicantpproved.toString()))
            list.add(DocumentsData("","businessProof",docDetails?.businessProof,isBusinessProof.toString()))
            list.add(DocumentsData("","businessAddrProof",docDetails?.businessAddrProof,isOfficeAddressProof.toString()))
            list.add(DocumentsData("","incomeProof",docDetails?.incomeProof,isIncomeProof.toString()))
            list.add(DocumentsData("","cheque",docDetails?.chequeUrl,isChequeProof.toString()))*/
            val postBody = DocScreeningStatusPostNew(
                loanId = mLoanId,
                userId=ArthanApp.getAppInstance().loginUser,
                custId = mCustomerId,
                documents = list,
                amId = ""+actvity.intent.getStringExtra("amId")
            )
            CoroutineScope(Dispatchers.IO).launch {
                try {
            val response = if(activity?.intent?.getStringExtra("recordType") == "AM"){
                RetrofitFactory.getApiService().amDocScreeningStatus(
                    postBody
                )
            }else {
                RetrofitFactory.getApiService().docScreeningStatus(
                    postBody
                )
            }
            if (response.isSuccessful && response.body() != null) {

                progressBar.dismmissLoading()
                if (response.body()?.apiCode == "200") {

                    withContext(Dispatchers.Main) {
                        //                    if (ArthanApp.getAppInstance().loginRole == "BM" || ArthanApp.getAppInstance().loginRole == "BCM") {
                        if (ArthanApp.getAppInstance().loginRole == "BCM") {
                            actvity.moveToData()
                        }
                     //   if (response.body()?.discrepancy!!.equals("n", ignoreCase = true)) {
                           else if (ArthanApp.getAppInstance().loginRole == "BM") {
                                actvity.moveToData()

                            } else {
                                //barring the condition check mail on aug 17 2020 tasks
                                actvity.moveToData()
                               // startActivity(Intent(actvity, PendingCustomersActivity::class.java))
                            }
                        /*} else {
                            startActivity(Intent(actvity, PendingCustomersActivity::class.java))
                        }*/

                    }
                }else {
                    withContext(Dispatchers.Main) {

                        Toast.makeText(actvity, "Failed,try again later", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
                catch ( e:Exception)
                {

                }
            }}
    }

    fun updateData(
         businessDocs:ArrayList<RequireDocs>?,
         bussPremisesDocs:ArrayList<RequireDocs>?,
         kycDocs:ArrayList<RequireDocs>?,
         coAppKycDocs:ArrayList<RequireDocs>?,
         residentialDocs:ArrayList<RequireDocs>?,
        bmDocumentVerificationActivity: BMDocumentVerificationActivity
    ) {

        list.addAll(businessDocs!!.toList())
        list.addAll(kycDocs!!.toList())
        list.addAll(residentialDocs!!.toList())
        list.addAll(bussPremisesDocs!!.toList())
        this.mContext=bmDocumentVerificationActivity

        DocumentVerificationFragmentNew.context=bmDocumentVerificationActivity
        rvBusinessDocs.adapter=BMVerificationBizDocsAdapter(bmDocumentVerificationActivity,businessDocs!!)
       rvKycDocs.adapter=BMVerificationkycDocsAdapter(bmDocumentVerificationActivity,kycDocs!!)
        rvCoAppsKyc.adapter=BMCoAppKycDocsAdapter(bmDocumentVerificationActivity,coAppKycDocs!!)
       rvResDocs.adapter=BMVerificationResidentialDocsAdapter(bmDocumentVerificationActivity,residentialDocs!!)
       rvbizPremDocs.adapter=BMVerificationBussPremisesDocsAdapter(bmDocumentVerificationActivity,bussPremisesDocs!!)


    }


    fun showPreview(urlString:String?)
    {
        if(ArthanApp.getAppInstance().loginRole=="BM"&&(urlString!=null||urlString!="")) {
            var alert = AlertDialog.Builder(activity)
            val view = activity?.layoutInflater?.inflate(R.layout.shop_img_preview_dialog, null)
            alert.setView(view)
            val close = view?.findViewById<ImageView>(R.id.closePreview)
            val edit = view?.findViewById<ImageView>(R.id.edit)
            val preview = view?.findViewById<ImageView>(R.id.preview)
            edit?.visibility = View.GONE
            Glide.with(context!!).load(urlString).into(preview!!)
            val dialog = alert.create()
            dialog.show()
            close?.setOnClickListener { dialog.dismiss() }


        }
    }

    companion object {
        const val TAG = "DocumentVerifFragment"
        var list=ArrayList<RequireDocs>()
        var context: Context? =null
        fun  modifyDocAt(docId:String,docName:String,docUrl:String,docStatus:String,pos:Int)
        {
            for (i in 0 until list.size)
            {
                if(docId.equals(list[i].docId,ignoreCase = true))
                {
                    list.removeAt(i)
                    list.add(RequireDocs((context as BMDocumentVerificationActivity).intent.getStringExtra(ArgumentKey.LoanId),docId,docName,docUrl,docStatus))

                }
            }
        }

    }
}