package com.example.arthan.dashboard.legal

import android.content.Intent
import android.view.View
import com.example.arthan.R
import com.example.arthan.global.ArthanApp
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.ScreeningActivity
import kotlinx.android.synthetic.main.activity_legal_dashboard.*

class LegalDashboardActivity: BaseActivity() {

    override fun contentView()= R.layout.activity_legal_dashboard

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {
        layout_toolbar.visibility= View.GONE

        cv_pending.setOnClickListener {
            startActivity(
                Intent(this@LegalDashboardActivity,
                    ScreeningActivity::class.java).apply {
                    putExtra("FROM", ArthanApp.getAppInstance().loginRole)
                }
            )
        }
    }

    override fun screenTitle()= ""
}