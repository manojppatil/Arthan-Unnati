package com.example.arthan.views.fragments


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.arthan.R
import com.example.arthan.model.PD3Data
import kotlinx.android.synthetic.main.fragment_pd3.*

/**
 * A simple [Fragment] subclass.
 */
class PD3Fragment : Fragment() {

    private var mPdFragmentClicklistener: PDFragmentSaveClickListener? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pd3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_save?.setOnClickListener {
            mPdFragmentClicklistener?.onPD3Fragment(
                PD3Data(
                    furnishedsemi = if (furnished_semi_furnished_checkbox?.isChecked == true) "Yes" else "No",
                    tv = if (tv_checkbox?.isChecked == true) "Yes" else "No",
                    refrigerate = if (refrigerate_checkbox?.isChecked == true) "Yes" else "No",
                    washingMachine = if (washing_machine_checkbox?.isChecked == true) "Yes" else "No",
                    twoWheeler = if (two_wheeler_checkbox?.isChecked == true) "Yes" else "No",
                    fourWheeler = if (four_wheeler_checkbox?.isChecked == true) "Yes" else "No",
                    childrenMedium = children_education_medium_spinner?.selectedItem as? String
                )
            )
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mPdFragmentClicklistener = parentFragment as? PDFragmentSaveClickListener
    }
}