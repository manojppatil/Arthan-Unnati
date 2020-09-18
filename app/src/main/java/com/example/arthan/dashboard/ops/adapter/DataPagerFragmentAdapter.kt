package com.example.arthan.dashboard.ops.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.arthan.dashboard.am.*
import com.example.arthan.dashboard.ops.model.DataPagerFragmentItem
import com.example.arthan.dashboard.rm.BMCollateralFragmentIndependent
import com.example.arthan.lead.BusinessInformationFragment
import com.example.arthan.lead.IncomeInformationFragment
import com.example.arthan.lead.OtherDetailsFragment
import com.example.arthan.views.fragments.PersonalDetailFragment

class DataPagerFragmentAdapter(
    fragmentManager: FragmentManager,
    private val recordType: String?
) : FragmentPagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    var list: ArrayList<DataPagerFragmentItem> = assignFragmentBasedOnRecordType()

    private fun assignFragmentBasedOnRecordType(): ArrayList<DataPagerFragmentItem> {
        list = if(recordType=="AM") {
            arrayListOf(
                DataPagerFragmentItem("Personal", AMPersonalDetailsDnD()),
                DataPagerFragmentItem("Professional", AMProfessionalDetailsDnD()),
                DataPagerFragmentItem("Other", AMOthersDetailsDnD())
            )
        }else {
            arrayListOf(
                DataPagerFragmentItem("Personal", PersonalDetailFragment()),
                DataPagerFragmentItem("Business", BusinessInformationFragment()),
                DataPagerFragmentItem("Income", IncomeInformationFragment()),
                DataPagerFragmentItem("Other", OtherDetailsFragment()),
                DataPagerFragmentItem("Collateral", BMCollateralFragmentIndependent())
            )
        }
        return list
    }

    override fun getItem(position: Int): Fragment = list[position].item

    override fun getCount(): Int = list.size

    override fun getPageTitle(position: Int): CharSequence? = list[position].tabName
}