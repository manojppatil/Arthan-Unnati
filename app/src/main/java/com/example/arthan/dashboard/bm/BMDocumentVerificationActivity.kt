package com.example.arthan.dashboard.bm

import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.arthan.R
import com.example.arthan.dashboard.bcm.BCMDashboardActivity
import com.example.arthan.dashboard.bcm.PdResponseInterface
import com.example.arthan.dashboard.bm.adapter.BMDocumentVerificationAdapter
import com.example.arthan.dashboard.ops.BCMDataFragment
import com.example.arthan.dashboard.ops.DataFragment
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.global.ArthanApp
import com.example.arthan.lead.model.postdata.DocumentsData
import com.example.arthan.lead.model.postdata.PD23PostData
import com.example.arthan.lead.model.responsedata.CustomerDocumentAndDataResponseData
import com.example.arthan.lead.model.responsedata.Pd1
import com.example.arthan.lead.model.responsedata.Pd23
import com.example.arthan.model.Customer
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.SplashActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_bm_document_verification.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class BMDocumentVerificationActivity : BaseActivity(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main
     var pd1ResponseInter:PdResponseInterface? = null
     var pd2ResponseInter:PdResponseInterface? = null
     var pd3ResponseInter:PdResponseInterface? = null

    override fun contentView() = R.layout.activity_bm_document_verification
    var pd1Response:Pd1?=null
    var pd23Response:PD23PostData?=null

    override fun onToolbarBackPressed() = onBackPressed()
    var list=ArrayList<DocumentsData>()
    override fun init() {

        btn_search.visibility = View.GONE
        btn_filter.visibility = View.GONE

        vp_profiler.adapter =
            BMDocumentVerificationAdapter(
                supportFragmentManager,
                intent?.getStringExtra("FROM")!!,
                intent.getStringExtra("recordType")

            )
        tb_profiler.setupWithViewPager(vp_profiler)
        vp_profiler?.offscreenPageLimit = 3
      /*  vp_profiler.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                tb_profiler.getTabAt(position)?.select()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })*/


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
                        if (intent.getStringExtra("recordType") == "AM") {

                              ((vp_profiler.adapter as? BMDocumentVerificationAdapter)?.getItem(0) as? DocumentVerificationFragment)?.updateData(
                                  result?.docDetails,this@BMDocumentVerificationActivity
                              )
                        }else {
                            pd1Response=result?.pd1
                            pd23Response=result?.pd23
                            pd1ResponseInter?.setResponseToFields()
                            pd3ResponseInter?.setResponseToFields()
                            pd2ResponseInter?.setResponseToFields()
                            ((vp_profiler.adapter as? BMDocumentVerificationAdapter)?.getItem(0) as? DocumentVerificationFragmentNew)?.updateData(
                                result?.businessDocs,
                                result?.bussPremisesDocs,
                                result?.kycDocs,
                                result?.coAppKycDocs,
                                result?.residentialDocs,
                                this@BMDocumentVerificationActivity
                            )
                        }
                        ((vp_profiler.adapter as? BMDocumentVerificationAdapter)?.getItem(1) as? DataFragment)?.updateData(
                            loanId,
                            result,
                            customerId
                        )
                        if(vp_profiler.adapter?.count!!>2) {
                            ((vp_profiler.adapter as? BMDocumentVerificationAdapter)?.getItem(2) as? BCMDataFragment)?.updateLoanAndCustomerId(
                                loanId,
                                customerId)
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
    fun moveVPinDataFragment(position:Int)
    {
        var frag=(vp_profiler.adapter as? BMDocumentVerificationAdapter)?.getItem(1) as? DataFragment
        frag?.moveToPosition(position)
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
                        ((vp_profiler.adapter as? BMDocumentVerificationAdapter)?.getItem(0) as? DocumentVerificationFragment)?.updateDataAM(
                            result?.docDetails,this@BMDocumentVerificationActivity
                        )
                        ((vp_profiler.adapter as? BMDocumentVerificationAdapter)?.getItem(1) as? DataFragment)?.updateDataAM(
                            loanId,
                            result,
                            customerId
                        )
                       /* if(vp_profiler.adapter?.count!!>2) {
                            ((vp_profiler.adapter as? BMDocumentVerificationAdapter)?.getItem(2) as? BCMDataFragment)?.updateLoanAndCustomerId(
                                loanId,
                                customerId,
                                result
                            )
                        }*/
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
        vp_profiler.setCurrentItem(1,true)
//        tb_profiler.getTabAt(1)?.select()

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
                putExtra("recordType",customer.recordType)
                putExtra("amId",customer.amId)

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