package com.example.arthan.dashboard.bcm

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.arthan.R
import com.example.arthan.global.ArthanApp
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_b_c_m_re_assigned.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BCMReAssignedActivity : BaseActivity() {
    override fun contentView(): Int {
        return R.layout.activity_b_c_m_re_assigned
    }

    override fun init() {

        getBCMReassignedData()
    }

    private fun getBCMReassignedData() {

        val progress=ProgrssLoader(this)
        progress.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            val res=RetrofitFactory.getApiService().getBCMReAssigned(ArthanApp.getAppInstance().loginUser)
            if(res.body()!=null)
            {
                withContext(Dispatchers.Main)
                {
                    progress.dismmissLoading()
                    bcmreassignedRv.adapter=BCMReassignAdapter(this@BCMReAssignedActivity,res.body()!!.reAssignedData)
                }
            }else
            {
                withContext(Dispatchers.Main)
                {
                    progress.dismmissLoading()
                }
            }
        }
    }


    override fun onToolbarBackPressed() {
        finish()
    }



    override fun screenTitle(): String {
        return "Re-Assigned"
    }
}
