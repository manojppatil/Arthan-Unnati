package com.example.arthan.dashboard.ops

import android.content.Context
import android.content.Intent
import android.view.View
import com.example.arthan.R
import com.example.arthan.dashboard.bm.BranchLaunchType
import com.example.arthan.dashboard.bm.RMInBranchListingActivity
import com.example.arthan.dashboard.rm.RMRejectedListingActivity
import com.example.arthan.dashboard.rm.RMScreeningListingActivity
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.ScreeningActivity
import kotlinx.android.synthetic.main.activity_ops_dashboard.*

class OpsDashboardActivity : BaseActivity() {

    override fun contentView() = R.layout.activity_ops_dashboard

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        pending_view?.setOnClickListener { ScreeningActivity.startMe(this, "OPS") }
        screening_view?.setOnClickListener { RMScreeningListingActivity.startMe(this) }
        rejected_view?.setOnClickListener { RMRejectedListingActivity.startMe(this, "OPS") }
        re_assigned_view?.setOnClickListener {
            RMInBranchListingActivity.startMe(
                this,
                BranchLaunchType.OPS
            )
        }
    }

    override fun screenTitle() = ""

    companion object {
        fun startMe(context: Context?) =
            context?.startActivity(Intent(context, OpsDashboardActivity::class.java))
    }
}