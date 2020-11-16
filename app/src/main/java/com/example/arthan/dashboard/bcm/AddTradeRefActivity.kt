package com.example.arthan.dashboard.bcm

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.example.arthan.R
import com.example.arthan.global.ArthanApp
import com.example.arthan.lead.IncomeInformationFragment
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.lead.model.postdata.TradeRefDetail
import com.example.arthan.lead.model.postdata.TradeReferencePostData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import kotlinx.android.synthetic.main.activity_add_trade_ref.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class AddTradeRefActivity : AppCompatActivity(),CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_trade_ref)

        fetchAndUpdateRelationshipWithApplicantAsync()

        trade_reference_more_years_working_with_count?.tag = 0
        trade_reference_more_years_working_with_plus_button?.setOnClickListener {
            updateCount(
                IncomeInformationFragment.UpdateCountType.Increment,
                trade_reference_more_years_working_with_count
            )
        }
        trade_reference_more_years_working_with_minus_button?.setOnClickListener {
            updateCount(
                IncomeInformationFragment.UpdateCountType.Decrement,
                trade_reference_more_years_working_with_count
            )
        }
        addtrade.setOnClickListener {
            val postBody = TradeReferencePostData(
                resubmit = null,
                userId = ArthanApp.getAppInstance().loginUser,
                tradeRef = mutableListOf(
                    TradeRefDetail(
                        loanId = intent.getStringExtra("loanId"),
                        firmName = trade_reference_more_firm_name_input?.text?.toString(),
                        nameofPersonDealingWith = trade_reference_more_person_name_dealing_with_input?.text?.toString(),
                        rshipWithApplicant = (trade_reference_2_relationship_with_applicant_spinner?.selectedItem as? Data)?.value,
                        contactDetails = trade_reference_2_contact_details_input?.text?.toString(),
                        noOfYrsWorkingWith = (trade_reference_more_years_working_with_count?.tag as? Int)?.toString(),
                        productPurchaseSale = "",
                        customerId = intent.getStringExtra("custId"),
                        tradeRefId = ""
                    )
                )
            )
            val progress=ProgrssLoader(this)
            progress.showLoading()
            CoroutineScope(Dispatchers.IO).launch {
                val response = RetrofitFactory.getApiService().saveTradeReference(postBody)
                if (response?.isSuccessful == true) {
                    val result = response.body()
                    result?.apiCode == "200"
                    withContext(Dispatchers.Main)
                    {
                        progress.dismmissLoading()

                        if(result!=null) {
                            Toast.makeText(
                                this@AddTradeRefActivity,
                                "New trade reference added",
                                Toast.LENGTH_LONG
                            ).show()
                            val resultIntent=Intent()
                            resultIntent.putExtra("TradeRefDetail",postBody)
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        }else
                        {
                            Toast.makeText(
                                this@AddTradeRefActivity,
                                "Failed to add New trade reference",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    false
                }
            }
        }
    }
    private fun getAdapter(list: List<Data>?): DataSpinnerAdapter? =
        if (this != null)
            DataSpinnerAdapter(this, list?.toMutableList() ?: mutableListOf()).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            } else null
    private fun fetchAndUpdateRelationshipWithApplicantAsync(): Deferred<Boolean> =
        async(context = ioContext) {
            try {
                val response =
                    RetrofitFactory.getMasterApiService().getRelationshipWithApplicant()
                if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                    withContext(uiContext) {
                        trade_reference_2_relationship_with_applicant_spinner?.adapter =
                            getAdapter(response.body()?.data)
                        trade_reference_2_relationship_with_applicant_spinner?.adapter =
                            getAdapter(response.body()?.data)
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Crashlytics.log(e.message)

            }
            return@async true
        }

    private fun performIncrement(initialCount: Int) = initialCount + 1
    private fun performDecrement(initialCount: Int): Int =
        if (initialCount - 1 < 0) 0 else initialCount - 1
    private fun updateCount(
        updateType: IncomeInformationFragment.UpdateCountType,
        countText: TextView?
    ) = when (updateType) {
        is IncomeInformationFragment.UpdateCountType.Increment -> performIncrement(
            countText?.tag as? Int ?: 0
        )
        is IncomeInformationFragment.UpdateCountType.Decrement -> performDecrement(
            countText?.tag as? Int ?: 0
        )
    }.also {
        countText?.text = "$it"
        countText?.tag = it
    }
}
