package com.example.arthan.views.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.arthan.views.fragments.BankDetailsFragment
import com.example.arthan.views.fragments.KYCDocumentFragment
import com.example.arthan.views.fragments.PermanentAddressFragment
import com.example.arthan.views.fragments.PersonalDetailFragment

class ProfileTabAdapter(fm:FragmentManager,from:String) : FragmentPagerAdapter(fm){

    private val lstTitles= mutableListOf<String>()
    private val frags= mutableListOf<Fragment>()

    init {

        when {
            from.equals("legal",true) -> {
                lstTitles.add("Applicant Info")
                lstTitles.add("Applicant Property")
                lstTitles.add("Business Property")

                frags.add(PersonalDetailFragment())
                frags.add(KYCDocumentFragment())
                frags.add(PermanentAddressFragment())
            }
            from.equals("RCU",true) -> {
                lstTitles.add("Applicant Info")
                lstTitles.add("Business Info")
                lstTitles.add("RCU Checking Document")

                frags.add(PersonalDetailFragment())
                frags.add(KYCDocumentFragment())
                frags.add(PermanentAddressFragment())
            }
            else -> {

                lstTitles.add("Personal Details")
                lstTitles.add("KYC Documents")
                lstTitles.add("Permanent Address")
                lstTitles.add("Bank Details")

                frags.add(PersonalDetailFragment())
                frags.add(KYCDocumentFragment())
                frags.add(PermanentAddressFragment())
                frags.add(BankDetailsFragment())
            }
        }
    }

    override fun getCount()= lstTitles.size

    override fun getItem(position: Int): Fragment {
        return frags[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return lstTitles[position]
    }
}