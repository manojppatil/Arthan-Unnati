package com.example.arthan.dashboard.bm.fragment

import com.example.arthan.R
import com.example.arthan.lead.model.responsedata.IdAddrData
import com.example.arthan.lead.model.responsedata.ipAddressVO
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_individual_id_address.*

class IndividualIdAddressFragment: BaseFragment() {

    override fun contentView()= R.layout.fragment_individual_id_address

    override fun init() {

        val ipAddData:ipAddressVO?= activity?.intent?.extras?.getParcelable<ipAddressVO>("idData")

        var loanId=ipAddData?.loanId
        var userData=ipAddData?.idAddrData?.get(0)
        txt_applicant_value.text=userData?.fullName
        txt_applicant_dob_value.text=userData?.dob
        pancardLabel.text=userData?.idText
        panCardValue.text=userData?.idVal
        AddProofLabel.text=userData?.addressText
        addreValue.text=userData?.addressVal

    }
}