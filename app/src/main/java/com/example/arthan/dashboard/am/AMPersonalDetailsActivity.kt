package com.example.arthan.dashboard.am

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import com.example.arthan.R
import com.example.arthan.global.OTHERS
import com.example.arthan.global.PERSONAL
import com.example.arthan.global.PROFESSIONAL
import com.example.arthan.lead.model.postdata.KYCPostData
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_am_personal_information.*

class AMPersonalDetailsActivity : BaseActivity() {
    var mKYCPostData: KYCPostData? = null
    var navController: NavController? = null
    var aadharNo: String = ""
    override fun contentView() = R.layout.activity_am_personal_information

    override fun init() {
        Log.i("AMPersonalDetailsAct", "Personal details.... ")
        navController = Navigation.findNavController(this, R.id.frag_container)

        if (intent.hasExtra("PAN_DATA")) {
            mKYCPostData = intent.getParcelableExtra("PAN_DATA") as? KYCPostData
            aadharNo = intent.getStringExtra("AADHAR_NO")
            Log.i("KYC Data", mKYCPostData?.panFirstname)
        }
//        val bundle  = Bundle()
//        bundle.putParcelable("PAN_DATA", mKYCPostData)
//        NavHostFragment.create(R.navigation.nav_personal_info,bundle)
    }

    fun infoCompleteState(type: String) {
        when (type) {
            PERSONAL -> {
                txt_personal.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    R.drawable.ic_info_completed,
                    0,
                    0
                )
            }
            PROFESSIONAL -> {
                txt_professional.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    R.drawable.ic_info_completed,
                    0,
                    0
                )
            }
            OTHERS -> {
                txt_others.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    R.drawable.ic_info_completed,
                    0,
                    0
                )
            }
        }
    }

    fun infoInCompleteState(type: String) {
        when (type) {
            PERSONAL -> {
                txt_personal.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    R.drawable.ic_personal_info,
                    0,
                    0
                )
            }
            PROFESSIONAL -> {
                txt_professional.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    R.drawable.ic_business_info,
                    0,
                    0
                )
            }
            OTHERS -> {
                txt_others.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    R.drawable.ic_income_info,
                    0,
                    0
                )
            }
        }
    }

    fun enableProfessional() {
        vw_professional.visibility = View.GONE
    }

    fun enableOthers() {
        vw_others.visibility = View.GONE
    }


    override fun onToolbarBackPressed() = onBackPressed()

    override fun screenTitle() = "Personal Details"

    override fun onBackPressed() {
        val navController: NavController? = Navigation.findNavController(
            this,
            R.id.frag_container
        )

        when ((navController?.currentDestination as FragmentNavigator.Destination).className) {
            "com.example.arthan.dashboard.am.AMProfessionalDetailsFragment" -> {

                var b = Bundle()
                b.putString("from", "rmIncome")
                navController?.navigate(R.id.frag_am_per_info, b)
                vw_professional.visibility = View.VISIBLE
                enableProfessional()
//                infoCompleteState(INCOME)
                infoInCompleteState(PERSONAL)
                //income
                // finish()
            }
            "com.example.arthan.dashboard.am.AMOtherDetailsFragment" -> {
                enableOthers()
                infoCompleteState(OTHERS)
                vw_others.visibility = View.VISIBLE
                navController?.navigate(R.id.frag_am_pro_info)
                infoInCompleteState(PROFESSIONAL)
                infoCompleteState(OTHERS)

            }
            else -> {

                finish()

            }
        }
    }
}