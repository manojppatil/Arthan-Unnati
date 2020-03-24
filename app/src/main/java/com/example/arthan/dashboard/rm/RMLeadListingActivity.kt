package com.example.arthan.dashboard.rm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.arthan.R
import com.example.arthan.dashboard.rm.adapters.LeadsAdapter
import com.example.arthan.dashboard.rm.adapters.ScreeningAdapter
import com.example.arthan.dashboard.rm.viewmodel.RMDashboardViewModel
import kotlinx.android.synthetic.main.activity_lisiting.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class RMLeadListingActivity : AppCompatActivity() {

    private lateinit var mViewModel: RMDashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lisiting)

        mViewModel= ViewModelProvider(this).get(RMDashboardViewModel::class.java)

        toolbar_title?.text = "My Leads"
        back_button?.setOnClickListener { onBackPressed() }

        loadLeadList()
    }

    private fun loadLeadList(){
        mViewModel.loadLeadList().observe(this, Observer { data->
            if(data.isNullOrEmpty()){
                Toast.makeText(this,"No Record Found", Toast.LENGTH_SHORT).show()
            } else {
                rv_listing.adapter = LeadsAdapter(this, intent.getStringExtra("FROM"),data)
            }

        })
    }

    companion object {
        fun startMe(context: Context?) =
            context?.startActivity(Intent(context, RMLeadListingActivity::class.java))
    }
}