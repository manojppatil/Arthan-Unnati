package com.example.arthan.dashboard.bm

import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.arthan.R
import com.example.arthan.dashboard.bm.adapter.BMDocumentVerificationAdapter
import com.example.arthan.dashboard.ops.BCMDataFragment
import com.example.arthan.dashboard.ops.DataFragment
import com.example.arthan.lead.model.responsedata.CustomerDocumentAndDataResponseData
import com.example.arthan.model.Customer
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.views.activities.SplashActivity

class BMDocumentVerificationActivity : BaseActivity(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    override fun contentView() = R.layout.activity_bm_document_verification

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        btn_search.visibility = View.GONE
        btn_filter.visibility = View.GONE

        vp_profile.adapter =
            BMDocumentVerificationAdapter(
                supportFragmentManager,
                intent?.getStringExtra("FROM")!!
            )
        tb_profile.setupWithViewPager(vp_profile)
        vp_profile?.offscreenPageLimit = 3

        if (intent.getStringExtra("recordType") == "AM") {

            loadInitialDataForAM(
                intent?.getStringExtra(ArgumentKey.LoanId),
                intent?.getStringExtra(ArgumentKey.CustomerId)
            )
        } else {
            loadInitialData(
                intent?.getStringExtra(ArgumentKey.LoanId),
                intent?.getStringExtra(ArgumentKey.CustomerId)
            )
        }
    }


    private suspend fun stopLoading(progressBar: ProgrssLoader, message: String?) {
        withContext(uiContext) {
            progressBar.dismmissLoading()
            message?.let {
                Toast.makeText(this@BMDocumentVerificationActivity, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadInitialData(loanId: String?, customerId: String?) {
        val progressBar = ProgrssLoader(this)
        progressBar.showLoading()

        CoroutineScope(ioContext).launch {
            try {
                val response = RetrofitFactory.getApiService().getBMDocumentAndData(loanId)
                if (response?.isSuccessful == true) {
                    val result = response.body()
                    withContext(Dispatchers.Main) {
                        ((vp_profile.adapter as? BMDocumentVerificationAdapter)?.getItem(0) as? DocumentVerificationFragment)?.updateData(
                            result?.docDetails,this@BMDocumentVerificationActivity
                        )
                        ((vp_profile.adapter as? BMDocumentVerificationAdapter)?.getItem(1) as? DataFragment)?.updateData(
                            loanId,
                            result,
                            customerId
                        )
                        if(vp_profile.adapter?.count!!>2) {
                            ((vp_profile.adapter as? BMDocumentVerificationAdapter)?.getItem(2) as? BCMDataFragment)?.updateLoanAndCustomerId(
                                loanId,
                                customerId
                            )
                        }
                        progressBar.dismmissLoading()
                    }
                } else {
                    try {
                        val result: CustomerDocumentAndDataResponseData? = Gson().fromJson(
                            response?.errorBody()?.string(),
                            CustomerDocumentAndDataResponseData::class.java
                        )
                        stopLoading(progressBar, result?.message)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        stopLoading(progressBar, "Data missing. Something went wrong. Please try later!")
                    }
                }
            } catch (e: Exception) {
                stopLoading(progressBar, "Something went wrong. Please try later!")
                e.printStackTrace()
            }
        }
    }
    private fun loadInitialDataForAM(loanId: String?, customerId: String?) {
        val progressBar = ProgrssLoader(this)
        progressBar.showLoading()

        CoroutineScope(ioContext).launch {
            try {
                val response = RetrofitFactory.getApiService().getBMAmDocnData(intent.getStringExtra("amId"))
                if (response?.isSuccessful == true) {
                    val result = response.body()
                    withContext(Dispatchers.Main) {
                        ((vp_profile.adapter as? BMDocumentVerificationAdapter)?.getItem(0) as? DocumentVerificationFragment)?.updateDataAM(
                            result?.docDetails,this@BMDocumentVerificationActivity
                        )
                        ((vp_profile.adapter as? BMDocumentVerificationAdapter)?.getItem(1) as? DataFragment)?.updateDataAM(
                            loanId,
                            result,
                            customerId
                        )
                        if(vp_profile.adapter?.count!!>2) {
                            ((vp_profile.adapter as? BMDocumentVerificationAdapter)?.getItem(2) as? BCMDataFragment)?.updateLoanAndCustomerId(
                                loanId,
                                customerId
                            )
                        }
                        progressBar.dismmissLoading()
                    }
                } else {
                    try {
                        val result: CustomerDocumentAndDataResponseData? = Gson().fromJson(
                            response?.errorBody()?.string(),
                            CustomerDocumentAndDataResponseData::class.java
                        )
                        stopLoading(progressBar, result?.message)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        stopLoading(progressBar, "Data missing. Something went wrong. Please try later!")
                    }
                }
            } catch (e: Exception) {
                stopLoading(progressBar, "Something went wrong. Please try later!")
                e.printStackTrace()
            }
        }
    }

    override fun screenTitle() = "Document Verification"

    fun moveToData()
    {
        vp_profile.currentItem = 1
    }
    companion object {
        fun startMe(
            context: Context?,
            loanId: String?,
            customerId: String?,
            customer: Customer,
            from: String,
            recordType: String?
        ) = context?.startActivity(
            Intent(
                context,
                BMDocumentVerificationActivity::class.java
            ).apply {
                putExtra(ArgumentKey.LoanId, loanId)
                putExtra(ArgumentKey.CustomerId, customerId)
                putExtra("FROM",from)
                putExtra("indSeg",customer.indSeg)
                putExtra("loginDate",customer.loginDate)
                putExtra("loanId",customer.loanId)
                putExtra("loanAmt",customer.loanAmt)
                putExtra("cname",customer.customerName)
                putExtra("custId",customer.customerId)
                putExtra("loanType",customer.loanType)
                putExtra("recordType","AM")
                putExtra("amId","0017")

            })
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
}