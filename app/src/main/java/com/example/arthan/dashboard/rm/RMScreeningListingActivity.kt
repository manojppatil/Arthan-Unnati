package com.example.arthan.dashboard.rm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.arthan.R
import com.example.arthan.dashboard.bcm.BCMDashboardActivity
import com.example.arthan.dashboard.bm.BMDashboardActivity
import com.example.arthan.dashboard.rm.adapters.ScreeningAdapter
import com.example.arthan.dashboard.rm.viewmodel.RMDashboardViewModel
import com.example.arthan.global.ArthanApp
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_lisiting.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class RMScreeningListingActivity : AppCompatActivity() {

    private lateinit var mViewModel: RMDashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lisiting)

        setSupportActionBar(toolbar as Toolbar?)
        toolbar_title.text = "Screening(${intent.getStringExtra("screeningCount")})"
        mViewModel= ViewModelProvider(this).get(RMDashboardViewModel::class.java)

        back_button?.setOnClickListener { onBackPressed() }
        if(intent.getStringExtra("tile")=="AMCASES")
        {

//            loadAMCasesesList()
        }else {
            loadScreeningList()
        }
    }

    private fun loadScreeningList(){
        mViewModel.loadScreeningList().observe(this, Observer { data->
            if(data.isNullOrEmpty()){
                Toast.makeText(this,"No Record Found",Toast.LENGTH_SHORT).show()
            } else {
                rv_listing.adapter = ScreeningAdapter(this,data)
            }

        })
    }


    companion object {
        fun startMe(context: Context?) =
            context?.startActivity(Intent(context, RMScreeningListingActivity::class.java))
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {


        menuInflater.inflate(R.menu.more,menu)
        val searchItem=menu?.findItem(R.id.searchMenu)
        val searchView=searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                (rv_listing.adapter as ScreeningAdapter).filter?.filter(query)
                //Toast.makeText(this,"searchItems",Toast.LENGTH_LONG).show();
                return  true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText.toString()
                var results=(rv_listing.adapter as ScreeningAdapter).filter?.filter(query)
                rv_listing!!.adapter?.notifyDataSetChanged()
                return false
            }
        }
        )
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
            R.id.searchMenu->
            {

            }

        }
        return super.onOptionsItemSelected(item)
    }
}