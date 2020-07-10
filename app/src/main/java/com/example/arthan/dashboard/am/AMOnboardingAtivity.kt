package com.example.arthan.dashboard.am

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.arthan.R
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_am_onboarding.*

class AMOnboardingAtivity : BaseActivity() {
    override fun contentView() = R.layout.activity_am_onboarding

    override fun init() {
        Log.d("TAG", "In AMOnboardingAtivity")
    }

    override fun onToolbarBackPressed() = onBackPressed()

    override fun screenTitle() = "AM Onboarding"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        btn_am_onboarding.setOnClickListener {
            startActivity(Intent(this@AMOnboardingAtivity, AddAMKYCDetailsActivity::class.java))
        }
    }
}