package com.example.arthan.dashboard.ops.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.arthan.views.fragments.PDFragment
import com.example.arthan.views.fragments.PD2Fragment
import com.example.arthan.views.fragments.PD3Fragment
import com.example.arthan.views.fragments.PD4Fragment

class BCDDataAdapter(fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    val fragmentList: MutableList<BCMAdapterListItem> = mutableListOf(
        BCMAdapterListItem("PD1", PDFragment()),
        BCMAdapterListItem("PD2", PD2Fragment()),
        BCMAdapterListItem("PD3", PD3Fragment()),
        BCMAdapterListItem("PD4", PD4Fragment())
    )

    override fun getItem(position: Int): Fragment = fragmentList[position].fragment

    override fun getCount(): Int = fragmentList.size

    override fun getPageTitle(position: Int): CharSequence? = fragmentList[position].tabName

}

data class BCMAdapterListItem(val tabName: String, val fragment: Fragment)