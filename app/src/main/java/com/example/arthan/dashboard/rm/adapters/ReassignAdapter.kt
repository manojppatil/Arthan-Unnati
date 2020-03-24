package com.example.arthan.dashboard.rm.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.dashboard.rm.PendingInfoActivity
import com.example.arthan.model.ReassignLeadData
import org.w3c.dom.Text

class ReassignAdapter(private val context: Context,
                      private val from:String,
private val data: List<ReassignLeadData>): RecyclerView.Adapter<ReassignAdapter.ReassignVH>() {

    inner class ReassignVH(private val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(position: Int) {
            when (from) {
                "REASSIGN" -> {
                    root.findViewById<TextView>(R.id.txt_amount).text= "Assigned By: ${data[position].assignedBy}"
                    root.findViewById<TextView>(R.id.txt_segment).apply {
                        visibility= View.VISIBLE
                        text= data[position].segment
                    }
                    root.setOnClickListener {
                        context.startActivity(Intent(context,PendingInfoActivity::class.java))
                    }
                }
                "REASSIGN-BY" -> {
                    root.findViewById<TextView>(R.id.txt_amount).text= "Assigned By: BM"
                    root.findViewById<TextView>(R.id.txt_segment).apply {
                        visibility= View.GONE
                        text= data[position].segment
                    }
                    root.setOnClickListener {
                        context.startActivity(Intent(context,PendingInfoActivity::class.java))
                    }
                }
                "REASSIGN-TO" -> {
                    root.findViewById<TextView>(R.id.txt_segment).apply {
                        visibility= View.GONE
                        text= data[position].segment
                    }
                    root.findViewById<TextView>(R.id.txt_amount).text= "Assigned To: RM"
                }
            }
            root.findViewById<TextView>(R.id.txt_caseid).text= data[position].id
            root.findViewById<TextView>(R.id.txt_customer_name).text= data[position].name
            root.findViewById<TextView>(R.id.txt_assign_date).text= data[position].assignedDate
            root.findViewById<TextView>(R.id.txt_stage).text= "Pending: ${data[position].pending}"
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
        ReassignVH(LayoutInflater.from(context).inflate(R.layout.row_reassignedby, parent, false))


    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ReassignVH, position: Int) {
        holder.bind(position)
    }
}