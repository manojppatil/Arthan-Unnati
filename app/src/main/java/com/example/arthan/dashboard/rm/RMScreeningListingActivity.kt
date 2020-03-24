package com.example.arthan.dashboard.rm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.arthan.R
import com.example.arthan.dashboard.rm.adapters.ScreeningAdapter
import com.example.arthan.dashboard.rm.viewmodel.RMDashboardViewModel
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_lisiting.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class RMScreeningListingActivity : AppCompatActivity() {

    private lateinit var mViewModel: RMDashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lisiting)

        mViewModel= ViewModelProvider(this).get(RMDashboardViewModel::class.java)

        back_button?.setOnClickListener { onBackPressed() }

        loadScreeningList()
    }

    private fun loadScreeningList(){
        mViewModel.loadScreeningList().observe(this, Observer { data->
            if(data.isNullOrEmpty()){
                Toast.makeText(this,"No Record Found",Toast.LENGTH_SHORT).show()
            } else {
                rv_listing.adapter = ScreeningAdapter(this,data)
            }

        })
    }

    companion object {
        fun startMe(context: Context?) =
            context?.startActivity(Intent(context, RMScreeningListingActivity::class.java))
    }
}