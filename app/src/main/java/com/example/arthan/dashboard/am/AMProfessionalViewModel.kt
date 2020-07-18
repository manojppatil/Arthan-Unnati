package com.example.arthan.dashboard.am

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AMProfessionalViewModel : ViewModel() {

    val amgrosAnnualIncome = MutableLiveData<String>("")
    val ambankName = MutableLiveData<String>("")
    val amAccountNumber = MutableLiveData<String>("")
    val amconfAccountNumber = MutableLiveData<String>("")
    val amIfscCode = MutableLiveData<String>("")
    val amUPICode = MutableLiveData<String>("")
}