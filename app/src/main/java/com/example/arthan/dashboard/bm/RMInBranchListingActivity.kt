package com.example.arthan.dashboard.bm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.arthan.R
import com.example.arthan.dashboard.bcm.BCMDashboardActivity
import com.example.arthan.dashboard.bm.adapter.RmInBranchAdapter
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.global.ArthanApp
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.views.adapters.BranchAdapter
import kotlinx.android.parcel.Parcelize
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_lisiting.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RMInBranchListingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lisiting)

        setSupportActionBar(toolbar as Toolbar?)
        toolbar_title?.text =
            when (intent?.getParcelableExtra<BranchLaunchType?>(ArgumentKey.BranchLaunchType)) {
                is BranchLaunchType.BM -> {
                  //  rv_listing.adapter = RmInBranchAdapter(this)
                    getDataForBMRmStatus()
                    "RM in my Branch"
                }
                is BranchLaunchType.OPS -> {
                    rv_listing.adapter = BranchAdapter()
                    "Branch"
                }
                else -> ""
            }
        back_button?.setOnClickListener { onBackPressed() }
    }

    private fun getDataForBMRmStatus() {
        val progrssLoader=ProgrssLoader(this@RMInBranchListingActivity)
        progrssLoader.showLoading()

        val map=HashMap<String,String>()
        map["userId"]=ArthanApp.getAppInstance().loginUser
        CoroutineScope(Dispatchers.IO).launch {

            val res=RetrofitFactory.getApiService().getBMRMStatus(map)
            if(res?.body()!=null)
            {
                withContext(Dispatchers.Main){
                    progrssLoader.dismmissLoading()
                    rv_listing.adapter = RmInBranchAdapter(this@RMInBranchListingActivity,res.body()!!)

                }

            }else
            {
                withContext(Dispatchers.Main) {
                    progrssLoader.dismmissLoading()
                    Toast.makeText(
                        this@RMInBranchListingActivity,
                        "Please try again later",
                        Toast.LENGTH_LONG
                    ).show()
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
    companion object {
        fun startMe(context: Context?, launchType: BranchLaunchType) =
            context?.startActivity(Intent(context, RMInBranchListingActivity::class.java).apply {
                putExtra(ArgumentKey.BranchLaunchType, launchType)
            })
    }
}


sealed class BranchLaunchType : Parcelable {
    @Parcelize
    object BM : BranchLaunchType()

    @Parcelize
    object OPS : BranchLaunchType()
}