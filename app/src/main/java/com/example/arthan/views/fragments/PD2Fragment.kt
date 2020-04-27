package com.example.arthan.views.fragments


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.arthan.R
import com.example.arthan.lead.model.postdata.PD1PostData
import com.example.arthan.model.PD2Data
import com.example.arthan.model.PD3Data
import kotlinx.android.synthetic.main.fragment_pd2.*

/**
 * A simple [Fragment] subclass.
 */
class PD2Fragment : Fragment() {

    private var mPdFragmentClickListener: PDFragmentSaveClickListener? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pd2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        succession_plan_switch.setOnCheckedChangeListener { _, isChecked ->
            when(isChecked){
                true->succession_plan_switch.text="Yes"
                false->succession_plan_switch.text="No"
            }
        }
        adequate_business_inventory_switch.setOnCheckedChangeListener { _, isChecked ->
            when(isChecked){
                true->adequate_business_inventory_switch.text="Yes"
                false->adequate_business_inventory_switch.text="No"
            }
        }


        btn_continue?.setOnClickListener {
            mPdFragmentClickListener?.onPD2Fragment(
                PD2Data(
                    businessCashCredit = type_of_business_cash_or_credit_spinner?.selectedItem as? String,
                    dependencyNoofProducts = dependency_no_of_product_spinner?.selectedItem as? String,
                    dpendencyNoofCustomers = dependency_on_customer_spinner?.selectedItem as? String,
                    dependencyNoofSuppliers = dependency_no_of_supplier_spinner?.selectedItem as? String,
                    qualityofStock = quality_of_stock_moving_spinner?.selectedItem as? String,
                    impactofCompetition = impact_of_competition_spinner?.selectedItem as? String,
                    networth = net_worth_spinner?.selectedItem as? String,
                    tunoverToTotalExistingLoan = turnover_total_existing_loan_spinner?.selectedItem as? String,
                    seasonality = seasonality_spinner?.selectedItem as? String,
                    perishability = perishability_spinner?.selectedItem as? String,
                    businessSkillset = business_skill_set_spinner?.selectedItem as? String,
                    successionPlan = if (succession_plan_switch?.isChecked == true) "Yes" else "No",
                    collateralOwership = collateral_ownership_spinner?.selectedItem as? String,
                    businessreferencePayontime = business_reference_client_spinner?.selectedItem as? String,
                    allowAdvCredit = business_reference_customer_spinner?.selectedItem as? String,
                    ladyCoapplicant = if (lady_co_applicant_switch?.isChecked == true) "Yes" else "No",
                    timeCurrentLocation_years = time_at_current_location_spinner?.selectedItem as? String,
                    officepremisesownership = office_premises_ownership_spinner?.selectedItem as? String,
                    homeOwnership = home_ownership_spinner?.selectedItem as? String,
                    maintenanceBusinessRecords = if (business_maintenance_checkbox?.isChecked == true) "Yes" else "No",
                    referenceFromNeighboursAndAssociations = neighbour_association_reference_spinner?.selectedItem as? String,
                    adequateInventory = if (adequate_business_inventory_switch?.isChecked == true) "Yes" else "No",
                    neighbourReferenceStability = neighbour_reference_stability_spinner?.selectedItem as? String,
                    homeLocation = home_location_spinner?.selectedItem as? String
                )
            )
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mPdFragmentClickListener = parentFragment as? PDFragmentSaveClickListener
    }
}

interface PDFragmentSaveClickListener {
    fun onPD1Fragment(pd1Data: PD1PostData)
    fun onPD2Fragment(pd2Data: PD2Data)
    fun onPD3Fragment(pd3Data: PD3Data)
}
