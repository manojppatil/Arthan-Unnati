package com.example.arthan.dashboard.ops


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.arthan.R
import com.example.arthan.dashboard.am.*
import com.example.arthan.dashboard.ops.adapter.DataPagerFragmentAdapter
import com.example.arthan.global.ArthanApp
import com.example.arthan.lead.BusinessInformationFragment
import com.example.arthan.lead.IncomeInformationFragment
import com.example.arthan.lead.OtherDetailsFragment
import com.example.arthan.lead.model.responsedata.BMAmDocnDataResponse
import com.example.arthan.lead.model.responsedata.CustomerDocumentAndDataResponseData
import com.example.arthan.views.fragments.PersonalDetailFragment
import kotlinx.android.synthetic.main.fragment_data.*

/**
 * A simple [Fragment] subclass.
 */
class DataFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vp_profile.adapter = DataPagerFragmentAdapter(childFragmentManager,activity?.intent?.getStringExtra("recordType"))
        tb_profile.setupWithViewPager(vp_profile)
        vp_profile?.offscreenPageLimit = 4
    }

    fun updateDataAM(
        loanId: String?,
        data: BMAmDocnDataResponse?,
        customerId: String?
    ) {
        val adapter = vp_profile?.adapter as? DataPagerFragmentAdapter

        if (ArthanApp.getAppInstance().loginRole == "BM"&& activity?.intent?.getStringExtra("recordType")=="AM") {
            (adapter?.getItem(0) as? AMPersonalDetailsDnD)?.updateData(
                data?.personalDetails
            )
            (adapter?.getItem(1) as? AMProfessionalDetailsDnD)?.updateData(
                data?.professionalDetails
            )
            (adapter?.getItem(2) as? AMOthersDetailsDnD)?.updateData(
                data?.otherDetails


            )
        }
    }

    fun moveToPosition(position:Int)
    {
        vp_profile.currentItem=position
    }

    fun updateData(
        loanId: String?,
        data: CustomerDocumentAndDataResponseData?,
        customerId: String?
    ) {
        val adapter = vp_profile?.adapter as? DataPagerFragmentAdapter

        if (ArthanApp.getAppInstance().loginRole == "BM"&& activity?.intent?.getStringExtra("recordType")=="AM") {
            /*(adapter?.getItem(0) as? AMOthersDetailsDnD)?.updateData(
                data?.personalDetails, data?.inPrincipleAmt,
                data?.loanAmt, data?.roi, data?.tenure
            )
            (adapter?.getItem(1) as? AMProfessionalDetailsFragment)?.updateData(
                data?.businessDetails,
                data?.businessComments
            )
            (adapter?.getItem(3) as? AMOtherDetailsFragment)?.updateData(
                data?.neighborRefDetails,
                data?.tradeRefDetails,
                data?.collateralDetails,
                loanId,
                data?.loanType,
                data?.OtherComments


            )
*/
        } else {
            (adapter?.getItem(0) as? PersonalDetailFragment)?.updateData(
                data?.personalDetails, data?.inPrincipleAmt,
                data?.loanAmt, data?.roi, data?.tenure
            )
            (adapter?.getItem(1) as? BusinessInformationFragment)?.updateData(
                data?.businessDetails,
                data?.businessComments
            )
            (adapter?.getItem(2) as? IncomeInformationFragment)?.updateData(
                data?.incomeDetails,
                customerId,
                loanId,
                data?.incomeComments
            )
            (adapter?.getItem(3) as? OtherDetailsFragment)?.updateData(
                data?.neighborRefDetails,
                data?.tradeRefDetails,
                data?.collateralDetails,
                loanId,
                data?.loanType,
                data?.otherComments


            )
        }
    }

}
