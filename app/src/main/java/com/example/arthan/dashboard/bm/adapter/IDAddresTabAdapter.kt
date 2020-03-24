package com.example.arthan.dashboard.bm.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.arthan.dashboard.bm.fragment.IndividualIdAddressFragment
import com.example.arthan.dashboard.bm.fragment.NonIndividualIdAddressFragment

class IDAddresTabAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm){

    private val lstTitles= mutableListOf<String>()
    private val frags= mutableListOf<Fragment>()

    init {
        lstTitles.add("Individual")
        lstTitles.add("Non-Individual")

        frags.add(IndividualIdAddressFragment())
        frags.add(NonIndividualIdAddressFragment())
    }

    override fun getCount()= lstTitles.size

    override fun getItem(position: Int): Fragment {
        return frags[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return lstTitles[position]
    }
}