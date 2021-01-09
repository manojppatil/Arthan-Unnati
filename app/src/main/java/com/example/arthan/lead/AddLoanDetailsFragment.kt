package com.example.arthan.lead


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment

import com.example.arthan.R
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.global.AppPreferences
import com.example.arthan.global.ArthanApp
import com.example.arthan.global.Crashlytics
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.postdata.LoanPostData
import com.example.arthan.lead.model.responsedata.LoanResponseData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_add_loan_details.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.getRupeeSymbol

/**
 * A simple [Fragment] subclass.
 */
class AddLoanDetailsFragment : NavHostFragment(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO + job

    private var mProgressBar: ProgrssLoader? = null
    private var mLeadId: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_loan_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments.apply {
            //            if (hasExtra(ArgumentKey.LeadId)) {
//                mLeadId = getStringExtra(ArgumentKey.LeadId) ?: ""
//            }
        }

        loadInitialData()

        loan_amount_input?.setCompoundDrawablesWithIntrinsicBounds(
            getRupeeSymbol(
                context,
                loan_amount_input?.textSize ?: 14f,
                loan_amount_input?.currentHintTextColor ?: 0
            ), null, null, null
        )

        et_years?.tag = 0
        btn_plus?.setOnClickListener {
            var years = et_years?.tag as? Int ?: 0
            years++
            et_years?.text = "$years yrs"
            et_years?.tag = years
        }

        btn_minus?.setOnClickListener {
            var years = et_years.tag as? Int ?: 0
            years--
            if (years < 0)
                years = 0
            et_years?.text = "$years yrs"
            et_years?.tag = years
        }

        btn_submit?.setOnClickListener {
            saveLoanDetails()
//            startActivity(Intent(this@LoanDetailActivity,LeadScreeningActivity::class.java))
        }
    }

    private suspend fun stopLoading(progressBar: ProgrssLoader?, message: String?) {
        withContext(Dispatchers.Main) {
            progressBar?.dismmissLoading()
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveLoanDetails() {
        var reassign="N"
        if(activity?.intent?.getStringExtra("task").equals("RM_AssignList",ignoreCase = true)){
            reassign="Y"
        }
        val progressBar: ProgrssLoader? = if (context != null) ProgrssLoader(context!!) else null
        progressBar?.showLoading()
        val postBody = LoanPostData(
            leadId = mLeadId,
            loanAmount = loan_amount_input?.text?.toString() ?: "",
            tenure = (et_years?.tag as? Int)?.toString() ?: "",
            loanType = loan_type_spinner?.selectedItem as? String ?: "",
            purposeofLoan = spnr_loan_purpose?.selectedItem as? String ?: "",
            securityJurisdiction = security_jurisdiction_spinner?.selectedItem as? String ?: "",
            propertyValue = property_value_input?.text?.toString() ?: "",
            turnover = business_turnover_input?.text?.toString() ?: "",
            netprofitMargin = net_profit_margin_input?.text?.toString() ?: "",
            existingLoan = existing_loan_input?.text?.toString() ?: "",
            existingLoanObligationPm = existing_loan_obligation_input?.text?.toString() ?: "",
            additionalIncomePm = additional_income_input?.text?.toString() ?: "",
            householdExpensesPm = household_expenses_input?.text?.toString() ?: "",
            createdBy = ArthanApp.getAppInstance().loginUser,
            reassign = reassign
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitFactory.getApiService().saveLoanDetail(postBody)
                if (response?.isSuccessful == true) {
                    val result = response.body()
                    if (result?.apiCode == "200") {
                        withContext(Dispatchers.Main) {
                            progressBar?.dismmissLoading()
                            if (result.eligibility?.toLowerCase() == "y") {
                                AppPreferences.getInstance()
                                    .addString(AppPreferences.Key.LoanId, result.loanId)
                                navController?.navigate(R.id.action_addLoanDetailsFragment_to_loanEligibilityFragment)
                            } else {

                                startActivity(Intent(activity,RMDashboardActivity::class.java))
                                activity?.finish()

                            }

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
                        e.printStackTrace()
                        Crashlytics.log(e.message)

                        stopLoading(progressBar, "Something went wrong. Please try later!")
                    }
                }
            } catch (e: Exception) {
                stopLoading(progressBar, "Something went wrong. Please try later!")
                e.printStackTrace()
                Crashlytics.log(e.message)

            }
        }
    }

    private fun loadInitialData() {
        val progressLoader: ProgrssLoader? = if (context != null) ProgrssLoader(context!!) else null
        progressLoader?.showLoading()
        launch(ioContext) {
            try {
                val purposeOfLoan = fetchAndUpdatePurposeOfLoanAsync().await()
                val collateralNature = fetchAndUpdateCollateralNatureAsync().await()

                if (purposeOfLoan && collateralNature) {
                    withContext(coroutineContext) {
                        try {
                            progressLoader?.dismmissLoading()
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
                        context?.let { context ->
                            spnr_loan_purpose?.adapter = DataSpinnerAdapter(
                                context,
                                response.body()?.data?.toMutableList() ?: mutableListOf()
                            ).also {
                                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            }
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
                            context?.let { context ->
                                security_offered_spinner?.adapter = DataSpinnerAdapter(
                                    context,
                                    response.body()?.data?.toMutableList() ?: mutableListOf()
                                ).also {
                                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                }
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
}
