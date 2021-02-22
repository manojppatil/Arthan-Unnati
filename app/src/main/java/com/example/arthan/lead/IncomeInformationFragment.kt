package com.example.arthan.lead

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.size
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.dashboard.bcm.UploadStatmentsDialog
import com.example.arthan.dashboard.bm.BMDocumentVerificationActivity
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.dashboard.rm.RMReAssignListingActivity
import com.example.arthan.dashboard.rm.RMScreeningNavigationActivity
import com.example.arthan.dashboard.rm.ReUsableFragmentSpace
import com.example.arthan.global.AppPreferences
import com.example.arthan.global.ArthanApp
import com.example.arthan.global.DOC_TYPE
import com.example.arthan.global.INCOME
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.lead.model.postdata.*
import com.example.arthan.lead.model.responsedata.BaseResponseData
import com.example.arthan.model.BankindDocUploadRequest
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.ocr.CardResponse
import com.example.arthan.utils.*
import com.example.arthan.views.activities.PendingCustomersActivity
import com.example.arthan.views.fragments.BaseFragment
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_income_information.*
import kotlinx.android.synthetic.main.layout_income_source.*
import kotlinx.android.synthetic.main.layout_loan_details.*
import kotlinx.android.synthetic.main.list_of_statements.*
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class IncomeInformationFragment : BaseFragment(),TextWatcher, CompoundButton.OnCheckedChangeListener,
    CoroutineScope {

    private  var alert: Dialog?=null
    private var rv:RecyclerView?=null

    private var loanDocUrl: String = ""
    private  var incomeDetails:IncomeDetails?=null
    private var listOfStatements:ArrayList<String> = ArrayList()
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

                        if (view?.id == R.id.spnr_loan_type) {
                            if (it.equals("immovable", ignoreCase = true)) {
                                parent.findViewById<TextInputLayout>(R.id.tl_ownerName).visibility =
                                    View.GONE
                                parent.findViewById<TextInputLayout>(R.id.tl_Address).visibility =
                                    View.GONE
                            } else {
                                parent.findViewById<TextInputLayout>(R.id.tl_ownerName).visibility =
                                    View.GONE
                                parent.findViewById<TextInputLayout>(R.id.tl_Address).visibility =
                                    View.GONE
                            }
                        }
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

    private var sourceInceomeAdapter: DataSpinnerAdapter? = null
    override fun init() {

        navController =
            if (activity is LeadInfoCaptureActivity) Navigation.findNavController(
                activity!!,
                R.id.frag_container
            ) else null
        loadInitialData()
        if (arguments?.getString("task").equals("RM_AssignList") || arguments?.getString("task").equals(
                "RMreJourney"
            )
        ) {
            getIncomeDetailsFromRm()
        }
        itr_container?.visibility = View.GONE
        bill_container?.visibility = View.GONE

        if(ArthanApp.getAppInstance().loginRole=="BM")
        {
            stmt_text_view.visibility=View.VISIBLE
            stmt_text_view.setOnClickListener {
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
        }
        if (navController != null) {
            banking_text_view?.visibility = View.GONE
            gst_text_view_button?.visibility = View.GONE
            bills_text_view_button?.visibility = View.GONE
            obligations_text_view?.visibility = View.GONE
        } else {
            if (arguments?.getString("from").equals("rmIncome")) {
                banking_text_view?.visibility = View.GONE
                gst_text_view_button?.visibility = View.GONE
                bills_text_view_button?.visibility = View.GONE
                obligations_text_view?.visibility = View.GONE

            } else {
                cl_banking?.visibility = View.GONE
                itr_container?.visibility = View.GONE
                gst_container?.visibility = View.GONE
                bill_container?.visibility = View.GONE
            }
        }

        et_from?.setOnClickListener { dateSelection(context!!, et_from) }
        et_to?.setOnClickListener {

            val c = Calendar.getInstance()
            DatePickerDialog(
                context!!,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val date = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                    if (SimpleDateFormat("dd-MM-yyyy").parse(date)
                            .after(SimpleDateFormat("dd-MM-yyyy").parse(et_from.text.toString()))
                    )
                        et_to.setText(date)
                    else
                        Toast.makeText(
                            activity,
                            "Please select date greater than from date",
                            Toast.LENGTH_LONG
                        ).show()
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
            ).show()

        }
        grocery_expenditure_input.addTextChangedListener(this)
        transport_expenditure_input.addTextChangedListener(this)
        education_expenditure_input.addTextChangedListener(this)
        medicine_expenditure_input.addTextChangedListener(this)
        other_expenditure_input.addTextChangedListener(this)
        type1_expenditure_input.addTextChangedListener(this)
        type2_expenditure_input.addTextChangedListener(this)
        type3_expenditure_input.addTextChangedListener(this)
        type4_expenditure_input.addTextChangedListener(this)
        type5_expenditure_input.addTextChangedListener(this)
        type6_expenditure_input.addTextChangedListener(this)
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
            type6_expenditure_input?.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
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
            //
            if (ArthanApp.getAppInstance().loginRole == "BM") {
                updateIncomeDetails()
            } else {
                saveBusinessData()
            }
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

        uploadLoanDoc.setOnClickListener {
            val request = permissionsBuilder(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).build()
            request.listeners {
                onAccepted {
                    val pdfPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
                    pdfPickerIntent.type = "application/pdf"
                    /* startActivityForResult(
                         Intent.createChooser(pdfPickerIntent, "Choose File"),
                         102
                     )*/
                    uploadLoanDoc()

                }
                onDenied {
                }
                onPermanentlyDenied {
                }
            }
            request.send()
        }
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

        income_per_month_input.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                getTotalIncome()

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        btn_add_another.setOnClickListener {
            val partnerView =
                LayoutInflater.from(activity!!).inflate(R.layout.layout_income_source, null, false)
            partnerView?.findViewById<View?>(R.id.remove_button)?.setOnClickListener {
                ll_income_source?.removeView(partnerView)

            }
            partnerView?.findViewById<Spinner>(R.id.source_of_income_input)?.adapter =
                sourceInceomeAdapter

            partnerView?.findViewById<TextInputEditText?>(R.id.income_per_month_input)?.addTextChangedListener(object :TextWatcher{
                override fun afterTextChanged(s: Editable?) {
                    getTotalIncome()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

            })

            partnerView?.findViewById<TextInputEditText?>(R.id.income_per_month_input)?.let { tv ->
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

                val c = Calendar.getInstance()
                DatePickerDialog(
                    context!!,
                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        val date = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                        if (SimpleDateFormat("dd-MM-yyyy").parse(date)
                                .after(SimpleDateFormat("dd-MM-yyyy").parse(et_from.text.toString()))
                        )
                            loanView.findViewById<EditText>(R.id.et_to)?.setText(date)
                        else
                            Toast.makeText(
                                activity,
                                "Please select date greater than from date",
                                Toast.LENGTH_LONG
                            ).show()
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
                ).show()

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
            if ((family_members_count.tag as Int) <= (no_of_earning_family_member_count.tag as Int)) {
                Toast.makeText(
                    activity,
                    "Earning count should be less than Family count",
                    Toast.LENGTH_LONG
                ).show()

            } else
                updateCount(UpdateCountType.Decrement, family_members_count)
        }

        no_of_earning_family_member_count?.tag = 0
        no_of_earning_family_member_count.text = "0"
        no_of_earning_family_member_plus_button?.setOnClickListener {
            if ((family_members_count.tag as Int) > (no_of_earning_family_member_count.tag as Int))
                updateCount(UpdateCountType.Increment, no_of_earning_family_member_count)
            else
                Toast.makeText(
                    activity,
                    "Earning count should be less than Family count",
                    Toast.LENGTH_LONG
                ).show()
        }
        no_of_earning_family_member_minus_button?.setOnClickListener {
            updateCount(UpdateCountType.Decrement, no_of_earning_family_member_count)
        }

        getTotalIncome()
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

    /* private fun updateIncomeDetails() {

         if (ArthanApp.getAppInstance().loginRole == "BM") {
             var dialog = AlertDialog.Builder(activity)
             var view: View? = activity?.layoutInflater?.inflate(R.layout.remarks_popup, null)
             dialog.setView(view)
             var et_remarks = view?.findViewById<EditText>(R.id.et_remarks)
             var btn_submit_remark = view?.findViewById<Button>(R.id.btn_submit)
             var btn_cancel = view?.findViewById<Button>(R.id.btn_cancel)
             var alert = dialog.create() as AlertDialog
             btn_cancel?.setOnClickListener {
                 alert.dismiss()
             }

             btn_submit_remark?.setOnClickListener {
                 val progressBar = ProgrssLoader(this.context!!)
                 progressBar.showLoading()
                 alert.dismiss()
                 var map = HashMap<String, String>()
                 map["loanId"] = mLoanId!!
                 map["remarks"] = et_remarks?.text.toString()
                 map["userId"] = ArthanApp.getAppInstance().loginUser + ""

                 CoroutineScope(Dispatchers.IO).launch {
                     val respo = RetrofitFactory.getApiService().updateIncomeDetails(
                         map
                     )

                     val result = respo?.body()
                     if (respo?.body() != null && result?.apiCode == "200") {

                         withContext(Dispatchers.Main) {
                             if(ArthanApp.getAppInstance().loginRole == "BM" && et_remarks?.text.toString().isNotEmpty())
                             {
                               //  Toast.makeText(activity,"Case ReAssigned to RM",Toast.LENGTH_LONG).show()
                             }
                             *//*  AppPreferences.getInstance()
                                  .addString(AppPreferences.Key.BusinessId, result.businessId)*//*
                            progressBar.dismmissLoading()
//                            if (respo.body()!!.discrepancy?.toLowerCase() == "y") {
                                if (ArthanApp.getAppInstance().loginRole == "BM" || ArthanApp.getAppInstance().loginRole == "BCM") {

                                    if (activity is LeadInfoCaptureActivity) {
                                        (activity as LeadInfoCaptureActivity).enableDoc()
                                        (activity as LeadInfoCaptureActivity).infoCompleteState(INCOME)
                                    }
                                    var b = Bundle()
                                    b.putString("loanType", result.loanType)
                                    activity?.intent?.putExtra("loanType", result.loanType)
                                    navController?.navigate(R.id.action_income_to_doc, b)

                                    if(activity is BMDocumentVerificationActivity) {

                                        (activity as BMDocumentVerificationActivity).moveVPinDataFragment(3)
                                    }else
                                    {

                                    }
                                    *//* startActivity(
                                         Intent(
                                             activity,
                                             PendingCustomersActivity::class.java
                                         )
                                     )

                                     activity?.finish()
 *//*
                                } else {
                                    startActivity(Intent(activity, RMDashboardActivity::class.java))
                                    activity?.finish()
                                }
                          *//*  } else {
                                if (activity is LeadInfoCaptureActivity) {
                                    (activity as LeadInfoCaptureActivity).enableDoc()
                                    (activity as LeadInfoCaptureActivity).infoCompleteState(INCOME)
                                }
                                var b = Bundle()
                                b.putString("loanType", result.loanType)
                                activity?.intent?.putExtra("loanType", result.loanType)
                                navController?.navigate(R.id.action_income_to_doc, b)
                                if(activity is BMDocumentVerificationActivity) {

                                    (activity as BMDocumentVerificationActivity).moveVPinDataFragment(3)
                                }else
                                {

                                }

                            }*//*

                        }
                    } else {
                        withContext(Dispatchers.Main) {

                            progressBar.dismmissLoading()
                            Toast.makeText(activity, "something went wrong", Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                }
            }


            alert.show()
        }
    }*/
    private fun updateIncomeDetails() {

        if (ArthanApp.getAppInstance().loginRole == "BM") {
            var dialog = AlertDialog.Builder(activity)
            var view: View? = activity?.layoutInflater?.inflate(R.layout.remarks_popup, null)
            dialog.setView(view)
            var et_remarks = view?.findViewById<EditText>(R.id.et_remarks)
            var btn_submit_remark = view?.findViewById<Button>(R.id.btn_submit)
            var btn_cancel = view?.findViewById<Button>(R.id.btn_cancel)
            var alert = dialog.create() as AlertDialog
            btn_cancel?.setOnClickListener {
                alert.dismiss()
            }
            alert.show()

            btn_submit_remark?.setOnClickListener {
                val progressBar = ProgrssLoader(this.context!!)
                progressBar.showLoading()
                alert.dismiss()
                var map = HashMap<String, String>()
               /* map["loanId"] = mLoanId!!
                map["remarks"] = et_remarks?.text.toString()
                map["userId"] = ArthanApp.getAppInstance().loginUser + ""*/

                if (mLoanId == null || mLoanId == "") {
                    mLoanId = activity?.intent?.getStringExtra("loanId")
                    mCustomerId = activity?.intent?.getStringExtra("custId")
                    if (arguments?.getString("task").equals("RM_AssignList", ignoreCase = true) ||
                        arguments?.getString("task").equals("RMreJourney", ignoreCase = true)
                    ) {
                        mLoanId = arguments?.getString("loanId")
                        mCustomerId = arguments?.getString("custId")
                    }
                }

                val postdata: IncomeDetailsPostData=getPostdata()
                postdata.remarks=et_remarks?.text.toString()
                postdata.loanId= mLoanId!!
                postdata.customerId=mCustomerId
                postdata.userId=ArthanApp.getAppInstance().loginUser + ""
                CoroutineScope(Dispatchers.IO).launch {
                    val respo = RetrofitFactory.getApiService().updateIncomeDetails(
                        postdata
                    )

                    val result = respo?.body()
                    if (respo?.body() != null && result?.apiCode == "200") {

                        withContext(Dispatchers.Main) {
                            if (ArthanApp.getAppInstance().loginRole == "BM") {
                                //  Toast.makeText(activity,"Case ReAssigned to RM",Toast.LENGTH_LONG).show()
                            }
                            /*  AppPreferences.getInstance()
                              .addString(AppPreferences.Key.BusinessId, result.businessId)*/
                            progressBar.dismmissLoading()
//                            if (respo.body()!!.discrepancy?.toLowerCase() == "y") {
                            if (ArthanApp.getAppInstance().loginRole == "BM" || ArthanApp.getAppInstance().loginRole == "BCM") {

                                if (activity is LeadInfoCaptureActivity) {
                                    (activity as LeadInfoCaptureActivity).enableDoc()
                                    (activity as LeadInfoCaptureActivity).infoCompleteState(INCOME)
                                }
                                var b = Bundle()
                                b.putString("loanType", result.loanType)
                                activity?.intent?.putExtra("loanType", result.loanType)
                                navController?.navigate(R.id.action_income_to_doc, b)

                                if (activity is BMDocumentVerificationActivity) {

                                    (activity as BMDocumentVerificationActivity).moveVPinDataFragment(
                                        3
                                    )
                                } else {

                                }
                                /* startActivity(
                                 Intent(
                                     activity,
                                     PendingCustomersActivity::class.java
                                 )
                             )

                             activity?.finish()
*/
                            } else {
                                startActivity(Intent(activity, RMDashboardActivity::class.java))
                                activity?.finish()
                            }
                            /*  } else {
                              if (activity is LeadInfoCaptureActivity) {
                                  (activity as LeadInfoCaptureActivity).enableDoc()
                                  (activity as LeadInfoCaptureActivity).infoCompleteState(INCOME)
                              }
                              var b = Bundle()
                              b.putString("loanType", result.loanType)
                              activity?.intent?.putExtra("loanType", result.loanType)
                              navController?.navigate(R.id.action_income_to_doc, b)
                              if(activity is BMDocumentVerificationActivity) {

                                  (activity as BMDocumentVerificationActivity).moveVPinDataFragment(3)
                              }else
                              {

                              }

                          }*/

                        }
                    } else {
                        withContext(Dispatchers.Main) {

                            progressBar.dismmissLoading()
                            Toast.makeText(activity, "something went wrong", Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                }
            }
        }
    }
    private fun getPostdata(): IncomeDetailsPostData {
        if(incomeDetails!=null){


        }
        val sourceOfIncomeList: MutableList<Income> = mutableListOf()
        if (source_of_income_input?.selectedItem.toString()
                .isNotEmpty()
        ) {
            /*sourceOfIncomeList.add(
                Income(
                    incomePerMonth = income_per_month_input?.text?.toString(),
                    incomeSource = (source_of_income_input?.selectedItem as Data).value.toString()
                )
            )*/

            if ((ll_income_source?.childCount ?: 0) > 0) {
                for (childCount in 0 until (ll_income_source?.childCount ?: 0)) {
                    val sourceOfIncome = ll_income_source?.getChildAt(childCount)
                    (((((ll_income_source?.getChildAt(childCount) as ViewGroup).getChildAt(2) as ViewGroup)).getChildAt(0) as ViewGroup).getChildAt(0) as TextInputEditText).addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {
                            getTotalIncome()
                        }

                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        }
                    })
                    sourceOfIncomeList.add(
                        Income(
                            incomeSource = ((((ll_income_source?.getChildAt(childCount) as ViewGroup).getChildAt(1) as ViewGroup).getChildAt(1) as Spinner).selectedItem as Data).value,
                                    incomePerMonth =    (((((ll_income_source?.getChildAt(childCount) as ViewGroup).getChildAt(2) as ViewGroup)).getChildAt(0) as ViewGroup).getChildAt(0) as TextInputEditText).text.toString()
//                            incomePerMonth = sourceOfIncome?.findViewById<TextInputEditText?>(R.id.income_per_month_input)?.text?.toString(),
//                            incomeSource = (source_of_income_input?.selectedItem as Data).value.toString()
//                            incomeSource = (sourceOfIncome?.findViewById<Spinner?>(R.id.source_of_income_input)?.selectedItem as Data).value.toString()
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
                    amountPerMonth = grocery_expenditure_input?.text?.toString(),
                    expenditureType = "household"
                )
            )
        }
        if (transport_expenditure_label?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = transport_expenditure_label?.text?.toString(),
                    amountPerMonth = transport_expenditure_input?.text?.toString(),
                    expenditureType = "household"

                )
            )
        }
        education_expenditure_label?.isChecked = true
        if (education_expenditure_label?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = education_expenditure_label?.text?.toString(),
                    amountPerMonth = education_expenditure_input?.text?.toString(),
                    expenditureType = "household"

                )
            )
        }
        if (medicine_expenditure_label?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = medicine_expenditure_label?.text?.toString(),
                    amountPerMonth = medicine_expenditure_input?.text?.toString(),
                    expenditureType = "household"

                )
            )
        }
        if (other_expenditure_label?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = other_expenditure_label?.text?.toString(),
                    amountPerMonth = other_expenditure_input?.text?.toString(),
                    expenditureType = "household"

                )
            )
        }

        if (chk_type1?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = chk_type1?.text?.toString(),
                    amountPerMonth = type1_expenditure_input?.text?.toString(),
                    expenditureType = "business"

                )
            )
        }
        if (chk_type2?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = chk_type2?.text?.toString(),
                    amountPerMonth = type2_expenditure_input?.text?.toString(),
                    expenditureType = "business"

                )
            )
        }
        if (chk_type3?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = chk_type3?.text?.toString(),
                    amountPerMonth = type3_expenditure_input?.text?.toString(),
                    expenditureType = "business"

                )
            )
        }
        if (chk_type4?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = chk_type4?.text?.toString(),
                    amountPerMonth = type4_expenditure_input?.text?.toString(),
                    expenditureType = "business"

                )
            )
        }
        if (chk_type5?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = chk_type5?.text?.toString(),
                    amountPerMonth = type5_expenditure_input?.text?.toString(),
                    expenditureType = "business"

                )
            )
        }
        if (chk_type6?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = chk_type6?.text?.toString(),
                    amountPerMonth = type6_expenditure_input?.text?.toString(),
                    expenditureType = "business"

                )
            )
        }

        if (mLoanId == null || mLoanId == "") {
            mLoanId = activity?.intent?.getStringExtra("loanId")
            mCustomerId = activity?.intent?.getStringExtra("custId")
            if (arguments?.getString("task").equals("RM_AssignList", ignoreCase = true) ||
                arguments?.getString("task").equals("RMreJourney", ignoreCase = true)
            ) {
                mLoanId = arguments?.getString("loanId")
                mCustomerId = arguments?.getString("custId")
            }
        }

        val liablitiesList: MutableList<Liability> = mutableListOf()
        val loanView =
            LayoutInflater.from(activity!!)
                .inflate(R.layout.layout_loan_details, null, false)
//        if (loan_amount_input?.text?.isNotEmpty() == true && emi_input?.text?.isNotEmpty() == true) {
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
                    } else if (qtrly_radio_button?.isChecked == true) {
                        qtrly_radio_button?.text?.toString()
                    } else if (halfyearly_radio_button?.isChecked == true) {
                        halfyearly_radio_button?.text?.toString()
                    } else {
                        ""
                    },
                    loanTenureFrom = et_from?.text?.toString(),
                    loanTenureTo = et_to?.text?.toString(),
                    outstandingAmount = outstanding_amount_input?.text?.toString(),
                    loanDocumentUrl = loanDocUrl,
                    considerCFA = cfa_cb.isChecked
                )
            )
            if ((ll_loan_details?.childCount ?: 0) > 1) {
                for (childCount in 0 until (ll_loan_details?.childCount ?: 0)) {
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
                            } else if (loanDetails?.findViewById<RadioButton?>(R.id.qtrly_radio_button)?.isChecked == true) {
                                loanDetails?.findViewById<RadioButton?>(R.id.qtrly_radio_button)
                                    ?.text?.toString()
                            } else if (loanDetails?.findViewById<RadioButton?>(R.id.halfyearly_radio_button)?.isChecked == true) {
                                loanDetails?.findViewById<RadioButton?>(R.id.halfyearly_radio_button)
                                    ?.text?.toString()
                            } else {
                                ""
                            },
                            loanTenureFrom = loanDetails?.findViewById<TextInputEditText?>(R.id.et_from)?.text?.toString(),
                            loanTenureTo = loanDetails?.findViewById<TextInputEditText?>(R.id.et_to)?.text?.toString(),
                            outstandingAmount = loanDetails?.findViewById<TextInputEditText?>(R.id.outstanding_amount_input)?.text?.toString(),
                            loanDocumentUrl = loanDocUrl,
                            considerCFA = loanDetails?.findViewById<CheckBox?>(R.id.cfa_cb)?.isChecked

                        )
                    )
//                }
            }
        }

        val postBody = IncomeDetailsPostData(
            resubmit = "",
            loanId = mLoanId,
            customerId = mCustomerId,
            anyOtherSourceofIncome = if (switch_other_sources?.isChecked == true) "Yes" else "No",
            incomes = sourceOfIncomeList,
            numberofFamilyMembers = family_members_count?.text?.toString(),
            noofEarningFamilyMembers = no_of_earning_family_member_count?.text?.toString(),
            monthlyFamilyIncome = monthly_income_input?.text?.toString(),
            monthlyhouseholdexpenditures = total_amount_input?.text?.toString(),
            monthlybusinessexpenditures = business_expenditure_amount_input?.text?.toString(),
            expenditures = expenditureList,
            liabilities = liablitiesList,
            methodUsed = spnr_loan_type?.selectedItem as? String,
            reassign = "N"
        )
        return postBody
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
                        stopLoading(progressBar, /*result?.apiDesc*/"Something went wrong!!")
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
        var reassign = "N"
        if (arguments?.getString("task").equals("RM_AssignList", ignoreCase = true)) {
            reassign = "Y"
        }

        val progressBar = ProgrssLoader(context ?: return)
        progressBar.showLoading()
        val sourceOfIncomeList: MutableList<Income> = mutableListOf()
        /*if (income_per_month_input?.text?.isNotEmpty() == true && source_of_income_input?.selectedItem.toString()
                .isNotEmpty()
        ) {*/
          /*  sourceOfIncomeList.add(
                Income(
                    incomePerMonth = income_per_month_input?.text?.toString(),
                    incomeSource = (source_of_income_input?.selectedItem as Data).value.toString()
                )
            )
*/
        for (childCount in 0 until (ll_income_source?.childCount ?: 0)) {
            sourceOfIncomeList.add(
                Income(
                    incomeSource = ((((ll_income_source?.getChildAt(childCount) as ViewGroup).getChildAt(
                        1
                    ) as ViewGroup).getChildAt(1) as Spinner).selectedItem as Data).value,
                    incomePerMonth = (((((ll_income_source?.getChildAt(childCount) as ViewGroup).getChildAt(
                        2
                    ) as ViewGroup)).getChildAt(0) as ViewGroup).getChildAt(0) as TextInputEditText).text.toString()
//                            incomePerMonth = sourceOfIncome?.findViewById<TextInputEditText?>(R.id.income_per_month_input)?.text?.toString(),
//                            incomeSource = (source_of_income_input?.selectedItem as Data).value.toString()
//                            incomeSource = (sourceOfIncome?.findViewById<Spinner?>(R.id.source_of_income_input)?.selectedItem as Data).value.toString()
                )
            )
        }
            /*if ((ll_income_source?.childCount ?: 0) > 1) {
                for (childCount in 0 until (ll_income_source?.childCount ?: 0)) {
                    val sourceOfIncome = ll_income_source?.getChildAt(childCount)
                    sourceOfIncomeList.add(
                        Income(
                            incomePerMonth = sourceOfIncome?.findViewById<TextInputEditText?>(R.id.income_per_month_input)?.text?.toString(),
//                            incomeSource = (source_of_income_input?.selectedItem as Data).value.toString()
                            incomeSource = (sourceOfIncome?.findViewById<Spinner?>(R.id.source_of_income_input)?.selectedItem as Data).value.toString()
                        )
                    )
                }
            }*/
//        }
        val expenditureList: MutableList<Expenditure> = mutableListOf()
        if (grocery_expenditure_label?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = grocery_expenditure_label?.text?.toString(),
                    amountPerMonth = grocery_expenditure_input?.text?.toString(),
                    expenditureType = "household"
                )
            )
        }
        if (transport_expenditure_label?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = transport_expenditure_label?.text?.toString(),
                    amountPerMonth = transport_expenditure_input?.text?.toString(),
                    expenditureType = "household"

                )
            )
        }
        if (education_expenditure_label?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = education_expenditure_label?.text?.toString(),
                    amountPerMonth = education_expenditure_input?.text?.toString(),
                    expenditureType = "household"

                )
            )
        }
        if (medicine_expenditure_label?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = medicine_expenditure_label?.text?.toString(),
                    amountPerMonth = medicine_expenditure_input?.text?.toString(),
                    expenditureType = "household"

                )
            )
        }
        if (other_expenditure_label?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = other_expenditure_label?.text?.toString(),
                    amountPerMonth = other_expenditure_input?.text?.toString(),
                    expenditureType = "household"

                )
            )
        }

        if (chk_type1?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = chk_type1?.text?.toString(),
                    amountPerMonth = type1_expenditure_input?.text?.toString(),
                    expenditureType = "business"

                )
            )
        }
        if (chk_type2?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = chk_type2?.text?.toString(),
                    amountPerMonth = type2_expenditure_input?.text?.toString(),
                    expenditureType = "business"

                )
            )
        }
        if (chk_type3?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = chk_type3?.text?.toString(),
                    amountPerMonth = type3_expenditure_input?.text?.toString(),
                    expenditureType = "business"

                )
            )
        }
        if (chk_type4?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = chk_type4?.text?.toString(),
                    amountPerMonth = type4_expenditure_input?.text?.toString(),
                    expenditureType = "business"

                )
            )
        }
        if (chk_type5?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = chk_type5?.text?.toString(),
                    amountPerMonth = type5_expenditure_input?.text?.toString(),
                    expenditureType = "business"

                )
            )
        }
        if (chk_type6?.isChecked == true) {
            expenditureList.add(
                Expenditure(
                    expenditureName = chk_type6?.text?.toString(),
                    amountPerMonth = type6_expenditure_input?.text?.toString(),
                    expenditureType = "business"

                )
            )
        }

        if (mLoanId == null || mLoanId == "") {
            mLoanId = activity?.intent?.getStringExtra("loanId")
            mCustomerId = activity?.intent?.getStringExtra("custId")
            if (arguments?.getString("task").equals("RM_AssignList", ignoreCase = true) ||
                arguments?.getString("task").equals("RMreJourney", ignoreCase = true)
            ) {
                mLoanId = arguments?.getString("loanId")
                mCustomerId = arguments?.getString("custId")
            }
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
                    } else if (qtrly_radio_button?.isChecked == true) {
                        qtrly_radio_button?.text?.toString()
                    } else if (halfyearly_radio_button?.isChecked == true) {
                        halfyearly_radio_button?.text?.toString()
                    } else {
                        ""
                    },
                    loanTenureFrom = et_from?.text?.toString(),
                    emisPaid = emisPaid?.text?.toString(),
                    tenor = tenor?.text?.toString(),
                    loanTenureTo = et_to?.text?.toString(),
                    outstandingAmount = outstanding_amount_input?.text?.toString(),
                    loanDocumentUrl = loanDocUrl,
                    considerCFA = cfa_cb.isChecked
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
                            } else if (loanDetails?.findViewById<RadioButton?>(R.id.qtrly_radio_button)?.isChecked == true) {
                                loanDetails?.findViewById<RadioButton?>(R.id.qtrly_radio_button)
                                    ?.text?.toString()
                            } else if (loanDetails?.findViewById<RadioButton?>(R.id.halfyearly_radio_button)?.isChecked == true) {
                                loanDetails?.findViewById<RadioButton?>(R.id.halfyearly_radio_button)
                                    ?.text?.toString()
                            } else {
                                ""
                            },
                            loanTenureFrom = loanDetails?.findViewById<TextInputEditText?>(R.id.et_from)?.text?.toString(),
                            loanTenureTo = loanDetails?.findViewById<TextInputEditText?>(R.id.et_to)?.text?.toString(),
                            tenor = loanDetails?.findViewById<TextInputEditText?>(R.id.tenor)?.text?.toString(),
                            emisPaid = loanDetails?.findViewById<TextInputEditText?>(R.id.emisPaid)?.text?.toString(),
                            outstandingAmount = loanDetails?.findViewById<TextInputEditText?>(R.id.outstanding_amount_input)?.text?.toString(),
                            loanDocumentUrl = loanDocUrl,
                            considerCFA = loanDetails?.findViewById<CheckBox?>(R.id.cfa_cb)?.isChecked

                        )
                    )
                }
            }
        }

        val postBody = IncomeDetailsPostData(
            resubmit = "",
            loanId = mLoanId,
            customerId = mCustomerId,
            anyOtherSourceofIncome = if (switch_other_sources?.isChecked == true) "Yes" else "No",
            incomes = sourceOfIncomeList,
            numberofFamilyMembers = family_members_count?.text?.toString(),
            noofEarningFamilyMembers = no_of_earning_family_member_count?.text?.toString(),
            monthlyFamilyIncome = monthly_income_input?.text?.toString(),
            monthlyhouseholdexpenditures = total_amount_input?.text?.toString(),
            monthlybusinessexpenditures = business_expenditure_amount_input?.text?.toString(),
            expenditures = expenditureList,
            liabilities = liablitiesList,
            methodUsed = spnr_loan_type?.selectedItem as? String,
            reassign = reassign
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                var response: Response<BaseResponseData>? = null
                response =
                    if (arguments?.getString("from") == "rmIncome" && (arguments?.getString("task")
                            .equals(
                                "RM_AssignList",
                                ignoreCase = true
                            ) || arguments?.getString("task")
                            .equals("RMreJourney", ignoreCase = true))
                    ) {
                        postBody.resubmit = "yes"
//                        RetrofitFactory.getApiService().rmResubmitIncome(postBody)
                        RetrofitFactory.getApiService().saveIncomeDetail(postBody)
                    } else {
                        RetrofitFactory.getApiService().saveIncomeDetail(postBody)
                    }
                if (response?.isSuccessful == true && arguments?.getString("from") == "rmIncome") {
                    withContext(Dispatchers.Main) {
                        if (arguments?.getString("task").equals("RMreJourney", ignoreCase = true)) {
                            withContext(Dispatchers.IO)
                            {
                                progressBar.dismmissLoading()

                                startActivity(
                                    Intent(
                                        activity,
                                        RMScreeningNavigationActivity::class.java
                                    ).apply {
                                        putExtra("loanId", mLoanId)
                                    })
                                activity?.finish()
                            }
                        } else {
                            withContext(Dispatchers.IO)
                            {
                                if (context is RMReAssignListingActivity) {
                                    var con = context as RMReAssignListingActivity
                                    con.showAssignListFragment()
                                } else if (context is ReUsableFragmentSpace) {
                                    activity?.finish()
                                }
                                progressBar.dismmissLoading()
                            }
                        }
                    }


                } else if (response!!.isSuccessful) {
                    val result = response.body()
                    if (result?.apiCode == "200") {
                        withContext(Dispatchers.Main) {
                            progressBar.dismmissLoading()

                            if (result.eligibility?.toLowerCase() == "y") {
                                if (activity is LeadInfoCaptureActivity) {
                                    (activity as LeadInfoCaptureActivity).enableDoc()
                                    (activity as LeadInfoCaptureActivity).infoCompleteState(INCOME)
                                }
                                var b = Bundle()
                                b.putString("loanType", result.loanType)
                                activity?.intent?.putExtra("loanType", result.loanType)
                                activity?.intent?.putExtra("apMarketValue", result.marketValue)
                                navController?.navigate(R.id.action_income_to_doc, b)
                            } else {
                                startActivity(Intent(activity, RMDashboardActivity::class.java))
                                activity?.finish()

                            }
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

    private fun getIncomeDetailsFromRm() {

        try {
            val progressLoader = ProgrssLoader(context!!)
            progressLoader.showLoading()
            CoroutineScope(ioContext).launch {
                /* val response =
                     RetrofitFactory.getApiService().getIncomeData(arguments?.getString("loanId"))*/
                var map = HashMap<String, String>()
                map["loanId"] = arguments?.getString("loanId")!!
                map["screen"] = "INCOME"
                val response =
                    RetrofitFactory.getApiService().getScreenData(map)
                withContext(uiContext) {
                    if (response!!.isSuccessful) {
                        val res = response.body()
                        progressLoader.dismmissLoading()
                        updateData(
                            res?.incomeDetails,
                            res?.incomeDetails?.customerId,
                            res?.incomeDetails?.loanId,
                            ""
                        )
                    }
                }
            }
        } catch (e: java.lang.Exception) {

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
                    val respone = RetrofitFactory.getApiService().getIncSrcMstr()
                    if (respone != null) {
                        withContext(uiContext) {
                            sourceInceomeAdapter = getAdapter(respone.body()?.data)
                            if (source_of_income_input != null)
                                source_of_income_input.adapter = sourceInceomeAdapter
                        }
                        //                        spnr_nature_of_collateral?.adapter = getAdapter(response.body()?.data)
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return@async true
        }

    private fun uploadStatement(fileLocation: String) {
        if(mLoanId==null&&mCustomerId==null)
        {
            mLoanId=activity?.intent?.getStringExtra("loanId")
            mCustomerId=activity?.intent?.getStringExtra("custId")
        }
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
                            mLoanId,
                            mCustomerId
                        )
                withContext(Dispatchers.Main) {
                    try {
                        listOfStatements.add(file.name)
//                        val alert=AlertDialog.Builder(context,android.R.style.Theme_Translucent_NoTitleBar);
                        if (alert == null )
                        {
                            alert = Dialog(context!!, R.style.DialogTheme);
                            val view =
                                activity?.layoutInflater?.inflate(R.layout.list_of_statements, null)
                            alert!!.setContentView(view!!)
                             rv = view.findViewById<RecyclerView>(R.id.rvStatements)
                            val upload = view.findViewById<Button>(R.id.submit)
                            val cancel = view.findViewById<Button>(R.id.cancel)
//                        val dialog= alert.create()
                            /*alert.requestWindowFeature(Window.FEATURE_NO_TITLE);

                        alert.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT);*/
                            cancel?.setOnClickListener {
                                alert!!.cancel()
                            }
                            upload?.setOnClickListener {
                                btn_statement_upload.performClick()
//                            alert.cancel()
                            }
                            rv?.adapter = UploadStatmentsDialog(context!!, listOfStatements)


                            /*  alert.setMessae("Statement uploaded successfully. Do you want to upload another ?")
                        alert.setPositiveButton("Upload", DialogInterface.OnClickListener { dialog, which ->
                            btn_statement_upload.performClick()
                            dialog.cancel()
                        })
                        alert.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                            dialog.cancel()
                        })*/
                            alert!!.show()
                        }else{
                            rv?.adapter=UploadStatmentsDialog(context!!,listOfStatements)
                        }
                        Toast.makeText(
                            activity, "Statement Uploaded Successfully",
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
            activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "Arthan"
        )/*  val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Arthan"
        )*/
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
                RequestCode.LoanDoc -> {
                    data?.let {
                        val loanDoc: CardResponse? =
                            it.extras?.getParcelable<CardResponse>(ArgumentKey.LoanDoc)
                        loanDocUrl = loanDoc?.cardFrontUrl.toString()

                    }
                }
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
                102 -> {
                    data?.apply {
                        if (this.data == null) return
                        val file = copyFile(context!!, this.data!!)
                        if (file != null && file?.absolutePath.isNotEmpty()) {
                            uploadLoanDoc()
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

    private fun uploadLoanDoc() {
        startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
            putExtra(DOC_TYPE, RequestCode.LoanDoc)
        }, RequestCode.LoanDoc)
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

    fun retrieveAllLoanTypeItems(theSpinner: Spinner): ArrayList<String>? {
        val adapter: Adapter = theSpinner.adapter
        val n = adapter.count
        val users: ArrayList<String>? = ArrayList<String>()
        for (i in 0 until n) {
            val user: String = adapter.getItem(i) as String
            users?.add(user)

        }
        return users
    }

    fun updateData(
        incomeDetails: IncomeDetails?,
        customerId: String?,
        loanId: String?,
        incomeComments: String?
    ) {
        launch(ioContext) {
            this@IncomeInformationFragment.incomeDetails=incomeDetails

            fetchAndUpdateCollateralAsync().await()

            withContext(Dispatchers.Main) {
                switch_other_sources?.isChecked = false
                if (incomeDetails?.incomes?.size != 0) {
                    switch_other_sources?.isChecked = true

                }
                family_members_count?.text = incomeDetails?.numberofFamilyMembers
                no_of_earning_family_member_count?.text = incomeDetails?.noofEarningFamilyMembers
                monthly_income_input?.setText(incomeDetails?.monthlyFamilyIncome)
                total_amount_input?.setText(incomeDetails?.monthlyhouseholdexpenditures)
                business_expenditure_amount_input?.setText(incomeDetails?.monthlybusinessexpenditures)
                txt_income_list_msg?.setText(activity?.resources?.getString(R.string.list_all_sources_of_income_1) + " (" + incomeDetails?.incomes?.size + ")")
                mLoanId = loanId
//        mCustomerId = incomeDetails?.customerId
                if (activity is ReUsableFragmentSpace) {
                    (activity as ReUsableFragmentSpace).setCommentsToField(incomeComments.toString() + "")
                }
                mCustomerId = customerId
                for (item in incomeDetails?.expenditures ?: listOf()) {


                    when {
                        item.expenditureName?.equals(
                            grocery_expenditure_label?.text?.toString(),
                            ignoreCase = true
                        ) ?: false -> {
                            grocery_expenditure_label?.isChecked = true
                            grocery_expenditure_input?.setText(item.amountPerMonth)
                        }
                        item.expenditureName?.equals(
                            transport_expenditure_label?.text?.toString(),
                            ignoreCase = true
                        ) ?: false -> {
                            transport_expenditure_label?.isChecked = true
                            transport_expenditure_input?.setText(item.amountPerMonth)
                        }
                        item.expenditureName?.equals(
                            education_expenditure_label?.text?.toString(),
                            ignoreCase = true
                        ) ?: false -> {
                            education_expenditure_label?.isChecked = true
                            education_expenditure_input?.setText(item.amountPerMonth)
                        }
                        item.expenditureName?.equals(
                            medicine_expenditure_label?.text?.toString(),
                            ignoreCase = true
                        ) ?: false -> {
                            medicine_expenditure_label?.isChecked = true
                            medicine_expenditure_input?.setText(item.amountPerMonth)
                        }
                        item.expenditureName?.equals(
                            other_expenditure_label?.text?.toString(),
                            ignoreCase = true
                        ) ?: false -> {
                            other_expenditure_label.isChecked = true
                            other_expenditure_input?.setText(item.amountPerMonth)
                        }

                        item.expenditureName?.equals(
                            chk_type1?.text?.toString(),
                            ignoreCase = true
                        ) ?: false -> {
                            chk_type1?.isChecked = true

                            type1_expenditure_input?.setText(item.amountPerMonth)
                        }
                        item.expenditureName?.equals(
                            chk_type4?.text?.toString(),
                            ignoreCase = true
                        ) ?: false -> {

                            chk_type4?.isChecked = true
                            type4_expenditure_input?.setText(item.amountPerMonth)
                        }
                        item.expenditureName?.equals(
                            chk_type3?.text?.toString(),
                            ignoreCase = true
                        ) ?: false -> {
                            chk_type3?.isChecked = true
                            type3_expenditure_input?.setText(item.amountPerMonth)
                        }
                        item.expenditureName?.equals(
                            chk_type2?.text?.toString(),
                            ignoreCase = true
                        ) ?: false -> {
                            chk_type2?.isChecked = true
                            type2_expenditure_input?.setText(item.amountPerMonth)
                        }
                        item.expenditureName?.equals(
                            chk_type5?.text?.toString(),
                            ignoreCase = true
                        ) ?: false -> {
                            chk_type5?.isChecked = true

                            type5_expenditure_input?.setText(item.amountPerMonth)
                        }
                        item.expenditureName?.equals(
                            chk_type6?.text?.toString(),
                            ignoreCase = true
                        ) ?: false -> {
                            chk_type6?.isChecked = true

                            type6_expenditure_input?.setText(item.amountPerMonth)
                        }
                    }
                }



                ll_income_source.removeAllViews()
                    for (item in incomeDetails?.incomes ?: listOf()) {
                        var defaultDataAddedInStaticView=true
                        if(!defaultDataAddedInStaticView){
                            var spinner =
                                source_of_income_input

                            spinner?.adapter = sourceInceomeAdapter

                            val listAdap =
                                (spinner?.adapter as? DataSpinnerAdapter)?.list
                            if (listAdap != null) {
                                for (i in listAdap) {

                                    if (i.value == item.incomeSource) {
                                        spinner?.setSelection(listAdap.indexOf(i))
                                    }
                                }
                            }


                           income_per_month_input
                                ?.let { tv ->
                                    tv.setCompoundDrawablesWithIntrinsicBounds(
                                        getRupeeSymbol(
                                            context,
                                            tv.textSize,
                                            tv.currentTextColor
                                        ), null, null, null
                                    )
                                    tv.setText( item.incomePerMonth)
                                }
                            defaultDataAddedInStaticView=true

                        } else {

                            val partnerView =
                                LayoutInflater.from(activity!!)
                                    .inflate(R.layout.layout_income_source, null, false)
                            if ((ll_income_source?.size ?: 0) > 0) {
                                partnerView?.findViewById<View?>(R.id.remove_button)
                                    ?.setOnClickListener {
                                        ll_income_source?.removeView(partnerView)
                                    }
                            } else {
                                partnerView?.findViewById<View?>(R.id.remove_button)?.visibility =
                                    View.GONE
                            }
                            var spinner =
                                partnerView?.findViewById<Spinner?>(R.id.source_of_income_input)

                            spinner?.adapter = sourceInceomeAdapter

                            val listAdap =
                                (spinner?.adapter as? DataSpinnerAdapter)?.list
                            if (listAdap != null) {
                                for (i in listAdap) {

                                    if (i.value == item.incomeSource) {
                                        spinner?.setSelection(listAdap.indexOf(i))
                                    }
                                }
                            }
                            partnerView?.findViewById<TextInputEditText?>(R.id.income_per_month_input)?.addTextChangedListener(object :TextWatcher{
                                override fun afterTextChanged(s: Editable?) {
                                    getTotalIncome()
                                }

                                override fun beforeTextChanged(
                                    s: CharSequence?,
                                    start: Int,
                                    count: Int,
                                    after: Int
                                ) {
                                }

                                override fun onTextChanged(
                                    s: CharSequence?,
                                    start: Int,
                                    before: Int,
                                    count: Int
                                ) {
                                }

                            })


                            partnerView?.findViewById<TextView?>(R.id.income_per_month_input)
                                ?.let { tv ->
                                    tv.setCompoundDrawablesWithIntrinsicBounds(
                                        getRupeeSymbol(
                                            context,
                                            tv.textSize,
                                            tv.currentTextColor
                                        ), null, null, null
                                    )
                                    tv.text = item.incomePerMonth
                                    ll_income_source.addView(partnerView)
                                }
                        }
                    }
                getTotalIncome()
//                ll_loan_details.removeAllViews()
                for (item in incomeDetails?.liabilities ?: listOf()) {

                    if (incomeDetails?.liabilities?.size == 1) {

                        var list =
                            retrieveAllLoanTypeItems(spnr_loan_type)
                        var list2 =
                            retrieveAllLoanTypeItems(spnr_loan_sanctioned_by)
                        for (i in 0 until list!!.size) {
                            if (item.loanType == list[i]) {
                                var spnr_loan_type =
                                    spnr_loan_type
                                spnr_loan_type.setSelection(i)
                            }

                        }
                        for (i in 0 until list2!!.size) {
                            if (item.loanSanctionedBy == list2[i]) {
                                var spnr_santioned_by =
                                    spnr_loan_sanctioned_by
                                spnr_santioned_by.setSelection(i)
                            }

                        }
                        loan_amount_input
                            ?.setText(item.loanAmount)
                        emi_input?.setText(item.emi)
                        outstanding_amount_input
                            ?.setText(item.outstandingAmount)
                        et_from
                            ?.setText(item.loanTenureFrom)
                        et_to?.setText(item.loanTenureTo)

                        tenor
                            ?.setText(item.tenor)
                        emisPaid?.setText(item.emisPaid)
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
                                weekly_radio_button
                                    ?.isChecked =
                                    true
                            }
                            item.frequencyOfInstallment?.equals("monthly", ignoreCase = true) -> {
                                monthly_radio_button
                                    ?.isChecked =
                                    true
                            }
                            item.frequencyOfInstallment?.equals("yearly", ignoreCase = true) -> {
                                yearly_radio_button
                                    ?.isChecked =
                                    true
                            }
                            item.frequencyOfInstallment?.equals("Quarterly", ignoreCase = true) -> {
                                qtrly_radio_button
                                    ?.isChecked =
                                    true
                            }
                            item.frequencyOfInstallment?.equals(
                                "Half yearly",
                                ignoreCase = true
                            ) -> {
                                halfyearly_radio_button
                                    ?.isChecked =
                                    true
                            }
                        }

                        et_from?.setOnClickListener {
                            dateSelection(
                                context!!,
                                et_from
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
                        et_to?.setOnClickListener {


                            val c = Calendar.getInstance()
                            DatePickerDialog(
                                context!!,
                                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                                    val date =
                                        dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year

                                    if (SimpleDateFormat("dd-MM-yyyy").parse(date)
                                            .after(SimpleDateFormat("dd-MM-yyyy").parse(et_from.text.toString()))
                                    )
                                        et_to?.setText(date)
                                    else
                                        Toast.makeText(
                                            activity,
                                            "Please select date greater than from date",
                                            Toast.LENGTH_LONG
                                        ).show()
                                },
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH)
                            ).show()

                        }
                        spnr_loan_type
                            ?.onItemSelectedListener =
                            mOnLoanTypeItemSelectedListener
                        et_to?.let {
                            it.setOnClickListener {


                                dateSelection(
                                    context!!,
                                    et_to
                                )
                            }
                        }
                    }
                  else  if (incomeDetails?.liabilities?.size!! > 1) {


                        var loanView =
                            LayoutInflater.from(activity!!)
                                .inflate(R.layout.layout_loan_details, null, false)
                        if ((ll_loan_details?.size ?: 0) > 0) {
                            loanView?.findViewById<View?>(R.id.remove_button)?.setOnClickListener {
                                ll_loan_details?.removeView(loanView)
                            }
                        } else {
                            loanView?.findViewById<View?>(R.id.remove_button)?.visibility =
                                View.GONE
                        }

                        var list =
                            retrieveAllLoanTypeItems(loanView.findViewById<Spinner>(R.id.spnr_loan_type))
                        var list2 =
                            retrieveAllLoanTypeItems(loanView.findViewById<Spinner>(R.id.spnr_loan_sanctioned_by))
                        for (i in 0 until list!!.size) {
                            if (item.loanType == list[i]) {
                                var spnr_loan_type =
                                    loanView.findViewById<Spinner>(R.id.spnr_loan_type)
                                spnr_loan_type.setSelection(i)
                            }

                        }
                        for (i in 0 until list2!!.size) {
                            if (item.loanSanctionedBy == list2[i]) {
                                var spnr_santioned_by =
                                    loanView.findViewById<Spinner>(R.id.spnr_loan_sanctioned_by)
                                spnr_santioned_by.setSelection(i)
                            }

                        }
                        loanView.findViewById<EditText>(R.id.loan_amount_input)
                            ?.setText(item.loanAmount)
                        loanView.findViewById<EditText?>(R.id.emi_input)?.setText(item.emi)
                        loanView.findViewById<EditText?>(R.id.outstanding_amount_input)
                            ?.setText(item.outstandingAmount)
                        loanView?.findViewById<EditText?>(R.id.et_from)
                            ?.setText(item.loanTenureFrom)
                        loanView?.findViewById<EditText?>(R.id.et_to)?.setText(item.loanTenureTo)
                        loanView?.findViewById<EditText?>(R.id.tenor)
                            ?.setText(item.tenor)
                        loanView?.findViewById<EditText?>(R.id.emisPaid)?.setText(item.emisPaid)
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
                                loanView?.findViewById<RadioButton>(R.id.weekly_radio_button)
                                    ?.isChecked =
                                    true
                            }
                            item.frequencyOfInstallment?.equals("monthly", ignoreCase = true) -> {
                                loanView?.findViewById<RadioButton>(R.id.monthly_radio_button)
                                    ?.isChecked =
                                    true
                            }
                            item.frequencyOfInstallment?.equals("yearly", ignoreCase = true) -> {
                                loanView?.findViewById<RadioButton>(R.id.yearly_radio_button)
                                    ?.isChecked =
                                    true
                            }
                            item.frequencyOfInstallment?.equals("Quarterly", ignoreCase = true) -> {
                                loanView?.findViewById<RadioButton>(R.id.qtrly_radio_button)
                                    ?.isChecked =
                                    true
                            }
                            item.frequencyOfInstallment?.equals(
                                "Half yearly",
                                ignoreCase = true
                            ) -> {
                                loanView?.findViewById<RadioButton>(R.id.halfyearly_radio_button)
                                    ?.isChecked =
                                    true
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


                            val c = Calendar.getInstance()
                            DatePickerDialog(
                                context!!,
                                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                                    val date =
                                        dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year

                                    if (SimpleDateFormat("dd-MM-yyyy").parse(date)
                                            .after(SimpleDateFormat("dd-MM-yyyy").parse(et_from.text.toString()))
                                    )
                                        loanView.findViewById<EditText>(R.id.et_to)?.setText(date)
                                    else
                                        Toast.makeText(
                                            activity,
                                            "Please select date greater than from date",
                                            Toast.LENGTH_LONG
                                        ).show()
                                },
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH)
                            ).show()

                        }
                        loanView.findViewById<AppCompatSpinner?>(R.id.spnr_loan_type)
                            ?.onItemSelectedListener =
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
        }
    }

     fun addHouseHoldExpSum()
    {
        var sum=0;
        var householdTotal=0;
        if(grocery_expenditure_input.length()>0)
        {
            sum += (grocery_expenditure_input.text.toString().toInt())
        }
        if(transport_expenditure_input.length()>0)
        {
            sum += (transport_expenditure_input.text.toString().toInt())
        }

        if(education_expenditure_input.length()>0)
        {
            sum += (education_expenditure_input.text.toString().toInt())
        }

        if(medicine_expenditure_input.length()>0)
        {
            sum += (medicine_expenditure_input.text.toString().toInt())
        }
        if(other_expenditure_input.length()>0)
        {
            sum += (other_expenditure_input.text.toString().toInt())
        }

//        total_amount_input.setText(sum.toString())


        if(type1_expenditure_input.length()>0)
        {
            householdTotal += (type1_expenditure_input.text.toString().toInt())
        }
        if(type2_expenditure_input.length()>0)
        {
            householdTotal += (type2_expenditure_input.text.toString().toInt())
        }

        if(type3_expenditure_input.length()>0)
        {
            householdTotal += (type3_expenditure_input.text.toString().toInt())
        }

        if(type4_expenditure_input.length()>0)
        {
            householdTotal += (type4_expenditure_input.text.toString().toInt())
        }
        if(type5_expenditure_input.length()>0)
        {
            householdTotal += (type5_expenditure_input.text.toString().toInt())
        }
        if(type6_expenditure_input.length()>0)
        {
            householdTotal += (type6_expenditure_input.text.toString().toInt())
        }

//        business_expenditure_amount_input.setText(householdTotal.toString())






    }

    fun getTotalIncome()
    {
        var sum=0
        if ((ll_income_source?.childCount ?: 0) > 0) {
            for (childCount in 0 until (ll_income_source?.childCount ?: 0)) {

                val income =    (((((ll_income_source?.getChildAt(childCount) as ViewGroup).getChildAt(2) as ViewGroup)).getChildAt(0) as ViewGroup).getChildAt(0) as TextInputEditText).text.toString()
                if(income.isNotEmpty()) {
                    sum += income.toInt()
                    total_income_et.setText(sum.toString())
                }
            }
        }

    }
    override fun afterTextChanged(s: Editable?) {
        addHouseHoldExpSum()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}

