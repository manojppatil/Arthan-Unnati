package com.example.arthan.dashboard.bm.fragment

import android.app.AlertDialog
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.arthan.R
import com.example.arthan.lead.model.responsedata.IdAddrData
import com.example.arthan.lead.model.responsedata.ipAddressVO
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_individual_id_address.*

class IndividualIdAddressFragment: BaseFragment() {

    override fun contentView()= R.layout.fragment_individual_id_address

    override fun init() {

        val ipAddData:ipAddressVO?= activity?.intent?.extras?.getParcelable<ipAddressVO>("idData")

        var loanId=ipAddData?.loanId
        var userData:IdAddrData?=null
        if(ipAddData?.idAddrData?.isNotEmpty()!!) {
             userData = ipAddData?.idAddrData?.get(0)
            txt_applicant_value.text = userData?.fullName
            txt_applicant_dob_value.text = userData?.dob
            pancardLabel.text = userData?.idText
            panCardValue.text = userData?.idVal
            AddProofLabel.text = userData?.addressText
            addreValue.text = userData?.addressVal

        }
        btnViewPan.setOnClickListener {
            showDialog(userData?.idUrl)
        }
        btnViewadddproof.setOnClickListener {
            showDialog(userData?.addressUrl)

        }
    }

    fun showDialog(url:String?)
    {
        var dialog=AlertDialog.Builder(activity)
        var view=activity?.layoutInflater?.inflate(R.layout.image_viewer_dailog,null)
        var imagephoto=view?.findViewById<ImageView>(R.id.img_document_front)
        var closeBtn=view?.findViewById<Button>(R.id.closeDialog)
        dialog.setView(view!!)
        var alert=dialog.create()
        Glide.with(this).load(url).into(imagephoto!!)
        alert.show()
        closeBtn?.setOnClickListener {
            alert.dismiss()
        }
    }
}