package com.example.arthan.dashboard.am

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.crashlytics.android.Crashlytics
import com.example.arthan.R
import com.example.arthan.dashboard.am.model.AMPersonalDetailsData
import com.example.arthan.global.AppPreferences
import com.example.arthan.global.ArthanApp
import com.example.arthan.global.PERSONAL
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.lead.model.postdata.KYCPostData
import com.example.arthan.lead.model.postdata.PersonalDetails
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.utils.dateSelection
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.activity_personal_information.*
import kotlinx.android.synthetic.main.activity_personal_information.cb_sameAddress
import kotlinx.android.synthetic.main.activity_personal_information.sameAsAddressLL
import kotlinx.android.synthetic.main.fragment_am_personal_information.*
import kotlinx.android.synthetic.main.fragment_am_personal_information.address1_line1_input
import kotlinx.android.synthetic.main.fragment_am_personal_information.address1_line2_input
import kotlinx.android.synthetic.main.fragment_am_personal_information.address_line1_input
import kotlinx.android.synthetic.main.fragment_am_personal_information.address_line2_input
import kotlinx.android.synthetic.main.fragment_am_personal_information.area_name1_input
import kotlinx.android.synthetic.main.fragment_am_personal_information.area_name_input
import kotlinx.android.synthetic.main.fragment_am_personal_information.city1_input
import kotlinx.android.synthetic.main.fragment_am_personal_information.city_input
import kotlinx.android.synthetic.main.fragment_am_personal_information.district_input
import kotlinx.android.synthetic.main.fragment_am_personal_information.district_input1
import kotlinx.android.synthetic.main.fragment_am_personal_information.email_id_input
import kotlinx.android.synthetic.main.fragment_am_personal_information.female_radio_button
import kotlinx.android.synthetic.main.fragment_am_personal_information.landmark1_input
import kotlinx.android.synthetic.main.fragment_am_personal_information.landmark_input
import kotlinx.android.synthetic.main.fragment_am_personal_information.male_radio_button
import kotlinx.android.synthetic.main.fragment_am_personal_information.pincode1_input
import kotlinx.android.synthetic.main.fragment_am_personal_information.pincode_input
import kotlinx.android.synthetic.main.fragment_am_personal_information.rgrp_gender
import kotlinx.android.synthetic.main.fragment_am_personal_information.transgender_radio_button
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class AMPersonaldetailsfragment : BaseFragment(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    override fun contentView() = R.layout.fragment_am_personal_information
    var mKYCPostData: KYCPostData? = null
    override fun init() {

        launch(ioContext) {
            fetchAndUpdateStateNameAsync().await()
        }

        cb_sameAddress.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) {
                sameAsAddressLL.visibility = View.VISIBLE
            } else {
                sameAsAddressLL.visibility = View.GONE
            }
        }
        cb_sameWhatsApp.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) {
                whatsapp_number_input.visibility = VISIBLE
            } else {
                whatsapp_number_input.visibility = GONE
            }
        }
        et_am_dob.setOnClickListener {
            dateSelection(activity as AMPersonalDetailsActivity, et_dob)
        }
    }

    private var navController: NavController? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController =
            if (activity is AMPersonalDetailsActivity) Navigation.findNavController(
                activity!!,
                R.id.frag_container
            ) else null

        mKYCPostData = (activity as AMPersonalDetailsActivity).mKYCPostData

        et_am_name.setText(mKYCPostData?.panFirstname.toString())
        et_am_pan.setText(mKYCPostData?.panId.toString())
        et_am_aadhar_number.setText(mKYCPostData?.aadharId.toString())
        et_am_dob.setText(mKYCPostData?.panDob.toString())
        et_am_aadhar_number.setText(AppPreferences.getInstance().getString(AppPreferences.Key.AadharId))
        address_line1_input.setText(AppPreferences.getInstance().getString(AppPreferences.Key.AddressLine1))
        address1_line2_input.setText(AppPreferences.getInstance().getString(AppPreferences.Key.AddressLine2))
        pincode_input.setText(AppPreferences.getInstance().getString(AppPreferences.Key.Pincode))
        city_input.setText(AppPreferences.getInstance().getString(AppPreferences.Key.City))
        district_input.setText(AppPreferences.getInstance().getString(AppPreferences.Key.City))
//        state_input.setText(AppPreferences.getInstance().getString(AppPreferences.Key.State))
        et_am_contactno.setText(AppPreferences.getInstance().getString("amMobNo"))
        btn_am_next.setOnClickListener {

            if (et_am_name.length() > 0 && et_am_pan.length() > 0 && et_am_aadhar_number.length() > 0 && et_am_dob.length() > 0 && et_am_contactno.length() > 0 && (whatsapp_number_input.length() >= 0 && !cb_sameWhatsApp.isChecked )&& email_id_input.length() > 0 &&
                address_line1_input.length() > 0  && pincode_input.length() > 0 && city_input.length() > 0 && district_input.length() > 0

            ) {
                if (activity is AMPersonalDetailsActivity) {
                    (activity as AMPersonalDetailsActivity).enableProfessional()
                    (activity as AMPersonalDetailsActivity).infoCompleteState(PERSONAL)
                    savePersonalData("AM")
                }
            } else {
                Toast.makeText(context, "Please fill all the details", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun savePersonalData(applicantType: String) {

        val progressBar: ProgrssLoader? = if (context != null) ProgrssLoader(context!!) else null
        progressBar?.showLoading()
        val postBody = AMPersonalDetailsData(
            //    applicantPanNo = mKYCPostData?.panId,
//            title = (spnr_ti tle?.selectedItem as? Data)?.value ?: "",
            fullName = et_am_name?.text?.toString() ?: "",

//            aadharno = et_aadhar_number?.text?.toString() ?: "",
            dob = et_am_dob?.text?.toString() ?: "",
            contactNo = et_am_contactno?.text?.toString() ?: "",
            whatsappNo = whatsapp_number_input?.text?.toString() ?: "",
            email = email_id_input?.text?.toString() ?: "",
            gender = when (rgrp_gender?.checkedRadioButtonId) {
                R.id.male_radio_button -> male_radio_button?.text?.toString() ?: "Male"
                R.id.female_radio_button -> female_radio_button?.text?.toString() ?: "Female"
                R.id.transgender_radio_button -> transgender_radio_button?.text?.toString()
                    ?: "Transgender"
                else -> ""
            },
            addressLine1 = address_line1_input?.text?.toString() ?: "",
            addressLine2 = address_line2_input?.text?.toString() ?: "",
            landmark = landmark_input?.text?.toString() ?: "",
            pinCode = pincode_input?.text?.toString() ?: "",
            areaName = area_name_input?.text?.toString() ?: "",
            city = city_input?.text?.toString() ?: "",
            district = district_input?.text?.toString() ?: "",
            state = (spnr_am_state?.selectedItem as? Data)?.value ?: "",
            addressLine1p = address1_line1_input?.text?.toString() ?: "",
            addressLine2p = address1_line2_input?.text?.toString() ?: "",
            landmarkp = landmark1_input?.text?.toString() ?: "",
            pinCodep = pincode1_input?.text?.toString() ?: "",
            areaNameP = area_name1_input?.text?.toString() ?: "",
            cityp = city1_input?.text?.toString() ?: "",
            districtp = district_input1?.text?.toString() ?: "",
            statep = (spnr_am_state1?.selectedItem as? Data)?.value ?: "",
            addressProofUrl = "addressproofUrl",
            amId = ArthanApp.getAppInstance().loginUser
        )

        CoroutineScope(ioContext).launch {
            try {
                val response =
                    RetrofitFactory.getApiService().saveAMPersonalDetail(postBody)
                if (response?.isSuccessful == true) {
                    val result = response.body()
                    if (result?.apiCode == "200") {
                        withContext(uiContext) {
                            progressBar?.dismmissLoading()
                            navController?.navigate(R.id.action_personal_to_professional)
                        }
                    }
                } else {
                    progressBar?.dismmissLoading()
                    Toast.makeText(
                        context,
                        response?.body()?.apiCode + "Something went wrong. Please try later!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                if (progressBar != null) {
                    stopLoading(progressBar, "Something went wrong. Please try later!")
                }
                e.printStackTrace()
                Crashlytics.log(e.message)

            }
        }
    }

    private suspend fun stopLoading(progressBar: ProgrssLoader, message: String?) {
        withContext(uiContext) {
            progressBar.dismmissLoading()
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun fetchAndUpdateStateNameAsync(): Deferred<Boolean> =
        async(context = ioContext) {
            try {
                val response = RetrofitFactory.getMasterApiService().getamStates()
                if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                    withContext(uiContext) {
                        spnr_am_state?.adapter = getAdapter(response.body()?.data)
                        spnr_am_state1?.adapter = getAdapter(response.body()?.data)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Crashlytics.log(e.message)

            }
            return@async true
        }

    private fun getAdapter(list: List<Data>?): DataSpinnerAdapter =
        DataSpinnerAdapter(requireActivity(), list?.toMutableList() ?: mutableListOf()).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

    fun updateData(
        personalDetails: List<PersonalDetails>?,
        inPrincipleAmt: String?,
        loanAmt: String?,
        roi: String?,
        tenure: String?
    ) {


    }

}