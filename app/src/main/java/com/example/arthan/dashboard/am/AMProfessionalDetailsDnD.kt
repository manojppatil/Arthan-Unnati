package com.example.arthan.dashboard.am

import android.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.arthan.R
import com.example.arthan.dashboard.bm.BMDocumentVerificationActivity
import com.example.arthan.lead.model.responsedata.ProfessionalDetailsAM
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.am_professional_details_dnd.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AMProfessionalDetailsDnD : BaseFragment() {
    override fun contentView(): Int {

        return R.layout.am_professional_details_dnd
    }

    override fun init() {
        //not using this for now

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
                        updateData(res.body()!!.professionalDetails)
                        btn_am_pro_next.visibility=View.VISIBLE
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
        btn_am_pro_next.setOnClickListener {

            var dialog = AlertDialog.Builder(context)
            var view: View? = activity?.layoutInflater?.inflate(R.layout.remarks_popup, null)
            dialog.setView(view)
            var et_remarks = view?.findViewById<EditText>(R.id.et_remarks)
            var btn_submit_remark = view?.findViewById<Button>(R.id.btn_submit)
            var btn_cancel = view?.findViewById<Button>(R.id.btn_cancel)
            var alert= dialog.create() as AlertDialog
            alert.show()
            btn_cancel?.setOnClickListener {
                alert.dismiss()
            }
            btn_submit_remark?.setOnClickListener {
                val progress= ProgrssLoader(context!!)
                progress.showLoading()
                val map=HashMap<String,String?>()
                map["amId"]=activity?.intent?.getStringExtra("amId")
                map["remarks"]=et_remarks?.text.toString()
                CoroutineScope(Dispatchers.IO).launch {
                    val res= RetrofitFactory.getApiService().updateProfessionalDetails(map)
                    if(res?.body()!=null)
                    {
                        withContext(Dispatchers.Main)
                        {
                            alert.dismiss()
                            progress.dismmissLoading()
                            (activity as BMDocumentVerificationActivity).moveVPinDataFragment(2)
                            btn_am_pro_next.visibility=View.VISIBLE
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

    }

    fun updateData(professionalDetails: ProfessionalDetailsAM?) {


        spnr_am_eduction.setText(professionalDetails?.educationlevel)
        spnr_am_occupation_name.setText(professionalDetails?.prof)
        et_am_gross_annualincome.setText(professionalDetails?.grossannualIncome)
        et_am_bank_name.setText(professionalDetails?.bankName)
        et_am_account_number.setText(professionalDetails?.acNumber1)
        et_am_ifsc_code.setText(professionalDetails?.ifscCode)
        et_am_UPIid.setText(professionalDetails?.upiId)
    }
}