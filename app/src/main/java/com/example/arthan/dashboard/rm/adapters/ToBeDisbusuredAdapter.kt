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
import com.example.arthan.dashboard.rm.PDTDocUploadActivity
import com.example.arthan.model.ToDisbursedData

class ToBeDisbusuredAdapter(private val context: Context,
private val data: List<ToDisbursedData>): RecyclerView.Adapter<ToBeDisbusuredAdapter.ToBeDisbusuredVH>() {


    inner class ToBeDisbusuredVH(private val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(position: Int) {
            root.findViewById<Button>(R.id.btn_reopen).visibility= View.GONE
            root.setOnClickListener {
                context.startActivity(Intent(context, PDTDocUploadActivity::class.java))
            }

            root.findViewById<TextView>(R.id.txt_caseid).text= "Case ID: ${data[position].caseId}"
            root.findViewById<TextView>(R.id.txt_customer_name).text= "Customer Name: ${data[position].name}"
            root.findViewById<TextView>(R.id.txt_doc_type).text= "Doc Type: ${data[position].docType}"

            root.findViewById<TextView>(R.id.txt_segment).text= "Segment: ${data[position].segment}"
            root.findViewById<TextView>(R.id.txt_days_passed).text= "Days Passed: ${data[position].daysPassed}"

            root.findViewById<TextView>(R.id.txt_target_date).text= "Target Date: ${data[position].targetDate}"
            root.findViewById<TextView>(R.id.txt_due_date).text= "Due Date: ${data[position].disbursalDate}"
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
        ToBeDisbusuredVH(LayoutInflater.from(context).inflate(R.layout.row_to_be_disbursed, parent, false))


    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ToBeDisbusuredVH, position: Int) {
        holder.bind(position)
    }
}