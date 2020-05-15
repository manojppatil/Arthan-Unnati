package com.example.arthan.dashboard.bm

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.arthan.R
import com.example.arthan.dashboard.bcm.BureauDetailsLevel2
import com.example.arthan.dashboard.bcm.BureauViewAllDetailsActivity
import com.example.arthan.dashboard.bm.model.BureauDetails
import com.example.arthan.lead.model.responsedata.ipAddressVO
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_bureau.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.lang.Exception

class BureauActivity : BaseActivity() {

    override fun contentView() = R.layout.activity_bureau

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        btn_search.visibility = View.GONE
        btn_filter.visibility = View.GONE
        customer_detail?.visibility = View.GONE
        /*  txt_customer_name?.text = "Rabiya P"
          btn_view_report?.setOnClickListener {
              val loader = ProgrssLoader(this)
              loader.showLoading()
              CoroutineScope(Dispatchers.IO).launch {
                  try {
                      val response: Response<BureauDetails>? =
                          RetrofitFactory.getMasterApiService().getBureau(
                              "Rabiya P",
                              "AFUPJ7365N",
                              "165049,1128,KFC,BANU NAGAR 29TH AVUNUE PUDUR,Silkboard,BANGALORE,KARNATAKA,600053"
                          )
                      if (response?.isSuccessful == true) {
                          withContext(Dispatchers.Main) {
                              try {
                                  val bureauDetail = response.body()
                                  txt_inquiry_count?.text = "${bureauDetail?.inquiryCount}"
                                  txt_field1?.text = "Age"
                                  txt_value1?.text = "${bureauDetail?.age}"
                                  txt_field2?.text = "Father Name"
                                  txt_value2?.text = "${bureauDetail?.fatherName}"
                                  customer_detail?.visibility = View.VISIBLE
                                  loader.dismmissLoading()
                              } catch (e: Exception) {
                                  e.printStackTrace()
                                  loader.dismmissLoading()
                              }
                          }
                      }
                  } catch (e: Exception) {
                      e.printStackTrace()
                      withContext(Dispatchers.Main) {
                          loader.dismmissLoading()
                      }
                  }
              }
          }*/

        setData()

    }

    private fun setData() {

        val bureau: BureauDetails? = intent?.extras?.getParcelable<BureauDetails>("data")

        txt_appName.text = "Name of the applicant:${bureau?.applicantName}"
        txt_scoreValue.text = "Score:${bureau?.score}"
        noOfLoans.text = "No Of Loans:${bureau?.noOfLoans}"
        noOfActiveLoans.text = "No of active loans:${bureau?.noOfActiveLoans}"
        moOfUnsecuredLoans.text = "No of Unsecured loans:${bureau?.noOfUnsecuredLoans}"
        moOfsecuredLoans.text = "No of secured loans:${bureau?.noOfSecuredLoans}"
        noOfDpdAccounts.text = "No of Dpd account:${bureau?.noOfDpdAccounts}"
        defaultCreditLoan.text = "Default in credit card/Gold loan/Agri loan:${bureau?.defaultCGA}"
        defaultInAutoLoans.text = "Default in Auto loan/other loans:${bureau?.defaultAO}"
        NoOfSuitFiled.text = "No of suit filed,written:${bureau?.noOfSuitfiledWritten}"
        noOfLoanAsGarunter.text = "No of loans as guarantor:${bureau?.noOfLoanAsGuarantor}"
        last6MonthsHist.text = "Last 6 months history:${bureau?.sixMonthsHistory}"
        lastLoanTaken.text = "Last loan taken:${bureau?.lastLoanTaken}"


        noOfActiveLoans.setOnClickListener {


            startActivity(Intent(this, BureauDetailsLevel2::class.java).apply {

                putExtra("type", "noOfActiveLoans")
                putExtra("data",bureau)
            })
        }
        moOfsecuredLoans.setOnClickListener {
            startActivity(Intent(this, BureauDetailsLevel2::class.java).apply {

                putExtra("type", "moOfsecuredLoans")
                putExtra("data",bureau)

            })
        }
        moOfUnsecuredLoans.setOnClickListener {
            startActivity(Intent(this, BureauDetailsLevel2::class.java).apply {

                putExtra("type", "moOfUnsecuredLoans")
                putExtra("data",bureau)

            })
        }
        defaultCreditLoan.setOnClickListener {
            startActivity(Intent(this, BureauDetailsLevel2::class.java).apply {

                putExtra("type", "defaultCreditLoan")
                putExtra("data",bureau)

            })
        }
        defaultInAutoLoans.setOnClickListener {
            startActivity(Intent(this, BureauDetailsLevel2::class.java).apply {

                putExtra("type", "defaultInAutoLoans")
                putExtra("data",bureau)

            })
        }
        noOfActiveLoans.setOnClickListener {
            startActivity(Intent(this, BureauDetailsLevel2::class.java).apply {

                putExtra("type", "noOfActiveLoans")
                putExtra("data",bureau)

            })
        }
        NoOfSuitFiled.setOnClickListener {
            startActivity(Intent(this, BureauDetailsLevel2::class.java).apply {

                putExtra("type", "NoOfSuitFiled")
                putExtra("data",bureau)

            })
        }
        noOfLoanAsGarunter.setOnClickListener {
            startActivity(Intent(this, BureauDetailsLevel2::class.java).apply {

                putExtra("type", "noOfLoanAsGarunter")
                putExtra("data",bureau)

            })
        }
        last6MonthsHist.setOnClickListener {
            startActivity(Intent(this, BureauDetailsLevel2::class.java).apply {

                putExtra("type", "last6MonthsHist")
                putExtra("data",bureau)

            })
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {


        menuInflater.inflate(R.menu.more, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.homeMenu -> {
                finish()

            }
            R.id.logoutMenu -> {
                finish()
                startActivity(Intent(this, SplashActivity::class.java))
            }

        }
        return super.onOptionsItemSelected(item)
    }


    override fun screenTitle() = "Bureau"
}