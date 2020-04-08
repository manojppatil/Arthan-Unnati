package com.example.arthan.lead

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.size
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.arthan.R
import com.example.arthan.global.AppPreferences
import com.example.arthan.global.INCOME
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.lead.model.postdata.*
import com.example.arthan.lead.model.responsedata.BaseResponseData
import com.example.arthan.model.BankindDocUploadRequest
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.*
import com.example.arthan.views.fragments.BaseFragment
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_income_information.*
import kotlinx.android.synthetic.main.layout_income_source.*
import kotlinx.android.synthetic.main.layout_loan_details.*
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import kotlin.coroutines.CoroutineContext

class IncomeInformationFragment : BaseFragment(), CompoundButton.OnCheckedChangeListener,
    CoroutineScope {

    private val mOnLoanTypeItemSelectedListener: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                parent?.getItemAtPosition(position)?.let {
                    if (it is String) {
//                        collateral_container?.visibility =
//                            if ("Secure".equals(it, ignoreCase = true)) {
//                                View.VISIBLE
//                            } else {
//                                View.GONE
//                            }
                    }
                }
            }
        }
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    private var navController: NavController? = null
    private var docUri: Uri? = null
    override fun contentView() = R.layout.fragment_income_information
    private var mDocId: String = "DOC00053300"
    private var mLoanId: String? = null
    private var mCustomerId: String? = null

    override fun init() {

        navController =
            if (activity is LeadInfoCaptureActivity) Navigation.findNavController(
                activity!!,
                R.id.frag_container
            ) else null
        loadInitialData()
        itr_container?.visibility = View.GONE
        bill_container?.visibility = View.GONE

        if (navController != null) {
            banking_text_view?.visibility = View.GONE
            gst_text_view_button?.visibility = View.GONE
            bills_text_view_button?.visibility = View.GONE
            obligations_text_view?.visibility = View.GONE
        } else {
            if(arguments?.getString("from").equals("rmIncome")){
                banking_text_view?.visibility = View.GONE
                gst_text_view_button?.visibility = View.GONE
                bills_text_view_button?.visibility = View.GONE
                obligations_text_view?.visibility = View.GONE
            }else {
                cl_banking?.visibility = View.GONE
                itr_container?.visibility = View.GONE
                gst_container?.visibility = View.GONE
                bill_container?.visibility = View.GONE
            }
        }

        et_from?.setOnClickListener { dateSelection(context!!, et_from) }
        et_to?.setOnClickListener { dateSelection(context!!, et_to) }
        getRupeeSymbol(
            context,
            grocery_expenditure_input?.textSize ?: 14f,
            grocery_expenditure_input?.currentTextColor ?: 0
        ).let {
            type1_expenditure_input?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            type2_expenditure_input?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            type3_expenditure_input?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            type4_expenditure_input?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            type5_expenditure_input?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            emi_input?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            loan_amount_input?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            outstanding_amount_input?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            total_amount_input?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            income_per_month_input?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            business_expenditure_amount_input?.setCompoundDrawablesWithIntrinsicBounds(
                it,
                null,
                null,
                null
            )
//            collateral_property_value_input?.setCompoundDrawablesWithIntrinsicBounds(
//                it,
//                null,
//                null,
//                null
//            )
//            sb_no_of_members?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//                override fun onProgressChanged(
//                    seekBar: SeekBar?,
//                    progress: Int,
//                    fromUser: Boolean
//                ) {
//                    seek_bar_current_value?.text = if (progress > 0) {
//                        "$progress"
//                    } else {
//                        ""
//                    }
//                }
//
//                override fun onStartTrackingTouch(seekBar: SeekBar?) {
//                }
//
//                override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                }
//            })
//            business_turnover_input?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            monthly_income_input?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            grocery_expenditure_input?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
            transport_expenditure_input?.setCompoundDrawablesWithIntrinsicBounds(
                it,
                null,
                null,
                null
            )
            education_expenditure_input?.setCompoundDrawablesWithIntrinsicBounds(
                it,
                null,
                null,
                null
            )
            medicine_expenditure_input?.setCompoundDrawablesWithIntrinsicBounds(
                it,
                null,
                null,
                null
            )
            other_expenditure_input?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
        }

//        collateral_container?.visibility = View.VISIBLE
        spnr_loan_type?.onItemSelectedListener = mOnLoanTypeItemSelectedListener
        btn_save_continue?.setOnClickListener {
            saveBusinessData()
        }

        banking_text_view?.setOnClickListener {
            BankingInputDetailsActivity.startMeForResult(
                this, null, RequestCode.BankingDetailsActivity,
                mLoanId, mCustomerId
            )
        }
        gst_text_view_button?.setOnClickListener {
            GSTInputDetailsActivity.startMeForResult(
                this, null, RequestCode.GSTDetailsActivity,
                mLoanId, mCustomerId
            )
        }
        bills_text_view_button?.setOnClickListener {
            BillsInputDetailsActivity.startMeForResult(
                this, activity, RequestCode.BillsDetailsActivity,
                mLoanId, mCustomerId
            )
        }
        obligations_text_view?.setOnClickListener {
            ObligationsInputDetailsActivity.startMeForResult(
                this,
                null,
                RequestCode.ObligationsDetailsActivity,
                mLoanId,
                mCustomerId
            )
        }

        ll_income_source?.findViewById<View?>(R.id.remove_button)?.visibility = View.GONE
        ll_loan_details?.findViewById<View?>(R.id.remove_button)?.visibility = View.GONE

        switch_other_sources?.setOnCheckedChangeListener { buttonView, isChecked ->
            source_of_income_layout?.visibility = if (isChecked) {
                buttonView?.text = "Yes"
                View.VISIBLE
            } else {
                buttonView?.text = "No"
                View.GONE
            }
        }

        switch_other_liability?.setOnCheckedChangeListener { buttonView, isChecked ->
            liabilities_container?.visibility = if (isChecked) {
                buttonView?.text = "Yes"
                View.VISIBLE
            } else {
                buttonView?.text = "No"
                View.GONE
            }
        }

//        btn_net_banking_report.setOnClickListener {
//            generateReport("9876543210")
//        }

        btn_statement_upload.setOnClickListener {
            val request = permissionsBuilder(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).build()
            request.listeners {
                onAccepted {
                    val pdfPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
                    pdfPickerIntent.type = "application/pdf"
                    startActivityForResult(
                        Intent.createChooser(pdfPickerIntent, "Choose File"),
                        101
                    )
                }
                onDenied {
                }
                onPermanentlyDenied {
                }
            }
            request.send()
        }
//
//        btn_statement_report?.setOnClickListener {
//            getStatementReport()
//        }

        /*et_from.setOnClickListener {
            dateSelection(et_from)
        }

        et_to.setOnClickListener {
            dateSelection(et_to)
        }*/

        btn_add_another.setOnClickListener {
            val partnerView =
                LayoutInflater.from(activity!!).inflate(R.layout.layout_income_source, null, false)
            partnerView?.findViewById<View?>(R.id.remove_button)?.setOnClickListener {
                ll_income_source?.removeView(partnerView)
            }
            partnerView?.findViewById<TextView?>(R.id.income_per_month_input)?.let { tv ->
                tv.setCompoundDrawablesWithIntrinsicBounds(
                    getRupeeSymbol(
                        context,
                        tv.textSize,
                        tv.currentTextColor
                    ), null, null, null
                )
                ll_income_source.addView(partnerView)
            }
        }

        btn_add_another_loan.setOnClickListener {
            val loanView =
                LayoutInflater.from(activity!!).inflate(R.layout.layout_loan_details, null, false)
            loanView?.findViewById<View?>(R.id.remove_button)?.setOnClickListener {
                ll_loan_details?.removeView(loanView)
            }
            loanView?.findViewById<TextView?>(R.id.loan_amount_input)?.let { tv ->
                getRupeeSymbol(context, tv.textSize, tv.currentTextColor).let { drawable ->
                    tv.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    loanView.findViewById<TextView?>(R.id.emi_input)
                        ?.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    loanView.findViewById<TextView?>(R.id.outstanding_amount_input)
                        ?.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                }
            }
            loanView?.findViewById<View?>(R.id.et_from)?.setOnClickListener {
                dateSelection(
                    context!!,
                    loanView.findViewById<EditText?>(R.id.et_from)
                )
            }
            loanView.findViewById<AppCompatSpinner?>(R.id.spnr_loan_type)?.onItemSelectedListener =
                mOnLoanTypeItemSelectedListener
            loanView?.findViewById<View?>(R.id.et_to)?.setOnClickListener {
                dateSelection(
                    context!!,
                    loanView.findViewById<EditText?>(R.id.et_to)
                )
            }
            ll_loan_details.addView(loanView)
        }

//        chk_generate_report.setOnCheckedChangeListener(this)
//        chk_upload_documents.setOnCheckedChangeListener(this)


        family_members_count?.tag = 0
        family_members_plus_button?.setOnClickListener {
            updateCount(UpdateCountType.Increment, family_members_count)
        }
        family_members_minus_button?.setOnClickListener {
            updateCount(UpdateCountType.Decrement, family_members_count)
        }

        no_of_earning_family_member_count?.tag = 0
        no_of_earning_family_member_count.text = "0"
        no_of_earning_family_member_plus_button?.setOnClickListener {
            updateCount(UpdateCountType.Increment, no_of_earning_family_member_count)
        }
        no_of_earning_family_member_minus_button?.setOnClickListener {
            updateCount(UpdateCountType.Decrement, no_of_earning_family_member_count)
        }

        generate_gst_report_checkbox?.setOnClickListener {
            val dialogView =
                LayoutInflater.from(context).inflate(R.layout.layout_gst_dialog, null, false)
            val userName: TextInputEditText? = dialogView?.findViewById(R.id.username_input)
            val passwordName: TextInputEditText? = dialogView?.findViewById(R.id.password_input)
            val gstNo: TextInputEditText? = dialogView?.findViewById(R.id.gst_input)
            AlertDialog.Builder(context!!)
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton("Submit") { dialog, _ ->
                    if (saveGSTDetail(
                            userName?.text?.toString() ?: "",
                            passwordName?.text?.toString() ?: "",
                            gstNo?.text?.toString() ?: ""
                        )
                    ) {
                        dialog?.dismiss()
                    }
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog?.dismiss()
                }
                .show()
        }
    }

    private fun saveGSTDetail(userName: String, password: String, gstNo: String): Boolean {
        val progressBar = ProgrssLoader(context ?: return false)
        progressBar.showLoading()
//        if (userName.isEmpty()) {
//            Toast.makeText(context, "Please fill user name!", Toast.LENGTH_LONG).show()
//            return false
//        }
//        if (password.isEmpty()) {
//            Toast.makeText(context, "Please fill password!", Toast.LENGTH_LONG).show()
//            return false
//        }
//        if (gstNo.isEmpty()) {
//            Toast.makeText(context, "Please fill GST no!", Toast.LENGTH_LONG).show()
//            return false
//        }
        val postBody = GSTReportPostData(
            username = "testuser1",
            password = "Testpassword1",
            gstin = "27AAHFG0551H1ZL",
            consent = "y",
            loanId = AppPreferences.getInstance().getString(AppPreferences.Key.LoanId),
            customerId = AppPreferences.getInstance().getString(AppPreferences.Key.CustomerId)
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitFactory.getMasterApiService().saveGSTDetail(postBody)
                if (response?.isSuccessful == true) {
                    val result = response.body()
                    if (result?.apiCode == "200") {
                        stopLoading(progressBar, result?.apiDesc)
                        withContext(Dispatchers.Main) {
                            progressBar.dismmissLoading()
                            Toast.makeText(
                                activity, "Report generated successfully...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        stopLoading(progressBar, /*result?.apiDesc*/"Some thing went wrong!!")
                    }
                } else {
                    try {
                        val result: BaseResponseData? = Gson().fromJson(
                            response?.errorBody()?.string(),
                            BaseResponseData::class.java
                        )
                        stopLoading(progressBar, result?.message)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        stopLoading(progressBar, "Something went wrong. Please try later!")
                    }
                }
            } catch (e: Exception) {
                stopLoading(progressBar, "Something went wrong. Please try later!")
                e.printStackTrace()
            }
        }
        return true
    }

    private suspend fun stopLoading(progressBar: ProgrssLoader, message: String?) {
        withContext(Dispatchers.Main) {
            progressBar.dismmissLoading()
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveBusinessData() {
        val progressBar = ProgrssLoader(context ?: return)
        progressBar.showLoading()
        val sourceOfIncomeList: MutableList<Income> = mutableListOf()
        if (income_per_month_input?.text?.isNotEmpty() == true && source_of_income_input?.text?.isNotEmpty() == true) {
            sourceOfIncomeList?.add(
                Income(
                    incomePerMonth = income_per_month_input?.text?.toString(),
                    incomeSource = source_of_income_input?.text?.toString()
                )
            )

            if ((ll_income_source?.childCount ?: 0) > 1) {
                for (childCount in 1 until (ll_income_source?.childCount ?: 0)) {
                    val sourceOfIncome = ll_income_source?.getChildAt(childCount)
                    sourceOfIncomeList.add(
                        Income(
                            incomePerMonth = sourceOfIncome?.findViewById<TextInputEditText?>(R.id.income_per_month_input)?.text?.toString(),
                            incomeSource = sourceOfIncome?.findViewById<TextInputEditText?>(R.id.source_of_income_input)?.text?.toString()
                        )
                    )
                }
            }
        }
        val expenditureList: MutableList<Expenditure> = mutableListOf()
        if (grocery_expenditure_label?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = grocery_expenditure_label?.text?.toString(),
                    amountPerMonth = grocery_expenditure_input?.text?.toString()
                )
            )
        }
        if (transport_expenditure_label?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = transport_expenditure_label?.text?.toString(),
                    amountPerMonth = transport_expenditure_input?.text?.toString()
                )
            )
        }
        if (education_expenditure_label?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = education_expenditure_label?.text?.toString(),
                    amountPerMonth = education_expenditure_input?.text?.toString()
                )
            )
        }
        if (medicine_expenditure_label?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = medicine_expenditure_label?.text?.toString(),
                    amountPerMonth = medicine_expenditure_input?.text?.toString()
                )
            )
        }
        if (other_expenditure_label?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = other_expenditure_label?.text?.toString(),
                    amountPerMonth = other_expenditure_input?.text?.toString()
                )
            )
        }

        val liablitiesList: MutableList<Liability> = mutableListOf()
        if (loan_amount_input?.text?.isNotEmpty() == true && emi_input?.text?.isNotEmpty() == true) {
            liablitiesList?.add(
                Liability(
                    emi = emi_input?.text?.toString(),
                    loanType = spnr_loan_type?.selectedItem as? String,
                    loanSanctionedBy = spnr_loan_sanctioned_by?.selectedItem as? String,
                    loanAmount = loan_amount_input?.text?.toString(),
                    frequencyOfInstallment = if (weekly_radio_button?.isChecked == true) {
                        weekly_radio_button?.text?.toString()
                    } else if (monthly_radio_button?.isChecked == true) {
                        monthly_radio_button?.text?.toString()
                    } else if (yearly_radio_button?.isChecked == true) {
                        yearly_radio_button?.text?.toString()
                    } else {
                        ""
                    },
                    loanTenureFrom = et_from?.text?.toString(),
                    loanTenureTo = et_to?.text?.toString(),
                    outstandingAmount = outstanding_amount_input?.text?.toString()
                )
            )

            if ((ll_loan_details?.childCount ?: 0) > 1) {
                for (childCount in 1 until (ll_loan_details?.childCount ?: 0)) {
                    val loanDetails = ll_loan_details?.getChildAt(childCount)
                    liablitiesList.add(
                        Liability(
                            emi = loanDetails?.findViewById<TextInputEditText?>(R.id.emi_input)?.text?.toString(),
                            loanType = loanDetails?.findViewById<AppCompatSpinner?>(R.id.emi_input)?.selectedItem as? String,
                            loanSanctionedBy = loanDetails?.findViewById<AppCompatSpinner?>(R.id.spnr_loan_sanctioned_by)?.selectedItem as? String,
                            loanAmount = loanDetails?.findViewById<TextInputEditText?>(R.id.loan_amount_input)?.text?.toString(),
                            frequencyOfInstallment = if (loanDetails?.findViewById<RadioButton?>(R.id.weekly_radio_button)?.isChecked == true) {
                                loanDetails?.findViewById<RadioButton?>(R.id.weekly_radio_button)
                                    ?.text?.toString()
                            } else if (loanDetails?.findViewById<RadioButton?>(R.id.monthly_radio_button)?.isChecked == true) {
                                loanDetails?.findViewById<RadioButton?>(R.id.monthly_radio_button)
                                    ?.text?.toString()
                            } else if (loanDetails?.findViewById<RadioButton?>(R.id.yearly_radio_button)?.isChecked == true) {
                                loanDetails?.findViewById<RadioButton?>(R.id.yearly_radio_button)
                                    ?.text?.toString()
                            } else {
                                ""
                            },
                            loanTenureFrom = loanDetails?.findViewById<TextInputEditText?>(R.id.et_from)?.text?.toString(),
                            loanTenureTo = loanDetails?.findViewById<TextInputEditText?>(R.id.et_to)?.text?.toString(),
                            outstandingAmount = loanDetails?.findViewById<TextInputEditText?>(R.id.outstanding_amount_input)?.text?.toString()
                        )
                    )
                }
            }
        }

        val postBody = IncomeDetailsPostData(
            loanId = AppPreferences.getInstance().getString(AppPreferences.Key.LoanId),
            customerId = AppPreferences.getInstance().getString(AppPreferences.Key.CustomerId),
            anyOtherSourceofIncome = if (switch_other_sources?.isChecked == true) "Yes" else "No",
            incomes = sourceOfIncomeList,
            numberofFamilyMembers = family_members_count?.text?.toString(),
            noofEarningFamilyMembers = no_of_earning_family_member_count?.text?.toString(),
            monthlyFamilyIncome = monthly_income_input?.text?.toString(),
            monthlyhouseholdexpenditures = total_amount_input?.text?.toString(),
            monthlybusinessexpenditures = business_expenditure_amount_input?.text?.toString(),
            expenditures = expenditureList,
            liabilities = liablitiesList,
            methodUsed = method_used_spinner?.selectedItem as? String
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitFactory.getApiService().saveIncomeDetail(postBody)
                if (response?.isSuccessful == true) {
                    val result = response.body()
                    if (result?.apiCode == "200") {
                        withContext(Dispatchers.Main) {
                            progressBar.dismmissLoading()

                            if (activity is LeadInfoCaptureActivity) {
                                (activity as LeadInfoCaptureActivity).enableDoc()
                                (activity as LeadInfoCaptureActivity).infoCompleteState(INCOME)
                            }
                            navController?.navigate(R.id.action_income_to_doc)
                        }
                    } else {
                        stopLoading(progressBar, result?.apiDesc)
                    }
                } else {
                    try {
                        val result: BaseResponseData? = Gson().fromJson(
                            response?.errorBody()?.string(),
                            BaseResponseData::class.java
                        )
                        stopLoading(progressBar, result?.message)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        stopLoading(progressBar, "Something went wrong. Please try later!")
                    }
                }
            } catch (e: Exception) {
                stopLoading(progressBar, "Something went wrong. Please try later!")
                e.printStackTrace()
            }
        }
    }

    private fun updateCount(
        updateType: UpdateCountType,
        countText: TextView?
    ) = when (updateType) {
        is UpdateCountType.Increment -> performIncrement(countText?.tag as? Int ?: 0)
        is UpdateCountType.Decrement -> performDecrement(countText?.tag as? Int ?: 0)
    }.also {
        countText?.text = "$it"
        countText?.tag = it
    }

    private fun performIncrement(initialCount: Int) = initialCount + 1

    sealed class UpdateCountType {
        object Increment : UpdateCountType()
        object Decrement : UpdateCountType()
    }

    private fun performDecrement(initialCount: Int): Int =
        if (initialCount - 1 < 0) 0 else initialCount - 1

    private fun loadInitialData() {
        val progressLoader = ProgrssLoader(context!!)
        progressLoader.showLoading()
        launch(ioContext) {
            val collateral = fetchAndUpdateCollateralAsync().await()
            if (collateral) {
                withContext(uiContext) {
                    progressLoader.dismmissLoading()
                }
            }
        }
    }

    private fun getAdapter(list: List<Data>?): DataSpinnerAdapter =
        DataSpinnerAdapter(context!!, list?.toMutableList() ?: mutableListOf()).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

    private fun fetchAndUpdateCollateralAsync(): Deferred<Boolean> =
        async(context = ioContext) {
            try {
                val response = RetrofitFactory.getMasterApiService().getCollateral()
                if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                    withContext(uiContext) {
                        //                        spnr_nature_of_collateral?.adapter = getAdapter(response.body()?.data)
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return@async true
        }

    private fun uploadStatement(fileLocation: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val file = File(fileLocation)
                val requestBody: RequestBody =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file)
                val multiPartBody =
                    MultipartBody.Part.createFormData("file", file.name, requestBody)
                val response =
                    RetrofitFactory.getMasterApiService()
                        .uploadStatement(
                            multiPartBody,
                            AppPreferences.getInstance().getString(AppPreferences.Key.LoanId),
                            AppPreferences.getInstance().getString(AppPreferences.Key.CustomerId)
                        )
                withContext(Dispatchers.Main) {
                    try {
                        Toast.makeText(
                            activity, "Report generated successfully...",
                            Toast.LENGTH_SHORT
                        ).show()
                        mDocId = response?.docId ?: "DOC00053300"

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getStatementReport() {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitFactory.getMasterApiService()
                        .getReport(hashMapOf<String, String>().apply {
                            put("docId", "DOC00066918")
                        })
                    withContext(Dispatchers.Main) {
                        try {
                            if (response?.isSuccessful == true) {
                                val result = response.body()
                                Log.e("####", result?.toString() ?: "Error is docId")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*fun dateSelection(editText: EditText) {
        val c = Calendar.getInstance()
        DatePickerDialog(
            activity!!,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val date = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                editText.setText(date)
            },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        ).show()
    }*/

    override fun onCheckedChanged(componendButton: CompoundButton?, p1: Boolean) {
        when (componendButton?.id) {
//            R.id.chk_generate_report -> {
//                chk_generate_report.isChecked= true
//                chk_upload_documents.isChecked = false
//                rgrp_report_option.visibility = View.VISIBLE
//            }
//            R.id.chk_upload_documents -> {
            //chk_upload_documents.isChecked= true
//                chk_generate_report.isChecked = false
//                rgrp_report_option.visibility = View.GONE

//                val request = permissionsBuilder(
//                    Manifest.permission.CAMERA,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                ).build()
//                request.listeners {
//                    onAccepted {
//                        navigateToCamera()
//                    }
//                    onDenied {
//                    }
//                    onPermanentlyDenied {
//                    }
//                }
//                request.send()

//            }
        }

    }

    private fun getOutputMediaFile(): File {
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Arthan"
        )
        if (!dir.exists())
            dir.mkdirs()
        return File(
            dir.absolutePath + "/IMG_shop.jpg"
        )
    }

    private fun navigateToCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        docUri = FileProvider.getUriForFile(
            activity!!, activity!!.applicationContext.packageName + ".provider",
            getOutputMediaFile()
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, docUri)
        startActivityForResult(intent, 100)
    }

    private fun enableCustomDrawable(textView: TextView?) {
        textView?.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_document_attached,
            0,
            0,
            0
        )
        context?.let { textView?.setTextColor(ContextCompat.getColor(it, R.color.black)) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                100 -> {
                    uploadDocument()
                }
                101 -> {
                    data?.apply {
                        if (this.data == null) return
                        val file = copyFile(context!!, this.data!!)
                        if (file != null && file?.absolutePath.isNotEmpty()) {
                            uploadStatement(file?.absolutePath)
                        }
                    }
                }
                RequestCode.BankingDetailsActivity -> {
                    enableCustomDrawable(banking_text_view)
                }
                RequestCode.GSTDetailsActivity -> {
                    enableCustomDrawable(gst_text_view_button)
                }
                RequestCode.BillsDetailsActivity -> {
                    enableCustomDrawable(bills_text_view_button)
                }
                RequestCode.ObligationsDetailsActivity -> {
                    enableCustomDrawable(obligations_text_view)
                }
                else -> super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun uploadDocument() {
        if (docUri != null) {
            val bankingService = RetrofitFactory.getNetBankingService()
            val loader = ProgrssLoader(activity!!)
            loader.showLoading()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val stream = activity?.contentResolver?.openInputStream(docUri!!)
                    val bm = BitmapFactory.decodeStream(stream)
                    val base64 = BitmapUtils.getBase64(bm)

                    val response = bankingService.uploadReport(
                        BankindDocUploadRequest(
                            "HDFC", "VIR",
                            "V123", base64
                        )
                    )
                    withContext(Dispatchers.Main) {
                        loader.dismmissLoading()
                        if (response.isSuccessful) {
                            val uploadResponse = response.body()
                            if (uploadResponse != null) {
                                Toast.makeText(
                                    activity,
                                    "Document Uploaded Successfully... DocID: ${uploadResponse.docId}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else
                                Toast.makeText(
                                    activity, "Please Try again...",
                                    Toast.LENGTH_SHORT
                                ).show()
                        } else
                            Toast.makeText(
                                activity,
                                "Please Try again...",
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                } catch (e: HttpException) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        loader.dismmissLoading()
                        Toast.makeText(activity, "Please Try again...", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        loader.dismmissLoading()
                        Toast.makeText(activity, "Please Try again...", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun generateReport(mobileNo: String) {
        val loader = ProgrssLoader(activity!!)
        loader.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitFactory.getMasterApiService().getNetBankingReport(mobileNo)
                withContext(Dispatchers.Main) {
                    loader.dismmissLoading()
                    try {
                        if (response?.isSuccessful == true) {
                            Toast.makeText(
                                activity, "Report generated successfully...",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else
                            Toast.makeText(
                                activity,
                                "Please Try again...",
                                Toast.LENGTH_SHORT
                            ).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            activity,
                            "Please Try again...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: HttpException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    loader.dismmissLoading()
                    Toast.makeText(activity, "Please Try again...", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    loader.dismmissLoading()
                    Toast.makeText(activity, "Please Try again...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun updateData(
        incomeDetails: IncomeDetails?,
        customerId: String?,
        loanId: String?
    ) {
        switch_other_sources?.isChecked = false
        if (incomeDetails?.incomes?.size != 0) {
            switch_other_sources?.isChecked = true

        }
        family_members_count?.text = incomeDetails?.numberofFamilyMembers
        no_of_earning_family_member_count?.text = incomeDetails?.noofEarningFamilyMembers
        monthly_income_input?.setText(incomeDetails?.monthlyFamilyIncome)
        total_amount_input?.setText(incomeDetails?.monthlyhouseholdexpenditures)
        txt_income_list_msg?.setText(activity?.resources?.getString(R.string.list_all_sources_of_income_1) + " (" + incomeDetails?.incomes?.size + ")")
        mLoanId = loanId
//        mCustomerId = incomeDetails?.customerId
        mCustomerId = customerId
        for (item in incomeDetails?.expenditures ?: listOf()) {
            when {
                item.expenditureName?.equals(
                    grocery_expenditure_label?.text?.toString(),
                    ignoreCase = true
                ) ?: false -> {
                    grocery_expenditure_input?.setText(item.amountPerMonth)
                }
                item.expenditureName?.equals(
                    transport_expenditure_label?.text?.toString(),
                    ignoreCase = true
                ) ?: false -> {
                    transport_expenditure_input?.setText(item.amountPerMonth)
                }
                item.expenditureName?.equals(
                    education_expenditure_label?.text?.toString(),
                    ignoreCase = true
                ) ?: false -> {
                    education_expenditure_input?.setText(item.amountPerMonth)
                }
                item.expenditureName?.equals(
                    medicine_expenditure_label?.text?.toString(),
                    ignoreCase = true
                ) ?: false -> {
                    medicine_expenditure_input?.setText(item.amountPerMonth)
                }
                item.expenditureName?.equals(
                    other_expenditure_label?.text?.toString(),
                    ignoreCase = true
                ) ?: false -> {
                    other_expenditure_input?.setText(item.amountPerMonth)
                }
            }
        }

        ll_income_source.removeAllViews()
        for (item in incomeDetails?.incomes ?: listOf()) {
            val partnerView =
                LayoutInflater.from(activity!!).inflate(R.layout.layout_income_source, null, false)
            if ((ll_income_source?.size ?: 0) > 0) {
                partnerView?.findViewById<View?>(R.id.remove_button)?.setOnClickListener {
                    ll_income_source?.removeView(partnerView)
                }
            } else {
                partnerView?.findViewById<View?>(R.id.remove_button)?.visibility = View.GONE
            }
            partnerView?.findViewById<TextView?>(R.id.source_of_income_input)?.text =
                item.incomeSource
            partnerView?.findViewById<TextView?>(R.id.income_per_month_input)?.let { tv ->
                tv.setCompoundDrawablesWithIntrinsicBounds(
                    getRupeeSymbol(
                        context,
                        tv.textSize,
                        tv.currentTextColor
                    ), null, null, null
                )
                tv.text = item.incomeSource
                ll_income_source.addView(partnerView)
            }
        }
        ll_loan_details.removeAllViews()
        for (item in incomeDetails?.liabilities ?: listOf()) {

            val loanView =
                LayoutInflater.from(activity!!).inflate(R.layout.layout_loan_details, null, false)
            if ((ll_loan_details?.size ?: 0) > 0) {
                loanView?.findViewById<View?>(R.id.remove_button)?.setOnClickListener {
                    ll_loan_details?.removeView(loanView)
                }
            } else {
                loanView?.findViewById<View?>(R.id.remove_button)?.visibility = View.GONE
            }

            loanView.findViewById<EditText>(R.id.loan_amount_input)?.setText(item.loanAmount)
            loanView.findViewById<EditText?>(R.id.emi_input)?.setText(item.emi)
            loanView.findViewById<EditText?>(R.id.outstanding_amount_input)
                ?.setText(item.outstandingAmount)
            loanView?.findViewById<EditText?>(R.id.et_from)?.setText(item.loanTenureFrom)
            loanView?.findViewById<EditText?>(R.id.et_to)?.setText(item.loanTenureTo)
            /*loanView?.findViewById<EditText?>(R.id.loan_amount_input)?.let { tv ->
                tv.setText(item.loanAmount)
                getRupeeSymbol(context, tv.textSize, tv.currentTextColor).let { drawable ->
                    tv.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    loanView.findViewById<EditText?>(R.id.emi_input)?.let {
                        it.setText(item.emi)
                        it.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    }
                    loanView.findViewById<EditText?>(R.id.outstanding_amount_input)?.let {
                        it.setText(item.outstandingAmount)
                        it.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    }
                }
            }
*/
            when {
                item.frequencyOfInstallment?.equals("weekly", ignoreCase = true) -> {
                    loanView?.findViewById<RadioButton>(R.id.weekly_radio_button)?.isChecked = true
                }
                item.frequencyOfInstallment?.equals("monthly", ignoreCase = true) -> {
                    loanView?.findViewById<RadioButton>(R.id.monthly_radio_button)?.isChecked = true
                }
                item.frequencyOfInstallment?.equals("yearly", ignoreCase = true) -> {
                    loanView?.findViewById<RadioButton>(R.id.yearly_radio_button)?.isChecked = true
                }
            }

            loanView?.findViewById<EditText?>(R.id.et_from)?.setOnClickListener {
                dateSelection(
                    context!!,
                    loanView.findViewById(R.id.et_from)
                )
            }
              /*loanView?.findViewById<EditText?>(R.id.et_from)?.let {
                it.setText(item.loanTenureFrom)
                it.setOnClickListener {
                    dateSelection(
                        context!!,
                        loanView.findViewById(R.id.et_from)
                    )
                }*/
            loanView?.findViewById<EditText?>(R.id.et_to)?.setOnClickListener {
                dateSelection(
                    context!!,
                    loanView.findViewById(R.id.et_to)
                )
            }
            loanView.findViewById<AppCompatSpinner?>(R.id.spnr_loan_type)?.onItemSelectedListener =
                mOnLoanTypeItemSelectedListener
            loanView?.findViewById<EditText?>(R.id.et_to)?.let {
                it.setOnClickListener {
                    dateSelection(
                        context!!,
                        loanView.findViewById(R.id.et_to)
                    )
                }
            }
            ll_loan_details.addView(loanView)
        }

    }
}

