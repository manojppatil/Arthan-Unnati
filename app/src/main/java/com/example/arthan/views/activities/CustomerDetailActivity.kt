package com.example.arthan.views.activities

import android.content.Intent
import android.view.View
import com.example.arthan.R
import com.example.arthan.global.ArthanApp
import kotlinx.android.synthetic.main.activity_customer_details.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*

class CustomerDetailActivity: BaseActivity(),View.OnClickListener {

    override fun contentView()= R.layout.activity_customer_details

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        btn_take_action.setOnClickListener {
            startActivity(Intent(this@CustomerDetailActivity,FinalReportActivity::class.java))
        }

        cl_personal.setOnClickListener(this)
        cl_business.setOnClickListener(this)
        cl_income.setOnClickListener(this)
        ll_login_checklist.setOnClickListener(this)

        btn_search.visibility= View.GONE
        btn_filter.visibility= View.GONE


        if(ArthanApp.getAppInstance().loginRole.equals("OPS")){
            img_sanction_letter.visibility= View.VISIBLE
            txt_sanction_condition1.visibility= View.VISIBLE
            txt_sanction_condition2.visibility= View.VISIBLE
        }else {
            img_sanction_letter.visibility= View.GONE
            txt_sanction_condition1.visibility= View.GONE
            txt_sanction_condition2.visibility= View.GONE
        }
    }

    override fun screenTitle()= "Customer Full Name"


    override fun onClick(view: View?) {

        when(view?.id){
            R.id.cl_personal,R.id.cl_business,R.id.cl_income,R.id.ll_login_checklist -> {
                startActivity(Intent(this@CustomerDetailActivity,ProfileActivity::class.java))
            }
        }

    }
}