package com.example.arthan.dashboard.bm

import android.content.Intent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.arthan.R
import com.example.arthan.dashboard.bcm.BCMDashboardActivity
import com.example.arthan.dashboard.bm.model.BureauDetails
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.global.ArthanApp
import com.example.arthan.lead.model.responsedata.CollateralVO
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_collateral.*
import kotlinx.android.synthetic.main.collateral_layout.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*

class CollateralActivity : BaseActivity() {

    override fun contentView() = R.layout.activity_collateral

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        btn_search.visibility = View.GONE
        btn_filter.visibility = View.GONE

        val collateral: CollateralVO? = intent?.extras?.getParcelable<CollateralVO>("data")

        if(collateral!=null) {
            txt_ownerNameValue.text=collateral?.ownerName
            txt_address_value.text = collateral?.address
            txt_property_type_value.text = collateral?.collateralType
            txt_occBy_value.text = collateral?.occupiedBy
            txt_area_property_value.text = collateral?.areaOfProperty
            txt_market_value.text = collateral?.marketValue
            txt_tentative_value.text = collateral?.distressValue
            txt_4bound_value.text = collateral?.boundaryMatch
            txt_LTV_value.text = collateral?.ltvConsidered
            txt_Remarks_Value.text = collateral?.remarks
        }

        findViewById<View?>(R.id.remove_button)?.visibility = View.GONE
        add_collateral_button?.setOnClickListener {
            collateral_linear_layout?.addView(
                LayoutInflater.from(this).inflate(
                    R.layout.collateral_layout,
                    collateral_linear_layout,
                    false
                )?.apply {
                    findViewById<View?>(R.id.remove_button)?.setOnClickListener {
                        collateral_linear_layout?.removeView(this)
                    }
                })
        }
    }

    override fun screenTitle() = "Collateral Details"
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {


        menuInflater.inflate(R.menu.more,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.homeMenu->{
                finish()
                if(ArthanApp.getAppInstance().loginRole=="RM")
                {
                    startActivity(Intent(this, RMDashboardActivity::class.java))
                }else if(ArthanApp.getAppInstance().loginRole=="BCM")
                {
                    startActivity(Intent(this, BCMDashboardActivity::class.java))

                }else if(ArthanApp.getAppInstance().loginRole=="BM")
                {
                    startActivity(Intent(this,BMDashboardActivity::class.java))

                }
            }
            R.id.logoutMenu->
            {
                finish()
                startActivity(Intent(this, SplashActivity::class.java))
            }

        }
        return super.onOptionsItemSelected(item)
    }
}