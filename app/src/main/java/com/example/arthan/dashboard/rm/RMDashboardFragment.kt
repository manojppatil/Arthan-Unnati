package com.example.arthan.dashboard.rm

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.arthan.R
import com.example.arthan.dashboard.rm.viewmodel.RMDashboardViewModel
import com.example.arthan.profile.MyProfileActivity
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_rm_dashboard.*
import java.lang.Exception

class RMDashboardFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mViewModel:RMDashboardViewModel

    override fun contentView() = R.layout.fragment_rm_dashboard

    override fun init() {

        mViewModel= ViewModelProvider(this).get(RMDashboardViewModel::class.java)

        cv_leads.setOnClickListener(this)
        cv_screening.setOnClickListener(this)
        cv_rejected.setOnClickListener(this)
        cv_approved.setOnClickListener(this)
        cv_reassign.setOnClickListener(this)
        cv_to_be_disbursed.setOnClickListener(this)

        loadRmData()
    }

    private fun loadRmData(){
        mViewModel.loadRMDashboard().observe(this, Observer { data->
            if(data != null){
                txt_bm_name.text="Hello ${data.rmName}"
                txt_branch_rank_count.text= "${data.branchRank}"
                txt_all_india_rank_count.text= "${data.airRank}"
                txt_earnings_per.text= "${data.earning}"

                txt_last_sync.text= "Since ${data.since}"

                txt_lead_count.text= "${data.leads.count}"
                //txt_lead.text= "${data.leads.label}"
                txt_lead_amount.text= "${data.leads.total}"

                txt_screening_count.text= "${data.screening.count}"
                //txt_screening.text= "${data.screening.label}"
                txt_screening_amt.text= "${data.screening.total}"

                txt_approved_count.text= "${data.approved.count}"
                //txt_approved.text= "${data.approved.label}"
                txt_approved_amt.text= "${data.approved.total}"

                txt_rejected_count.text= "${data.rejected.count}"
                //txt_rejected.text= "${data.rejected.label}"
                txt_rejected_amt.text= "${data.rejected.total}"

                txt_to_be_disbursed_count.text= "${data.toDisburse.count}"
                //txt_to_be_disbursed.text= "${data.toDisburse.label}"
                txt_to_disbursed_amt.text= "${data.toDisburse.total}"

                txt_reassign_count.text= "${data.reassign.count}"
                //txt_reassign.text= "${data.reassign.label}"
                txt_reassign_amt.text= "${data.reassign.total}"
            }
        })
    }

    override fun onResume() {
        super.onResume()
        try {
            context?.let {
                if (MyProfileActivity.profileImage.isNotEmpty()) {
                    Glide.with(it).load(MyProfileActivity.profileImage).into(img_profile)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {

            R.id.cv_leads -> startActivity(
                Intent(activity, RMLeadListingActivity::class.java).apply {
                    putExtra("FROM", "RM")
                })
            R.id.cv_screening -> startActivity(Intent(activity, RMScreeningListingActivity::class.java))
            R.id.cv_approved -> startActivity(
                Intent(
                    activity,
                    CommonApprovedListingActivity::class.java
                ).apply {
                    putExtra("FROM", "RM")
                })
            R.id.cv_rejected -> startActivity(
                Intent(
                    activity,
                    RMRejectedListingActivity::class.java
                ).apply {
                    putExtra("FROM", "RM")
                })
            R.id.cv_reassign -> startActivity(
                Intent(
                    activity,
                    RMReAssignListingActivity::class.java
                ).apply {
                    putExtra("FROM", "REASSIGN")
                })
            R.id.cv_to_be_disbursed -> startActivity(
                Intent(
                    activity,
                    RMTobeDisbursedListingActivity::class.java
                )
            )
        }
    }
}