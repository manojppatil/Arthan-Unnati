package com.example.arthan.dashboard.bcm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.arthan.R
import com.example.arthan.dashboard.bm.model.BureauDetails
import com.example.arthan.dashboard.bm.model.InnerDetailsBanking
import kotlinx.android.synthetic.main.activity_bcm_pending_customers.*
import kotlinx.android.synthetic.main.activity_bcm_pending_customers.toolbar
import kotlinx.android.synthetic.main.activity_bureau_details_level2.*
import kotlinx.android.synthetic.main.activity_bureau_view_all_details.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class BureauViewAllDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bureau_view_all_details)
        setSupportActionBar(toolbar as Toolbar?)

        back_button?.setOnClickListener { finish() }
        findViewById<TextView>(R.id.toolbar_title).text= intent.getStringExtra("type")?: "Bureau details"

        search_button.visibility=View.GONE
        filter_button.visibility=View.GONE

            allDataLL.visibility=View.VISIBLE
            last6MonthLL.visibility=View.GONE
        val bureau: InnerDetailsBanking? = intent?.extras?.getParcelable<InnerDetailsBanking>("data")

        AccountType.text=bureau?.details?.accountType
        ownership.text=bureau?.details?.ownership
        DisbursedAmt.text=bureau?.details?.disbursedAmt
        DisbursedDate.text=bureau?.details?.disbursedDate
        LastPaymentDate.text=bureau?.details?.lastPaymentDate
        OverdueAmount.text=bureau?.details?.overdueAmount
        WriteOffAmount.text=bureau?.details?.writeOffAmount
        CurrentBalance.text=bureau?.details?.currentBalance

        SecurityStatus.text=bureau?.details?.securityStatus
        PrincipalWriteOffAmount.text=bureau?.details?.principalWriteOffAmount
        WriteoffSettleStatus.text=bureau?.details?.writeoffSettleStatus

    }
}
