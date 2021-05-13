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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BMRequestWaiverActivity : BaseActivity() {
    override fun contentView(): Int {
      return R.layout.activity_bm_request_waiver
    }

    override fun init() {
        cName.setText(intent.getStringExtra("customerName"))
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

            }

    override fun onToolbarBackPressed() {
        finish()
    }

    override fun screenTitle(): String {
        return "BM Approved"
    }
}
