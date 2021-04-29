package com.example.arthan.lead

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.arthan.R
import com.example.arthan.lead.model.postdata.BankingInputPostData
import com.example.arthan.lead.model.postdata.EntryDetail
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import kotlinx.android.synthetic.main.activity_banking_input_details.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlin.coroutines.CoroutineContext
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.getRupeeSymbol
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*

class BankingInputDetailsActivity : AppCompatActivity(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    private var mDrawable: Drawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banking_input_details)

        toolbar_title?.text = "Banking Details"
        back_button?.setOnClickListener { onBackPressed() }
        submit_button?.setOnClickListener {
            saveBankDetails()
        }

        var monthCount = 0
        for (index in 0 until (month_list?.childCount ?: 0)) {
            val child = month_list?.getChildAt(index)
            if (child !is ConstraintLayout) {
                continue
            }
            if (mDrawable == null) {
                mDrawable = getRupeeSymbol(
                    this,
                    child?.findViewById<TextInputEditText?>(R.id.total_business_credit_input)?.textSize
                        ?: 14f,
                    child?.findViewById<TextInputEditText?>(R.id.total_business_credit_input)?.currentTextColor
                        ?: 0
                )
            }
            monthCount++
            child?.findViewById<TextView?>(R.id.month_text_view)?.text = "Month $monthCount"
            setDrawableToEditText(child?.findViewById(R.id.total_business_credit_input), mDrawable)
            setDrawableToEditText(child?.findViewById(R.id.cash_deposited_input), mDrawable)
            setDrawableToEditText(child?.findViewById(R.id.balance_on_5th_input), mDrawable)
            setDrawableToEditText(child?.findViewById(R.id.balance_on_15th_input), mDrawable)
            setDrawableToEditText(child?.findViewById(R.id.balance_on_29th_input), mDrawable)
        }
    }

    private fun setDrawableToEditText(
        editText: EditText?,
        leftDrawable: Drawable?
    ) = editText?.setCompoundDrawablesWithIntrinsicBounds(
        leftDrawable,
        null,
        null,
        null
    )

    private suspend fun stopLoading(progressBar: ProgrssLoader, message: String?) {
        withContext(uiContext) {
            progressBar.dismmissLoading()
            message?.let {
                Toast.makeText(this@BankingInputDetailsActivity, it, Toast.LENGTH_LONG).show()
            }
            failure()
        }
    }

    private fun saveBankDetails() {
        val progressBar = ProgrssLoader(this)
        progressBar.showLoading()
        CoroutineScope(ioContext).launch {
            try {
                val arrayList: MutableList<EntryDetail> = mutableListOf()
                for (index in 0 until (month_list?.childCount ?: 0)) {
                    val child = month_list?.getChildAt(index)
                    if (child !is ConstraintLayout) {
                        continue
                    }
                    arrayList.add(
                        EntryDetail(
                            bal5 = child?.findViewById<TextInputEditText?>(R.id.balance_on_5th_input)?.text?.toString(),
                            bal15 = child?.findViewById<TextInputEditText?>(R.id.balance_on_15th_input)?.text?.toString(),
                            bal29 = child?.findViewById<TextInputEditText?>(R.id.balance_on_29th_input)?.text?.toString(),
                            cashDeposit = child?.findViewById<TextInputEditText?>(R.id.cash_deposited_input)?.text?.toString(),
                            month = "${arrayList.size + 1}",
                            noOfChqDeposit = child?.findViewById<TextInputEditText?>(R.id.no_of_cheque_deposited_input)?.text?.toString(),
                            noOfCrd = child?.findViewById<TextInputEditText?>(R.id.no_of_credit_entries_input)?.text?.toString(),
                            noOfInwardBounce = child?.findViewById<TextInputEditText?>(R.id.inward_bounce_input)?.text?.toString(),
                            noOfInwardChqIssued = child?.findViewById<TextInputEditText?>(R.id.no_of_inward_cheque_issued_input)?.text?.toString(),
                            noOfOutwardBounce = child?.findViewById<TextInputEditText?>(R.id.outward_bounce_input)?.text?.toString(),
                            totalBusinessCrd = child?.findViewById<TextInputEditText?>(R.id.total_business_credit_input)?.text?.toString()
                        )
                    )
                }
                val postData = BankingInputPostData(
                    accName = account_name_input?.text?.toString(),
                    accNo = account_number_input?.text?.toString(),
                    accType = account_type_input?.text?.toString(),
                    bankName = bank_name_input?.text?.toString(),
                    custId = intent?.getStringExtra(ArgumentKey.CustomerId),
                    entryDetails = arrayList,
                    limit = account_limit_input?.text?.toString(),
                    loanId = intent?.getStringExtra(ArgumentKey.LoanId)
                )
                val apiResponse = RetrofitFactory.getApiService().saveBanking(postData)
                if (apiResponse?.isSuccessful == true) {
                    val result = apiResponse?.body()
                    if (result?.apiCode == "200") {
                        withContext(uiContext) {
                            withContext(uiContext) {
                                try {
                                    progressBar.dismmissLoading()
                                    success()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    stopLoading(progressBar, e.message)
                                }
                            }
                        }
                    } else {
                        stopLoading(progressBar, "Something went wrong")
                    }
                } else {
                    stopLoading(progressBar, "Something went wrong")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                stopLoading(progressBar, "Something went wrong")
            }
        }
    }

    private fun failure() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun success() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    companion object {
        fun startMeForResult(
            fragment: Fragment?, activity: Activity?, requestCode: Int,
            loanId: String?, customerId: String?
        ) {
            fragment?.let {
                it.startActivityForResult(
                    Intent(it.context, BankingInputDetailsActivity::class.java).apply {
                        putExtra(ArgumentKey.LoanId, loanId)
                        putExtra(ArgumentKey.CustomerId, customerId)
                    },
                    requestCode
                )
                return
            }
            activity?.let {
                it.startActivityForResult(
                    Intent(it, BankingInputDetailsActivity::class.java).apply {
                        putExtra(ArgumentKey.LoanId, loanId)
                        putExtra(ArgumentKey.CustomerId, customerId)
                    },
                    requestCode
                )
            }
        }
    }

}
