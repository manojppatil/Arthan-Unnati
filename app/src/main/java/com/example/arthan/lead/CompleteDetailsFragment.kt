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
        if(arguments?.getString("screen")!=null)
        {
            if(arguments?.getString("screen").equals("business",ignoreCase = true)) {
                enableBusiness()
                var b= Bundle()
                        b.putString("from","rmbusiness")
                b.putString("loanId",arguments?.getString("loanId"))
                b.putString("custId",arguments?.getString("custId"))
                b.putString("task",arguments?.getString("task"))
                val navController: NavController? = if (activity is AddLeadActivity) Navigation.findNavController(
                    activity!!,
                    R.id.frag_container
                ) else null
                navController?.navigate(R.id.frag_business_info,b)
            }
            if(arguments?.getString("screen").equals("income",ignoreCase = true)) {
                enableInCome()
                val navController: NavController? = if (activity is AddLeadActivity) Navigation.findNavController(
                    activity!!,
                    R.id.frag_container
                ) else null
                var b=Bundle()
                b.putString("from","rmIncome")
                b.putString("loanId",activity?.intent?.getStringExtra("loanId"))
                b.putString("custId",activity?.intent?.getStringExtra("custId"))
                b.putString("task",arguments?.getString("task"))
                navController?.navigate(R.id.frag_income_info,b)
            }
            if(arguments?.getString("screen").equals("OTHERS_TRADE",ignoreCase = true)||arguments?.getString("screen").equals("OTHERS_SECURITY",ignoreCase = true)) {
                enableDoc()
                enableInCome()//already in last page and business is enabled in layout itslef
                var b=Bundle()
                b.putString("from","rmOthers")
                b.putString("loanId",activity?.intent?.getStringExtra("loanId"))
                b.putString("custId",activity?.intent?.getStringExtra("custId"))
                b.putString("task",arguments?.getString("task"))

                val navController: NavController? = if (activity is AddLeadActivity) Navigation.findNavController(
                    activity!!,
                    R.id.frag_container
                ) else null
                navController?.navigate(R.id.frag_document_info,b)
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
