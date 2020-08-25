package com.example.arthan.dashboard.bcm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.arthan.R
import com.example.arthan.global.ArthanApp
import com.example.arthan.model.EXceptionReportResponse
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_exception_report.*
import kotlinx.android.synthetic.main.collateral_section.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExceptionReportActivity : BaseActivity() {
    override fun contentView(): Int {

        return R.layout.activity_exception_report
    }

    override fun init() {

        getExceptionReport()
    }

    private fun getExceptionReport() {

        val progress=ProgrssLoader(this)
        progress.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            val res=RetrofitFactory.getApiService().getExceptionRpt(intent.getStringExtra("loanId"))
            if(res.body()!=null)
            {
                withContext(Dispatchers.Main)
                {
                    progress.dismmissLoading()
                    setupUI(res.body()!!)
                }
            }else
            {
                withContext(Dispatchers.Main)
                {
                    Toast.makeText(this@ExceptionReportActivity,"Please try again later",Toast.LENGTH_LONG).show()
                    progress.dismmissLoading()
                }
            }
        }

    }

    private fun setupUI(body: EXceptionReportResponse) {

        legalValue1.text=body.propertyOwner.legal
        legalValue2.text=body.area.legal
        legalValue3.text=body.east.legal
        legalValue4.text=body.west.legal
        legalValue5.text=body.north.legal
        legalValue6.text=body.south.legal

        techvalue1.text=body.propertyOwner.tech
        techvalue2.text=body.area.tech
        techvalue3.text=body.east.tech
        techvalue4.text=body.west.tech
        techvalue5.text=body.north.tech
        techvalue6.text=body.south.tech

        submitException.setOnClickListener {
            val map=HashMap<String,String>()
            map["loanId"]=intent.getStringExtra("loanId")
            map["userId"]=ArthanApp.getAppInstance().loginUser
            map["propOwner"]=remarksvalue1.text.toString()
            map["area"]=remarksvalue2.text.toString()
            map["west"]=remarksvalue3.text.toString()
            map["north"]=remarksvalue4.text.toString()
            map["south"]=remarksvalue5.text.toString()

            val progrssLoader=ProgrssLoader(this)
            progrssLoader.showLoading()
            CoroutineScope(Dispatchers.IO).launch {
                val res=RetrofitFactory.getApiService().submitExceptionRpt(map)
                if(res.body()!=null)
                {
                    withContext(Dispatchers.Main)
                    {
                        progrssLoader.dismmissLoading()
                        Toast.makeText(this@ExceptionReportActivity,"Submitted successfully",Toast.LENGTH_LONG).show()
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
        return "Exception Report"
    }
}
