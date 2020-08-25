package com.example.arthan.dashboard.bcm

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.RmReassignNavActivity
import com.example.arthan.dashboard.rm.PendingInfoActivity
import com.example.arthan.dashboard.rm.RMReAssignListingActivity
import com.example.arthan.model.ReAssignedData
import com.example.arthan.model.ReassignLeadData

class BCMReassignAdapter(
    private val context: Context,
    private val data: List<ReAssignedData>
): RecyclerView.Adapter<BCMReassignAdapter.ReassignVH>() {

    inner class ReassignVH(private val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(position: Int) {


            root.findViewById<TextView>(R.id.name).text = data[position].customerName
            root.findViewById<TextView>(R.id.loanAmt).text = data[position].loanAmount
            root.findViewById<TextView>(R.id.loginDate).text = data[position].loginDate
            root.findViewById<TextView>(R.id.assignedDate).text =data[position].assignedDate

            root.findViewById<TextView>(R.id.assignedBy).text = data[position].assignedBy
//            data[position].loanId


            var activity: BCMReAssignedActivity = context as BCMReAssignedActivity


            }

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
        ReassignVH(
            LayoutInflater.from(context).inflate(R.layout.bcm_reasign_row,
            parent,
            false
        ))


    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ReassignVH, position: Int) {
        holder.bind(position)
    }
}