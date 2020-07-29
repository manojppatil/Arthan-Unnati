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

class AMOthersDetailsDnD : BaseFragment() {
    override fun contentView(): Int {

        return R.layout.am_others_details_dnd
    }

    override fun init() {


    }
    var CheckBox.checked: Boolean
        get() = isChecked
        set(value) {
            if(isChecked != value) {
                isChecked = value
                jumpDrawablesToCurrentState()
            }
        }

    fun updateData(otherDetails: OtherDetailsList?) {

        if(otherDetails!=null) {
            if(otherDetails.languages.size>=1) {
                tv_am_lang1.text = otherDetails.languages[0].lang
                cb_lang1_read.checked = otherDetails.languages[0].read=="true"
                cb_lang1_write.checked = otherDetails.languages[0].write =="true"
                cb_lang1_speak.checked = otherDetails.languages[0].speak=="true"
            }
            if(otherDetails.languages.size>=2) {

                tv_am_lang2.text = otherDetails.languages[1].lang
                cb_lang2_read.checked = otherDetails.languages[0].read=="true"
                cb_lang2_write.checked = otherDetails.languages[0].write =="true"
                cb_lang2_speak.checked = otherDetails.languages[0].speak=="true"
            }
            if(otherDetails.languages.size==3) {

                tv_am_lang3.text = otherDetails.languages[2].lang
                cb_lang3_read.checked = otherDetails.languages[0].read =="true"
                cb_lang3_write.checked = otherDetails.languages[0].write =="true"
                cb_lang3_speak.checked = otherDetails.languages[0].speak =="true"
            }
            rb_yes.isChecked=otherDetails.smartphone =="Yes"
            rb_no.isChecked=otherDetails.smartphone =="No"
            rb_tw_yes.isChecked=otherDetails.twoWheeler =="Yes"
            rb_tw_no.isChecked=otherDetails.twoWheeler =="No"

            et_am_ref1_name.setText(otherDetails.references[0].name)
            et_am_ref1_mno.setText(otherDetails.references[0].mobNo)
            et_am_ref1_address.setText(otherDetails.references[0].address)
            spnr_am_profession.setText(otherDetails.references[0].profession)
            et_am_ref1_comments.setText(otherDetails.references[0].comments)

            et_am_ref2_comments.setText(otherDetails.references[1].comments)
            et_am_ref2_name.setText(otherDetails.references[1].name)
            et_am_ref2_mno.setText(otherDetails.references[1].mobNo)
            et_am_ref2_address.setText(otherDetails.references[1].address)
            spnr_am_ref2_profession.setText(otherDetails.references[1].profession)

        }
    }


}