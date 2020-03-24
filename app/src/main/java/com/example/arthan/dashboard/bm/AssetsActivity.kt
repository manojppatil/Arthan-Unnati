package com.example.arthan.dashboard.bm

import android.view.View
import com.example.arthan.R
import com.example.arthan.dashboard.bm.adapter.AssetsAdapter
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_assets.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*

class AssetsActivity : BaseActivity() {

    override fun contentView() = R.layout.activity_assets

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        btn_search.visibility = View.GONE
        btn_filter.visibility = View.GONE

        rv_assets.adapter = AssetsAdapter(this)
    }

    override fun screenTitle() = "Assets"
}