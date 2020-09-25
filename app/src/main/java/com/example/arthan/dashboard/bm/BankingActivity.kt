package com.example.arthan.dashboard.bm

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.arthan.R
import com.example.arthan.dashboard.bcm.BCMDashboardActivity
import com.example.arthan.dashboard.bm.adapter.BankingAdapter
import com.example.arthan.dashboard.bm.model.Banking360DetailsResponseData
import com.example.arthan.dashboard.bm.model.BureauDetails
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.global.ArthanApp
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_deviations.*
import kotlinx.android.synthetic.main.activity_lisiting.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class BankingActivity : AppCompatActivity(),CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private val ioContext: CoroutineContext
        get() = Dispatchers.IO
    private val uiContext: CoroutineContext
        get() = Dispatchers.Main
    private var detailsList: ArrayList<Banking360DetailsResponseData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lisiting)


        toolbar_title?.text = "Banking | "+intent.getStringExtra("cname")?:" "
        back_button?.setOnClickListener { onBackPressed() }
//        rv_listing.adapter = BankingAdapter(this, detailsList)
//        loadInitialData()
        setDataToAdapter()
    }

    private fun setDataToAdapter() {

        val data: Banking360DetailsResponseData? = intent?.extras?.getParcelable<Banking360DetailsResponseData>("data")
        if(data!= null) {
            detailsList.add(data)
            rv_listing.adapter = BankingAdapter(this@BankingActivity, detailsList)
        }


    }

    private fun loadInitialData() {
        val progressBar = ProgrssLoader(this)
        progressBar.showLoading()
        CoroutineScope(ioContext).launch {
            try {
                val apiResponse = RetrofitFactory.getApiService()
                    .getBankingC360(intent.getStringExtra("loanId"))
                if (apiResponse?.isSuccessful == true) {
                    val result = apiResponse.body()
                    withContext(uiContext) {
                        if (result != null) {

                            detailsList.add(result)
                            rv_listing.adapter=BankingAdapter(this@BankingActivity,detailsList)

                           /* (recycler_list?.adapter as? BankingAdapter)
                                ?.updateList(detailsList)*/
                        }
                        stopLoading(progressBar, null)
                    }
                } else {
                    stopLoading(progressBar, "Something went wrong!!")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                stopLoading(progressBar, "Something went wrong!!")
            }
        }
    }
    private suspend fun stopLoading(progressBar: ProgrssLoader, message: String?) {
        withContext(uiContext) {
            progressBar.dismmissLoading()
            message?.let {
                Toast.makeText(this@BankingActivity, it, Toast.LENGTH_LONG).show()
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
}