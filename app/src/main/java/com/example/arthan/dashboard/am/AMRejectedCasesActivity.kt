package com.example.arthan.dashboard.am

import android.widget.Toast
import com.example.arthan.R
import com.example.arthan.global.ArthanApp
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.am_rejected_screenlist.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AMRejectedCasesActivity: BaseActivity() {
    override fun contentView(): Int {

        return R.layout.am_rejected_screenlist
    }

    override fun init() {

        getRejectedScreens()
    }

    private fun getRejectedScreens() {
        val progres=ProgrssLoader(this)
        progres.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            val res=RetrofitFactory.getApiService().getAMRejectStatus(ArthanApp.getAppInstance().loginUser)
            if(res.body()!=null)
            {
                withContext(Dispatchers.Main)
                {
                    progres.dismmissLoading()

                    amrejectedScreens.adapter=AMRejectedCompletedScreensAdapter(this@AMRejectedCasesActivity,"am",res.body()!!)
                }
            }else
            {
                withContext(Dispatchers.Main)
                {
                    Toast.makeText(
                        this@AMRejectedCasesActivity,
                        "No screens found or please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onToolbarBackPressed() {
        finish()
    }

    override fun screenTitle(): String {
      return "AM Rejected"
    }
}