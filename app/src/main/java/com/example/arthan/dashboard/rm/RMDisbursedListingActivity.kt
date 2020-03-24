package com.example.arthan.dashboard.rm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.arthan.R
import com.example.arthan.dashboard.rm.adapters.DisbursedAdapter
import kotlinx.android.synthetic.main.activity_lisiting.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class RMDisbursedListingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lisiting)

        toolbar_title?.text = "Disbursed Cases"
        back_button?.setOnClickListener { onBackPressed() }
        rv_listing.adapter = DisbursedAdapter(this)
    }
}