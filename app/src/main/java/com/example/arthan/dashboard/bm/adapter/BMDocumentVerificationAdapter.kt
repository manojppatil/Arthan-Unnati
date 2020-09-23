package com.example.arthan.dashboard.bm.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.arthan.dashboard.bm.DocumentVerificationFragment
import com.example.arthan.dashboard.bm.DocumentVerificationFragmentNew
import com.example.arthan.dashboard.ops.BCMDataFragment
import com.example.arthan.dashboard.ops.DataFragment
import com.example.arthan.global.ArthanApp

class BMDocumentVerificationAdapter(
    fm: FragmentManager,
    from: String,
    recordType: String?
) : FragmentPagerAdapter(fm) {

    private val lstTitles = mutableListOf<String>()
    private val frags = mutableListOf<Fragment>()

    init {
        lstTitles.add("Documents")
        if(recordType=="AM") {
            frags.add(DocumentVerificationFragment())
        }else {
            frags.add(DocumentVerificationFragmentNew())
        }

        lstTitles.add("Data")
        frags.add(DataFragment())
        if (ArthanApp.getAppInstance().loginRole == "BCM") {
            lstTitles.add("BCM Data")
            frags.add(BCMDataFragment())
        }

    }

    override fun getCount() = lstTitles.size

    override fun getItem(position: Int): Fragment {
        return frags[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return lstTitles[position]
    }
}