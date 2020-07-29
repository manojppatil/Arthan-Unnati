package com.example.arthan.dashboard.am

import com.example.arthan.R
import com.example.arthan.lead.model.responsedata.ProfessionalDetailsAM
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.am_professional_details_dnd.*

class AMProfessionalDetailsDnD : BaseFragment() {
    override fun contentView(): Int {

        return R.layout.am_professional_details_dnd
    }

    override fun init() {


    }

    fun updateData(professionalDetails: ProfessionalDetailsAM?) {


        spnr_am_eduction.setText(professionalDetails?.educationlevel)
        spnr_am_occupation_name.setText(professionalDetails?.prof)
        et_am_gross_annualincome.setText(professionalDetails?.grossannualIncome)
        et_am_bank_name.setText(professionalDetails?.bankName)
        et_am_account_number.setText(professionalDetails?.acNumber1)
        et_am_ifsc_code.setText(professionalDetails?.ifscCode)
        et_am_UPIid.setText(professionalDetails?.upiId)
        btn_am_pro_next.setOnClickListener {

        }
    }
}