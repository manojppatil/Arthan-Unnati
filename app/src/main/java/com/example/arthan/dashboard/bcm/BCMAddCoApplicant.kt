package com.example.arthan.dashboard.bcm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.arthan.R
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_b_c_m_add_co_applicant.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BCMAddCoApplicant : BaseActivity() {
    override fun contentView(): Int {
        return R.layout.activity_b_c_m_add_co_applicant
    }

    override fun init() {
        coApplicantAdd.setOnClickListener {

            startActivity(Intent(this,BCMApprovedAddCoApplicant::class.java).apply {
                putExtra("loanId",intent.getStringExtra("loanId"))
                putExtra("task","Add-CoApplicant")
                putExtra("stage","Add-CoApplicant")
            })
        }

        submitlegal.setOnClickListener {
            val progres=ProgrssLoader(this)
            progres.showLoading()
            val map=HashMap<String,String>()
            map["loanId"] = intent.getStringExtra("loanId")!!
            map["remarks"] = Legalremarks.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                val res=RetrofitFactory.getApiService().bcmReAssignSubmit(map)
                if(res?.body()!=null)
                {
                    withContext(Dispatchers.Main){
                        progres.dismmissLoading()
                        Toast.makeText(this@BCMAddCoApplicant,"Submitted successfully",Toast.LENGTH_LONG).show()
                        finish()
                    }
                }else
                {
                    withContext(Dispatchers.Main) {
                        progres.dismmissLoading()
                    }
                }

            }

        }
    }

    override fun onToolbarBackPressed() {
       finish()
    }

    override fun screenTitle(): String {
        return "BCM Reassigned"
    }
}
