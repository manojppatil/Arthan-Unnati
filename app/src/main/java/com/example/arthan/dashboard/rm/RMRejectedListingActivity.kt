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
import com.example.arthan.dashboard.rm.adapters.ApprovedAdapter
import com.example.arthan.dashboard.rm.adapters.RejectedAdapter
import com.example.arthan.dashboard.rm.viewmodel.RMDashboardViewModel
import com.example.arthan.global.ArthanApp
import kotlinx.android.synthetic.main.activity_lisiting.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.views.activities.SplashActivity

class RMRejectedListingActivity : AppCompatActivity() {

    private lateinit var mViewModel: RMDashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lisiting)
        toolbar_title?.text = "Rejected Cases"

        setSupportActionBar(toolbar as Toolbar?)
        mViewModel= ViewModelProvider(this).get(RMDashboardViewModel::class.java)

        back_button?.setOnClickListener { onBackPressed() }

        loadRejectedList()
    }

    private fun loadRejectedList(){

        if(ArthanApp.getAppInstance().loginRole == "BM")
        {
            mViewModel.loadBMRejectedList(ArthanApp.getAppInstance().loginRole).observe(this, Observer { data->
                if(data.isNullOrEmpty()){
                    Toast.makeText(this,"No Record Found", Toast.LENGTH_SHORT).show()
                } else {
                    rv_listing.adapter = RejectedAdapter(this, "BM",data)
                }

            })
        }else
        {
            mViewModel.loadRejectedList(ArthanApp.getAppInstance().loginRole).observe(this, Observer { data->
                if(data.isNullOrEmpty()){
                    Toast.makeText(this,"No Record Found", Toast.LENGTH_SHORT).show()
                } else {
                    rv_listing.adapter = RejectedAdapter(this, "RM",data)
                }

            })
        }

    }

    companion object {
        fun startMe(context: Context?, from: String) =
            context?.startActivity(Intent(context, RMRejectedListingActivity::class.java).apply {
                putExtra(ArgumentKey.FROM, from)
            })
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {


        menuInflater.inflate(R.menu.more,menu)

        val searchItem=menu?.findItem(R.id.searchMenu)
        val searchView=searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                (rv_listing.adapter as RejectedAdapter).filter?.filter(query)
                //Toast.makeText(this,"searchItems",Toast.LENGTH_LONG).show();
                return  true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText.toString()
                var results=(rv_listing.adapter as RejectedAdapter).filter?.filter(query)
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

            }
            R.id.logoutMenu->
            {
                finish()
                startActivity(Intent(this, SplashActivity::class.java))
            }

        }
        return super.onOptionsItemSelected(item)
    }
    fun notifyReopen()
    {
        loadRejectedList()
    }
}