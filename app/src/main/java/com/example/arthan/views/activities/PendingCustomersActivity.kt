package com.example.arthan.views.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.crashlytics.android.Crashlytics
import com.example.arthan.R
import com.example.arthan.dashboard.rm.adapters.ScreeningAdapter
import com.example.arthan.global.ArthanApp
import com.example.arthan.lead.model.responsedata.BMQueueResponseData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.views.adapters.PendingCustomerAdapter
import kotlinx.android.synthetic.main.activity_bcm_pending_customers.*
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.ProgrssLoader
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_bcm_pending_customers.toolbar
import kotlinx.android.synthetic.main.activity_lisiting.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class PendingCustomersActivity : AppCompatActivity(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bcm_pending_customers)

        setSupportActionBar(toolbar as Toolbar?)
        back_button?.setOnClickListener { onBackPressed() }
        toolbar_title?.text = getString(R.string.pending_placeholder, 31)

        loadInitialData()
    }

    private suspend fun stopLoading(progressBar: ProgrssLoader, message: String?) {
        withContext(uiContext) {
            progressBar.dismmissLoading()
            message?.let {
                Toast.makeText(this@PendingCustomersActivity, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadInitialData() {
        val progressBar = ProgrssLoader(this)
        progressBar.showLoading()

        if(ArthanApp.getAppInstance().loginRole=="BM") {
            CoroutineScope(ioContext).launch {
                try {
                    val response = RetrofitFactory.getMasterApiService().getBMQueue(ArthanApp.getAppInstance().loginUser)
                    if (response?.isSuccessful == true) {
                        val result = response.body()
                        withContext(Dispatchers.Main) {

                            rv_pending_customer.adapter =
                                PendingCustomerAdapter(
                                    this@PendingCustomersActivity,
                                    intent.getStringExtra(ArgumentKey.FROM)
                                ).also {
                                    it.updateList(result?.myQueue)
                                }
                            progressBar.dismmissLoading()
                        }
                    } else {
                        try {
                            val result: BMQueueResponseData? = Gson().fromJson(
                                response?.errorBody()?.string(),
                                BMQueueResponseData::class.java
                            )
                            stopLoading(progressBar, result?.message)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Crashlytics.log(e.message)

                            //    stopLoading(progressBar, "Something went wrong. Please try later!")
                        }
                    }
                } catch (e: Exception) {
                   // stopLoading(progressBar, "Something went wrong. Please try later!")
                    e.printStackTrace()
                    Crashlytics.log(e.message)

                }
            }
        }else if(ArthanApp.getAppInstance().loginRole=="BCM")
        CoroutineScope(ioContext).launch {
            try {
                val response = RetrofitFactory.getMasterApiService().getBCMQueue(ArthanApp.getAppInstance().loginUser)
                if (response?.isSuccessful == true) {
                    val result = response.body()
                    withContext(Dispatchers.Main) {

                        rv_pending_customer.adapter =
                            PendingCustomerAdapter(
                                this@PendingCustomersActivity,
                                intent.getStringExtra(ArgumentKey.FROM)
                            ).also {
                                it.updateList(result?.myQueue)
                            }
                        progressBar.dismmissLoading()
                    }
                } else {
                    try {
                        val result: BMQueueResponseData? = Gson().fromJson(
                            response?.errorBody()?.string(),
                            BMQueueResponseData::class.java
                        )
                        stopLoading(progressBar, result?.message)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Crashlytics.log(e.message)

                        // stopLoading(progressBar, "Something went wrong. Please try later!")
                    }
                }
            } catch (e: Exception) {
                //stopLoading(progressBar, "Something went wrong. Please try later!")
                e.printStackTrace()
                Crashlytics.log(e.message)

            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {


        menuInflater.inflate(R.menu.more,menu)
        val searchItem=menu?.findItem(R.id.searchMenu)
        val searchView=searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                (rv_pending_customer.adapter as PendingCustomerAdapter).filter?.filter(query)
                //Toast.makeText(this,"searchItems",Toast.LENGTH_LONG).show();
                return  true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText.toString()
                var results=(rv_pending_customer.adapter as PendingCustomerAdapter).filter?.filter(query)
                rv_pending_customer!!.adapter?.notifyDataSetChanged()
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
    companion object {
        fun startMe(context: Context?, from: String) =
            context?.startActivity(Intent(context, PendingCustomersActivity::class.java).apply {
                putExtra(ArgumentKey.FROM, from)
            })
    }
}