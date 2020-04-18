package com.example.arthan.dashboard.bm

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.arthan.R
import com.example.arthan.dashboard.bm.adapter.IDAddresTabAdapter
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*

class IDAddressActivity: BaseActivity() {

    override fun contentView()= R.layout.activity_id_address

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        btn_search.visibility= View.GONE
        btn_filter.visibility= View.GONE

        vp_profile.adapter=
            IDAddresTabAdapter(
                supportFragmentManager
            )
        tb_profile.setupWithViewPager(vp_profile)

    }

    override fun screenTitle()= "ID Address"
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