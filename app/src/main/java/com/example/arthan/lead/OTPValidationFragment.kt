package com.example.arthan.lead


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment

import com.example.arthan.R
import kotlinx.android.synthetic.main.fragment_otpvalidation.*

/**
 * A simple [Fragment] subclass.
 */
class OTPValidationFragment : NavHostFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_otpvalidation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_submit?.setOnClickListener {
            navController?.navigate(R.id.action_OTPValidationFragment_to_applicationFeePaymentFragment)
        }
    }

}
