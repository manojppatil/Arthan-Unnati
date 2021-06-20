package com.example.arthan.dashboard.bcm

import android.content.Intent
import android.widget.Toast
import com.example.arthan.R
import com.example.arthan.dashboard.bm.ShowPDFActivity
import com.example.arthan.global.ArthanApp
import com.example.arthan.model.GetLegalnTechBCMResponse
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_b_c_m_approved_tech_doc.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BCMApprovedTechDocActivity : BaseActivity() {
    override fun contentView(): Int {

        return R.layout.activity_b_c_m_approved_tech_doc
    }

     var techData:GetLegalnTechBCMResponse?=null
    override fun init() {
     loadInitialData()
        viewTechReport.setOnClickListener {
            startActivity(Intent(this,ShowPDFActivity::class.java).apply {
                putExtra("pdf_url",techData?.techRptUrl)
            })
        }
    }

    private fun loadInitialData() {

        val progress = ProgrssLoader(this)
        progress.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            val response=RetrofitFactory.getApiService().getTechBcmData(intent.getStringExtra("loanId"))
            if(response?.body()!=null)
            {
                techData=response.body()!!
                withContext(Dispatchers.Main)
                {
                    progress.dismmissLoading()
                    setUiData()
                }
            }
        }

    }

    private fun setUiData() {

        newLabelValue1.text=techData?.ltvNew
        newLabelValue2.text=techData?.loanAmtNew
        oldLabelValue1.text=techData?.ltvOld
        oldLabelValue2.text=techData?.loanAmtOld
        submitTech.setOnClickListener {
            val progrssLoader=ProgrssLoader(this)
            progrssLoader.showLoading()
            val map=HashMap<String,String>()
            map["userId"]=ArthanApp.getAppInstance().loginUser
            map["loanId"]=intent.getStringExtra("loanId")!!
            map[ "remarks"]=TechRemarks.text.toString()
            map[ "bcmLoanAmt"]=assumedLoan.text.toString()
            CoroutineScope(Dispatchers.IO).launch {

                val result=RetrofitFactory.getApiService().bcmTechSubmit(map)
                if(result.body()!=null)
                {
                    withContext(Dispatchers.Main)
                    {
                        progrssLoader.dismmissLoading()
                        finish()
                        Toast.makeText(this@BCMApprovedTechDocActivity,"Submitted",Toast.LENGTH_LONG).show()
                    }
                }else
                {
                    withContext(Dispatchers.Main)
                    {
                        progrssLoader.dismmissLoading()
                    }
                }

            }
        }

    }

    override fun onToolbarBackPressed() {
        finish()
    }


    override fun screenTitle(): String {

        return "Technical"
    }
}
