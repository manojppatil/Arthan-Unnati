package com.example.arthan.dashboard.rm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.arthan.R
import com.example.arthan.lead.BusinessInformationFragment
import com.example.arthan.lead.DocumentFragment
import com.example.arthan.lead.IncomeInformationFragment
import com.example.arthan.lead.OtherDetailsFragment
import com.example.arthan.utils.DocumentUploadFragment
import kotlinx.android.synthetic.main.activity_re_usable_fragment_space.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class ReUsableFragmentSpace : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_re_usable_fragment_space)

        when (intent.getStringExtra("screen")) {

            "Documents" -> {
                showDocumentsFragment(intent.getStringExtra("loanId"))
            }
            "Business" -> {
                showBusinessFragment(intent.getStringExtra("loanId"))
            }
            "Income" -> {
                showIncomeFragment(intent.getStringExtra("loanId"))
            }
            "OTHERS", "OTHERS_TRADE", "OTHERS_SECURITY", "others" -> {
                showOthersFragment(intent.getStringExtra("loanId"))
            }
        }
    }

    fun showBusinessFragment(loanId: String) {
        supportFragmentManager.beginTransaction().replace(R.id.frag,
            BusinessInformationFragment().apply {
                var b = Bundle()
                b.putString("task", "RM_AssignList")
                b.putString("loanId", loanId)
                b.putString("from", "rmbusiness")

                this.arguments = b
            }).commit()
        toolbar_title.text = "Business details"

    }

    fun showIncomeFragment(loanId: String) {
        supportFragmentManager.beginTransaction().replace(R.id.frag, IncomeInformationFragment()
            .apply {
                var b = Bundle()
                b.putString("task", "RM_AssignList")
                b.putString("loanId", loanId)
                b.putString("from", "rmIncome")
                this.arguments = b

            }).commit()
        toolbar_title.text = "Income details"


    }

    fun showOthersFragment(loanId: String) {
        supportFragmentManager.beginTransaction().replace(R.id.frag, OtherDetailsFragment()
            .apply {
                var b = Bundle()
                b.putString("task", "RM_AssignList")
                b.putString("loanId", loanId)
                b.putString("screen",intent.getStringExtra("screen"))
                this.arguments = b
            }).commit()

        when(intent.getStringExtra("screen")){
            "OTHERS_TRADE"->  toolbar_title.text ="Others Trade"
            "OTHERS_SECURITY"->  toolbar_title.text ="Collateral Details"
            null->  toolbar_title.text ="Others Trade"
        }
    }
    fun showColateralFragment(loanId: String) {
        supportFragmentManager.beginTransaction().replace(R.id.frag, BMCollateralFragmentIndependent()
            .apply {
                var b = Bundle()
                b.putString("task", "RM_AssignList")
                b.putString("loanId", loanId)
                b.putString("screen",intent.getStringExtra("screen"))
                this.arguments = b
            }).commit()

        toolbar_title.text = "Other details"

    }

    fun showDocumentsFragment(loanId: String) {
        supportFragmentManager.beginTransaction().replace(R.id.frag, DocumentUploadFragment()
            .apply {
                var b = Bundle()
                b.putString("task", "RM_AssignList")
                b.putString("loanId", loanId)
                this.arguments = b
            }).commit()
        toolbar_title.text = "Documents"

    }

    fun setCommentsToField(value: String) {
        commentsValue.text = value
    }
}
