package com.example.arthan.lead

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.arthan.R
import com.example.arthan.lead.model.postdata.BillDetail
import com.example.arthan.lead.model.postdata.BillsPostData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.utils.getRupeeSymbol
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_bills_input_details.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class BillsInputDetailsActivity : AppCompatActivity(), CoroutineScope {

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
        setContentView(R.layout.activity_bills_input_details)

        toolbar_title?.text = "Bills Details"
        back_button?.setOnClickListener { onBackPressed() }
        submit_button?.setOnClickListener {
            saveBillDetails()
        }

        for (index in 0 until (month_list?.childCount ?: 0)) {
            val child = month_list?.getChildAt(index)
            if (child !is ConstraintLayout) {
                continue
            }
            if (mDrawable == null) {
                mDrawable = getRupeeSymbol(
                    this,
                    child?.findViewById<TextInputEditText?>(R.id.sales_kaccha_input)?.textSize
                        ?: 14f,
                    child?.findViewById<TextInputEditText?>(R.id.sales_kaccha_input)?.currentTextColor
                        ?: 0
                )
            }
            child?.findViewById<TextView?>(R.id.month)?.text = "Month ${index + 1}"
            setDrawableToEditText(
                child?.findViewById<TextInputEditText?>(R.id.sales_kaccha_input),
                mDrawable, null, null, null
            )
            setDrawableToEditText(
                child?.findViewById<TextInputEditText?>(R.id.sales_pacca_input),
                mDrawable, null, null, null
            )
            setDrawableToEditText(
                child?.findViewById<TextInputEditText?>(R.id.purchase_kaccha_input),
                mDrawable, null, null, null
            )
            setDrawableToEditText(
                child?.findViewById<TextInputEditText?>(R.id.purchase_pacca_input),
                mDrawable, null, null, null
            )
        }
    }

    private fun setDrawableToEditText(
        editText: EditText?,
        leftDrawable: Drawable?,
        topDrawable: Drawable?,
        rightDrawable: Drawable?,
        bottomDrawable: Drawable?
    ) = editText?.setCompoundDrawablesWithIntrinsicBounds(
        leftDrawable,
        topDrawable,
        rightDrawable,
        bottomDrawable
    )

    private suspend fun stopLoading(progressBar: ProgrssLoader, message: String?) {
        withContext(uiContext) {
            progressBar.dismmissLoading()
            message?.let {
                Toast.makeText(this@BillsInputDetailsActivity, it, Toast.LENGTH_LONG).show()
            }
            failure()
        }
    }

    private fun saveBillDetails() {
        val progressBar = ProgrssLoader(this)
        progressBar.showLoading()
        CoroutineScope(ioContext).launch {
            val billDetail: MutableList<BillDetail> = mutableListOf()
            for (index in 0 until (month_list?.childCount ?: 0)) {
                val child = month_list?.getChildAt(index)
                if (child !is ConstraintLayout) {
                    continue
                }
                billDetail.add(
                    BillDetail(
                        month = "${index + 1}",
                        kacchaSales = child?.findViewById<TextInputEditText?>(R.id.sales_kaccha_input)?.text?.toString(),
                        kacchaPurchases = child?.findViewById<TextInputEditText?>(R.id.purchase_kaccha_input)?.text?.toString(),
                        paccaSales = child?.findViewById<TextInputEditText?>(R.id.sales_pacca_input)?.text?.toString(),
                        paccaPurchases = child?.findViewById<TextInputEditText?>(R.id.purchase_pacca_input)?.text?.toString()
                    )
                )
            }
            val postBody = BillsPostData(
                loanId = intent?.getStringExtra(ArgumentKey.LoanId),
                custId = intent?.getStringExtra(ArgumentKey.CustomerId),
                billDetails = billDetail
            )
            try {
                val response = RetrofitFactory.getApiService().saveBills(postBody)
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
                    Intent(it.context, BillsInputDetailsActivity::class.java).apply {
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
