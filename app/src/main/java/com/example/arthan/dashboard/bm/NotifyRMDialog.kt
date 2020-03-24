package com.example.arthan.dashboard.bm

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.arthan.R

class NotifyRMDialog(context: Context): Dialog(context, R.style.NotifyRMAnim) {

    init {

        val view= LayoutInflater.from(context).inflate(R.layout.dialog_notify_rm,null,false)
        setContentView(view)

        /*setCancelable(false)
        setCanceledOnTouchOutside(false)*/
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.BOTTOM)
        window?.setBackgroundDrawableResource(android.R.color.white)

        view.findViewById<TextView>(R.id.txt_notify_rm).setOnClickListener {
            dismiss()
        }

    }

}