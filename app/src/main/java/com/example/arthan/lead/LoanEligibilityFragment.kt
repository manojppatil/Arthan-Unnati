package com.example.arthan.lead


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment

import com.example.arthan.R
import com.example.arthan.utils.ArgumentKey
import kotlinx.android.synthetic.main.fragment_loan_eligibility.*

/**
 * A simple [Fragment] subclass.
 */
class LoanEligibilityFragment : NavHostFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loan_eligibility, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_ok?.setOnClickListener {
            navController?.navigate(R.id.action_loanEligibilityFragment_to_addKYCDetailsFragment)

        }
    }

}
