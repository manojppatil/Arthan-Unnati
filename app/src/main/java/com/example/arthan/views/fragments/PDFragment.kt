package com.example.arthan.views.fragments


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

import com.example.arthan.R
import com.example.arthan.dashboard.bcm.PdResponseInterface
import com.example.arthan.dashboard.bm.BMDocumentVerificationActivity
import com.example.arthan.lead.model.postdata.PD1PostData
import com.example.arthan.lead.model.postdata.ProductData
import kotlinx.android.synthetic.main.fragment_pd1.*
import kotlinx.android.synthetic.main.sales_estimation_layout.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * A simple [Fragment] subclass.
 */
class PDFragment : Fragment(), CoroutineScope,PdResponseInterface {

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
        (activity as BMDocumentVerificationActivity).pd1ResponseInter=this
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

    private fun loadDataFromBCMResponse() {
        if(activity is BMDocumentVerificationActivity)
        {
            if((activity as BMDocumentVerificationActivity).pd1Response!=null)
            {
                val pd1Data= (activity as BMDocumentVerificationActivity).pd1Response ?: return

                val list =
                    resources.getStringArray(R.array.arr_method_used)
                if (list != null) {
                    for (i in list.indices) {
                        if (list[i].toLowerCase() == pd1Data.methodused?.toLowerCase()) {
                            method_used_spinner.setSelection(i)
                        }
                    }

                }
                high_per_week_count_input.setText(pd1Data.highPerWeekCount)
                medium_per_week_count_input.setText(pd1Data.mediumPerWeekCount)
                low_per_week_count_input.setText(pd1Data.lowPerWeekCount)
                high_per_day_count_input.setText(pd1Data.highPerDayAmount)
                medium_per_day_count_input.setText(pd1Data.mediumPerDayAmount)
                low_per_day_count_input.setText(pd1Data.lowPerDayAmount)
                gross_margin_input.setText(pd1Data.grossMarginVerifiedByBcm)
                other_income_salaries_input.setText(pd1Data.otherIncomeSalaries)
                other_income_rent_input.setText(pd1Data.otherIncomeRent)
                other_income_agriculture_input.setText(pd1Data.otherIncomeAgriculture)
                any_other_income_input.setText(pd1Data.otherIncome)
                remarks_input.setText(pd1Data.incomeRemarks)
                operating_expenses_salaries_input.setText(pd1Data.salaries)
                operating_expenses_rent_input.setText(pd1Data.rent)
                operating_expenses_utilities_input.setText(pd1Data.utilities)
                operating_expenses_licence_renewal_input.setText(pd1Data.licenseRenewal)
                operating_expenses_transportation_rent_input.setText(pd1Data.transportation)
                operating_expenses_financial_expenses_input.setText(pd1Data.financialExpenses)
                any_operating_expenses_other_input.setText(pd1Data.otherOpex)
                household_expenses_food_input.setText(pd1Data.food)
                household_expenses_rent_input.setText(pd1Data.rent)
                household_expenses_utilities_input.setText(pd1Data.utilities)
                household_expenses_clothing_input.setText(pd1Data.clothing)
                household_expenses_education_input.setText(pd1Data.education)
                household_expenses_health_care_input.setText(pd1Data.healthcare)
                household_expenses_personal_debt_input.setText(pd1Data.personalDebt)
                household_expenses_any_other_input.setText(pd1Data.otherOpex)
                loan_amount_recommended_input.setText(pd1Data.loanamountrecommended)
                customerComfortableEMi.setText(pd1Data.comfEmi)


            }
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
                    salepriceUnit = sales_price_input?.text?.toString(),
                    turnoverFreq = when(rb_monthly.isChecked){
                                  true->"monthly"
                                 false->"Yearly"
                    }
                )
            )

            if ((sales_estimation_list?.childCount ?: 0) > 1) {
                for (childCount in 1 until (sales_estimation_list?.childCount ?: 0)) {
                    val productView = sales_estimation_list?.getChildAt(childCount)
                    productData.add(
                        ProductData(
                            product = productView?.findViewById<EditText?>(R.id.product_input)?.text?.toString(),
                            unitsSold = productView?.findViewById<EditText?>(R.id.units_sold_input)?.text?.toString(),
                            costpriceUnit = productView?.findViewById<EditText?>(R.id.cost_price_input)?.text?.toString(),
                            salepriceUnit = productView?.findViewById<EditText?>(R.id.sales_price_input)?.text?.toString(),
                            turnoverFreq = when(rb_monthly.isChecked){
                                true->"monthly"
                                false->"Yearly"
                            }
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
            loanamountrecommended = loan_amount_recommended_input?.text?.toString(),
            incomeRemarks=remarks_input.text.toString(),
            comfEmi=customerComfortableEMi?.text.toString()
        )
        mPdFragmentClickListener?.onPD1Fragment(postData)

    }

    override fun setResponseToFields() {
        loadDataFromBCMResponse()
    }
}
