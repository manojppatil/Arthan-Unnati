package com.example.arthan.dashboard.rm

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.arthan.R
import com.example.arthan.lead.BusinessInformationFragment
import com.example.arthan.lead.DocumentFragment
import com.example.arthan.lead.IncomeInformationFragment
import com.example.arthan.lead.OtherDetailsFragment
import com.example.arthan.model.ReassignLeadData
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_lisiting.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class RMReAssignListingActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_lisiting)
        toolbar_title?.text = "Re-Assigned Cases"

        setSupportActionBar(toolbar as Toolbar?)
        if(intent.getStringExtra("FROM") == "REASSIGN") {

            rel_frags.visibility=View.VISIBLE
            rv_listing.visibility=View.GONE
            showAssignListFragment()
        }

        back_button?.setOnClickListener {

            if(toolbar_title.text == "Re-Assigned Cases")
            {
                onBackPressed()
            }else
            {
                toolbar_title.text == "Re-Assigned Cases"
                showAssignListFragment()
            }
        }
    }


     fun showAssignListFragment() {
        toolbar_title?.text = "Re-Assigned Cases"
        supportFragmentManager.beginTransaction().replace(R.id.rel_frags,RMAssignListFragment()).commit()

    }

    fun showBusinessFragment(loanId:String)
    {
        supportFragmentManager.beginTransaction().replace(R.id.rel_frags,BusinessInformationFragment().apply {
            var b=Bundle()
            b.putString("task","RM_AssignList")
            b.putString("loanId",loanId)
            b.putString("from","rmbusiness")

            this.arguments=b
        }).commit()
        toolbar_title.text="Business details"

    }
    fun showIncomeFragment(loanId:String)
    {
        supportFragmentManager.beginTransaction().replace(R.id.rel_frags,IncomeInformationFragment()
            .apply {
                var b=Bundle()
                b.putString("task","RM_AssignList")
                b.putString("loanId",loanId)
                b.putString("from","rmIncome")
                this.arguments=b

            }).commit()
        toolbar_title.text="Income details"


    }
    fun showOthersFragment(loanId:String)
    {
        supportFragmentManager.beginTransaction().replace(R.id.rel_frags,OtherDetailsFragment()
            .apply {
                var b=Bundle()
                b.putString("task","RM_AssignList")
                b.putString("loanId",loanId)
                this.arguments=b
            }).commit()

        toolbar_title.text="Other details"

    }
    fun showDocumentsFragment(loanId:String)
    {
        supportFragmentManager.beginTransaction().replace(R.id.rel_frags,DocumentFragment()
            .apply {
                var b=Bundle()
                b.putString("task","RM_AssignList")
                b.putString("loanId",loanId)
                this.arguments=b
            }).commit()
        toolbar_title.text="Documents"

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

    fun showPendingScreenList(data: ReassignLeadData) {

        var businessFlag = false
        var incomeFlag = false
        var othersFlag = false
        var documentsFlag = false
        //startActivity(Intent(this,RMReassignPendingScreenlist::class.java).apply {
        for (doc in data.pending) {
            if (doc.equals("Documents", ignoreCase = true)) {
                documentsFlag = true
            }
            if (doc.equals("Business", ignoreCase = true)) {
                businessFlag = true

            }
            if (doc.equals("Income", ignoreCase = true)) {
                incomeFlag = true
            }
            if (doc.equals("Others", ignoreCase = true)) {
                othersFlag = true

            }
        }

        startActivity(Intent(this, RMReassignPendingScreenlist::class.java).apply {
            putExtra("task", "RM_AssignList")
            putExtra("loanId", data.loanId)
            putExtra("Documents", documentsFlag)
            putExtra("Business", businessFlag)
            putExtra("Income", incomeFlag)
            putExtra("Others", businessFlag)
        })

    }

}