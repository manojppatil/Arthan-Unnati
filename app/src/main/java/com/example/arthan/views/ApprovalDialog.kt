package com.example.arthan.views

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import com.example.arthan.R
import com.example.arthan.dashboard.rm.PDTDocUploadActivity

class ApprovalDialog(context: Context, private val pos: Int): Dialog(context) {

    private var selectedType=""
    private var remarks=""

    init {

        val view= LayoutInflater.from(context).inflate(R.layout.dialog_approval_remark,null,false)
        setContentView(view)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        view.findViewById<Button>(R.id.btn_ok).setOnClickListener{
          if( view.findViewById<RadioButton>(R.id.pddRb).isChecked)
          {
              selectedType="PDD"
          }else if(view.findViewById<RadioButton>(R.id.rbOTC).isChecked)
          {
              selectedType="OTC"

          }else
          {
              selectedType=(context as PDTDocUploadActivity).rmDocsRequestBuilder.pendingDocs[pos].docStatus
          }

            (context as PDTDocUploadActivity).rmDocsRequestBuilder.pendingDocs[pos].docStatus=selectedType

            (context as PDTDocUploadActivity).rmDocsRequestBuilder.pendingDocs[pos].docRemarks=
                view.findViewById<EditText>(R.id.et_remark).text.toString()
            dismiss()
        }
        view.findViewById<Button>(R.id.btn_cancel).setOnClickListener{
            dismiss()
        }
        view.findViewById<RadioButton>(R.id.pddRb).setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {
             selectedType="PDD"
            }
        }
        view.findViewById<RadioButton>(R.id.rbOTC).setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {

                selectedType="OTC"
            }
        }
    }
}