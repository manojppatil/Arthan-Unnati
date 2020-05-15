package com.example.arthan.dashboard.bcm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.arthan.R
import com.example.arthan.dashboard.bm.model.BureauDetails
import kotlinx.android.synthetic.main.activity_bureau.*
import kotlinx.android.synthetic.main.activity_bureau_details_level2.*
import kotlinx.android.synthetic.main.activity_bureau_details_level2.toolbar
import kotlinx.android.synthetic.main.custom_toolbar.*

class BureauDetailsLevel2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bureau_details_level2)

        setSupportActionBar(toolbar as Toolbar?)
        toolbar_title.text = "Bureau"
        back_button.setOnClickListener{finish()}
        val bureau: BureauDetails? = intent?.extras?.getParcelable<BureauDetails>("data")

        when (intent.getStringExtra("type")) {
            "noOfActiveLoans" -> {

                rvListing.adapter =
                    BureauDetailsLevel2Adapter(this, bureau?.activeDetails, "Active Loans")
            }
            "moOfsecuredLoans" -> {
                rvListing.adapter =
                    BureauDetailsLevel2Adapter(this, bureau?.secureDetails, "Secured Loans")

            }
            "moOfUnsecuredLoans" -> {
                rvListing.adapter =
                    BureauDetailsLevel2Adapter(this, bureau?.unsecureDetails, "Unsecured Loans")

            }
            "defaultCreditLoan" -> {
                rvListing.adapter =
                    BureauDetailsLevel2Adapter(this, bureau?.ccGoldAgriDetails, "Default Credit")

            }
            "defaultInAutoLoans" -> {
                rvListing.adapter =
                    BureauDetailsLevel2Adapter(this, bureau?.autoOtherDetails, "Default Auto Loans")

            }
            "NoOfSuitFiled" -> {
                rvListing.adapter =
                    BureauDetailsLevel2Adapter(this, bureau?.suitWrittenDetails, "Suit Filed")

            }
            "noOfLoanAsGarunter" -> {
                rvListing.adapter =
                    BureauDetailsLevel2Adapter(this, bureau?.guarantorDetails, "Loans as Guarantor")

            }
            "last6MonthsHist" -> {
                toolbar_title.text = "Last 6 months History"
                rvListing.adapter =
                    BureauDetailsLevel2AdapterForHistory(this, bureau?.historyDetails)

            }
        }
    }
}
