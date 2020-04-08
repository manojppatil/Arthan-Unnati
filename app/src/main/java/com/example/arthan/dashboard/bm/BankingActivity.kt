package com.example.arthan.dashboard.bm

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.arthan.R
import com.example.arthan.dashboard.bm.adapter.BankingAdapter
import com.example.arthan.dashboard.bm.model.Banking360DetailsResponseData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import kotlinx.android.synthetic.main.activity_deviations.*
import kotlinx.android.synthetic.main.activity_lisiting.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class BankingActivity : AppCompatActivity(),CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private val ioContext: CoroutineContext
        get() = Dispatchers.IO
    private val uiContext: CoroutineContext
        get() = Dispatchers.Main
    private var detailsList: ArrayList<Banking360DetailsResponseData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lisiting)


        toolbar_title?.text = "Banking | "+intent.getStringExtra("cname")
        back_button?.setOnClickListener { onBackPressed() }
//        rv_listing.adapter = BankingAdapter(this, detailsList)
        loadInitialData()
    }
    private fun loadInitialData() {
        val progressBar = ProgrssLoader(this)
        progressBar.showLoading()
        CoroutineScope(ioContext).launch {
            try {
                val apiResponse = RetrofitFactory.getApiService()
                    .getBankingC360(intent.getStringExtra("loanId"))
                if (apiResponse?.isSuccessful == true) {
                    val result = apiResponse.body()
                    withContext(uiContext) {
                        if (result != null) {

                            detailsList.add(result)
                            rv_listing.adapter=BankingAdapter(this@BankingActivity,detailsList)

                           /* (recycler_list?.adapter as? BankingAdapter)
                                ?.updateList(detailsList)*/
                        }
                        stopLoading(progressBar, null)
                    }
                } else {
                    stopLoading(progressBar, "Something went wrong!!")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                stopLoading(progressBar, "Something went wrong!!")
            }
        }
    }
    private suspend fun stopLoading(progressBar: ProgrssLoader, message: String?) {
        withContext(uiContext) {
            progressBar.dismmissLoading()
            message?.let {
                Toast.makeText(this@BankingActivity, it, Toast.LENGTH_LONG).show()
            }
        }
    }


}