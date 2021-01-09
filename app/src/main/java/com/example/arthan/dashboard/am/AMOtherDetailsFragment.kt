package com.example.arthan.dashboard.am

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.addCallback
import com.example.arthan.R
import com.example.arthan.dashboard.am.model.AMOtherdetailsPostData
import com.example.arthan.dashboard.am.model.Languages
import com.example.arthan.dashboard.am.model.References
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.global.ArthanApp
import com.example.arthan.global.Crashlytics
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.lead.model.postdata.CollateralDetailsPostData
import com.example.arthan.lead.model.postdata.NeighborReference
import com.example.arthan.lead.model.postdata.TradeRefDetail
import com.example.arthan.lead.model.responsedata.OtherDetailsList
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_am_otherdetails.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class AMOtherDetailsFragment : BaseFragment(), CoroutineScope {

    override fun contentView() = R.layout.fragment_am_otherdetails
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    override fun init() {
        Log.i("TAG", "other details...")
        launch(ioContext) {
            fetchAndUpdateOccupationNameAsync().await()
        }
        if(arguments!=null&& arguments!!.get("task")=="AMRejected" )
        {
            val progress= ProgrssLoader(context!!)
            progress.showLoading()
            val map=HashMap<String,String?>()
            map["screen"]=arguments!!.getString("screen")
            map["amId"]=arguments!!.getString("amId")
            CoroutineScope(Dispatchers.IO).launch {
                val res= RetrofitFactory.getApiService().getAMScreenData(map)
                if(res?.body()!=null)
                {
                    withContext(Dispatchers.Main)
                    {
                        progress.dismmissLoading()
                        updateData(res.body()!!.otherDetails)
                        et_remarks_am.setText(res.body()!!.otherDetails.remarks)
                        btn_am_submit.visibility=View.VISIBLE
                    }
                }
                else{
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"Try again later", Toast.LENGTH_LONG).show()
                        progress.dismmissLoading()
                    }
                }
            }
        }
    }
    var CheckBox.checked: Boolean
        get() = isChecked
        set(value) {
            if(isChecked != value) {
                isChecked = value
                jumpDrawablesToCurrentState()
            }
        }
    private fun updateData(otherDetails: OtherDetailsList) {

        if(otherDetails!=null) {
            if(otherDetails.languages.size>=1) {
                tv_am_lang1.text = otherDetails.languages[0].lang
                cb_lang1_read.checked = otherDetails.languages[0].read=="true"
                cb_lang1_write.checked = otherDetails.languages[0].write =="true"
                cb_lang1_speak.checked = otherDetails.languages[0].speak=="true"
            }
            if(otherDetails.languages.size>=2) {

                tv_am_lang2.text = otherDetails.languages[1].lang
                cb_lang2_read.checked = otherDetails.languages[1].read=="true"
                cb_lang2_write.checked = otherDetails.languages[1].write =="true"
                cb_lang2_speak.checked = otherDetails.languages[1].speak=="true"
            }
            if(otherDetails.languages.size==3) {

                tv_am_lang3.text = otherDetails.languages[2].lang
                cb_lang3_read.checked = otherDetails.languages[2].read =="true"
                cb_lang3_write.checked = otherDetails.languages[2].write =="true"
                cb_lang3_speak.checked = otherDetails.languages[2].speak =="true"
            }
            rb_yes.isChecked=otherDetails.smartphone =="Yes"
            rb_no.isChecked=otherDetails.smartphone =="No"
            rb_tw_yes.isChecked=otherDetails.twoWheeler =="Yes"
            rb_tw_no.isChecked=otherDetails.twoWheeler =="No"

            et_am_ref1_name.setText(otherDetails.references[0].name)
            et_am_ref1_mno.setText(otherDetails.references[0].mobNo)
            et_am_ref1_address.setText(otherDetails.references[0].address)


            et_am_ref1_comments.setText(otherDetails.references[0].comments)

            et_am_ref2_comments.setText(otherDetails.references[1].comments)
            et_am_ref2_name.setText(otherDetails.references[1].name)
            et_am_ref2_mno.setText(otherDetails.references[1].mobNo)
            et_am_ref2_address.setText(otherDetails.references[1].address)

            val list =
                (spnr_am_profession?.adapter as? DataSpinnerAdapter)?.list
            if(list!=null&&list.isNotEmpty())
            {
                for (i in 0 until list.size)
                {
                    if(list[i].value==otherDetails.references[0].profession)
                    {
                        spnr_am_profession.setSelection(i)

                    }

                }
            }
            val list2 =
                (spnr_am_ref2_profession?.adapter as? DataSpinnerAdapter)?.list
            if(list2!=null&&list2.isNotEmpty())
            {
                for (i in 0 until list2.size)
                {
                    if(list2[i].value==otherDetails.references[1].profession)
                    {
                        spnr_am_ref2_profession.setSelection(i)

                    }

                }
            }
           /* spnr_am_ref2_profession.setText(otherDetails.references[1].profession)
            spnr_am_profession.setText(otherDetails.references[0].profession)*/


        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*btn_am_submit.setOnClickListener {
            Toast.makeText(context,"Am Data Submitted successfully",Toast.LENGTH_LONG)
            startActivity(Intent(activity, RMDashboardActivity::class.java))
        }*/
        btn_am_submit.setOnClickListener { saveOtherDetailsData() }

    }

    lateinit var am_otherlanguages: ArrayList<Languages>
    lateinit var am_otherreferences: ArrayList<References>
    private fun saveOtherDetailsData() {

        if(et_am_ref1_name.length()>0&&et_am_ref1_mno.length()>0&&et_am_ref1_address.length()>0&&et_am_ref1_comments.length()>0&&et_am_ref2_name.length()>0&&et_am_ref2_mno.length()>0
            &&et_am_ref2_address.length()>0&&et_am_ref2_comments.length()>0) {
            am_otherlanguages = ArrayList()
            am_otherlanguages.add(
                Languages(
                    lang = tv_am_lang1.text.toString() ?: "",
                    speak = cb_lang1_speak.isChecked.toString() ?: "Yes",
                    read = cb_lang1_read.isChecked.toString() ?: "Yes",
                    write = cb_lang1_write.isChecked.toString() ?: "Yes"
                )
            )
            am_otherlanguages.add(
                Languages(
                    lang = tv_am_lang2.text.toString() ?: "",
                    speak = cb_lang2_speak.isChecked.toString() ?: "Yes",
                    read = cb_lang2_read.isChecked.toString() ?: "Yes",
                    write = cb_lang2_write.isChecked.toString() ?: "Yes"
                )
            )
            am_otherlanguages.add(
                Languages(
                    lang = tv_am_lang3.text.toString() ?: "",
                    speak = cb_lang3_speak.isChecked.toString() ?: "Yes",
                    read = cb_lang3_read.isChecked.toString() ?: "Yes",
                    write = cb_lang3_write.isChecked.toString() ?: "Yes"
                )
            )

            am_otherreferences = ArrayList()
            am_otherreferences.add(
                References(
                    name = et_am_ref1_name.text.toString() ?: "",
                    mobNo = et_am_ref1_mno.text.toString() ?: "",
                    address = et_am_ref1_address.text.toString() ?: "",
                    profession = (spnr_am_profession?.selectedItem as? Data)?.value ?: "",
                    comments = et_am_ref1_comments.text.toString() ?: ""
                )
            )
            am_otherreferences.add(
                References(
                    name = et_am_ref2_name.text.toString() ?: "",
                    mobNo = et_am_ref2_mno.text.toString() ?: "",
                    address = et_am_ref2_address.text.toString() ?: "",
                    profession = (spnr_am_ref2_profession?.selectedItem as? Data)?.value ?: "",
                    comments = et_am_ref2_comments.text.toString() ?: ""
                )
            )
//        profession.add((spnr_am_occupation_name?.selectedItem as? Data)?.value ?: "")
            val progressBar: ProgrssLoader? =
                if (context != null) ProgrssLoader(context!!) else null
            progressBar?.showLoading()
            val smartphone = when (rgrp_ownphone?.checkedRadioButtonId) {
                R.id.rb_yes -> rb_yes?.text?.toString() ?: "Yes"
                R.id.rb_no -> rb_no?.text?.toString() ?: "no"
                else -> ""
            }
            val twoWheeler = when (rgrp_twowheeler?.checkedRadioButtonId) {
                R.id.rb_tw_yes -> rb_yes?.text?.toString() ?: "Yes"
                R.id.rb_tw_no -> rb_no?.text?.toString() ?: "no"
                else -> ""
            }
            Log.d("creat", "" + smartphone + "" + twoWheeler);
            val postBody = AMOtherdetailsPostData(
                smartphone = smartphone,
                twoWheeler = twoWheeler,
                languages = am_otherlanguages,
                references = am_otherreferences,
                amId = ArthanApp.getAppInstance().loginUser
            )
            CoroutineScope(ioContext).launch {
                try {
                    val response =
                        RetrofitFactory.getApiService().saveAMOtherDetails(postBody)
                    if (response?.isSuccessful == true) {
                        val result = response.body()
                        if (result?.apiCode == "200") {
                            withContext(uiContext) {
                                progressBar?.dismmissLoading()
                                Toast.makeText(
                                    activity,
                                    "enrolment is complete",
                                    Toast.LENGTH_LONG
                                ).show()
                                if (arguments != null && arguments!!.get("task") == "AMRejected"){
                                    Toast.makeText(context, "Re-submitted case.", Toast.LENGTH_LONG).show()
                                    activity!!.finish()
                            }else{
                                startActivity(Intent(activity, RMDashboardActivity::class.java))
                            }
                            }
                        }
                    }
                } catch (e: Exception) {
                    if (progressBar != null) {
                        stopLoading(progressBar, "Something went wrong. Please try later!")
                    }
                    e.printStackTrace()
                    Crashlytics.log(e.message)

                }
            }
        }else
        {
            Toast.makeText(activity,"Please fill all the details",Toast.LENGTH_LONG).show()
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

    private fun fetchAndUpdateOccupationNameAsync(): Deferred<Boolean> =
        async(context = ioContext) {
            try {
                val response = RetrofitFactory.getMasterApiService().getamOccupationName()
                if (response?.isSuccessful == true && response.body()?.errorCode?.toInt() == 200) {
                    withContext(uiContext) {
                        spnr_am_profession?.adapter = getAdapter(response.body()?.data)
                        spnr_am_ref2_profession?.adapter = getAdapter(response.body()?.data)
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
        neighborRefDetails: List<NeighborReference>?,
        tradeRefDetails: List<TradeRefDetail>?,
        collateralDetails: CollateralDetailsPostData?,
        loanId: String?,
        loanType: String?,
        otherComments: String?
    ) {


    }
}