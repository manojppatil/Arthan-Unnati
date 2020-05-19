package com.example.arthan.lead


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment

import com.example.arthan.R
import com.example.arthan.global.BUSINESS
import com.example.arthan.global.DOCUMENT
import com.example.arthan.global.INCOME
import kotlinx.android.synthetic.main.fragment_complete_details.*

/**
 * A simple [Fragment] subclass.
 */
class CompleteDetailsFragment : NavHostFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_complete_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments?.getString("screenTo")!=null)
        {
            if(arguments?.getString("screenTo").equals("business",ignoreCase = true)) {
                enableBusiness()
                val navController: NavController? = if (activity is AddLeadActivity) Navigation.findNavController(
                    activity!!,
                    R.id.frag_container
                ) else null
                navController?.navigate(R.id.frag_business_info)
            }
            if(arguments?.getString("screenTo").equals("income",ignoreCase = true)) {
                enableInCome()
                val navController: NavController? = if (activity is AddLeadActivity) Navigation.findNavController(
                    activity!!,
                    R.id.frag_container
                ) else null
                var b=Bundle()
                b.putString("from","rmIncome")
                navController?.navigate(R.id.frag_income_info,b)
            }
            if(arguments?.getString("screenTo").equals("others",ignoreCase = true)) {
                enableBusiness()
                val navController: NavController? = if (activity is AddLeadActivity) Navigation.findNavController(
                    activity!!,
                    R.id.frag_container
                ) else null
             }
        }

    }

    fun infoCompleteState(type: String) {
        when (type) {
            DOCUMENT -> {
                txt_doc.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    R.drawable.ic_info_completed,
                    0,
                    0
                )
            }
            BUSINESS -> {
                txt_Business.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    R.drawable.ic_info_completed,
                    0,
                    0
                )
            }
            INCOME -> {
                txt_income.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    R.drawable.ic_info_completed,
                    0,
                    0
                )
            }
        }
    }

    fun enableBusiness() {
        vw_dim_business.visibility = View.GONE
    }

    fun enableInCome() {
        vw_dim_income.visibility = View.GONE
    }

    fun enableDoc() {
        vw_dim_doc.visibility = View.GONE
    }

    private val visibilityListener = object : EnableCompleteDetail {
        override fun enableFragment1() {
            enableBusiness()
        }

        override fun enableFragment2() {
            enableInCome()
        }

        override fun enableFragment3() {
            enableDoc()
        }
    }
}


interface EnableCompleteDetail {
    fun enableFragment1()
    fun enableFragment2()
    fun enableFragment3()
}
