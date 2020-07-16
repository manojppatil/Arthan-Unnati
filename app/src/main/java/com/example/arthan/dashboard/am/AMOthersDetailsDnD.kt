package com.example.arthan.dashboard.am

import android.widget.CheckBox
import com.example.arthan.R
import com.example.arthan.lead.model.responsedata.OtherDetailsList
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.am_others_details_dnd.*
import kotlinx.android.synthetic.main.am_others_details_dnd.cb_lang1_read
import kotlinx.android.synthetic.main.am_others_details_dnd.cb_lang1_speak
import kotlinx.android.synthetic.main.am_others_details_dnd.cb_lang1_write
import kotlinx.android.synthetic.main.am_others_details_dnd.cb_lang2_read
import kotlinx.android.synthetic.main.am_others_details_dnd.cb_lang2_speak
import kotlinx.android.synthetic.main.am_others_details_dnd.cb_lang2_write
import kotlinx.android.synthetic.main.am_others_details_dnd.cb_lang3_read
import kotlinx.android.synthetic.main.am_others_details_dnd.cb_lang3_speak
import kotlinx.android.synthetic.main.am_others_details_dnd.cb_lang3_write
import kotlinx.android.synthetic.main.am_others_details_dnd.rb_no
import kotlinx.android.synthetic.main.am_others_details_dnd.rb_tw_no
import kotlinx.android.synthetic.main.am_others_details_dnd.rb_tw_yes
import kotlinx.android.synthetic.main.am_others_details_dnd.rb_yes
import kotlinx.android.synthetic.main.am_others_details_dnd.tv_am_lang1
import kotlinx.android.synthetic.main.am_others_details_dnd.tv_am_lang2
import kotlinx.android.synthetic.main.am_others_details_dnd.tv_am_lang3
import kotlinx.android.synthetic.main.fragment_am_otherdetails.*

class AMOthersDetailsDnD : BaseFragment() {
    override fun contentView(): Int {

        return R.layout.am_others_details_dnd
    }

    override fun init() {


    }

    fun updateData(otherDetails: OtherDetailsList?) {

        if(otherDetails!=null) {
            if(otherDetails.languages.size>=1) {
                tv_am_lang1.text = otherDetails.languages[0].lang


                cb_lang1_read.isChecked = otherDetails.languages[0].read=="Yes"
                cb_lang1_write.isChecked = otherDetails.languages[0].write =="Yes"
                cb_lang1_speak.isChecked = otherDetails.languages[0].speak=="Yes"
            }
            if(otherDetails.languages.size>=2) {

                tv_am_lang2.text = otherDetails.languages[1].lang
                cb_lang2_read.isChecked = otherDetails.languages[0].read=="Yes"
                cb_lang2_write.isChecked = otherDetails.languages[0].write =="Yes"
                cb_lang2_speak.isChecked = otherDetails.languages[0].speak=="Yes"
            }
            if(otherDetails.languages.size==3) {

                tv_am_lang3.text = otherDetails.languages[2].lang
                cb_lang3_read.isChecked = otherDetails.languages[0].read =="Yes"
                cb_lang3_write.isChecked = otherDetails.languages[0].write =="Yes"
                cb_lang3_speak.isChecked = otherDetails.languages[0].speak =="Yes"
            }
            rb_yes.isChecked=otherDetails.smartphone =="Yes"
            rb_no.isChecked=otherDetails.smartphone =="Yes"
            rb_tw_yes.isChecked=otherDetails.twoWheeler =="Yes"
            rb_tw_no.isChecked=otherDetails.twoWheeler =="Yes"
        }
    }


}