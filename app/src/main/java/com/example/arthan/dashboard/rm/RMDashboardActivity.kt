package com.example.arthan.dashboard.rm

import android.content.Intent
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.arthan.R
import com.example.arthan.lead.AddLeadStep1Activity
import com.example.arthan.profile.MyProfileActivity
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_rm_dashboard.*

class RMDashboardActivity : BaseActivity() {

    var navController: NavController? = null

    override fun screenTitle() = ""

    override fun contentView() = R.layout.activity_rm_dashboard

    override fun onToolbarBackPressed() {
        drawer_layout.openDrawer(GravityCompat.START)
       // startActivity(Intent(this, MyProfileActivity::class.java))
    }

    override fun init() {

        btn_add_lead.setOnClickListener {
            startActivity(Intent(this@RMDashboardActivity, AddLeadStep1Activity::class.java))
        }

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
    }

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