package com.example.arthan.dashboard.am

import android.widget.Toast
import com.example.arthan.R
import com.example.arthan.lead.model.responsedata.PersonalDetailsAM
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.am_personal_details_dnd.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AMPersonalDetailsDnD : BaseFragment() {
    override fun contentView(): Int {

        return R.layout.am_personal_details_dnd
    }

    override fun init() {
        //not using this for now

        if(arguments!=null&& arguments!!.get("task")=="AMRejected" )
        {
            val progress=ProgrssLoader(context!!)
            progress.showLoading()
            val map=HashMap<String,String?>()
            map["screen"]=arguments!!.getString("screen")
            map["amId"]=arguments!!.getString("amId")
            CoroutineScope(Dispatchers.IO).launch {
                val res=RetrofitFactory.getApiService().getAMScreenData(map)
                if(res?.body()!=null)
                {
                    withContext(Dispatchers.Main)
                    {
                        progress.dismmissLoading()
                        updateData(res.body()!!.personalDetails)

                    }
                }
                else{
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"Try again later",Toast.LENGTH_LONG).show()
                        progress.dismmissLoading()
                    }
                }
            }
        }


    }

    fun updateData(
        personalDetails: PersonalDetailsAM?

    ) {
        txt_full_name.text=personalDetails?.fullName
        txt_dob.text=personalDetails?.dob
        txt_gender.text=personalDetails?.gender
        txt_contact_number.text=personalDetails?.contactNo
        txt_email.text=personalDetails?.email
        txt_father_name.text=personalDetails?.fatherOrSpousename
        txt_mother_name.text=personalDetails?.motherName
        txt_permanent_address1.text=personalDetails?.addressLine1
        txt_permanent_address2.text=personalDetails?.addressLine2
        txt_permanent_address_landmark.text=personalDetails?.landmark
        txt_pincode.text=personalDetails?.pinCode
        txt_city.text=personalDetails?.city
        txt_district.text=personalDetails?.district
        txt_state.text=personalDetails?.state
        nationality.text=personalDetails?.nationality
        eduLevel.text=personalDetails?.educationlevel
        OccupationTypeValue.text=personalDetails?.occupationType
        txt_occupation.text=personalDetails?.occupation
        sourceIncome.text=personalDetails?.sourceofIncome
        grossAnulIncome.text=personalDetails?.grossannualIncome

    }
}