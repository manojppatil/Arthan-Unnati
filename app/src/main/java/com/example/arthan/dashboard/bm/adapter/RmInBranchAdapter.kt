package com.example.arthan.dashboard.bm.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.dashboard.bm.RmStatusListingActivity
import com.example.arthan.model.BmRmStatusModel
import com.example.arthan.views.activities.PendingCustomersActivity

class RmInBranchAdapter(private val context: Context,private val list:BmRmStatusModel): RecyclerView.Adapter<RmInBranchAdapter.BranchRMVH>(),View.OnClickListener{

    inner class BranchRMVH(private val root: View): RecyclerView.ViewHolder(root){

        init {

            root.setOnClickListener {
                context.startActivity(Intent(context, RmStatusListingActivity::class.java))
            }
        }

        fun bind(position: Int){

            root.findViewById<TextView>(R.id.txt_rm_name).text=list.status[position].rmName
            root.findViewById<TextView>(R.id.txt_rm_employee_id).text=list.status[position].rmId
            root.findViewById<TextView>(R.id.txt_rm_leads_count).text=list.status[position].leadCnt
            root.findViewById<TextView>(R.id.txt_rm_screening_count).text=list.status[position].screeningCnt
            root.findViewById<TextView>(R.id.txt_rm_pending_count).text=list.status[position].loginCnt
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BranchRMVH {
        val view= LayoutInflater.from(context).inflate(R.layout.row_rm_in_branch,parent,false)
        return BranchRMVH(view)
    }

    override fun getItemCount()= list.status.size

    override fun onBindViewHolder(holder: BranchRMVH, position: Int) {
        holder.bind(position)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.txt_rm_leads,R.id.txt_rm_leads_count -> {

            }

            R.id.txt_rm_screening, R.id.txt_rm_screening_count -> {

            }

            R.id.txt_rm_pending, R.id.txt_rm_pending_count -> {
                context.startActivity(Intent(context, PendingCustomersActivity::class.java))
            }
        }
    }

}