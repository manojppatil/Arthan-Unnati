package com.example.arthan.dashboard.rm

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
import com.example.arthan.global.ArthanApp
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_r_m_request_waiver.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RMRequestWaiverActivity : BaseActivity() {
    override fun contentView(): Int {
      return R.layout.activity_r_m_request_waiver
    }

    override fun init() {

        btn_collect_fees.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {
                var res= RetrofitFactory.getApiService().sendPaymentLink(intent.getStringExtra("loanId"))
                if(res?.body()!=null)
                {
                    withContext(Dispatchers.Main)
                    {
                        Toast.makeText(this@RMRequestWaiverActivity,"Payment link sent to customer successfully",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
       submitWaiver.setOnClickListener {

            val progressLoader = ProgrssLoader(this)
            progressLoader.showLoading()
            var map = HashMap<String, String>()
            map["loanId"] = intent.getStringExtra("loanId")
//            map["remarks"] = remarks.text.toString()
            map["eId"] = "RM1"
            map["userId"] = ArthanApp.getAppInstance().loginUser
            map["waiveAmt"] = waiverAmt.text.toString()
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
                }else
                    {
                        withContext(Dispatchers.Main) {
                            progressLoader.dismmissLoading()

                        }
                }
            }
        }
    }

    override fun onToolbarBackPressed() {
        finish()
    }

    override fun screenTitle(): String {
        return "RM Approved"
    }
}
