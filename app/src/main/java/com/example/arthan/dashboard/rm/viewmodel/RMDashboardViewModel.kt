package com.example.arthan.dashboard.rm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.arthan.model.*
import com.example.arthan.network.RetrofitFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class RMDashboardViewModel: ViewModel() {

    fun loadRMDashboard(): LiveData<RMDashboardData?>{

        val response= MutableLiveData<RMDashboardData?>()

        try {

            CoroutineScope(Dispatchers.IO).launch {
                val respo = RetrofitFactory.getRMServiceService()
                    .getRMDashboardData(RMDashboardRequest("R1234", ""))
                if (respo.isSuccessful && respo.body() != null) {
                    withContext(Dispatchers.Main) {
                        response.value = respo.body()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        response.value = null
                    }
                }
            }
        } catch (e: Exception){
            response.value = null
        }

        return response
    }

    fun loadScreeningList(): LiveData<List<ScreeningData>>{

        val response= MutableLiveData<List<ScreeningData>>()

        try {

            CoroutineScope(Dispatchers.IO).launch {
                val respo = RetrofitFactory.getRMServiceService().getScreeningList(
                    RMDashboardRequest(
                        "RM1",
                        SCREENING_SECTION
                    )
                )
                if (respo.isSuccessful && respo.body() != null) {
                    withContext(Dispatchers.Main) {
                        response.value = respo.body()?.screeningList
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        response.value = null
                    }
                }
            }
        } catch (e: Exception){
            response.value = null
        }

        return response
    }

    fun loadLeadList(): LiveData<List<LeadData>>{

        val response= MutableLiveData<List<LeadData>>()

        try{

        CoroutineScope(Dispatchers.IO).launch {
            val respo= RetrofitFactory.getRMServiceService().getLeadList(RMDashboardRequest("R1234",
                LEAD_SECTION))
            if(respo.isSuccessful && respo.body() != null){
                withContext(Dispatchers.Main){
                    response.value= respo.body()?.leadList
                }
            } else {
                withContext(Dispatchers.Main){
                    response.value= null
                }
            }
        }
        } catch (e: Exception){
            response.value = null
        }

        return response
    }

    fun loadReassignLeadList(): LiveData<List<ReassignLeadData>>{

        val response= MutableLiveData<List<ReassignLeadData>>()

        try{

        CoroutineScope(Dispatchers.IO).launch {
            val respo= RetrofitFactory.getRMServiceService().getReassignList(RMDashboardRequest("R1234",
                REASSIGN_LEAD_SECTION))
            if(respo.isSuccessful && respo.body() != null){
                withContext(Dispatchers.Main){
                    response.value= respo.body()?.reAssignList
                }
            } else {
                withContext(Dispatchers.Main){
                    response.value= null
                }
            }
        }
        } catch (e: Exception){
            response.value = null
        }

        return response
    }

    fun loadApprovedList(): LiveData<List<ApprovedCaseData>>{

        val response= MutableLiveData<List<ApprovedCaseData>>()

        try{

        CoroutineScope(Dispatchers.IO).launch {
            val respo= RetrofitFactory.getApiService().getApprovedList(RMDashboardRequest("R1234",
                APPROVED_SECTION))
            if(respo.isSuccessful && respo.body() != null){
                withContext(Dispatchers.Main){
                    response.value= respo.body()?.approvedCases
                }
            } else {
                withContext(Dispatchers.Main){
                    response.value= null
                }
            }
        }
        } catch (e: Exception){
            response.value = null
        }

        return response
    }

    fun loadRejectedList(): LiveData<List<RejectedCaseData>>{

        val response= MutableLiveData<List<RejectedCaseData>>()

        try{

        CoroutineScope(Dispatchers.IO).launch {
            val respo= RetrofitFactory.getRMServiceService().getRejectedCases(RMDashboardRequest("R1234",
                REJECTED_SECTION))
            if(respo.isSuccessful && respo.body() != null){
                withContext(Dispatchers.Main){
                    response.value= respo.body()?.rejectedList
                }
            } else {
                withContext(Dispatchers.Main){
                    response.value= null
                }
            }
        }
        } catch (e: Exception){
            response.value = null
        }

        return response
    }

    fun loadToDisbursedList(): LiveData<List<ToDisbursedData>>{

        val response= MutableLiveData<List<ToDisbursedData>>()

        try{

        CoroutineScope(Dispatchers.IO).launch {
            val respo= RetrofitFactory.getRMServiceService().getToDisbursedCases(RMDashboardRequest("R1234",
                TODISBURSED_SECTION))
            if(respo.isSuccessful && respo.body() != null){
                withContext(Dispatchers.Main){
                    response.value= respo.body()?.tobDisbList
                }
            } else {
                withContext(Dispatchers.Main){
                    response.value= null
                }
            }
        }
        } catch (e: Exception){
            response.value = null
        }

        return response
    }


    fun loadRMReviewList(): LiveData<List<RMReviewData>>{

        val response= MutableLiveData<List<RMReviewData>>()

        try{

            CoroutineScope(Dispatchers.IO).launch {
                val respo= RetrofitFactory.getRMServiceService().getRMReviewList(RMDashboardRequest("R1234",
                    RM_REVIEW_SECTION))
                if(respo.isSuccessful && respo.body() != null){
                    withContext(Dispatchers.Main){
                        response.value= respo.body()?.rmReviewList
                    }
                } else {
                    withContext(Dispatchers.Main){
                        response.value= null
                    }
                }
            }
        } catch (e: Exception){
            response.value = null
        }

        return response
    }
}

