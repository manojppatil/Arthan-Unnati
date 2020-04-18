package com.example.arthan.dashboard.rm

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.example.arthan.R
import com.example.arthan.dashboard.rm.adapters.PendingInfoAdapter
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_pending_info.*
import kotlinx.android.synthetic.main.layout_rm_toolbar.*

class PendingInfoActivity: BaseActivity() {

    override fun contentView()= R.layout.activity_pending_info

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {
        vp_info_type.adapter= PendingInfoAdapter(supportFragmentManager)
        tb_info_type.setupWithViewPager(vp_info_type)
        setSupportActionBar(toolbar as Toolbar?)
    }

    override fun screenTitle()= "Pending Info"
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
                startActivity(Intent(this,SplashActivity::class.java))
            }

        }
        return super.onOptionsItemSelected(item)
    }
}