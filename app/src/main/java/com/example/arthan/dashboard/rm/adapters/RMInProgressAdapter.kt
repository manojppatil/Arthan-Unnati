package com.example.arthan.dashboard.rm.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.dashboard.bm.adapter.InProgressAdapter
import com.example.arthan.model.CasesData

class RMInProgressAdapter (private val context: Context,private val list:ArrayList<CasesData>): RecyclerView.Adapter<RMInProgressAdapter.InProgressVH>() {


    inner class InProgressVH(private val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(position: Int) {
            root.findViewById<TextView>(R.id.caseId).text = list[position].caseId
            root.findViewById<TextView>(R.id.cname).text = list[position].cname
            root.findViewById<TextView>(R.id.loginDate).text = list[position].loginDate
            root.findViewById<TextView>(R.id.loanAmt).text = list[position].loanAmt
            root.findViewById<TextView>(R.id.stage).text = list[position].currentStage

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InProgressVH {
        val view = LayoutInflater.from(context).inflate(R.layout.rm_progress_row, parent, false)
        return InProgressVH(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: InProgressVH, position: Int) {
        holder.bind(position)
    }
}