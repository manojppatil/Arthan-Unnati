package com.example.arthan.dashboard.bm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.example.arthan.R
import com.example.arthan.dashboard.bm.adapter.RmInBranchAdapter
import com.example.arthan.views.adapters.BranchAdapter
import kotlinx.android.parcel.Parcelize
import com.example.arthan.utils.ArgumentKey
import kotlinx.android.synthetic.main.activity_lisiting.*
import kotlinx.android.synthetic.main.custom_toolbar.*


class RMInBranchListingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lisiting)

        toolbar_title?.text =
            when (intent?.getParcelableExtra<BranchLaunchType?>(ArgumentKey.BranchLaunchType)) {
                is BranchLaunchType.BM -> {
                    rv_listing.adapter = RmInBranchAdapter(this)
                    "RM in my Branch"
                }
                is BranchLaunchType.OPS -> {
                    rv_listing.adapter = BranchAdapter()
                    "Branch"
                }
                else -> ""
            }
        back_button?.setOnClickListener { onBackPressed() }
        rv_listing.adapter = RmInBranchAdapter(this)
    }

    companion object {
        fun startMe(context: Context?, launchType: BranchLaunchType) =
            context?.startActivity(Intent(context, RMInBranchListingActivity::class.java).apply {
                putExtra(ArgumentKey.BranchLaunchType, launchType)
            })
    }
}

sealed class BranchLaunchType : Parcelable {
    @Parcelize
    object BM : BranchLaunchType()

    @Parcelize
    object OPS : BranchLaunchType()
}