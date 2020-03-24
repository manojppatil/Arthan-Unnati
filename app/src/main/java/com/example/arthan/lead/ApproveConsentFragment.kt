package com.example.arthan.lead


import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment

import com.example.arthan.R
import kotlinx.android.synthetic.main.fragment_approve_consent.*
import com.example.arthan.utils.ArgumentKey

/**
 * A simple [Fragment] subclass.
 */
class ApproveConsentFragment : NavHostFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_approve_consent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val messageText = "Your in principle amount is : "
        val spannableStringBuilder = SpannableStringBuilder(
//            "$messageText${intent.getStringExtra(ArgumentKey.InPrincipleAmount)}"
        )
        spannableStringBuilder.setSpan(
            StyleSpan(Typeface.BOLD),
            messageText.length,
            spannableStringBuilder.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        in_principle_amount_text?.text = spannableStringBuilder

        chk_consent.setOnCheckedChangeListener { _, isChecked ->
            btn_next.isEnabled = isChecked
        }

        btn_next.setOnClickListener {
            navController?.navigate(R.id.action_approveConsentFragment_to_OTPValidationFragment)
        }
    }

}
