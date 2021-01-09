package com.example.arthan.views.fragments


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox

import com.example.arthan.R
import com.example.arthan.dashboard.bcm.PdResponseInterface
import com.example.arthan.dashboard.bm.BMDocumentVerificationActivity
import com.example.arthan.global.ArthanApp
import com.example.arthan.model.PD3Data
import kotlinx.android.synthetic.main.fragment_other_details.*
import kotlinx.android.synthetic.main.fragment_pd3.*

/**
 * A simple [Fragment] subclass.
 */
class PD3Fragment : Fragment(),PdResponseInterface {

    private var mPdFragmentClicklistener: PDFragmentSaveClickListener? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as BMDocumentVerificationActivity).pd3ResponseInter=this
        return inflater.inflate(R.layout.fragment_pd3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(ArthanApp.getAppInstance().loginRole == "BCM" || ArthanApp.getAppInstance().loginRole == "BM")
        {
            bcmPd3CheckBoxes.visibility=View.GONE
        }else
        {
            bcmPd3CheckBoxes.visibility=View.GONE

        }
        btn_save?.setOnClickListener {
            mPdFragmentClicklistener?.onPD3Fragment(
                PD3Data(
                    furnishedsemi = if (furnished_semi_furnished_checkbox?.isChecked == true) "Yes" else "No",
                    tv = if (tv_checkbox?.isChecked == true) "Yes" else "No",
                    refrigerate = if (refrigerate_checkbox?.isChecked == true) "Yes" else "No",
                    washingMachine = if (washing_machine_checkbox?.isChecked == true) "Yes" else "No",
                    twoWheeler = if (two_wheeler_checkbox?.isChecked == true) "Yes" else "No",
                    fourWheeler = if (four_wheeler_checkbox?.isChecked == true) "Yes" else "No",
                    childrenMedium = children_education_medium_spinner?.selectedItem as? String,
                    rltWOValue = rltPD3WOCheckBox.isChecked.toString(),
                    rltWFeeValue = rltWFeePD3CheckBox.isChecked.toString(),
                    risk = risk.text.toString(),
                    strength = strength.text.toString(),
                    overallRemarks = overallRemarks.text.toString()
                )
            )
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mPdFragmentClicklistener = parentFragment as? PDFragmentSaveClickListener
    }
    private fun setValueToCheckBox(value:String?,view:CheckBox)
    {
        if(value!=null)
        view.isChecked = value=="Yes"
    }

    override fun setResponseToFields() {

        val pd3Data= (activity as BMDocumentVerificationActivity).pd23Response
        if(pd3Data!=null)
        {
            setValueToCheckBox(pd3Data.furnishedsemi,furnished_semi_furnished_checkbox)
            setValueToCheckBox(pd3Data.tv,tv_checkbox)
            setValueToCheckBox(pd3Data.refrigerate,refrigerate_checkbox)
            setValueToCheckBox(pd3Data.washingMachine,washing_machine_checkbox)
            setValueToCheckBox(pd3Data.twoWheeler,two_wheeler_checkbox)
            setValueToCheckBox(pd3Data.fourWheeler,four_wheeler_checkbox)

            if(pd3Data.rltWOValue.equals("true",ignoreCase = true))
            {
                rltPD3WOCheckBox.isChecked=true
            }else
            {
                rltWFeePD3CheckBox.isChecked=true
            }
            val list =
                resources.getStringArray(R.array.arr_education_medium)
            if (list != null&&pd3Data.childrenMedium!=null) {
                for (i in list.indices) {
                    if (list[i].toLowerCase() == pd3Data.childrenMedium!!.toLowerCase()) {
                        children_education_medium_spinner.setSelection(i)
                    }
                }

            }

            risk.setText(pd3Data.risk)
            strength.setText(pd3Data.strength)
            overallRemarks.setText(pd3Data.overallRemarks)
        }

    }
}