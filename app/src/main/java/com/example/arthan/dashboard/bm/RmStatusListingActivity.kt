package com.example.arthan.dashboard.bm

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.arthan.R
import com.example.arthan.dashboard.bcm.BCMDashboardActivity
import com.example.arthan.dashboard.bm.adapter.RMStatusAdapter
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.global.ArthanApp
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_lisiting.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class RmStatusListingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lisiting)

        toolbar_title?.text = "RM's Full Name"
        back_button?.setOnClickListener { onBackPressed() }
        rv_listing.adapter = RMStatusAdapter(this)
    }
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