package com.example.arthan.dashboard.rm

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.arthan.R
import com.example.arthan.dashboard.rm.adapters.ApprovedAdapter
import com.example.arthan.dashboard.rm.adapters.LeadsAdapter
import com.example.arthan.dashboard.rm.viewmodel.RMDashboardViewModel
import kotlinx.android.synthetic.main.activity_lisiting.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class RMApprovedListingActivity : AppCompatActivity() {

    private lateinit var mViewModel: RMDashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lisiting)
        toolbar_title?.text = "Approved Cases"

        mViewModel= ViewModelProvider(this).get(RMDashboardViewModel::class.java)

        back_button?.setOnClickListener { onBackPressed() }
        loadApprovedList()
    }

    private fun loadApprovedList(){
        mViewModel.loadApprovedList().observe(this, Observer { data->
            if(data.isNullOrEmpty()){
                Toast.makeText(this,"No Record Found", Toast.LENGTH_SHORT).show()
            } else {
                rv_listing.adapter = ApprovedAdapter(this, intent.getStringExtra("FROM"),data)
            }

        })
    }
}
