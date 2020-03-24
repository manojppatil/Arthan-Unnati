package com.example.arthan.utils

import android.app.Dialog
import android.content.Context
import com.example.arthan.R

class ProgrssLoader(context: Context): Dialog(context) {

    init {
        setContentView(R.layout.dialog_progress_loader)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    fun showLoading(){
        if (!isShowing) {
            show()
        }
    }

    fun dismmissLoading(){
        if (isShowing) {
            dismiss()
        }
    }
}