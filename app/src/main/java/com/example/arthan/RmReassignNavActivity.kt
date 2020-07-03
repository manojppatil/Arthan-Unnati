package com.example.arthan

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.arthan.dashboard.rm.adapters.RMReAssignNavAdapter
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import kotlinx.android.synthetic.main.activity_r_m_screening_navigation.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RmReassignNavActivity : AppCompatActivity() {
    private var task: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_r_m_screening_navigation)
        //   back_button.setOnClickListener { onBackPressed() }
//        toolbar_title.text = "Complete Loan Details"

        getRmAssignPendingScreenList()

    }

    private fun getRmAssignPendingScreenList() {

        val loader = ProgrssLoader(this)
        loader.showLoading()

        CoroutineScope(Dispatchers.IO).launch {

            val response =
                RetrofitFactory.getApiService()
                    .getRMReAssignedStatus(intent.getStringExtra("loanId")!!)

            if (response?.body() != null) {
                withContext(Dispatchers.Main) {
                    continueScreen.visibility = View.GONE
                    loader.dismmissLoading()

                    rvScreeningList.adapter =
                        RMReAssignNavAdapter(
                            this@RmReassignNavActivity,
                            "ReScreen",
                            response.body()
                        )

                }
            } else {
                withContext(Dispatchers.Main)
                {
                    loader.dismmissLoading()
                }
            }
        }
    }


}