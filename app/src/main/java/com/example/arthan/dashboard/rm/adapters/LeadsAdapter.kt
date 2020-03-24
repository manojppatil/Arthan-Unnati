package com.example.arthan.dashboard.rm.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.model.LeadData
import org.w3c.dom.Text

class LeadsAdapter(private val context: Context,
                   private val from: String,
private val data: List<LeadData>): RecyclerView.Adapter<LeadsAdapter.LeadVH>() {


    inner class LeadVH(private val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(position: Int) {
            if(from == "RM") {
                root.findViewById<Button>(R.id.btn_reopen).visibility = View.GONE
            } else {
                root.findViewById<Button>(R.id.btn_reopen).visibility = View.VISIBLE
                root.findViewById<Button>(R.id.btn_reopen).setOnClickListener {
                    AlertDialog.Builder(context)
                        .setMessage("Rahul, loan rejected Successfully")
                        .setPositiveButton("Ok") { dialogInterface, i ->
                            dialogInterface.dismiss()
                        }.create().show()
                }
            }

            root.findViewById<TextView>(R.id.txt_occupation).text= data[position].segment
            root.findViewById<TextView>(R.id.txt_customer_name).text= "Customer Name: ${data[position].name}"
            root.findViewById<TextView>(R.id.txt_loan_id).text= "Loan Id: ${data[position].id}"
            root.findViewById<TextView>(R.id.txt_loan_amount).text= "Later Date: ${data[position].laterDate}"
            root.findViewById<TextView>(R.id.txt_contact_number).text= "${data[position].mobileNo}"
            root.findViewById<TextView>(R.id.txt_stage).text= "Branch: ${data[position].branch}"

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
        LeadVH(LayoutInflater.from(context).inflate(R.layout.row_lead, parent, false))

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: LeadVH, position: Int) {
        holder.bind(position)
    }
}