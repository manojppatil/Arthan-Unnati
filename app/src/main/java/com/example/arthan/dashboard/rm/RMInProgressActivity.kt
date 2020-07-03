package com.example.arthan.dashboard.rm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.crashlytics.android.Crashlytics
import com.example.arthan.R
import com.example.arthan.dashboard.rm.adapters.RMInProgressAdapter
import com.example.arthan.global.ArthanApp
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import kotlinx.android.synthetic.main.activity_lisiting.*
import kotlinx.android.synthetic.main.activity_lisiting.toolbar
import kotlinx.android.synthetic.main.activity_r_m_in_progress.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RMInProgressActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_r_m_in_progress)

        toolbar_title?.text = "In Progress"
        setSupportActionBar(toolbar as Toolbar?)
        back_button?.setOnClickListener { onBackPressed() }
        if(ArthanApp.getAppInstance().loginRole=="RM") {
            getRmInProgressData()
        }else if(ArthanApp.getAppInstance().loginRole=="BM") {
            getBMInProgressData()
        }else if(ArthanApp.getAppInstance().loginRole=="BCM") {
            getBCMInProgressData()
        }
    }

    private fun getRmInProgressData() {

        val progressBar: ProgrssLoader? = ProgrssLoader(this)
        progressBar?.showLoading()
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val response=RetrofitFactory.getApiService().getRMInprogress(ArthanApp.getAppInstance().loginUser)
                if(response?.body()!=null) {
                    withContext(Dispatchers.Main) {
                        progressBar?.dismmissLoading()
                        if (response.body()!!.casesData != null)
                            rv_rmInProgress.adapter = RMInProgressAdapter(
                                this@RMInProgressActivity,
                                response.body()!!.casesData!!
                            )
                    }
                }
            }
        }catch (e:Exception)
        {
                progressBar?.dismmissLoading()
            Crashlytics.log(e.message)
        }
    }
    private fun getBMInProgressData() {

        val progressBar: ProgrssLoader? = ProgrssLoader(this)
        progressBar?.showLoading()
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val response=RetrofitFactory.getApiService().getBMInprogress(ArthanApp.getAppInstance().loginUser)
                if(response?.body()!=null) {
                    withContext(Dispatchers.Main) {
                        progressBar?.dismmissLoading()
                        if (response.body()!!.casesData != null)
                            rv_rmInProgress.adapter = RMInProgressAdapter(
                                this@RMInProgressActivity,
                                response.body()!!.casesData!!
                            )
                    }
                }
            }
        }catch (e:Exception)
        {
                progressBar?.dismmissLoading()
            Crashlytics.log(e.message)
        }
    }
    private fun getBCMInProgressData() {

        val progressBar: ProgrssLoader? = ProgrssLoader(this)
        progressBar?.showLoading()
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val response=RetrofitFactory.getApiService().getBCMInprogress(ArthanApp.getAppInstance().loginUser)
                if(response?.body()!=null) {
                    withContext(Dispatchers.Main) {
                        progressBar?.dismmissLoading()
                        if (response.body()!!.casesData != null)
                            rv_rmInProgress.adapter = RMInProgressAdapter(
                                this@RMInProgressActivity,
                                response.body()!!.casesData!!
                            )
                    }
                }
            }
        }catch (e:Exception)
        {
                progressBar?.dismmissLoading()
            Crashlytics.log(e.message)
        }
    }
}
