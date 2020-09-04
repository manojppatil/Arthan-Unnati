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
import kotlinx.android.synthetic.main.activity_approved_customer_legal_status.*
import kotlinx.android.synthetic.main.activity_b_c_m_approved_legal_status.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BCMApprovedLegalStatusActivity : BaseActivity() {
    override fun contentView() = R.layout.activity_b_c_m_approved_legal_status
     var getLegalnTechBCMResponse:GetLegalnTechBCMResponse?=null
    override fun init() {

        coApplicantAdd.setOnClickListener {

            startActivity(Intent(this,BCMApprovedAddCoApplicant::class.java).apply {
                putExtra("loanId",intent.getStringExtra("loanId"))
                putExtra("task","BCMApproved")
            })
        }
        viewReport.setOnClickListener {
            startActivity(Intent(this,ShowPDFActivity::class.java).apply {
                putExtra("pdf_url",getLegalnTechBCMResponse?.legalRptUrl)
            })
        }
        loadInitialData()

        submitlegal.setOnClickListener {

            val progrssLoader=ProgrssLoader(this)
            progrssLoader.showLoading()
            val map=HashMap<String,String>()
            map["userId"]= ArthanApp.getAppInstance().loginUser
            map[ "loanId"]=intent.getStringExtra("loanId")
            map["remarks"]=Legalremarks.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                val res=RetrofitFactory.getApiService().bcmLegalSubmit(map)
                if(res.body()!=null)
                {
                    withContext(Dispatchers.Main)
                    {
                        finish()
                        progrssLoader.dismmissLoading()

                        Toast.makeText(this@BCMApprovedLegalStatusActivity,"Submitted",
                            Toast.LENGTH_LONG).show()

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

    private fun loadInitialData() {

        val progressBar=ProgrssLoader(this)
        progressBar.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            val response=RetrofitFactory.getApiService().getLegalBcmData(intent.getStringExtra("loanId"))
            if(response?.body()!=null)
            {
                getLegalnTechBCMResponse=response.body()!!
                withContext(Dispatchers.Main){
                    progressBar.dismmissLoading()
                }
            }
            else{
                withContext(Dispatchers.Main)

                {
                    progressBar.dismmissLoading()
                }
            }
        }
    }

    override fun onToolbarBackPressed() {
        finish()
    }


    override fun screenTitle(): String {
        return "Legal"

    }
}
