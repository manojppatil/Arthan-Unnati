package com.example.arthan.dashboard.rm.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.dashboard.bm.ApprovedCustomerLegalStatusActivity
import com.example.arthan.dashboard.bm.ApprovedCustomerReviewActivity
import com.example.arthan.model.ApprovedCaseData
import java.io.Serializable

class ApprovedAdapter(private val context: Context,
                      private val from: String,
private val data: List<ApprovedCaseData>): RecyclerView.Adapter<ApprovedAdapter.ApprovedVH>() {

    inner class ApprovedVH(private val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(position: Int) {

            when(from){
                "BM","BCM" -> {
                    root.setOnClickListener {
                        context.startActivity(Intent(context, ApprovedCustomerLegalStatusActivity::class.java).apply {
                            putExtra("FROM",from)
                            putExtra("Name",this@ApprovedAdapter.data.get(position).name)
                            putExtra("rcu",this@ApprovedAdapter.data.get(position).rcuStatus)
                            putExtra("legal",this@ApprovedAdapter.data.get(position).legalStatus)
                            putExtra("tech",this@ApprovedAdapter.data.get(position).techStatus)
                            putExtra("object",this@ApprovedAdapter.data.get(position) as Serializable)

                        })
                    }
                }
                else -> {
                    root.setOnClickListener(null)
                }
            }
            root.findViewById<TextView>(R.id.txt_case_id).text= "CaseId: ${data[position].caseId}"
            root.findViewById<TextView>(R.id.txt_customer_name).text= "Customer Name: ${data[position].name}"
            root.findViewById<TextView>(R.id.txt_amount).text= "Approved Amount: ${data[position].approvedAmt}"
            root.findViewById<TextView>(R.id.txt_tenure).text= "Tenure: ${data[position].tenure}"
            root.findViewById<TextView>(R.id.txt_emi).text= "EMI: ${data[position].emi}"
            root.findViewById<TextView>(R.id.txt_loan_type).text= "Loan Type: ${data[position].loanType}"
            root.findViewById<TextView>(R.id.txt_login_date).text= "Login Date: ${data[position].loginDate}"
            root.findViewById<TextView>(R.id.txt_approved_date).text= "Approved Date: ${data[position].approvedDate}"

            var btnRcu=root.findViewById<Button>(R.id.btn_rcu)
            var btnLegal=root.findViewById<Button>(R.id.btn_legal)
            var btnTech=root.findViewById<Button>(R.id.btn_tech)
            if(data[position].rcuStatus.toString().contentEquals("Y"))
            {
                btnRcu.setBackgroundResource(R.drawable.curve_rect_btn_bg_enabled)
                btnRcu.setTextColor(Color.WHITE)
            }
            if(data[position].legalStatus.toString().contentEquals("Y"))
            {
                btnLegal.setBackgroundResource(R.drawable.curve_rect_btn_bg_enabled)
                btnLegal.setTextColor(Color.WHITE)
            }
            if(data[position].techStatus.toString().contentEquals("Y"))
            {
                btnTech.setBackgroundResource(R.drawable.curve_rect_btn_bg_enabled)
                btnTech.setTextColor(Color.WHITE)
            }
            if(data[position].feePaidStatus.toString().contentEquals("Y"))
            {
                root.findViewById<TextView>(R.id.txt_fee_paid).setTextColor(Color.parseColor("#43A047"))
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
        ApprovedVH(LayoutInflater.from(context).inflate(R.layout.row_approved, parent, false))


    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ApprovedVH, position: Int) {
        holder.bind(position)
    }
}