package com.example.arthan.dashboard.ops.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.arthan.dashboard.ops.model.DataPagerFragmentItem
import com.example.arthan.lead.BusinessInformationFragment
import com.example.arthan.lead.IncomeInformationFragment
import com.example.arthan.lead.OtherDetailsFragment
import com.example.arthan.views.fragments.PersonalDetailFragment

class DataPagerFragmentAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    val list: ArrayList<DataPagerFragmentItem> = arrayListOf(
        DataPagerFragmentItem("Personal", PersonalDetailFragment()),
        DataPagerFragmentItem("Business", BusinessInformationFragment()),
        DataPagerFragmentItem("Income", IncomeInformationFragment()),
        DataPagerFragmentItem("Other", OtherDetailsFragment())
    )

    override fun getItem(position: Int): Fragment = list[position].item

    override fun getCount(): Int = list.size

    override fun getPageTitle(position: Int): CharSequence? = list[position].tabName
}