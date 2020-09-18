package com.example.arthan.dashboard.rm

import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.arthan.R
import com.example.arthan.dashboard.rm.adapters.ReassignAdapter
import com.example.arthan.dashboard.rm.viewmodel.RMDashboardViewModel
import com.example.arthan.global.ArthanApp
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.assign_list_frag.*

class RMAssignListFragment : BaseFragment() {
    private lateinit var mViewModel: RMDashboardViewModel

    override fun contentView(): Int {
       return R.layout.assign_list_frag
    }
    var  progressLoader:ProgrssLoader?= null
    private fun loadReassignLeadList(){
        mViewModel.loadReassignLeadList().observe(this, Observer { res->

            progressLoader?.dismmissLoading()
            if(res.details.isNullOrEmpty()){
                Toast.makeText(activity,"No Record Found", Toast.LENGTH_SHORT).show()
            } else {
                rv_assign_listing.adapter = ReassignAdapter(context!!, ArthanApp.getAppInstance().loginRole,res.details,activity?.intent?.getStringExtra("tile"))
            }

        })
    }
    private fun loadAmCasesList(){
        mViewModel.loadAMCases().observe(this, Observer { data->

            progressLoader?.dismmissLoading()
            if(data.isNullOrEmpty()){
                Toast.makeText(activity,"No Record Found", Toast.LENGTH_SHORT).show()
            } else {
                rv_assign_listing.adapter = ReassignAdapter(
                    context!!,
                    ArthanApp.getAppInstance().loginRole,
                    data,
                    activity?.intent?.getStringExtra("tile")
                )
            }

        })
    }
    override fun init() {

        mViewModel= ViewModelProvider(this).get(RMDashboardViewModel::class.java)
         progressLoader = ProgrssLoader(context!!)
        progressLoader!!.showLoading()
        if(activity?.intent?.getStringExtra("tile")=="AMCASES")
        {

            loadAmCasesList()
        }else

        {
            loadReassignLeadList()
        }
    }

}