package com.example.arthan.lead

import android.content.Intent
import android.view.View
import com.example.arthan.R
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_lead_screening.*
import kotlinx.android.synthetic.main.fragment_income_information.*

class LeadScreeningActivity: BaseActivity() {

    override fun contentView()= R.layout.activity_lead_screening

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

//        rgrp_firm_type.setOnCheckedChangeListener { radioGroup, i ->
//
//           when(i){
//               R.id.rbtn_company -> {
//                   txt_no_of_director.visibility= View.VISIBLE
//                   btn_minus_director.visibility= View.VISIBLE
//                   btn_plus_director.visibility= View.VISIBLE
//                   txt_director.visibility= View.VISIBLE
//
//                   txt_no_of_partners.visibility= View.GONE
//                   btn_minus_partner.visibility= View.GONE
//                   btn_plus_partner.visibility= View.GONE
//                   txt_partners.visibility= View.GONE
//               }
//               R.id.rbtn_partner_firm -> {
//                   txt_no_of_director.visibility= View.GONE
//                   btn_minus_director.visibility= View.GONE
//                   btn_plus_director.visibility= View.GONE
//                   txt_director.visibility= View.GONE
//
//                   txt_no_of_partners.visibility= View.VISIBLE
//                   btn_minus_partner.visibility= View.VISIBLE
//                   btn_plus_partner.visibility= View.VISIBLE
//                   txt_partners.visibility= View.VISIBLE
//               }
//               else -> {
//                   txt_no_of_director.visibility= View.GONE
//                   btn_minus_director.visibility= View.GONE
//                   btn_plus_director.visibility= View.GONE
//                   txt_director.visibility= View.GONE
//
//                   txt_no_of_partners.visibility= View.VISIBLE
//                   btn_minus_partner.visibility= View.VISIBLE
//                   btn_plus_partner.visibility= View.VISIBLE
//                   txt_partners.visibility= View.VISIBLE
//               }
//           }
//        }
//
//
//        btn_plus_customer.tag = 0
//        btn_minus_customer.tag = 0
//        btn_plus_customer.setOnClickListener {
//            var cus= btn_plus_customer.tag as Int
//            cus++
//            txt_customer_age.text = "$cus"
//            btn_plus_customer.tag = cus
//            btn_minus_customer.tag = cus
//        }
//
//        btn_minus_customer.setOnClickListener {
//            var cus= btn_minus_customer.tag as Int
//            cus--
//            if(cus < 0)
//                cus= 0
//            txt_customer_age.text = "$cus"
//            btn_plus_customer.tag = cus
//            btn_minus_customer.tag = cus
//        }
//
//        btn_plus_business.tag = 0
//        btn_minus_business.tag = 0
//        btn_plus_customer.setOnClickListener {
//            var cus= btn_plus_business.tag as Int
//            cus++
//            txt_business_age.text = "$cus"
//            btn_plus_business.tag = cus
//            btn_minus_business.tag = cus
//        }
//
//        btn_minus_business.setOnClickListener {
//            var cus= btn_minus_business.tag as Int
//            cus--
//            if(cus < 0)
//                cus= 0
//            txt_business_age.text = "$cus"
//            btn_plus_business.tag = cus
//            btn_minus_business.tag = cus
//        }
//
//        btn_plus_partner.tag = 0
//        btn_minus_partner.tag = 0
//        btn_plus_partner.setOnClickListener {
//            var cus= btn_plus_partner.tag as Int
//            cus++
//            txt_partners.text = "$cus"
//            btn_plus_partner.tag = cus
//            btn_minus_partner.tag = cus
//        }
//
//        btn_minus_partner.setOnClickListener {
//            var cus= btn_minus_partner.tag as Int
//            cus--
//            if(cus < 0)
//                cus= 0
//            txt_partners.text = "$cus"
//            btn_plus_partner.tag = cus
//            btn_minus_partner.tag = cus
//        }
//
//        btn_plus_director.tag = 0
//        btn_minus_director.tag = 0
//        btn_plus_director.setOnClickListener {
//            var cus= btn_plus_director.tag as Int
//            cus++
//            txt_director.text = "$cus"
//            btn_plus_director.tag = cus
//            btn_minus_director.tag = cus
//        }
//
//        btn_minus_director.setOnClickListener {
//            var cus= btn_minus_director.tag as Int
//            cus--
//            if(cus < 0)
//                cus= 0
//            txt_director.text = "$cus"
//            btn_plus_director.tag = cus
//            btn_minus_director.tag = cus
//        }
//
//        btn_plus_rooms.tag = 0
//        btn_minus_rooms.tag = 0
//        btn_plus_rooms.setOnClickListener {
//            var cus= btn_plus_rooms.tag as Int
//            cus++
//            txt_rooms.text = "$cus"
//            btn_plus_rooms.tag = cus
//            btn_minus_rooms.tag = cus
//        }
//
//        btn_minus_rooms.setOnClickListener {
//            var cus= btn_minus_rooms.tag as Int
//            cus--
//            if(cus < 0)
//                cus= 0
//            txt_rooms.text = "$cus"
//            btn_plus_rooms.tag = cus
//            btn_minus_rooms.tag = cus
//        }
//
//
//        btn_next.setOnClickListener {
//            startActivity(Intent(this@LeadScreeningActivity,LeadEligibilityActivity::class.java))
//        }
    }

    override fun screenTitle()= "Lead Screening"

}