package com.example.arthan.views.activities

import android.view.View
import com.example.arthan.R
import com.example.arthan.global.ArthanApp
import com.example.arthan.views.adapters.ProfileTabAdapter
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*

class LegalRcuApplicationInfoActivity : BaseActivity() {

    override fun contentView() = R.layout.activity_legal_rcu_application_info

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        btn_search.visibility = View.GONE
        btn_filter.visibility = View.GONE

        vp_profile.adapter =
            ProfileTabAdapter(supportFragmentManager, ArthanApp.getAppInstance().loginRole)
        tb_profile.setupWithViewPager(vp_profile)

    }

    override fun screenTitle() = "Profile"
}