package com.example.arthan.dashboard.bm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.arthan.R
import com.example.arthan.dashboard.bm.adapter.BankingAdapter
import kotlinx.android.synthetic.main.activity_lisiting.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class BankingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lisiting)

        toolbar_title?.text = "Banking | Customer Full Name"
        back_button?.setOnClickListener { onBackPressed() }
        rv_listing.adapter = BankingAdapter(this)
    }
}