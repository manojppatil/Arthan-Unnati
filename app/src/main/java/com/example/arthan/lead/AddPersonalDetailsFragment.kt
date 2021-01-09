package com.example.arthan.lead


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.arthan.R
import com.example.arthan.global.AppPreferences
import com.example.arthan.global.Crashlytics
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.lead.model.postdata.PersonalPostData
import com.example.arthan.lead.model.responsedata.PersonalResponseData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.dateSelection
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_add_personal_details.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * A simple [Fragment] subclass.
 */
class AddPersonalDetailsFragment : NavHostFragment(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_personal_details, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        if (intent.hasExtra("PAN_DATA") && intent.getSerializableExtra("PAN_DATA") != null) {
//            val response = intent.getSerializableExtra("PAN_DATA") as CardResponse
//
//            if (!response.results.isNullOrEmpty()) {
//
//                var cardInfo = response.results[0].cardInfo
//                et_name.setText(cardInfo?.name)
//                et_father_name.setText(cardInfo?.fatherName)
//
//                if (cardInfo?.dateType.equals("dob", true))
//                    et_dob.setText(cardInfo?.dateInfo)
//            }
//        }

        loadInitialData()

//        ll_partners?.findViewById<View?>(R.id.remove_button)?.visibility = View.GONE
        btn_next.setOnClickListener {
            savePersonalData()
        }

        et_dob.setOnClickListener {
            dateSelection(context!!, et_dob)
        }
    }

    private suspend fun stopLoading(progressBar: ProgrssLoader?, message: String?) {
        withContext(Dispatchers.Main) {
            progressBar?.dismmissLoading()
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun savePersonalData() {
        val progressBar: ProgrssLoader? = if (context != null) ProgrssLoader(context!!) else null
        progressBar?.showLoading()
        val postBody = PersonalPostData(
            loanId = AppPreferences.getInstance().getString(AppPreferences.Key.LoanId),
            custId = AppPreferences.getInstance().getString(AppPreferences.Key.CustomerId),
            title = (spnr_title?.selectedItem as? Data)?.value ?: "",
            fullName = et_name?.text?.toString() ?: "",
            fatherOrSpousename = et_father_name?.text?.toString() ?: "",
            motherName = et_mother_name?.text?.toString() ?: "",
            dob = et_dob?.text?.toString() ?: "",
            contactNo = contact_number_input?.text?.toString() ?: "",
            email = email_id_input?.text?.toString() ?: "",
            gender = when (rgrp_gender?.checkedRadioButtonId) {
                R.id.male_radio_button -> male_radio_button?.text?.toString() ?: "Male"
                R.id.female_radio_button -> female_radio_button?.text?.toString() ?: "Female"
                R.id.transgender_radio_button -> transgender_radio_button?.text?.toString()
                    ?: "Transgender"
                else -> ""
            },
            nationality = spnr_nationality?.selectedItem as? String ?: "",
            educationlevel = spnr_eduction?.selectedItem as? String ?: "",
            occupationType = spnr_occupation_type?.selectedItem as? String ?: "",
            occupation = spnr_occupation_name?.selectedItem as? String ?: "",
            sourceofIncome = source_of_income_spinner?.selectedItem as? String ?: "",
            grossannualIncome = gross_annual_income_spinner?.selectedItem as? String ?: "",
            addressLine1 = address_line1_input?.text?.toString() ?: "",
            addressLine2 = address_line2_input?.text?.toString() ?: "",
            landmark = landmark_input?.text?.toString() ?: "",
            pinCode = pincode_input?.text?.toString() ?: "",
            areaName = area_name_input?.text?.toString() ?: "",
            city = city_input?.text?.toString() ?: "",
            district = district_input?.text?.toString() ?: "",
            state = state_input?.text?.toString() ?: ""
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitFactory.getApiService().savePersonalDetail(postBody)
                if (response?.isSuccessful == true) {
                    val result = response.body()
                    if (result?.apiCode == "200") {
                        withContext(Dispatchers.Main) {
                            progressBar?.dismmissLoading()
                            AppPreferences.getInstance()
                                .addString(AppPreferences.Key.CustomerId, result.customerId)
                            AppPreferences.getInstance().addString(
                                AppPreferences.Key.PrincipleLoanAmount,
                                result.inPrincipleLnAmt
                            )
                            navController?.navigate(
                                R.id.action_addPersonalDetailsFragment_to_approveConsentFragment,
                                Bundle().also {
                                    it.putString(
                                        ArgumentKey.InPrincipleAmount,
                                        result.inPrincipleLnAmt
                                    )
                                })
//                            ConsentActivity.startMe(
//                                context,
//                                result.inPrincipleLnAmt
//                            )
                        }
                    } else {
                        stopLoading(progressBar, result?.apiDesc)
                    }
                } else {
                    try {
                        val result: PersonalResponseData? = Gson().fromJson(
                            response?.errorBody()?.string(),
                            PersonalResponseData::class.java
                        )
                        stopLoading(progressBar, result?.message)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Crashlytics.log(e.message)

                        stopLoading(progressBar, "Something went wrong. Please try later!")
                    }
                }
            } catch (e: Exception) {
                stopLoading(progressBar, "Something went wrong. Please try later!")
                e.printStackTrace()
                Crashlytics.log(e.message)

            }
        }
    }

    private fun loadInitialData() {
        val progressLoader: ProgrssLoader? = if (context != null) ProgrssLoader(context!!) else null
        progressLoader?.showLoading()
        launch(ioContext) {
            val title = fetchAndUpdateServerTitleAsync().await()
            val nationality = fetchAndUpdateNationalityAsync().await()
            val education = fetchAndUpdateEducationAsync().await()
            val occupationType = fetchAndUpdateOccupationTypeAsync().await()
            val occupationName = fetchAndUpdateOccupationNameAsync().await()
            val sourceOfIncome = fetchAndUpdateSourceOfIncomeAsync().await()
            val annualIncom = fetchAndUpdateGrossAnnualIncomeAsync().await()

            if (title && nationality && education && occupationType && occupationName && sourceOfIncome && annualIncom) {
                withContext(uiContext) {
                    progressLoader?.dismmissLoading()
                }
            }
        }
    }

    private fun getAdapter(list: List<Data>?): DataSpinnerAdapter? =
        if (context != null)
            DataSpinnerAdapter(context!!, list?.toMutableList() ?: mutableListOf()).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            } else null

    private fun fetchAndUpdateServerTitleAsync(): Deferred<Boolean> = async(context = ioContext) {
        try {
            val response = RetrofitFactory.getMasterApiService().getTitle()
            if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                try {
                    withContext(uiContext) {
                        spnr_title?.adapter = getAdapter(response.body()?.data)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Crashlytics.log(e.message)

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Crashlytics.log(e.message)

        }
        return@async true
    }

    private fun fetchAndUpdateNationalityAsync(): Deferred<Boolean> = async(context = ioContext) {
        try {
            val response = RetrofitFactory.getMasterApiService().getNationality()
            if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                withContext(uiContext) {
                    spnr_nationality?.adapter = getAdapter(response.body()?.data)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@async true
    }

    private fun fetchAndUpdateEducationAsync(): Deferred<Boolean> = async(context = ioContext) {
        try {
            val response = RetrofitFactory.getMasterApiService().getEducation()
            if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                withContext(uiContext) {
                    spnr_eduction?.adapter = getAdapter(response.body()?.data)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@async true
    }

    private fun fetchAndUpdateOccupationTypeAsync(): Deferred<Boolean> =
        async(context = ioContext) {
            try {
                val response = RetrofitFactory.getMasterApiService().getOccupationType()
                if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                    withContext(uiContext) {
                        spnr_occupation_type?.adapter = getAdapter(response.body()?.data)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@async true
        }

    private fun fetchAndUpdateOccupationNameAsync(): Deferred<Boolean> =
        async(context = ioContext) {
            try {
                val response = RetrofitFactory.getMasterApiService().getOccupationName()
                if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                    withContext(uiContext) {
                        spnr_occupation_name?.adapter = getAdapter(response.body()?.data)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Crashlytics.log(e.message)

            }
            return@async true
        }

    private fun fetchAndUpdateSourceOfIncomeAsync(): Deferred<Boolean> =
        async(context = ioContext) {
            try {
                val response = RetrofitFactory.getMasterApiService().getIncomeSource()
                if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                    withContext(uiContext) {
                        source_of_income_spinner?.adapter = getAdapter(response.body()?.data)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Crashlytics.log(e.message)

            }
            return@async true
        }

    private fun fetchAndUpdateGrossAnnualIncomeAsync(): Deferred<Boolean> =
        async(context = ioContext) {
            try {
                val response = RetrofitFactory.getMasterApiService().getGrossAnnualIncome()
                if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                    withContext(uiContext) {
                        gross_annual_income_spinner?.adapter = getAdapter(response.body()?.data)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Crashlytics.log(e.message)

            }
            return@async true
        }

}
