package com.example.arthan.views.activities

import android.view.View
import com.example.arthan.R
import kotlinx.android.synthetic.main.layout_bm_toolbar.*

class TakeDecisionActivity : BaseActivity() {

    override fun contentView() = R.layout.activity_take_decision

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        btn_filter.visibility = View.GONE
        btn_search.visibility = View.GONE
    }

    override fun screenTitle() = "Take a Decision"
}