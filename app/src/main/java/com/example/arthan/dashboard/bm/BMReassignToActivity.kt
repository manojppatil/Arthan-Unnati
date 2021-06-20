package com.example.arthan.dashboard.bm

import android.widget.Toast
import com.example.arthan.R
import com.example.arthan.dashboard.bm.adapter.BMReassignToByAdapter
import com.example.arthan.global.ArthanApp
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_b_m_reassign_to.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BMReassignToActivity : BaseActivity() {
    override fun contentView(): Int {
        return R.layout.activity_b_m_reassign_to
    }

    override fun init() {

        loadInitialData()

    }

    private fun loadInitialData() {

        if (intent.getStringExtra("FROM") == "REASSIGN-TO") {
            val progress = ProgrssLoader(this)
            progress.showLoading()
            CoroutineScope(Dispatchers.IO).launch {
                val resp = RetrofitFactory.getApiService()
                    .getBMReassignedTo(ArthanApp.getAppInstance().loginUser)

                if (resp?.body() != null) {
                    withContext(Dispatchers.Main) {
                        progress.dismmissLoading()


                        rvBmReassign.adapter = BMReassignToByAdapter(
                            this@BMReassignToActivity,
                            resp.body()!!.id,
                            resp.body()!!.reAssignedCases
                        )
                    }
                } else {
                    withContext(Dispatchers.Main) {

                        progress.dismmissLoading()
                        Toast.makeText(this@BMReassignToActivity,"No records found",Toast.LENGTH_LONG).show()

                    }
                }
            }
        } else {
            val progress = ProgrssLoader(this)
            progress.showLoading()
            CoroutineScope(Dispatchers.IO).launch {
                val resp = RetrofitFactory.getApiService()
                    .getBMReassignedBy(ArthanApp.getAppInstance().loginUser)

                if (resp?.body() != null) {
                    withContext(Dispatchers.Main) {
                        progress.dismmissLoading()


                        rvBmReassign.adapter = BMReassignToByAdapter(
                            this@BMReassignToActivity,
                            resp.body()!!.id,
                            resp.body()!!.reAssignedCases
                        )
                    }
                } else {
                    withContext(Dispatchers.Main) {

                        progress.dismmissLoading()
                        Toast.makeText(this@BMReassignToActivity,"No records found",Toast.LENGTH_LONG).show()

                    }
                }
            }

        }
    }

    override fun onToolbarBackPressed() {
        finish()
    }

    override fun screenTitle(): String {
        return intent.getStringExtra("FROM")!!
    }

}
