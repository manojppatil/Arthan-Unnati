package com.example.arthan.dashboard.am

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.crashlytics.android.Crashlytics
import com.example.arthan.R
import com.example.arthan.dashboard.am.model.ProfessionPostData
import com.example.arthan.global.ArthanApp
import com.example.arthan.global.DOC_TYPE
import com.example.arthan.global.PROFESSIONAL
import com.example.arthan.lead.UploadDocumentActivity
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.ocr.CardResponse
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.utils.RequestCode
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_am_professional_details.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class AMProfessionalDetailsFragment : BaseFragment(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    override fun contentView() = R.layout.fragment_am_professional_details
    private var navController: NavController? = null
    var crossedCheque: String = ""
    override fun init() {
        Log.i("TAG", "Professional details")
        navController =
            if (activity is AMPersonalDetailsActivity) Navigation.findNavController(
                activity!!,
                R.id.frag_container
            ) else null
        launch(ioContext) {
            fetchAndUpdateEducationAsync().await()
            fetchAndUpdateOccupationNameAsync().await()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController: NavController? =
            if (activity is AMPersonalDetailsActivity) Navigation.findNavController(
                activity!!,
                R.id.frag_container
            ) else null

        btn_upload_check.setOnClickListener {
            startActivityForResult(
                Intent(
                    activity as AMPersonalDetailsActivity,
                    UploadDocumentActivity::class.java
                ).apply {
                    putExtra(DOC_TYPE, RequestCode.CrossedCheque)
                }, RequestCode.CrossedCheque
            )
        }

        btn_am_pro_next.setOnClickListener {
            if (activity is AMPersonalDetailsActivity) {
                (activity as AMPersonalDetailsActivity).enableOthers()
                (activity as AMPersonalDetailsActivity).infoCompleteState(PROFESSIONAL)
                if ((et_am_account_number.text.toString() == et_am_conf_account_number.text.toString())) {
                    if (crossedCheque != null) {
                        saveProfessionalData()
                    } else {
                        Toast.makeText(context, "Pls upload Cheque", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "Pls enter correct A/C No.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    lateinit var profession: ArrayList<String>
    private fun saveProfessionalData() {
        profession = ArrayList()
        profession.add((spnr_am_occupation_name?.selectedItem as? Data)?.value ?: "")
        val progressBar: ProgrssLoader? = if (context != null) ProgrssLoader(context!!) else null
        progressBar?.showLoading()
        val postBody = ProfessionPostData(
            educatiolevel = (spnr_am_eduction?.selectedItem as? Data)?.value ?: "",
//            profession = profession.[(spnr_am_occupation_name?.selectedItem as? Data)?.value ?: ""],
            profession = profession,
            acNumber1 = et_am_account_number?.text.toString() ?: "",
            acNumber2 = et_am_conf_account_number?.text.toString() ?: "",
            upiId = et_am_account_number?.text.toString() ?: "",
            grossannualIncome = et_am_gross_annualincome?.text.toString() ?: "",
            checqueUrl = crossedCheque,
            amId = ArthanApp.getAppInstance().loginUser
        )
        CoroutineScope(ioContext).launch {
            try {
                val response =
                    RetrofitFactory.getApiService().saveAMProfessionalDetails(postBody)
                if (response?.isSuccessful == true) {
                    val result = response.body()
                    if (result?.apiCode == "200") {
                        withContext(uiContext) {
                            progressBar?.dismmissLoading()
                            navController?.navigate(R.id.action_professional_to_others)
                        }
                    }
                } else {
                    progressBar?.dismmissLoading()
                    Toast.makeText(
                        context,
                        response?.body()?.apiCode + "    Something went wrong. Please try later!",
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

    private fun fetchAndUpdateEducationAsync(): Deferred<Boolean> = async(context = ioContext) {
        try {
            val response = RetrofitFactory.getMasterApiService().getEducation()
            if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                withContext(uiContext) {
                    spnr_am_eduction?.adapter = getAdapter(response.body()?.data)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Crashlytics.log(e.message)

        }
        return@async true
    }


    private fun fetchAndUpdateOccupationNameAsync(): Deferred<Boolean> =
        async(context = ioContext) {
            try {
                val response = RetrofitFactory.getMasterApiService().getamOccupationName()
                if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                    withContext(uiContext) {
                        spnr_am_occupation_name?.adapter = getAdapter(response.body()?.data)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCode.CrossedCheque -> {

                data?.let {
                    val applicantData: CardResponse? =
                        it.getParcelableExtra(ArgumentKey.CrossedCheque) as? CardResponse

                    crossedCheque =
                        applicantData?.cardFrontUrl!!
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
}