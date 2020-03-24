package com.example.arthan.views

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.arthan.R

class ApprovalDialog(context: Context): Dialog(context),View.OnClickListener {

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_ok -> dismiss()
            R.id.btn_cancel -> dismiss()
        }
    }

    init {

        val view= LayoutInflater.from(context).inflate(R.layout.dialog_approval_remark,null,false)
        setContentView(view)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        view.findViewById<Button>(R.id.btn_ok).setOnClickListener(this)
        view.findViewById<Button>(R.id.btn_cancel).setOnClickListener(this)
    }
}