package com.example.arthan.dashboard.rm

import android.content.Intent
import android.text.BoringLayout
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.arthan.R
import com.example.arthan.dashboard.am.AddAMKYCDetailsActivity
import com.example.arthan.global.ArthanApp
import com.example.arthan.lead.AddLeadStep1Activity
import com.example.arthan.profile.MyProfileActivity
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_rm_dashboard.*
import kotlinx.android.synthetic.main.layout_rm_toolbar.*

class RMDashboardActivity : BaseActivity() {

    var navController: NavController? = null

    override fun screenTitle() = ""

    override fun contentView() = R.layout.activity_rm_dashboard

   public lateinit var btn_add_lead: Button

    override fun onToolbarBackPressed() {
        drawer_layout.openDrawer(GravityCompat.START)
       // startActivity(Intent(this, MyProfileActivity::class.java))
    }

    override fun init() {
        btn_add_lead = findViewById(R.id.btn_add_lead) as Button
        btn_add_lead.setOnClickListener {
            startActivity(Intent(this@RMDashboardActivity, AddLeadStep1Activity::class.java))
        }
        if (ArthanApp.getAppInstance().loginRole == "RM") {
            btn_add_am.visibility = View.VISIBLE
        } else {
            btn_add_am.visibility = View.GONE
        }
        btn_add_am.setOnClickListener {
            startActivity(Intent(this,AddNewAmActivity::class.java))
        }
        //Harish start end
//        btn_add_am.setOnClickListener {
//            startActivity(Intent(this@RMDashboardActivity, AddAMKYCDetailsActivity::class.java))
//        }
        navController = Navigation.findNavController(this, R.id.fl_container)

        if (navController != null) {
            NavigationUI.setupActionBarWithNavController(this, navController!!, drawer_layout)
            NavigationUI.setupWithNavController(navigationView, navController!!)
        }

        //navigationView.setOnI

        /* btn_close.setOnClickListener {
             drawer_layout.closeDrawer(GravityCompat.START)
         }*/

        btn_logout.setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this@RMDashboardActivity, SplashActivity::class.java))
            finish()
        }
        logoutIv.visibility=View.GONE
        logoutIv.setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this@RMDashboardActivity, SplashActivity::class.java))
            finish()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {


        menuInflater.inflate(R.menu.more,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.logoutMenu->
                logoutIv.performClick()

        }
        return super.onOptionsItemSelected(item)
    }
    /*fun hideAddAM(hide:Boolean)
    {
        if(hide)
        btn_add_lead.visibility=View.GONE
        else btn_add_lead.visibility=View.VISIBLE

    }*/

    override fun onSupportNavigateUp() = findNavController(R.id.fl_container).navigateUp()

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.item_home -> {
                drawer_layout.closeDrawer(GravityCompat.START)
                true
            }
            R.id.item_profile -> {
                startActivity(Intent(this@RMDashboardActivity, AddLeadStep1Activity::class.java))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }*/

}