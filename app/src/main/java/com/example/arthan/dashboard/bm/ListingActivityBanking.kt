package com.example.arthan.dashboard.bm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.arthan.R
import com.example.arthan.dashboard.bm.adapter.BankingEntriesAdapter
import com.example.arthan.dashboard.bm.model.Banking360DetailsResponseData
import com.example.arthan.dashboard.bm.model.BureauDetails
import com.example.arthan.dashboard.bm.model.EntriesType
import kotlinx.android.synthetic.main.activity_listing_banking.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*

class ListingActivityBanking : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing_banking)

        setSupportActionBar(toolbar as Toolbar?)
        back_button?.setOnClickListener { finish() }
        val details: Banking360DetailsResponseData? = intent?.extras?.getParcelable("data")

        when(intent.getStringExtra("typeList"))
        {
            "emi"->{
                rvListing_banking.adapter=BankingEntriesAdapter(this,details?.emiEntries!!)
                toolbar_title?.text ="EMI Entries"
            }
            "cash"->
            {
                rvListing_banking.adapter=BankingEntriesAdapter(this,details?.cashEntries!!)
                toolbar_title?.text ="Cash Entries"

            }
            "credits"->{
                rvListing_banking.adapter=BankingEntriesAdapter(this,details?.creditEntries!!)
                toolbar_title?.text ="Credit Entries"

            }else->
        {
            Toast.makeText(this,"No data found",Toast.LENGTH_LONG).show()
        }
        }

    }
}
