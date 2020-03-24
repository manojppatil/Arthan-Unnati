package com.example.arthan.dashboard.rm.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.arthan.dashboard.rm.PendingDataFragment
import com.example.arthan.dashboard.rm.PendingDocFragment

class PendingInfoAdapter(fm: FragmentManager): FragmentPagerAdapter(fm){

    private val lstTitles= mutableListOf<String>()
    private val frags= mutableListOf<Fragment>()

    init {
        lstTitles.add("Data")
        lstTitles.add("Documents")

        frags.add(PendingDataFragment())
        frags.add(PendingDocFragment())

    }

    override fun getCount()= lstTitles.size

    override fun getItem(position: Int): Fragment {
        return frags[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return lstTitles[position]
    }
}