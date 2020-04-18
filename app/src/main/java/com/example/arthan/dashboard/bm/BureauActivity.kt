package com.example.arthan.dashboard.bm

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.arthan.R
import com.example.arthan.dashboard.bm.model.BureauDetails
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
        txt_customer_name?.text = "Rabiya P"
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
        }

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

    override fun screenTitle() = "Bureau"
}