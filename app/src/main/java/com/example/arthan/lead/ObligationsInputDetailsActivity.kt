package com.example.arthan.lead

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.Fragment
import com.example.arthan.R
import com.example.arthan.lead.model.postdata.ObligDetail
import com.example.arthan.lead.model.postdata.ObligationPostData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.getRupeeSymbol
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_obligations_input_details.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.layout_obligations_input.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ObligationsInputDetailsActivity : AppCompatActivity(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main
    private var mRupeeDrawable: Drawable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_obligations_input_details)

        mRupeeDrawable = getRupeeSymbol(
            this,
            loan_amount_input?.textSize ?: 14f,
            loan_amount_input?.currentTextColor ?: 0
        )

        toolbar_title?.text = "Obligations Details"
        back_button?.setOnClickListener { onBackPressed() }
        submit_button?.setOnClickListener {
            saveObligationsDetails()
        }
        remove_button?.visibility = View.GONE
        add_more_button?.setOnClickListener { _ ->
            obligation_linear_layout?.addView(
                LayoutInflater.from(this)
                    .inflate(
                        R.layout.layout_obligations_input,
                        obligation_linear_layout,
                        false
                    ).apply {
                        findViewById<View?>(R.id.remove_button)?.setOnClickListener {
                            obligation_linear_layout?.removeView(this)
                        }
                        setDrawableToEditText(
                            findViewById<EditText?>(R.id.loan_amount_input),
                            mRupeeDrawable, null, null, null
                        )
                        setDrawableToEditText(
                            findViewById<EditText?>(R.id.emi_input),
                            mRupeeDrawable, null, null, null
                        )
                        setDrawableToEditText(
                            findViewById<EditText?>(R.id.loan_os_input),
                            mRupeeDrawable, null, null, null
                        )
                        setDrawableToEditText(
                            findViewById<EditText?>(R.id.emi_os_input),
                            mRupeeDrawable, null, null, null
                        )
                    }
            )
        }
        setDrawableToEditText(loan_amount_input, mRupeeDrawable, null, null, null)
        setDrawableToEditText(emi_input, mRupeeDrawable, null, null, null)
        setDrawableToEditText(loan_os_input, mRupeeDrawable, null, null, null)
        setDrawableToEditText(emi_os_input, mRupeeDrawable, null, null, null)
    }

    private fun setDrawableToEditText(
        editText: EditText?,
        leftDrawable: Drawable?,
        topDrawable: Drawable?,
        rightDrawable: Drawable?,
        bottomDrawable: Drawable?
    ) {
        editText?.setCompoundDrawablesWithIntrinsicBounds(
            leftDrawable,
            topDrawable,
            rightDrawable,
            bottomDrawable
        )
    }

    private suspend fun stopLoading(progressBar: ProgrssLoader, message: String?) {
        withContext(uiContext) {
            progressBar.dismmissLoading()
            message?.let {
                Toast.makeText(this@ObligationsInputDetailsActivity, it, Toast.LENGTH_LONG).show()
            }
            failure()
        }
    }

    private fun saveObligationsDetails() {
        val progressBar = ProgrssLoader(this)
        progressBar.showLoading()
        CoroutineScope(ioContext).launch {
            val obligationList: MutableList<ObligDetail> = mutableListOf()
            for (index in 0 until (obligation_linear_layout?.childCount ?: 0)) {
                val child = obligation_linear_layout?.getChildAt(index)
                child?.let {
                    obligationList.add(
                        ObligDetail(
                            consideredFoir = if (it.findViewById<CheckBox?>(R.id.considered_for_foir_checkbox)?.isChecked == true) "Yes" else "No",
                            emi = it.findViewById<TextInputEditText?>(R.id.emi_input)?.text?.toString(),
                            emisBounced = it.findViewById<TextInputEditText?>(R.id.emi_bounces_input)?.text?.toString(),
                            emisOutstanding = it.findViewById<TextInputEditText?>(R.id.emi_os_input)?.text?.toString(),
                            emisPaid = it.findViewById<TextInputEditText?>(R.id.emi_paid_input)?.text?.toString(),
                            financierName = it.findViewById<TextInputEditText?>(R.id.name_of_financier_input)?.text?.toString(),
                            loanType = it.findViewById<AppCompatSpinner?>(R.id.loan_type_spinner)?.selectedItem as? String,
                            loanAmt = it.findViewById<TextInputEditText?>(R.id.loan_amount_input)?.text?.toString(),
                            loanOutstanding = it.findViewById<TextInputEditText?>(R.id.loan_os_input)?.text?.toString(),
                            loanTknBy = it.findViewById<TextInputEditText?>(R.id.loan_taken_by_input)?.text?.toString(),
                            tenor = it.findViewById<TextInputEditText?>(R.id.tenure_input)?.text?.toString()
                        )
                    )
                }
            }
            val postBody = ObligationPostData(
                loanId = intent?.getStringExtra(ArgumentKey.LoanId),
                custId = intent?.getStringExtra(ArgumentKey.CustomerId),
                obligDetails = obligationList
            )
            try {
                val response = RetrofitFactory.getApiService().saveObligations(postBody)
                if (response?.isSuccessful == true) {
                    val result = response.body()
                    if (result?.apiCode == "200") {
                        withContext(uiContext) {
                            try {
                                progressBar.dismmissLoading()
                                success()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                stopLoading(progressBar, e.message)
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
                    Intent(it.context, ObligationsInputDetailsActivity::class.java).apply {
                        putExtra(ArgumentKey.LoanId, loanId)
                        putExtra(ArgumentKey.CustomerId, customerId)
                    },
                    requestCode
                )
                return
            }
            activity?.let {
                it.startActivityForResult(
                    Intent(it, GSTInputDetailsActivity::class.java).apply {
                        putExtra(ArgumentKey.LoanId, loanId)
                        putExtra(ArgumentKey.CustomerId, customerId)
                    },
                    requestCode
                )
            }
        }
    }
}
