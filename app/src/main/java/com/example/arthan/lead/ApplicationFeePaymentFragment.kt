package com.example.arthan.lead


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment

import com.example.arthan.R
import kotlinx.android.synthetic.main.fragment_application_fee_payment.*

/**
 * A simple [Fragment] subclass.
 */
class ApplicationFeePaymentFragment : NavHostFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_application_fee_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_proceed.setOnClickListener {
//            startActivity(Intent(context, PaymentSuccessActivity::class.java))
            navController?.navigate(R.id.action_applicationFeePaymentFragment_to_paymentStatusFragment)
        }

        btn_proceed?.isEnabled = false
        chk_consent?.isChecked = false

        chk_consent?.setOnCheckedChangeListener { buttonView, isChecked ->
            btn_proceed?.isEnabled = isChecked
        }
    }
}
