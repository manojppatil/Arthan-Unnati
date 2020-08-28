package com.example.arthan.dashboard.bm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.example.arthan.R
import com.example.arthan.lead.model.responsedata.SCVO
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_scorecard.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class ScoreCardActivity : BaseActivity() {
    override fun contentView(): Int {
        return R.layout.activity_scorecard
    }

    override fun init() {

        setDataTFields()
    }

    private fun setDataTFields() {

       var data= intent.getParcelableExtra<SCVO>("scVOdata")
        demographic_value.text=data?.demographicScore
        alternate_score.text=data?.alternateScore
        financialScoreValue.text=data?.financialScore
        riskValue.text=data?.customerRiskProfile
        ok.setOnClickListener { finish() }
    }

    override fun onToolbarBackPressed() {

    }


   /* override fun onCreateOptionsMenu(menu: Menu?): Boolean {


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
*/
    override fun screenTitle(): String {
      return "Scorecard"
    }
}
