package com.example.arthan.dashboard.bm.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.dashboard.bm.NotifyRMDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior

class RMStatusAdapter(private val context: Context): RecyclerView.Adapter<RMStatusAdapter.RMStatusVH>() {

    inner class RMStatusVH(private val root: View): RecyclerView.ViewHolder(root){

        fun bind(position:Int){

            root.findViewById<TextView>(R.id.txt_notify_rm).setOnClickListener {
                NotifyRMDialog(context).show()
            }

            /*val sheetBehavior = BottomSheetBehavior.from(root.findViewById<LinearLayout>(R.id.ll_bottonsheet))

            root.findViewById<TextView>(R.id.txt_notify_rm).setOnClickListener {

                if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    //root.findViewById<LinearLayout>(R.id.bs_notify).visibility= View.VISIBLE
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                } else {
                    sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    //root.findViewById<LinearLayout>(R.id.bs_notify).visibility= View.GONE
                }
            }*/
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RMStatusVH {
        val view= LayoutInflater.from(context).inflate(R.layout.row_rm_status,parent,false)
        return RMStatusVH(view)
    }

    override fun getItemCount()=2

    override fun onBindViewHolder(holder: RMStatusVH, position: Int) {
        holder.bind(position)
    }
}