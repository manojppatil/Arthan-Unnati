package com.example.arthan.dashboard.am

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.example.arthan.R
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.am_rejected_case_screen_nav.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*

class AMRejectedScreenNavActivity:BaseActivity() {
    override fun contentView() =
         R.layout.am_rejected_case_screen_nav

    override fun init() {
      moveToScreen()
    }

    private fun moveToScreen() {

        when(intent.getStringExtra("screen"))
        {

            "PERSONAL_PA"->{
                supportFragmentManager.beginTransaction().replace(R.id.am_frags,AMPersonaldetailsfragment().apply {
                    var b=Bundle()
                    b.putString("amId",intent.getStringExtra("amId"))
                    b.putString("screen","PERSONAL_PA")
                    b.putString("task","AMRejected")
                    arguments=b
                }).commit()
            }

            "OTHERS","OTHERS_TRADE","OTHERS_SECURITY"->{
                supportFragmentManager.beginTransaction().replace(R.id.am_frags,AMOtherDetailsFragment().apply {
                    var b = Bundle()
                    b.putString("amId", intent.getStringExtra("amId"))
                    b.putString("screen", "OTHERS")
                    b.putString("task", "AMRejected")
                    arguments = b
                }).commit()
            }
            "KYC_PA"->{
                startActivity(Intent(this, AddAMKYCDetailsActivity::class.java).apply {
                    putExtra("screen","KYC_PA")
                    putExtra("amId",intent.getStringExtra("amId"))
                    putExtra("task","AMRejected")
                })

                finish()


            }
            "PROFESSIONAL"->{
                supportFragmentManager.beginTransaction().replace(R.id.am_frags,AMProfessionalDetailsFragment().apply {
                    var b = Bundle()
                    b.putString("amId", intent.getStringExtra("amId"))
                    b.putString("screen", "PROFESSIONAL")
                    b.putString("task", "AMRejected")
                    arguments = b
                }).commit()
            }

        }
    }

    override fun onToolbarBackPressed() =
       finish()
    override fun screenTitle() = intent.getStringExtra("screen")

}