package com.example.arthan.lead

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.view.accessibility.AccessibilityViewCommand
import com.crashlytics.android.Crashlytics
import com.example.arthan.R
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.global.AppPreferences
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.lead.model.postdata.LoanPostData
import com.example.arthan.lead.model.responsedata.LoanResponseData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_loan_detail.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.getRupeeSymbol
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_add_lead_step1.*

class LoanDetailActivity : BaseActivity(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO + job

    private var mProgressBar: ProgrssLoader? = null
    private var mLeadId: String = ""

    override fun contentView() = R.layout.activity_loan_detail

    override fun onToolbarBackPressed() = onBackPressed()

    private val nTextChangeListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) = Unit
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            var case: Boolean = checkForProceed()

            if(case){
                btn_submit?.isEnabled =case
            }else
            {
                if(property_value_input.hasFocus())
                {
                    if(property_value_input?.text.toString().isNotEmpty() && loan_amount_input?.text.toString().isNotEmpty()&&
                        loan_amount_input?.text.toString().toInt()>property_value_input?.text.toString().toInt())
                    {
                        Toast.makeText(this@LoanDetailActivity,"Loan Amount cant be more than Property Value",Toast.LENGTH_LONG).show()
                    }
                }
                if(loan_amount_input.hasFocus())
                {
                    if(property_value_input?.text.toString().isNotEmpty()  && loan_amount_input?.text.toString().isNotEmpty()&&
                        loan_amount_input?.text.toString().toInt()>property_value_input?.text.toString().toInt())
                    {
                        Toast.makeText(this@LoanDetailActivity,"Loan Amount cant be more than Property Value",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }

    private fun checkForProceed(): Boolean {

          return  loan_amount_input?.text?.isNotEmpty() == true && property_value_input?.text?.isNotEmpty() == true
                    && (property_value_input?.text.toString().isNotEmpty() &&(property_value_input?.text.toString().toInt()>0))
                    &&(business_turnover_input?.text.toString().isNotEmpty() &&(business_turnover_input?.text.toString().toInt()>0))
                    &&(net_profit_margin_input?.text.toString().isNotEmpty() &&(net_profit_margin_input?.text.toString().toInt()>0))
                    && business_turnover_input?.text?.isNotEmpty() == true && net_profit_margin_input?.text?.isNotEmpty() == true
                    && existing_loan_input?.text?.isNotEmpty() == true && existing_loan_obligation_input?.text?.isNotEmpty() == true
                    && additional_income_input?.text?.isNotEmpty() == true && household_expenses_input?.text?.isNotEmpty() == true
                   && (property_value_input?.text.toString().isNotEmpty() &&(property_value_input?.text.toString().toInt()>0) &&
                  loan_amount_input?.text.toString().isNotEmpty() &&(loan_amount_input?.text.toString().toInt()>0)&&
                  loan_amount_input?.text.toString().toInt()<property_value_input?.text.toString().toInt())
                  &&(netMonthlyIncomet?.text.toString().isNotEmpty() &&(netMonthlyIncomet?.text.toString().toInt()>0))


    }

    override fun init() {
        mProgressBar = ProgrssLoader(this)

        intent.apply {
            if (hasExtra(ArgumentKey.LeadId)) {
                mLeadId = getStringExtra(ArgumentKey.LeadId) ?: ""
            }
        }

        loadInitialData()

        loan_amount_input?.addTextChangedListener(nTextChangeListener)
        property_value_input?.addTextChangedListener(nTextChangeListener)
        business_turnover_input?.addTextChangedListener(nTextChangeListener)
        net_profit_margin_input?.addTextChangedListener(nTextChangeListener)
        existing_loan_input?.addTextChangedListener(nTextChangeListener)
        existing_loan_obligation_input?.addTextChangedListener(nTextChangeListener)
        additional_income_input?.addTextChangedListener(nTextChangeListener)
        household_expenses_input?.addTextChangedListener(nTextChangeListener)

        loan_amount_input?.setCompoundDrawablesWithIntrinsicBounds(
            getRupeeSymbol(
                this,
                loan_amount_input?.textSize ?: 14f,
                loan_amount_input?.currentHintTextColor ?: 0
            ), null, null, null
        )

        et_years?.tag = 1
        btn_plus?.setOnClickListener {
            var years = et_years?.tag as? Int ?: 1
            years++
            if(years<=7) {
                et_years?.text = "$years yrs"
                et_years?.tag = years
            }else
            {
                Toast.makeText(this,"maximum tenure is 7 years",Toast.LENGTH_LONG).show()
            }
        }

        btn_minus?.setOnClickListener {
            var years = et_years.tag as? Int ?: 1
            years--
            if (years >= 1) {
                et_years?.text = "$years yrs"
                et_years?.tag = years
            }else
            {
                Toast.makeText(this,"miniumum tenure is 1 year",Toast.LENGTH_LONG).show()

            }
        }

        et_months?.tag = 0
        btn_plus_months?.setOnClickListener {
            var months = et_months?.tag as? Int ?: 0
            months++
            if(months<=12) {
                et_months?.text = "$months months"
                et_months?.tag = months
            }else
            {
//                Toast.makeText(this,"maximum tenure is 7 years",Toast.LENGTH_LONG).show()
            }
        }

        btn_minus_months?.setOnClickListener {
            var months = et_months.tag as? Int ?: 0
            months--
            if (months >= 0) {
                et_months?.text = "$months months"
                et_months?.tag = months
            }else
            {
//                Toast.makeText(this,"miniumum tenure is 1 year",Toast.LENGTH_LONG).show()

            }
        }


        loan_type_spinner?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if(parent?.getItemAtPosition(position) == "Unsecure")
                    {
                        security_offered_spinner.visibility=View.GONE
                        security_jurisdiction_spinner.visibility=View.GONE
                        tl_property_value.visibility=View.GONE
                    }else
                    {
                        security_offered_spinner.visibility=View.VISIBLE
                        security_jurisdiction_spinner.visibility=View.VISIBLE
                        tl_property_value.visibility=View.VISIBLE
                    }
                }
            }

        security_offered_spinner?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if(parent?.getItemAtPosition(position) == "Other Properties") {

                        tl_otherSecurity.visibility=View.VISIBLE
                    }else
                    {
                        tl_otherSecurity.visibility=View.GONE

                    }
                }
            }
        btn_submit?.setOnClickListener {
            saveLoanDetails()
//            startActivity(Intent(this@LoanDetailActivity,LeadScreeningActivity::class.java))
        }

//        rgrp_firm_type.setOnCheckedChangeListener { radioGroup, i ->
//
//            when (i) {
//                R.id.rbtn_company -> {
//                    txt_no_of_director.visibility = View.VISIBLE
//                    btn_minus_director.visibility = View.VISIBLE
//                    btn_plus_director.visibility = View.VISIBLE
//                    txt_director.visibility = View.VISIBLE
//
//                    txt_no_of_partners.visibility = View.GONE
//                    btn_minus_partner.visibility = View.GONE
//                    btn_plus_partner.visibility = View.GONE
//                    txt_partners.visibility = View.GONE
//                }
//                R.id.rbtn_partner_firm -> {
//                    txt_no_of_director.visibility = View.GONE
//                    btn_minus_director.visibility = View.GONE
//                    btn_plus_director.visibility = View.GONE
//                    txt_director.visibility = View.GONE
//
//                    txt_no_of_partners.visibility = View.VISIBLE
//                    btn_minus_partner.visibility = View.VISIBLE
//                    btn_plus_partner.visibility = View.VISIBLE
//                    txt_partners.visibility = View.VISIBLE
//                }
//                else -> {
//                    txt_no_of_director.visibility = View.GONE
//                    btn_minus_director.visibility = View.GONE
//                    btn_plus_director.visibility = View.GONE
//                    txt_director.visibility = View.GONE
//
//                    txt_no_of_partners.visibility = View.VISIBLE
//                    btn_minus_partner.visibility = View.VISIBLE
//                    btn_plus_partner.visibility = View.VISIBLE
//                    txt_partners.visibility = View.VISIBLE
//                }
//            }
//        }


//        txt_customer_age.tag = 0
//        btn_plus_customer.setOnClickListener {
//            var cus = txt_customer_age.tag as Int
//            cus++
//            txt_customer_age.text = "$cus"
//            txt_customer_age.tag = cus
//        }

//        btn_minus_customer.setOnClickListener {
//            var cus = txt_customer_age.tag as Int
//            cus--
//            if (cus < 0)
//                cus = 0
//            txt_customer_age.text = "$cus"
//            txt_customer_age.tag = cus
//        }

//        txt_business_age.tag = 0
//        btn_plus_business.setOnClickListener {
//            var cus = txt_business_age.tag as Int
//            cus++
//            txt_business_age.text = "$cus"
//            txt_business_age.tag = cus
//        }

//        btn_minus_business.setOnClickListener {
//            var cus = txt_business_age.tag as Int
//            cus--
//            if (cus < 0)
//                cus = 0
//            txt_business_age.text = "$cus"
//            txt_business_age.tag = cus
//        }

//        txt_partners.tag = 0
//        btn_plus_partner.setOnClickListener {
//            var cus = txt_partners.tag as Int
//            cus++
//            txt_partners.text = "$cus"
//            txt_partners.tag = cus
//        }

//        btn_minus_partner.setOnClickListener {
//            var cus = txt_partners.tag as Int
//            cus--
//            if (cus < 0)
//                cus = 0
//            txt_partners.text = "$cus"
//            txt_partners.tag = cus
//        }

//        txt_director.tag = 0
//        btn_plus_director.setOnClickListener {
//            var cus = txt_director.tag as Int
//            cus++
//            txt_director.text = "$cus"
//            txt_director.tag = cus
//        }
//
//        btn_minus_director.setOnClickListener {
//            var cus = txt_director.tag as Int
//            cus--
//            if (cus < 0)
//                cus = 0
//            txt_director.text = "$cus"
//            txt_director.tag = cus
//        }
//
//        txt_rooms.tag = 0
//        btn_plus_rooms.setOnClickListener {
//            var cus = txt_rooms.tag as Int
//            cus++
//            txt_rooms.text = "$cus"
//            txt_rooms.tag = cus
//        }
//
//        btn_minus_rooms.setOnClickListener {
//            var cus = txt_rooms.tag as Int
//            cus--
//            if (cus < 0)
//                cus = 0
//            txt_rooms.text = "$cus"
//            txt_rooms.tag = cus
//        }


//        btn_next.setOnClickListener {
//
//        }
    }

    private suspend fun stopLoading(progressBar: ProgrssLoader, message: String?) {
        withContext(Dispatchers.Main) {
            progressBar.dismmissLoading()
            message?.let {
                Toast.makeText(this@LoanDetailActivity, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    // TODO netMonthlyIncomet
    private fun saveLoanDetails() {
        val progressBar = ProgrssLoader(this)
        progressBar.showLoading()
        val postBody = LoanPostData(
            leadId = mLeadId,
            loanAmount = loan_amount_input?.text?.toString() ?: "",
            tenure = (et_years?.tag as? Int)?.toString() ?: "",
            tenorMonth = (et_months?.tag as? Int)?.toString() ?: "",
            loanType = loan_type_spinner?.selectedItem as? String ?: "",
            purposeofLoan = (spnr_loan_purpose?.selectedItem as? Data)?.value ?: "",
            collateralType = (security_offered_spinner?.selectedItem as? Data)?.value ?: "",
            securityJurisdiction = security_jurisdiction_spinner?.selectedItem as? String ?: "",
            propertyValue = property_value_input?.text?.toString() ?: "",
            turnover = business_turnover_input?.text?.toString() ?: "",
            turnoverFreq=when(rb_monthly.isChecked){
                true->"Monthly"
                false->"Yearly"
            },
            netprofitMargin = net_profit_margin_input?.text?.toString() ?: "",
            existingLoan = existing_loan_input?.text?.toString() ?: "",
            existingLoanObligationPm = existing_loan_obligation_input?.text?.toString() ?: "",
            additionalIncomePm = additional_income_input?.text?.toString() ?: "",
            householdExpensesPm = household_expenses_input?.text?.toString() ?: "",
            createdBy = AppPreferences.getInstance().getString(AppPreferences.Key.LoginType),
            userId="RM1"
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitFactory.getApiService().saveLoanDetail(postBody)
                if (response?.isSuccessful == true) {
                    val result = response.body()
                    if (result?.apiCode == "200") {
                        withContext(Dispatchers.Main) {
                            progressBar.dismmissLoading()
                            if(result.eligibility?.toLowerCase()=="y") {
                                AppPreferences.getInstance()
                                    .addString(AppPreferences.Key.LoanId, result.loanId)
                                startActivity(
                                    Intent(
                                        this@LoanDetailActivity,
                                        LeadEligibilityActivity::class.java
                                    ).apply {
                                        putExtra(ArgumentKey.LeadId, mLeadId)
                                        putExtra(ArgumentKey.Eligibility, result.eligibility)
                                    })
                                finish()
                            }else
                            {
                                startActivity(Intent(this@LoanDetailActivity,RMDashboardActivity::class.java))
                                finish()
                            }
                            //removed @@@
                           /* if(result.eligibility.equals("N",ignoreCase = true))
                            {

                                startActivity(Intent(this@LoanDetailActivity,RMDashboardActivity::class.java))
                                finish()
                            }else {
                                startActivity(Intent(this@LoanDetailActivity, LeadEligibilityActivity::class.java).apply {
                                    putExtra(ArgumentKey.LeadId,mLeadId)
                                    putExtra(ArgumentKey.Eligibility,result.eligibility)
                                })
                                finish()
//                                LeadEligibilityActivity.startMe(this@LoanDetailActivity, mLeadId,result.eligibility)
                            }*/ //removed @@
                        }
                    } else {
                        stopLoading(progressBar, result?.apiDesc)
                    }
                } else {
                    try {
                        val result: LoanResponseData? = Gson().fromJson(
                            response?.errorBody()?.string(),
                            LoanResponseData::class.java
                        )
                        stopLoading(progressBar, result?.message)
                    } catch (e: Exception) {
                        Crashlytics.log(e.message)

                        e.printStackTrace()
                        stopLoading(progressBar, "Something went wrong. Please try later!")
                    }
                }
            } catch (e: Exception) {
                Crashlytics.log(e.message)

                stopLoading(progressBar, "Something went wrong. Please try later!")
                e.printStackTrace()
            }
        }
    }

    private fun loadInitialData() {
        val progressLoader = ProgrssLoader(this)
        progressLoader.showLoading()
        launch(ioContext) {
            try {
                val purposeOfLoan = fetchAndUpdatePurposeOfLoanAsync().await()
                val collateralNature = fetchAndUpdateCollateralNatureAsync().await()

                if (purposeOfLoan && collateralNature) {
                    withContext(coroutineContext) {
                        try {
                            progressLoader.dismmissLoading()
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            Crashlytics.log(e.message)

                        }
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Crashlytics.log(e.message)

            }
        }
    }

    private fun fetchAndUpdatePurposeOfLoanAsync(): Deferred<Boolean> = async(context = ioContext) {
        try {
            val response = RetrofitFactory.getMasterApiService().getPurposeOfLoan()
            if (response?.isSuccessful == true) {
                withContext(Dispatchers.Main) {
                    try {
                        spnr_loan_purpose?.adapter = DataSpinnerAdapter(
                            this@LoanDetailActivity,
                            response.body()?.data?.toMutableList() ?: mutableListOf()
                        ).also {
                            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Crashlytics.log(e.message)

                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Crashlytics.log(e.message)

        }
        return@async true
    }

    private fun fetchAndUpdateCollateralNatureAsync(): Deferred<Boolean> =
        async(context = ioContext) {
            try {
                val response = RetrofitFactory.getMasterApiService().getCollateralNature()
                if (response?.isSuccessful == true) {
                    withContext(Dispatchers.Main) {
                        try {
                            security_offered_spinner?.adapter = DataSpinnerAdapter(
                                this@LoanDetailActivity,
                                response.body()?.data?.toMutableList() ?: mutableListOf()
                            ).also {
                                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Crashlytics.log(e.message)

                        }
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Crashlytics.log(e.message)

            }
            return@async true
        }

    override fun screenTitle() = "Loan Details"

    companion object {
        fun startMe(context: Context?, leadId: String?) =
            context?.startActivity(Intent(context, LoanDetailActivity::class.java).apply {
                putExtra(ArgumentKey.LeadId, leadId)
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