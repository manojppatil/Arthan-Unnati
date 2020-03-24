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
import com.example.arthan.model.RejectedCaseData

class RejectedAdapter(private val context: Context,private val from: String,
private val data: List<RejectedCaseData>): RecyclerView.Adapter<RejectedAdapter.RejectedVH>() {


    inner class RejectedVH(private val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(position: Int) {

            if(from == "RM"){
                root.findViewById<Button>(R.id.btn_reopen).visibility= View.GONE
            }else {
                root.findViewById<Button>(R.id.btn_reopen).visibility= View.VISIBLE
                root.findViewById<Button>(R.id.btn_reopen).setOnClickListener {
                    AlertDialog.Builder(context)
                        .setMessage("Case Reopened Successfully")
                        .setPositiveButton("Ok") { dialogInterface, i ->
                            dialogInterface.dismiss()
                        }.create().show()
                }
            }

            root.findViewById<TextView>(R.id.txt_case_id).text= "Case Id: ${data[position].caseId}"
            root.findViewById<TextView>(R.id.txt_customer_name).text= "Customer Name: ${data[position].name}"
            root.findViewById<TextView>(R.id.txt_amount).text= "Loan Amount: ₹ ${data[position].loanAmount}"
            root.findViewById<TextView>(R.id.txt_rejection_reason).text= "Rejection Reason: ${data[position].rejectionReason}"
            root.findViewById<TextView>(R.id.txt_login_date).text= "Login Date: ${data[position].loginDate}"
            root.findViewById<TextView>(R.id.txt_rejection_date).text= "Rejected Date: ${data[position].rejectionDate}"
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
        RejectedVH(LayoutInflater.from(context).inflate(R.layout.row_rejected, parent, false))

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: RejectedVH, position: Int) {
        holder.bind(position)
    }
}