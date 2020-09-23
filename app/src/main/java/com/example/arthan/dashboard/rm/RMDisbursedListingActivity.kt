package com.example.arthan.dashboard.rm

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.arthan.R
import com.example.arthan.dashboard.bcm.BCMDashboardActivity
import com.example.arthan.dashboard.bm.BMDashboardActivity
import com.example.arthan.dashboard.bm.adapter.BMReassignToByAdapter
import com.example.arthan.dashboard.rm.adapters.DisbursedAdapter
import com.example.arthan.global.ArthanApp
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_lisiting.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RMDisbursedListingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lisiting)

        setSupportActionBar(toolbar as Toolbar?)
        toolbar_title?.text = "Disbursed Cases"
        back_button?.setOnClickListener { onBackPressed() }
        loadInitialData()
    }

    private fun loadInitialData() {

        val progress=ProgrssLoader(this)
        progress.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            val res=RetrofitFactory.getApiService().getBMDisburesed(ArthanApp.getAppInstance().loginUser)
            if(res?.body()!=null)
            {
                withContext(Dispatchers.Main)
                {
                    progress.dismmissLoading()
                    if(res.body()!!.reAssignedCases!=null&&res.body()!!.reAssignedCases.size>0)
                    rv_listing.adapter=BMReassignToByAdapter(this@RMDisbursedListingActivity,res?.body()!!.id,res?.body()!!.reAssignedCases)
                }

            }else
            {
                withContext(Dispatchers.Main)
                {
                    progress.dismmissLoading()
                    Toast.makeText(this@RMDisbursedListingActivity,"No Data found",Toast.LENGTH_LONG).show()
                }
            }
        }
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
                    startActivity(Intent(this,RMDashboardActivity::class.java))
                }else if(ArthanApp.getAppInstance().loginRole=="BCM")
                {
                    startActivity(Intent(this, BCMDashboardActivity::class.java))

                }else if(ArthanApp.getAppInstance().loginRole=="BM")
                {
                    startActivity(Intent(this, BMDashboardActivity::class.java))

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