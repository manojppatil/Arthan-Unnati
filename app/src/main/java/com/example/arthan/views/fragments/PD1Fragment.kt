package com.example.arthan.views.fragments


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.example.arthan.R
import com.example.arthan.lead.model.postdata.PD1PostData
import com.example.arthan.lead.model.postdata.ProductData
import com.example.arthan.lead.model.responsedata.BaseResponseData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_pd1.*
import kotlinx.android.synthetic.main.sales_estimation_layout.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * A simple [Fragment] subclass.
 */
class PD1Fragment : Fragment(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    private var mPdFragmentClickListener: PDFragmentSaveClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pd1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View?>(R.id.remove_button)?.visibility = View.GONE
        add_estimation_button?.setOnClickListener {
            sales_estimation_list?.addView(
                LayoutInflater.from(context).inflate(
                    R.layout.sales_estimation_layout,
                    null,
                    false
                ).apply {
                    findViewById<View?>(R.id.remove_button)?.setOnClickListener {
                        sales_estimation_list?.removeView(this)
                    }
                })
        }

        btn_save_continue?.setOnClickListener {
            savePD1Data()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mPdFragmentClickListener = parentFragment as? PDFragmentSaveClickListener
    }

    private fun savePD1Data() {
        val productData: MutableList<ProductData> = mutableListOf()
        if (product_input?.text?.isNotEmpty() == true && units_sold_input?.text?.isNotEmpty() == true) {
            productData?.add(
                ProductData(
                    product = product_input?.text?.toString(),
                    unitsSold = units_sold_input?.text?.toString(),
                    costpriceUnit = cost_price_input?.text?.toString(),
                    salepriceUnit = sales_price_input?.text?.toString()
                )
            )

            if ((sales_estimation_list?.childCount ?: 0) > 1) {
                for (childCount in 1 until (sales_estimation_list?.childCount ?: 0)) {
                    val productView = sales_estimation_list?.getChildAt(childCount)
                    productData.add(
                        ProductData(
                            product = productView?.findViewById<TextInputEditText?>(R.id.product_input)?.text?.toString(),
                            unitsSold = productView?.findViewById<TextInputEditText?>(R.id.units_sold_input)?.text?.toString(),
                            costpriceUnit = productView?.findViewById<TextInputEditText?>(R.id.cost_price_input)?.text?.toString(),
                            salepriceUnit = productView?.findViewById<TextInputEditText?>(R.id.sales_price_input)?.text?.toString()
                        )
                    )
                }
            }
        }
        val postData = PD1PostData(
            productData = productData,
            methodUsed = method_used_spinner?.selectedItem as? String,
            highPerWeekCount = high_per_week_count_input?.text?.toString(),
            mediumPerWeekCount = medium_per_week_count_input?.text?.toString(),
            lowPerWeekCount = low_per_week_count_input?.text?.toString(),
            highPerDayAmount = high_per_day_count_input?.text?.toString(),
            mediumPerDayAmount = medium_per_day_count_input?.text?.toString(),
            lowPerDayAmount = low_per_day_count_input?.text?.toString(),
            grossMarginVerifiedByBcm = gross_margin_input?.text?.toString(),
            otherIncomeSalaries = other_income_salaries_input?.text?.toString(),
            otherIncomeRent = other_income_rent_input?.text?.toString(),
            otherIncomeAgriculture = other_income_agriculture_input?.text?.toString(),
            otherIncome = any_other_income_input?.text?.toString(),
            salaries = operating_expenses_salaries_input?.text?.toString(),
            rent = operating_expenses_rent_input?.text?.toString(),
            utilities = operating_expenses_utilities_input?.text?.toString(),
            licenseRenewal = operating_expenses_licence_renewal_input?.text?.toString(),
            transportation = operating_expenses_transportation_rent_input?.text?.toString(),
            financialExpenses = operating_expenses_financial_expenses_input?.text?.toString(),
            otherOpex = any_operating_expenses_other_input?.text?.toString(),
            food = household_expenses_food_input?.text?.toString(),
            clothing = household_expenses_clothing_input?.text?.toString(),
            education = household_expenses_education_input?.text?.toString(),
            healthcare = household_expenses_health_care_input?.text?.toString(),
            personalDebt = household_expenses_personal_debt_input?.text?.toString(),
            otherhhExpnse = household_expenses_any_other_input?.text?.toString(),
            loanamountrecommended = loan_amount_recommended_input?.text?.toString()
        )
        mPdFragmentClickListener?.onPD1Fragment(postData)

    }
}
