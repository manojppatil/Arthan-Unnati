package com.example.arthan.views.activities

import android.content.Context
import android.content.Intent
import com.example.arthan.R
import com.example.arthan.views.adapters.ScreeningAdapter
import kotlinx.android.synthetic.main.activity_screening.*
import  com.example.arthan.utils.ArgumentKey

class ScreeningActivity : BaseActivity() {

    override fun contentView() = R.layout.activity_screening

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {
        rv_customer.adapter = ScreeningAdapter(this, intent.getStringExtra("FROM"))
    }

    override fun screenTitle() = "Screening"

    companion object {
        fun startMe(context: Context?, from: String) =
            context?.startActivity(Intent(context, ScreeningActivity::class.java).apply {
                putExtra(ArgumentKey.FROM, from)
            })
    }
}