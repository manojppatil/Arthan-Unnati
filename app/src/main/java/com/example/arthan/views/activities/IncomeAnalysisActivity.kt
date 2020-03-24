package com.example.arthan.views.activities

import android.view.View
import com.example.arthan.R
import kotlinx.android.synthetic.main.activity_income_analysis.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*

class IncomeAnalysisActivity: BaseActivity() {

    override fun contentView()= R.layout.activity_income_analysis

    override fun onToolbarBackPressed() = onBackPressed()

    val monthList= mutableListOf<String>()

    override fun init() {

        monthList.add("January")
        monthList.add("February")
        monthList.add("March")
        monthList.add("April")
        monthList.add("May")
        monthList.add("June")
        monthList.add("July")
        monthList.add("August")
        monthList.add("September")
        monthList.add("Octomber")
        monthList.add("November")
        monthList.add("December")

        btn_filter.visibility= View.GONE
        btn_search.visibility= View.GONE

        btn_prev_month.tag= 0
        btn_next_month.tag= 0

        btn_prev_month.setOnClickListener {

            var index= (btn_prev_month.tag as Int) - 1
            if(index > 11)
                index= 0

            if(index < 0)
                index= 0

            val month= monthList[index]
            txt_month.text= month

            btn_next_month.tag= index
            btn_prev_month.tag= index
        }

        btn_next_month.setOnClickListener {
            var index= (btn_next_month.tag as Int) + 1
            if(index > 11)
                index= 0

            if(index < 0)
                index= 0

            val month= monthList[index]
            txt_month.text= month

            btn_next_month.tag= index
            btn_prev_month.tag= index
        }

    }

    override fun screenTitle()= "Sales Estimation"
}