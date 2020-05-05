package com.example.arthan.dashboard.rm

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.arthan.R
import com.example.arthan.dashboard.rm.adapters.ApprovedAdapter
import com.example.arthan.dashboard.rm.adapters.LeadsAdapter
import com.example.arthan.dashboard.rm.viewmodel.RMDashboardViewModel
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_lisiting.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class CommonApprovedListingActivity : AppCompatActivity() {

    private lateinit var mViewModel: RMDashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lisiting)
        toolbar_title?.text = "Approved Cases"

        setSupportActionBar(toolbar as Toolbar?)
        mViewModel= ViewModelProvider(this).get(RMDashboardViewModel::class.java)

        back_button?.setOnClickListener { onBackPressed() }
        loadApprovedList()
    }

    private fun loadApprovedList(){
        mViewModel.loadApprovedList(intent.getStringExtra("FROM")).observe(this, Observer { data->
            if(data.isNullOrEmpty()){
                Toast.makeText(this,"No Record Found", Toast.LENGTH_SHORT).show()
            } else {
                rv_listing.adapter = ApprovedAdapter(this, intent.getStringExtra("FROM"),data)
            }

        })
    }
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
