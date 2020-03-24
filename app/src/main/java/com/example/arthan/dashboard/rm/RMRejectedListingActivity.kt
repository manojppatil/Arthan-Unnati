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
import com.example.arthan.dashboard.rm.adapters.RejectedAdapter
import com.example.arthan.dashboard.rm.viewmodel.RMDashboardViewModel
import kotlinx.android.synthetic.main.activity_lisiting.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import com.example.arthan.utils.ArgumentKey
class RMRejectedListingActivity : AppCompatActivity() {

    private lateinit var mViewModel: RMDashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lisiting)
        toolbar_title?.text = "Rejected Cases"

        mViewModel= ViewModelProvider(this).get(RMDashboardViewModel::class.java)

        back_button?.setOnClickListener { onBackPressed() }

        loadRejectedList()
    }

    private fun loadRejectedList(){
        mViewModel.loadRejectedList().observe(this, Observer { data->
            if(data.isNullOrEmpty()){
                Toast.makeText(this,"No Record Found", Toast.LENGTH_SHORT).show()
            } else {
                rv_listing.adapter = RejectedAdapter(this, intent.getStringExtra("FROM"),data)
            }

        })
    }

    companion object {
        fun startMe(context: Context?, from: String) =
            context?.startActivity(Intent(context, RMRejectedListingActivity::class.java).apply {
                putExtra(ArgumentKey.FROM, from)
            })
    }
}