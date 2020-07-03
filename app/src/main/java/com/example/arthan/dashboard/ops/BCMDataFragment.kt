package com.example.arthan.dashboard.ops


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.example.arthan.R
import com.example.arthan.dashboard.bcm.BCMDashboardActivity
import com.example.arthan.dashboard.bm.Customer360Activity
import com.example.arthan.dashboard.ops.adapter.BCDDataAdapter
import com.example.arthan.global.AppPreferences
import com.example.arthan.lead.model.postdata.PD1PostData
import com.example.arthan.lead.model.postdata.PD23PostData
import com.example.arthan.lead.model.responsedata.BaseResponseData
import com.example.arthan.model.PD2Data
import com.example.arthan.model.PD3Data
import com.example.arthan.model.PD4Data
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.PendingCustomersActivity
import com.example.arthan.views.fragments.PDFragmentSaveClickListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_bcmdata.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * A simple [Fragment] subclass.
 */
class BCMDataFragment : Fragment(), CoroutineScope, PDFragmentSaveClickListener {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    private var mLoanId: String? =  ""
    private var mCustomerId: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mLoanId=activity?.intent?.getStringExtra(ArgumentKey.LoanId)
        mCustomerId=activity?.intent?.getStringExtra(ArgumentKey.CustomerId)
        return inflater.inflate(R.layout.fragment_bcmdata, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vp_profile?.adapter = BCDDataAdapter(childFragmentManager)
        tb_profile?.setupWithViewPager(vp_profile)
    }

    private val pd23Data = PD23PostData(
        loanId = mLoanId,
        customerId = mCustomerId
    )

    override fun onPD1Fragment(pd1Data: PD1PostData) {
        mLoanId=activity?.intent?.getStringExtra(ArgumentKey.LoanId)
        mCustomerId=activity?.intent?.getStringExtra(ArgumentKey.CustomerId)
        pd1Data.loanId = mLoanId
        pd1Data.customerId = mCustomerId
        val progressBar = ProgrssLoader(context ?: return)
        progressBar.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitFactory.getApiService().savePD1(pd1Data)
                if (response?.isSuccessful == true) {
//                    val result = response.body()
                    withContext(Dispatchers.Main) {
                        vp_profile?.currentItem = 1
                        progressBar.dismmissLoading()
                    }
                } else {
                    try {
                        val result: BaseResponseData? = Gson().fromJson(
                            response?.errorBody()?.string(),
                            BaseResponseData::class.java
                        )
                        mLoanId=result?.loanId
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

    override fun onPD2Fragment(pd2Data: PD2Data) {
        pd23Data.add(pd2Data)
        vp_profile?.currentItem = 2
    }

    override fun onPD3Fragment(pd3Data: PD3Data) {
        pd23Data.add(pd3Data)
        savePD23Data()
    }

    override fun onPD4Fragment(pd4Data: PD4Data) {
        val progressBar = ProgrssLoader(context ?: return)
        progressBar.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            var result=RetrofitFactory.getApiService().submitAssets(pd4Data)
            if(result?.body()!=null)
            {
                withContext(Dispatchers.Main) {
                    if(result.body()?.eligibility.equals("y",ignoreCase = true))
                    {
                        context?.startActivity(Intent(context, Customer360Activity::class.java).apply {
                            putExtra("loanId", mLoanId)
                            putExtra("indSeg",activity?.intent?.getStringExtra("indSeg"))
                            putExtra("loginDate",activity?.intent?.getStringExtra("loginDate"))
                            putExtra("loanId",activity?.intent?.getStringExtra("loanId"))
                            putExtra("loanAmt",activity?.intent?.getStringExtra("loanAmt"))
                            putExtra("cname",activity?.intent?.getStringExtra("cname"))
                            putExtra("custId",activity?.intent?.getStringExtra("custId"))
                            putExtra("loanType",activity?.intent?.getStringExtra("loanType"))

                        })
                    }else {
                        withContext(Dispatchers.Main) {
                            startActivity(Intent(
                                activity,
                                PendingCustomersActivity::class.java
                            ).apply {
                                putExtra("FROM", "BCM")
                            })
                        }
                        activity?.finish()
                    }
                }
            }else {
                withContext(Dispatchers.Main) {

                    try {
                        stopLoading(
                            progressBar,
                            "Smething went wrong with api!!!"/*result?.message*/
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        stopLoading(progressBar, "Something went wrong. Please try later!")
                    }
                }


            }
        }
    }

    fun updateLoanAndCustomerId(loanId: String?, customerId: String?) {
        mLoanId = loanId
        mCustomerId = customerId
    }

    private suspend fun stopLoading(progressBar: ProgrssLoader, message: String?) {
        withContext(uiContext) {
            progressBar.dismmissLoading()
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun savePD23Data() {
        val progressBar = ProgrssLoader(context ?: return)
        progressBar.showLoading()
        pd23Data?.loanId = mLoanId
        pd23Data?.customerId = mCustomerId
        CoroutineScope(ioContext).launch {
            try {
                val response = RetrofitFactory.getApiService().savePD23(pd23Data)
                if (response?.isSuccessful == true) {
                    val result = response.body()

                    withContext(Dispatchers.Main) {
//                        activity?.finish()
                        vp_profile.currentItem = 3
                        progressBar.dismmissLoading()
                    }
                    }
                 else {
                    try {
//                        val result: BaseResponseData? = Gson().fromJson(
//                            response?.errorBody()?.string(),
//                            BaseResponseData::class.java
//                        )
                        stopLoading(
                            progressBar,
                            "Smething went wrong with api!!!"/*result?.message*/
                        )
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
}
