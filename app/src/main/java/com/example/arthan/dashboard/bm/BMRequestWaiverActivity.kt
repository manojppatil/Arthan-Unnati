package com.example.arthan.dashboard.bm

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.arthan.R
import com.example.arthan.dashboard.rm.CommonApprovedListingActivity
import com.example.arthan.global.ArthanApp
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_bm_request_waiver.*
import kotlinx.android.synthetic.main.activity_bm_request_waiver.cName
import kotlinx.android.synthetic.main.activity_bm_request_waiver.loanAmount
import kotlinx.android.synthetic.main.rm_bm_new_requestwaiver.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BMRequestWaiverActivity : BaseActivity() {
    override fun contentView(): Int {
      return R.layout.rm_bm_new_requestwaiver
    }

    override fun init() {

        editRoi.setOnClickListener {
            if(editRoi.text.toString().toLowerCase()=="edit") {
                roi.isEnabled = true
                editRoi.text="Done"
            }
            else{
                editRoi.text="Edit"
                roi.isEnabled=false

            }
        }
        editPf.setOnClickListener {
            if(editPf.text.toString().toLowerCase()=="edit") {
                pf.isEnabled = true
                editPf.text="Done"
            }
            else{
                editPf.text="Edit"
                pf.isEnabled=false

            }
        }
        if(ArthanApp.getAppInstance().loginRole=="BM")
        {
            cName.text=intent.getStringExtra("customerName")
            loanAmount.text=intent.getStringExtra("loanAmount")
            roi.setText(intent.getStringExtra("roi"))
            pf.setText(intent.getStringExtra("pf"))
            cb_collect_legalfee.visibility=View.GONE
            legalLabel.visibility=View.GONE
            techLabel.visibility=View.GONE
            cb_collect_techfee.visibility=View.GONE

            if(intent.getStringExtra("waiveLegal")=="true")
            {
                cb_waiver_legalfee.isChecked=true
            }else
            {
                cb_waiver_legalfee.visibility=View.GONE
            }

            if(intent.getStringExtra("waiveTech")=="true")
            {
                cb_waiver_techfee.isChecked=true
            }else
            {
                cb_waiver_techfee.visibility=View.GONE
            }

            if(intent.getStringExtra("deductLegal")=="true")
            {
                cb_deduct_disblegal.isChecked=true
            }else
            {
                cb_deduct_disblegal.visibility=View.GONE
            }

            if(intent.getStringExtra("deductTech")=="true")
            {
                cb_deduct_disbtech.isChecked=true
            }else
            {
                cb_deduct_disbtech.visibility=View.GONE
            }

            roiLL.visibility=View.VISIBLE
            pfLL.visibility=View.VISIBLE
        }
        btn_requestWaiver.setOnClickListener {
            var map = HashMap<String, String>()
            map["loanId"] = intent.getStringExtra("loanId")!!
            map["waiveLegal"] = cb_waiver_legalfee.isChecked.toString()
            map["waiveTech"] = cb_waiver_techfee.isChecked.toString()
            map["deductLegal"] = cb_deduct_disblegal.isChecked.toString()
            map["deductTech"] = cb_deduct_disbtech.isChecked.toString()
            map["roi"] = roi.text.toString()
            map["pf"] = pf.text.toString()
          /*  map["cbo"] = submitCBO.isChecked.toString()
            map["roi"] = cb_roi.isChecked.toString()
            map["pf"]= cb_pf.isChecked.toString()
            map["ltFee"]=cb_tl.isChecked.toString()*/
            map["remarks"]=remarks.text.toString()
          /*  map["bmDecision"]= when{
                rb_approve.isChecked->"approved"
                else->"reject"
            }*/
            val progrssLoader=ProgrssLoader(this)
            progrssLoader.showLoading()
            CoroutineScope(Dispatchers.IO).launch {
                val res=RetrofitFactory.getApiService().bmRequestWaiver(map)
                if(res?.body()!=null)
                {
                    withContext(Dispatchers.Main){
                        progrssLoader.dismmissLoading()
                        Toast.makeText(this@BMRequestWaiverActivity, "Request successful", Toast.LENGTH_LONG)
                            .show()
                        finish()
                        startActivity(Intent(this@BMRequestWaiverActivity,CommonApprovedListingActivity::class.java))
                    }
                }
            }
        }

        /*cName.setText(intent.getStringExtra("customerName"))
        loanAmount.setText(intent.getStringExtra("loanAmount"))
        roiValue.text=intent.getStringExtra("roi")
        requestType.text=intent.getStringExtra("requestType")
        pfValue.text=intent.getStringExtra("pf")
        remarksText.text=intent.getStringExtra("remarks")
        waiverAmount.text=intent.getStringExtra("waiverAmount")
        submitWaiver.setOnClickListener {
            var map = HashMap<String, String>()
            map["loanId"] = intent.getStringExtra("loanId")
            map["cbo"] = submitCBO.isChecked.toString()
            map["roi"] = cb_roi.isChecked.toString()
            map["pf"]= cb_pf.isChecked.toString()
            map["ltFee"]=cb_tl.isChecked.toString()
            map["remarks"]=bmRemarks.text.toString()
            map["bmDecision"]= when{
                rb_approve.isChecked->"approved"
                else->"reject"
            }
            val progrssLoader=ProgrssLoader(this)
            progrssLoader.showLoading()
            CoroutineScope(Dispatchers.IO).launch {
                val res=RetrofitFactory.getApiService().bmRequestWaiver(map)
                if(res?.body()!=null)
                {
                    withContext(Dispatchers.Main){
                        progrssLoader.dismmissLoading()
                        Toast.makeText(this@BMRequestWaiverActivity, "Request successful", Toast.LENGTH_LONG)
                            .show()
                       finish()
                       startActivity(Intent(this@BMRequestWaiverActivity,CommonApprovedListingActivity::class.java))
                    }
                }
            }
        }
        submitCBO.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {
                cboSubmitLl.visibility=View.VISIBLE
            }
            else
            {
                cboSubmitLl.visibility=View.GONE
                cb_roi.isChecked=false
                cb_pf.isChecked=false
                cb_tl.isChecked=false

            }
        }
*/
            }

    override fun onToolbarBackPressed() {
        finish()
    }

    override fun screenTitle(): String {
        return "BM Approved"
    }
}
