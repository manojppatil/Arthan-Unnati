package com.example.arthan.views.fragments

import android.view.View
import com.example.arthan.R
import com.example.arthan.lead.model.postdata.PersonalDetails
import kotlinx.android.synthetic.main.fragment_personal_details.*

class PersonalDetailFragment : BaseFragment() {

    override fun contentView() = R.layout.fragment_personal_details

    override fun init() {

        PAView.setOnClickListener {

            if (PAInner.visibility == View.VISIBLE) {
                PAInner.visibility = View.GONE
            } else {
                PAInner.visibility = View.VISIBLE
            }
            GInnerView.visibility = View.GONE
            CAInner.visibility = View.GONE
        }
        CAView.setOnClickListener {
            if (CAInner.visibility == View.VISIBLE) {
                CAInner.visibility = View.GONE
            } else {
                CAInner.visibility = View.VISIBLE
            }
            PAInner.visibility = View.GONE
            GInnerView.visibility = View.GONE
        }
        GView.setOnClickListener {
            if (GInnerView.visibility == View.VISIBLE) {
                GInnerView.visibility = View.GONE
            } else {
                GInnerView.visibility = View.VISIBLE
            }
            PAInner.visibility = View.GONE
            CAInner.visibility = View.GONE
        }
    }

    fun updateData(
        personalDetailsList: List<PersonalDetails>?,
        inPrincipleAmtValue: String?,
        loanAmt: String?,
        roi: String?,
        tenure: String?
    ) {
        inPrincipleAmtTextView.text = "In-Principle Eligible Amount: $inPrincipleAmtValue"
        LoanAmt.text = "Loan Amount: $loanAmt"
        Tenure.text = "Tenure : $tenure"
        ROI.text = "ROI : $roi"

        if (personalDetailsList?.get(0) != null) {
            var personalDetails = personalDetailsList?.get(0)
            txt_full_name?.text = personalDetails?.fullName
            txt_dob?.text = personalDetails?.dob
            txt_gender?.text = personalDetails?.gender
            txt_contact_number?.text = personalDetails?.contactNo
            txt_email?.text = personalDetails?.email
            txt_father_name?.text = personalDetails?.fatherOrSpousename
            txt_mother_name?.text = personalDetails?.motherName
            txt_permanent_address1.text = personalDetails?.addressLine1
            txt_permanent_address2.text = personalDetails?.addressLine2
            txt_permanent_address_landmark.text = personalDetails?.landmark
            txt_pincode.text = personalDetails?.pinCode
            txt_area_name.text = personalDetails?.areaName
            txt_city.text = personalDetails?.city
            txt_district.text = personalDetails?.district
            txt_state.text = personalDetails?.state
            nationality.text = personalDetails?.nationality
            eduLevel.text = personalDetails?.educationlevel
            OccupationTypeValue.text = personalDetails?.occupationType
            txt_occupation.text = personalDetails?.occupation
            sourceIncome.text = personalDetails?.sourceofIncome
            grossAnulIncome.text = personalDetails?.grossannualIncome
        }
        if (personalDetailsList!!.size > 1 && personalDetailsList?.get(1) != null) {
            var personalDetails = personalDetailsList?.get(1)
            txt_full_nameCA?.text = personalDetails?.fullName
            txt_dobCA?.text = personalDetails?.dob
            txt_genderCA?.text = personalDetails?.gender
            txt_contact_numberCA?.text = personalDetails?.contactNo
            txt_emailCA?.text = personalDetails?.email
            txt_father_nameCA?.text = personalDetails?.fatherOrSpousename
            txt_mother_nameCA?.text = personalDetails?.motherName
            txt_permanent_address1CA.text = personalDetails?.addressLine1
            txt_permanent_address2CA.text = personalDetails?.addressLine2
            txt_permanent_address_landmarkCA.text = personalDetails?.landmark
            txt_pincodeCA.text = personalDetails?.pinCode
            txt_area_nameCA.text = personalDetails?.areaName
            txt_cityCA.text = personalDetails?.city
            txt_districtCA.text = personalDetails?.district
            txt_stateCA.text = personalDetails?.state
            nationalityCA.text = personalDetails?.nationality
            eduLevelCA.text = personalDetails?.educationlevel
            OccupationTypeValueCA.text = personalDetails?.occupationType
            txt_occupationCA.text = personalDetails?.occupation
            sourceIncomeCA.text = personalDetails?.sourceofIncome
            grossAnulIncomeCA.text = personalDetails?.grossannualIncome
        } else {
            CAView.visibility = View.GONE
        }
        if (personalDetailsList!!.size > 2 && personalDetailsList?.get(2) != null) {
            var personalDetails = personalDetailsList?.get(2)
            txt_full_nameG?.text = personalDetails?.fullName
            txt_dobG?.text = personalDetails?.dob
            txt_genderG?.text = personalDetails?.gender
            txt_contact_numberG?.text = personalDetails?.contactNo
            txt_emailG?.text = personalDetails?.email
            txt_father_nameG?.text = personalDetails?.fatherOrSpousename
            txt_mother_nameG?.text = personalDetails?.motherName
            txt_permanent_address1G.text = personalDetails?.addressLine1
            txt_permanent_address2G.text = personalDetails?.addressLine2
            txt_permanent_address_landmarkG.text = personalDetails?.landmark
            txt_pincodeG.text = personalDetails?.pinCode
            txt_area_nameG.text = personalDetails?.areaName
            txt_cityG.text = personalDetails?.city
            txt_districtG.text = personalDetails?.district
            txt_stateG.text = personalDetails?.state
            nationalityG.text = personalDetails?.nationality
            eduLevelG.text = personalDetails?.educationlevel
            OccupationTypeValueG.text = personalDetails?.occupationType
            txt_occupationG.text = personalDetails?.occupation
            sourceIncomeG.text = personalDetails?.sourceofIncome
            grossAnulIncomeG.text = personalDetails?.grossannualIncome

        } else {
            GView.visibility = View.GONE
        }

    }
}