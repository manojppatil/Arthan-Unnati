package com.example.arthan.dashboard.bm

import android.content.Intent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.arthan.R
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_collateral.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*

class CollateralActivity : BaseActivity() {

    override fun contentView() = R.layout.activity_collateral

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        btn_search.visibility = View.GONE
        btn_filter.visibility = View.GONE

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

    override fun screenTitle() = "Collateral | Customer Full Name"
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {


        menuInflater.inflate(R.menu.more,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.homeMenu->{
                finish()

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