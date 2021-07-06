package com.example.arthan.dashboard.rm

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.arthan.R
import com.example.arthan.global.AppPreferences
import com.example.arthan.global.ArthanApp
import com.example.arthan.lead.AddLeadStep2Activity
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.rm_bm_new_requestwaiver.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RMRequestWaiverActivity : BaseActivity() {
    override fun contentView(): Int {
//      return R.layout.activity_r_m_request_waiver
      return R.layout.rm_bm_new_requestwaiver

    }

    private var waiveOption=""
    private var custId=""
    private var loanId=""
    private var aplicantType=""
    private fun getCustomerId() {

        val map=HashMap<String,String>()
        var loanId=""
        if(intent.getStringExtra("loanId")!=null)
        {
            loanId=intent.getStringExtra("loanId")?: AppPreferences.getInstance().getString(
                AppPreferences.Key.LoanId) ?: ""
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

    override fun init() {

        if(ArthanApp.getAppInstance().loginRole=="RM")
        {
            roiLL.visibility=View.GONE
            pfLL.visibility=View.GONE
            cName.text=intent.getStringExtra("customerName")
            loanAmount.text=intent.getStringExtra("loanAmount")
            roi.setText(intent.getStringExtra("roi"))
            pf.setText(intent.getStringExtra("pf"))

            getCustomerId()

        }

        addnewApplicant.setOnClickListener {
            val dialog= AlertDialog.Builder(this)
            dialog.setTitle("Add new Applicant")
            dialog.setMessage("Select the type of Applicant you want to Add")
            dialog.setNegativeButton("Guarantor", DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
                startActivity(Intent(this, AddLeadStep2Activity::class.java).apply {
                    putExtra("screen","KYC_PA")
                    putExtra("type","G")
                    putExtra("loanId",loanId)
                    putExtra("custId",custId)
                    putExtra("task","RmApprovedCo")
                })
//                finish()
            })
            dialog.setPositiveButton("Co-Applicant", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                startActivity(Intent(this, AddLeadStep2Activity::class.java).apply {
                    putExtra("screen","KYC_PA")
                    putExtra("type","CA")
                    putExtra("loanId",loanId)
                    putExtra("custId",custId)
                    putExtra("task","RmApprovedCo")
                })
//                finish()
            })
            dialog.setNeutralButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            dialog.create().show()
        }
        if(ArthanApp.getAppInstance().loginRole=="BM")
        {
            roiLL.visibility=View.VISIBLE
            pfLL.visibility=View.VISIBLE
        }

        cb_collect_legalfee.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {
                cb_waiver_legalfee.isChecked=false
                cb_deduct_disblegal.isChecked=false
                cb_waiver_legalfee.visibility=View.GONE
                cb_deduct_disblegal.visibility=View.GONE

            }
            else
            {
                cb_waiver_legalfee.visibility=View.VISIBLE
                cb_deduct_disblegal.visibility=View.VISIBLE
            }

        }
        cb_waiver_legalfee.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked)
            {
                cb_collect_legalfee.isChecked=false
                cb_deduct_disblegal.isChecked=false
                cb_collect_legalfee.visibility=View.GONE
                cb_deduct_disblegal.visibility=View.GONE
            }else
            {
                cb_collect_legalfee.visibility=View.VISIBLE
                cb_deduct_disblegal.visibility=View.VISIBLE
            }
        }
        cb_deduct_disblegal.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked)
            {
                cb_collect_legalfee.isChecked=false
                cb_waiver_legalfee.isChecked=false
                cb_waiver_legalfee.visibility=View.GONE
                cb_collect_legalfee.visibility=View.GONE
            }else
            {
                cb_waiver_legalfee.visibility=View.VISIBLE
                cb_collect_legalfee.visibility=View.VISIBLE
            }
        }

        cb_collect_techfee.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {
                cb_waiver_techfee.isChecked=false
                cb_deduct_disbtech.isChecked=false
                cb_waiver_techfee.visibility=View.GONE
                cb_deduct_disbtech.visibility=View.GONE

            }
            else
            {
                cb_waiver_techfee.visibility=View.VISIBLE
                cb_deduct_disbtech.visibility=View.VISIBLE
            }

        }
        cb_waiver_techfee.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked)
            {
                cb_collect_techfee.isChecked=false
                cb_deduct_disbtech.isChecked=false
                cb_collect_techfee.visibility=View.GONE
                cb_deduct_disbtech.visibility=View.GONE
            }else
            {
                cb_collect_techfee.visibility=View.VISIBLE
                cb_deduct_disbtech.visibility=View.VISIBLE
            }
        }
        cb_deduct_disbtech.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked)
            {
                cb_collect_techfee.isChecked=false
                cb_waiver_techfee.isChecked=false
                cb_waiver_techfee.visibility=View.GONE
                cb_collect_techfee.visibility=View.GONE
            }else
            {
                cb_waiver_techfee.visibility=View.VISIBLE
                cb_collect_techfee.visibility=View.VISIBLE
            }
        }

        btn_requestWaiver.setOnClickListener {

            val progressLoader = ProgrssLoader(this)
            progressLoader.showLoading()
            if(ArthanApp.getAppInstance().loginRole=="RM") {
                var map = HashMap<String, String>()
                map["loanId"] = intent.getStringExtra("loanId")!!
                map["waiveLegal"] = cb_waiver_legalfee.isChecked.toString()
                map["waiveTech"] = cb_waiver_techfee.isChecked.toString()
                map["deductLegal"] = cb_deduct_disblegal.isChecked.toString()
                map["deductTech"] = cb_deduct_disbtech.isChecked.toString()
                map["roi"] = roi.text.toString()
                map["pf"] = pf.text.toString()
                map["remarks"] = remarks.text.toString()
                /* map["roi"] = roi.text.toString()
            map["pf"] = pf.text.toString()*/
             /*   map["eId"] = "RM1"
                map["userId"] = ArthanApp.getAppInstance().loginUser*/
//                        map["waiveAmt"] = waiverAmt.text.toString()
//            map["waiveOption"] = waiveOption

                CoroutineScope(Dispatchers.IO).launch {

                    var res = RetrofitFactory.getApiService().rmRequestWaiver(map)
                    if (res?.body() != null) {
                        withContext(Dispatchers.Main) {
                            progressLoader.dismmissLoading()

                            Toast.makeText(
                                this@RMRequestWaiverActivity,
                                "Request successful",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            finish()
                            startActivity(
                                Intent(
                                    this@RMRequestWaiverActivity,
                                    CommonApprovedListingActivity::class.java
                                )
                            )
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            progressLoader.dismmissLoading()

                        }
                    }
                }
            }else if(ArthanApp.getAppInstance().loginRole=="BM")
            {

            }

        }
       /* btn_requestWaiver.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                waiveOption = "Req. Waiver"
                waiverAmt.visibility = View.VISIBLE
                remarks.visibility = View.VISIBLE
            } else {
                waiverAmt.visibility = View.GONE
                remarks.visibility = View.GONE

            }
        }
        btn_collectDisb_fees.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                waiveOption = "Collect From Disb. Amt"
*//*
                val progess=ProgrssLoader(this)
                progess.showLoading()
                val map=HashMap<String,String>()
                map["loanId"] = intent.getStringExtra("loanId")
//            map["remarks"] = remarks.text.toString()
                map["eId"] = "RM1"
                map["userId"] = ArthanApp.getAppInstance().loginUser
                CoroutineScope(Dispatchers.IO).launch {
                    val res=RetrofitFactory.getApiService().payRLTFee(map)
                    if(res.body()!=null)
                    {
                        withContext(Dispatchers.Main){
                            Toast.makeText(
                                this@RMRequestWaiverActivity,
                                "Request successful",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            finish()
                            startActivity(
                                Intent(
                                    this@RMRequestWaiverActivity,
                                    CommonApprovedListingActivity::class.java
                                )
                            )
                        }
                    }
                }
            }
        }*//*
            }
        }

                btn_collect_fees.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        waiveOption = "Collect fees"

                    }
                }
*/
                /*submitWaiver.setOnClickListener {
                    if (waiveOption == "Collect fees") {
                        val map = HashMap<String, String>()
                        map["loanId"] = intent.getStringExtra("loanId")
//                        map["remarks"] = remarks.text.toString()
                        map["eId"] = "RM1"
                        map["userId"] = ArthanApp.getAppInstance().loginUser
                        CoroutineScope(Dispatchers.IO).launch {
                            var res = RetrofitFactory.getApiService()
//                                .sendPaymentLink(intent.getStringExtra("loanId"))
                                .payRLTFee(map)
                            if (res?.body() != null) {
                                withContext(Dispatchers.Main)
                                {
                                    Toast.makeText(
                                        this@RMRequestWaiverActivity,
                                        "Payment link sent to customer successfully",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    finish()
                                    startActivity(
                                        Intent(
                                            this@RMRequestWaiverActivity,
                                            CommonApprovedListingActivity::class.java
                                        )
                                    )
                                }
                            }
                        }
                    } else {

                        val progressLoader = ProgrssLoader(this)
                        progressLoader.showLoading()
                        var map = HashMap<String, String>()
                        map["loanId"] = intent.getStringExtra("loanId")
                        map["remarks"] = remarks.text.toString()
                        map["roi"] = roi.text.toString()
                        map["pf"] = pf.text.toString()
                        map["eId"] = "RM1"
                        map["userId"] = ArthanApp.getAppInstance().loginUser
//                        map["waiveAmt"] = waiverAmt.text.toString()
                        map["waiveOption"] = waiveOption
                        CoroutineScope(Dispatchers.IO).launch {

                            var res = RetrofitFactory.getApiService().rmRequestWaiver(map)
                            if (res?.body() != null) {
                                withContext(Dispatchers.Main) {
                                    progressLoader.dismmissLoading()

                                    Toast.makeText(
                                        this@RMRequestWaiverActivity,
                                        "Request successful",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                    finish()
                                    startActivity(
                                        Intent(
                                            this@RMRequestWaiverActivity,
                                            CommonApprovedListingActivity::class.java
                                        )
                                    )
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    progressLoader.dismmissLoading()

                                }
                            }
                        }
                    }
                }*/
            }

    override fun onToolbarBackPressed() {
        finish()
    }

    override fun screenTitle(): String {
        return "RM Approved"
    }
}
