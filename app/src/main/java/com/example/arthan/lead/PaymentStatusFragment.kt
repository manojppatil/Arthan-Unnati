package com.example.arthan.lead


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment

import com.example.arthan.R
import kotlinx.android.synthetic.main.fragment_payment_status.*

/**
 * A simple [Fragment] subclass.
 */
class PaymentStatusFragment : NavHostFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_next?.setOnClickListener {
            navController?.navigate(R.id.action_paymentStatusFragment_to_completeDetailsFragment)
        }
    }
}
