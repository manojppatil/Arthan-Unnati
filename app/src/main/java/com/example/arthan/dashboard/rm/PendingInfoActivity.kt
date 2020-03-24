package com.example.arthan.dashboard.rm

import com.example.arthan.R
import com.example.arthan.dashboard.rm.adapters.PendingInfoAdapter
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_pending_info.*

class PendingInfoActivity: BaseActivity() {

    override fun contentView()= R.layout.activity_pending_info

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {
        vp_info_type.adapter= PendingInfoAdapter(supportFragmentManager)
        tb_info_type.setupWithViewPager(vp_info_type)
    }

    override fun screenTitle()= "Pending Info"
}