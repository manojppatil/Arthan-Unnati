package com.example.arthan.dashboard.bm

import android.app.AlertDialog
import android.content.Context
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
import com.example.arthan.global.ArthanApp
import com.example.arthan.lead.model.postdata.DocScreeningStatusPost
import com.example.arthan.lead.model.postdata.DocumentsData
import com.example.arthan.lead.model.responsedata.DocDetails
import com.example.arthan.lead.model.responsedata.DocDetailsAM
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_document_verification.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DocumentVerificationFragment : BaseFragment() {

    override fun contentView() = R.layout.fragment_document_verification

    private var isPanApproved=false
    private var isAdhaarFrontApproved=false
    private var isAdhaarBackApproved=false
    private var isVoterApproved=false
    private var isApplicantpproved=false
    private var isBusinessProof=false
    private var isOfficeAddressProof=false
    private var isIncomeProof=false
    private var docDetails:DocDetails?=null
    private var docDetailsam:DocDetailsAM?=null
    private var mContext:Context?=null
    private var textColor:Int=Color.parseColor("#09327a")
    override fun init() {
        img_pan.setOnClickListener {
            showPreview(docDetails?.panUrl)

        }
        img_aadhaar.setOnClickListener {  showPreview(docDetails?.aadharFrontUrl)}
        img_aadhaar_back.setOnClickListener {
            showPreview(docDetails?.aadharBackUrl)
        }
        img_voter.setOnClickListener {
            showPreview(docDetails?.voterUrl)
        }

        img_applicant.setOnClickListener {
            showPreview(docDetails?.paApplicantPhoto)
        }
        img_OfficeAddressProof.setOnClickListener {
            showPreview(docDetails?.businessAddrProof)
        }
        img_BusinessProof.setOnClickListener {
            showPreview(docDetails?.businessProof)
        }
        img_IncomeProof.setOnClickListener {
            showPreview(docDetails?.incomeProof)
        }

        btn_aadhar_approve.setOnClickListener {
            isAdhaarFrontApproved = true
            btn_aadhar_approve.setBackgroundResource(R.drawable.ic_next_enabled)
            btn_aadhar_disapprove.setBackgroundResource(R.drawable.ic_next_disable)
            btn_aadhar_approve.setTextColor(Color.WHITE)
            btn_aadhar_back_disapprove.setTextColor(textColor)
        }
        btn_aadhar_disapprove.setOnClickListener { isAdhaarFrontApproved = false
            btn_aadhar_disapprove.setBackgroundResource(R.drawable.ic_next_enabled)
            btn_aadhar_approve.setBackgroundResource(R.drawable.ic_next_disable)
            btn_aadhar_disapprove.setTextColor(Color.WHITE)
            btn_aadhar_approve.setTextColor(textColor)
        }

        btn_aadhar_back_disapprove.setOnClickListener { isAdhaarBackApproved = false
            btn_aadhar_back_disapprove.setBackgroundResource(R.drawable.ic_next_enabled)
            btn_aadhar_back_approve.setBackgroundResource(R.drawable.ic_next_disable)
            btn_aadhar_back_disapprove.setTextColor(Color.WHITE)
            btn_aadhar_back_approve.setTextColor(textColor)
        }

        btn_aadhar_back_approve.setOnClickListener { isAdhaarBackApproved = true
            btn_aadhar_back_approve.setBackgroundResource(R.drawable.ic_next_enabled)
            btn_aadhar_back_disapprove.setBackgroundResource(R.drawable.ic_next_disable)
            btn_aadhar_back_approve.setTextColor(Color.WHITE)
            btn_aadhar_back_disapprove.setTextColor(textColor)
        }

        btn_pan_approve.setOnClickListener { isPanApproved = true
            btn_pan_approve.setBackgroundResource(R.drawable.ic_next_enabled)
            btn_pan_disapprove.setBackgroundResource(R.drawable.ic_next_disable)
            btn_pan_approve.setTextColor(Color.WHITE)
            btn_pan_disapprove.setTextColor(textColor)
        }

        btn_pan_disapprove.setOnClickListener { isPanApproved = false
            btn_pan_disapprove.setBackgroundResource(R.drawable.ic_next_enabled)
            btn_pan_approve.setBackgroundResource(R.drawable.ic_next_disable)
            btn_pan_disapprove.setTextColor(Color.WHITE)
            btn_pan_approve.setTextColor(textColor)
        }

        btn_voter_approve.setOnClickListener { isVoterApproved = true
            btn_voter_approve.setBackgroundResource(R.drawable.ic_next_enabled)
            btn_voter_disapprove.setBackgroundResource(R.drawable.ic_next_disable)
            btn_voter_approve.setTextColor(Color.WHITE)
            btn_voter_disapprove.setTextColor(textColor)
        }

        btn_voter_disapprove.setOnClickListener { isVoterApproved = false
            btn_voter_disapprove.setBackgroundResource(R.drawable.ic_next_enabled)
            btn_voter_approve.setBackgroundResource(R.drawable.ic_next_disable)
            btn_voter_disapprove.setTextColor(Color.WHITE)
            btn_voter_approve.setTextColor(textColor)
        }

        btn_applicant_approve.setOnClickListener { isApplicantpproved = true
            btn_applicant_approve.setBackgroundResource(R.drawable.ic_next_enabled)
            btn_applicant_disapprove.setBackgroundResource(R.drawable.ic_next_disable)
            btn_applicant_approve.setTextColor(Color.WHITE)
            btn_applicant_disapprove.setTextColor(textColor)
        }

        btn_applicant_disapprove.setOnClickListener { isApplicantpproved = false
            btn_applicant_disapprove.setBackgroundResource(R.drawable.ic_next_enabled)
            btn_applicant_approve.setBackgroundResource(R.drawable.ic_next_disable)
            btn_applicant_disapprove.setTextColor(Color.WHITE)
            btn_applicant_approve.setTextColor(textColor)
        }
        btn_BusinessProof_approve.setOnClickListener { isBusinessProof = true
            btn_BusinessProof_approve.setBackgroundResource(R.drawable.ic_next_enabled)
            btn_BusinessProof_disapprove.setBackgroundResource(R.drawable.ic_next_disable)
            btn_BusinessProof_approve.setTextColor(Color.WHITE)
            btn_BusinessProof_disapprove.setTextColor(textColor)
        }

        btn_BusinessProof_disapprove.setOnClickListener { isBusinessProof = false
            btn_BusinessProof_disapprove.setBackgroundResource(R.drawable.ic_next_enabled)
            btn_BusinessProof_approve.setBackgroundResource(R.drawable.ic_next_disable)
            btn_BusinessProof_disapprove.setTextColor(Color.WHITE)
            btn_BusinessProof_approve.setTextColor(textColor)
        }


        btn_IncomeProof_approve.setOnClickListener { isIncomeProof   = true
            btn_IncomeProof_approve.setBackgroundResource(R.drawable.ic_next_enabled)
            btn_IncomeProof_disapprove.setBackgroundResource(R.drawable.ic_next_disable)
            btn_IncomeProof_approve.setTextColor(Color.WHITE)
            btn_IncomeProof_disapprove.setTextColor(textColor)
        }
        btn_IncomeProof_disapprove.setOnClickListener { isIncomeProof = false
            btn_IncomeProof_disapprove.setBackgroundResource(R.drawable.ic_next_enabled)
            btn_IncomeProof_approve.setBackgroundResource(R.drawable.ic_next_disable)
            btn_IncomeProof_disapprove.setTextColor(Color.WHITE)
            btn_IncomeProof_approve.setTextColor(textColor)
        }

        btn_OfficeAddressProof_approve.setOnClickListener { isOfficeAddressProof = true
            btn_OfficeAddressProof_approve.setBackgroundResource(R.drawable.ic_next_enabled)
            btn_OfficeAddressProof_disapprove.setBackgroundResource(R.drawable.ic_next_disable)
            btn_OfficeAddressProof_approve.setTextColor(Color.WHITE)
            btn_OfficeAddressProof_disapprove.setTextColor(textColor)
        }
        btn_OfficeAddressProof_disapprove.setOnClickListener { isOfficeAddressProof = false
            btn_OfficeAddressProof_disapprove.setBackgroundResource(R.drawable.ic_next_enabled)
            btn_OfficeAddressProof_approve.setBackgroundResource(R.drawable.ic_next_disable)
            btn_OfficeAddressProof_disapprove.setTextColor(Color.WHITE)
            btn_OfficeAddressProof_approve.setTextColor(textColor)
        }
        btn_next.setOnClickListener {

            val progressBar = ProgrssLoader(mContext!!)
            progressBar.showLoading()
            var actvity=context as BMDocumentVerificationActivity
            var mLoanId=actvity.intent.getStringExtra(ArgumentKey.LoanId)
            var mCustomerId=actvity.intent.getStringExtra(ArgumentKey.CustomerId)
            var list=ArrayList<DocumentsData>()

            //businessProof/businessAddrProof/incomeProof)
            list.add(DocumentsData("","aadhar_front",docUrl = docDetails?.aadharFrontUrl,docStatus = isAdhaarFrontApproved.toString()))
            list.add(DocumentsData("","aadhar_back",docDetails?.aadharBackUrl,isAdhaarBackApproved.toString()))
            list.add(DocumentsData("","voter",docDetails?.voterUrl,isVoterApproved.toString()))
            list.add(DocumentsData("","pan",docDetails?.panUrl,isPanApproved.toString()))
            list.add(DocumentsData("","paApplicantPhoto",docDetails?.paApplicantPhoto,isApplicantpproved.toString()))
            list.add(DocumentsData("","businessProof",docDetails?.businessProof,isBusinessProof.toString()))
            list.add(DocumentsData("","businessAddrProof",docDetails?.businessAddrProof,isOfficeAddressProof.toString()))
            list.add(DocumentsData("","incomeProof",docDetails?.incomeProof,isIncomeProof.toString()))
            val postBody = DocScreeningStatusPost(
                loanId = mLoanId,
                userId=ArthanApp.getAppInstance().loginUser,
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

                progressBar.dismmissLoading()
                if (response.body()?.apiCode == "200") {

                    if (ArthanApp.getAppInstance().loginRole == "BM" || ArthanApp.getAppInstance().loginRole == "BCM") {
                        actvity.moveToData()
                    }
                    /*if(response.body()?.discrepancy!!.equals("y",ignoreCase = true))
                        {
                            if(ArthanApp.getAppInstance().loginRole=="BM"||ArthanApp.getAppInstance().loginRole=="BCM")
                            {
                             startActivity(Intent(actvity,PendingCustomersActivity::class.java))
                            }else
                            {

                            }
                        }else {
                        actvity.moveToData()
                    }
*/

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

            Glide.with(it)
                .load(docDetails?.incomeProof)
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
                .into(img_IncomeProof)
            Glide.with(it)
                .load(docDetails?.businessAddrProof)
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
                .into(img_OfficeAddressProof)
            Glide.with(it)
                .load(docDetails?.businessProof)
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
                .into(img_BusinessProof)
        }
    }

    fun updateDataAM(
        docDetails: DocDetailsAM?,
        bmDocumentVerificationActivity: BMDocumentVerificationActivity
    ) {

        this.docDetailsam=docDetails
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

            Glide.with(it)
                .load(docDetails?.incomeProof)
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
                .into(img_IncomeProof)
            Glide.with(it)
                .load(docDetails?.businessAddrProof)
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
                .into(img_OfficeAddressProof)
            Glide.with(it)
                .load(docDetails?.businessProof)
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
                .into(img_BusinessProof)
        }
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
    }
}