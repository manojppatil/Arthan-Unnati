package com.example.arthan.dashboard.bm

import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.arthan.R
import com.example.arthan.dashboard.bcm.BCMDashboardActivity
import com.example.arthan.dashboard.bcm.PDActivity
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.global.ArthanApp
import com.example.arthan.lead.model.responsedata.Cust360ResponseData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.FinanceFragmentCust360
import com.example.arthan.views.activities.PendingCustomersActivity
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_customer360.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class Customer360Activity : BaseActivity(), View.OnClickListener, CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    private var mCustomer360Data: Cust360ResponseData? = null

    override fun contentView() = R.layout.activity_customer360

    override fun onToolbarBackPressed() = onBackPressed()
    private var loanId:String?=null
    private var customerId:String?=null


    override fun init() {

        btn_search.visibility = View.GONE
        btn_filter.visibility = View.GONE

        cl_id_address.setOnClickListener(this)
        cl_bureau.setOnClickListener(this)
        cl_banking.setOnClickListener(this)
        cl_collateral.setOnClickListener(this)
        cl_finances.setOnClickListener(this)
        cl_rcu_check.setOnClickListener(this)
        cl_assets.setOnClickListener(this)
        cl_score_card.setOnClickListener(this)
        cl_pd.setOnClickListener(this)
        cl_deviation.setOnClickListener(this)
        txt_industry.text=intent.getStringExtra("indSeg")
        txt_amount.text=intent.getStringExtra("loanAmt")
        txt_loan_type.text=intent.getStringExtra("loanType")
        loanId=intent.getStringExtra("loanId")
        customerId=intent.getStringExtra("custId")


        loadInitialData()
        submitStatus.setOnClickListener {
            val progressLoader=ProgrssLoader(this)
            progressLoader.showLoading()
            CoroutineScope(Dispatchers.IO).launch {

                val res=RetrofitFactory.getApiService().getCust360Status(intent.getStringExtra("loanId"))
                if(res?.body()!=null)
                {
                    withContext(Dispatchers.Main)
                    {
                        progressLoader.dismmissLoading()
                        startActivity(Intent(this@Customer360Activity,PendingCustomersActivity::class.java).apply {
                            putExtra("loanId",intent.getStringExtra("loanId"))
                        })
                        finish()
                        /*if(res.body().canNavigate=="yes")
                        {

                        }*/
                    }
                }
            }
        }

    }

    private suspend fun stopLoading(progressBar: ProgrssLoader, message: String?) {
        withContext(uiContext) {
            progressBar.dismmissLoading()
            message?.let {
                Toast.makeText(this@Customer360Activity, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadInitialData() {
        val progressBar = ProgrssLoader(this)
        progressBar.showLoading()

        CoroutineScope(ioContext).launch {
            try {
                val apiResponse = RetrofitFactory.getApiService()
                    .getCustomer360Details(intent.getStringExtra("loanId"))
                if (apiResponse?.isSuccessful == true) {
                    mCustomer360Data = apiResponse.body()
                    if(mCustomer360Data?.canNavigate.equals("N",ignoreCase = true)){ //TODO check mail 04-06-20
                        withContext(Dispatchers.Main) {

                            progressBar.dismmissLoading()
                            Toast.makeText(
                                this@Customer360Activity,
                                apiResponse.body()?.message,
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                    }else{
                    withContext(Dispatchers.Main) {
                        setDataToFields(mCustomer360Data)
                        progressBar.dismmissLoading()

                    }
                    stopLoading(progressBar, null)
                }
                } else {
                    stopLoading(progressBar, "Something went wrong!!")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setDataToFields(mCustomer360Data: Cust360ResponseData?) {

//        txt_industry.text=mCustomer360Data.collateralVO.
        txt_addressline1.text=mCustomer360Data?.collateralVO?.addressline1
        txt_addressline2.text=mCustomer360Data?.collateralVO?.addressline2
        txt_addressline3.text=mCustomer360Data?.collateralVO?.areaname
    }

    override fun screenTitle() = "Customer 360"

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.cl_id_address -> startActivity(Intent(this, IDAddressActivity::class.java).apply {
                putExtra("loanId",loanId)
                putExtra("custId",customerId)
                putExtra("cname",intent.getStringExtra("cname"))
                putExtra("idData",mCustomer360Data?.ipAddressVO)
            })
            R.id.cl_bureau -> startActivity(Intent(this, BureauActivity::class.java).apply {
                putExtra("data",mCustomer360Data?.bureauData)
                putExtra("cname",intent.getStringExtra("cname"))

            })
            R.id.cl_banking -> startActivity(Intent(this, BankingActivity::class.java).apply {
                putExtra("loanId",loanId)
                putExtra("custId",customerId)
                putExtra("cname",intent.getStringExtra("cname"))
                putExtra("data",mCustomer360Data?.bankData)
            })
            R.id.cl_collateral -> startActivity(Intent(this, CollateralActivity::class.java)
                .apply {
                    putExtra("data",mCustomer360Data?.collateralVO)

                })
            R.id.cl_finances ->  FinanceFragmentCust360.startMe(this, mCustomer360Data?.pdVO,intent.getStringExtra("cname"))
            R.id.cl_rcu_check -> ""
            R.id.cl_assets -> startActivity(Intent(this, AssetsActivity::class.java).apply {
                putExtra("data",mCustomer360Data?.asset)
                putExtra("cname",intent.getStringExtra("cname"))
            })
            R.id.cl_score_card -> startActivity(Intent(this, ScoreCardActivity::class.java).apply {
                putExtra("scVOdata",mCustomer360Data?.scVO)
//                putExtra("scVOdata",mCustomer360Data?.scVO)
//                putExtra("scVOdata",mCustomer360Data?.scVO)
//                putExtra("scVOdata",mCustomer360Data?.scVO)
//                putExtra("scVOdata",mCustomer360Data?.scVO)
            })
            R.id.cl_pd ->PDActivity.startMe(this, mCustomer360Data?.pdCust,intent.getStringExtra("cname"))
            R.id.cl_deviation -> startActivity(Intent(this, DeviationsActivity::class.java).apply {
                putExtra("loanId",loanId)
                putExtra("custId",customerId)
                putExtra("custId",intent?.getStringExtra("cname"))
            })
        }

    }

    companion object {
        fun startMe(context: Context?, loanId: String?) =
            context?.startActivity(Intent(context, Customer360Activity::class.java).apply {
                putExtra(ArgumentKey.LoanId, loanId)
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