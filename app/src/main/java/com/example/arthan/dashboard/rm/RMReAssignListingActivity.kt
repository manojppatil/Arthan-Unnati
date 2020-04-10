package com.example.arthan.dashboard.rm

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.arthan.R
import com.example.arthan.dashboard.rm.adapters.LeadsAdapter
import com.example.arthan.dashboard.rm.adapters.ReassignAdapter
import com.example.arthan.dashboard.rm.viewmodel.RMDashboardViewModel
import com.example.arthan.lead.BusinessInformationFragment
import com.example.arthan.lead.DocumentFragment
import com.example.arthan.lead.IncomeInformationFragment
import com.example.arthan.lead.OtherDetailsFragment
import kotlinx.android.synthetic.main.activity_lisiting.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class RMReAssignListingActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_lisiting)
        toolbar_title?.text = "Re-Assigned Cases"

        if(intent.getStringExtra("FROM") == "REASSIGN") {

            rel_frags.visibility=View.VISIBLE
            rv_listing.visibility=View.GONE
            showAssignListFragment()
        }

        back_button?.setOnClickListener {

            if(toolbar_title.text == "Re-Assigned Cases")
            {
                onBackPressed()
            }else
            {
                toolbar_title.text == "Re-Assigned Cases"
                showAssignListFragment()
            }
        }
    }

    private fun showAssignListFragment() {
        toolbar_title?.text = "Re-Assigned Cases"
        supportFragmentManager.beginTransaction().replace(R.id.rel_frags,RMAssignListFragment()).commit()

    }

    fun showBusinessFragment(loanId:String)
    {
        supportFragmentManager.beginTransaction().replace(R.id.rel_frags,BusinessInformationFragment().apply {
            var b=Bundle()
            b.putString("task","RM_AssignList")
            b.putString("loanId","WVMW-FZEA-UJBK")
            this.arguments=b
        }).commit()
        toolbar_title.text="Business details"

    }
    fun showIncomeFragment(loanId:String)
    {
        supportFragmentManager.beginTransaction().replace(R.id.rel_frags,IncomeInformationFragment()
            .apply {
                var b=Bundle()
                b.putString("task","RM_AssignList")
                b.putString("loanId","WVMW-FZEA-UJBK")
                b.putString("from","rmIncome")
                this.arguments=b

            }).commit()
        toolbar_title.text="Income details"


    }
    fun showOthersFragment(loanId:String)
    {
        supportFragmentManager.beginTransaction().replace(R.id.rel_frags,OtherDetailsFragment()
            .apply {
                var b=Bundle()
                b.putString("task","RM_AssignList")
                b.putString("loanId","WVMW-FZEA-UJBK")
                this.arguments=b
            }).commit()

        toolbar_title.text="Other details"

    }
    fun showDocumentsFragment(loanId:String)
    {
        supportFragmentManager.beginTransaction().replace(R.id.rel_frags,DocumentFragment()
            .apply {
                var b=Bundle()
                b.putString("task","RM_AssignList")
                b.putString("loanId","WVMW-FZEA-UJBK")
                this.arguments=b
            }).commit()
        toolbar_title.text="Documents"

    }

}