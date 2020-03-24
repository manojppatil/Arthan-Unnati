package com.example.arthan.views.activities

import android.view.View
import com.example.arthan.R
import com.example.arthan.dashboard.bm.adapter.NotificationAdapter
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*

class NotificationActivity : BaseActivity() {

    override fun contentView() = R.layout.activity_notification

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        btn_search.visibility = View.GONE
        btn_filter.visibility = View.GONE

        rv_notification.adapter = NotificationAdapter(this)
    }

    override fun screenTitle() = "Notifications"
}