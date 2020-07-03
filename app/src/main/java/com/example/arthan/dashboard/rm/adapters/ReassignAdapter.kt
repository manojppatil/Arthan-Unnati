package com.example.arthan.dashboard.rm.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.RmReassignNavActivity
import com.example.arthan.dashboard.rm.PendingInfoActivity
import com.example.arthan.dashboard.rm.RMReAssignListingActivity
import com.example.arthan.model.ReassignLeadData

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
            root.findViewById<TextView>(R.id.txt_caseid).text= data[position].loanId
            root.findViewById<TextView>(R.id.txt_customer_name).text= data[position].cname
            root.findViewById<TextView>(R.id.txt_assign_date).text= data[position].assignedDate
            var btnBusinessRm=root.findViewById<Button>(R.id.btn_business_rm)
            var btnIncomeRm=root.findViewById<Button>(R.id.btn_income_rm)
            var btnOthersRm=root.findViewById<Button>(R.id.btn_others_rm)
            var btnDocumentsRm=root.findViewById<Button>(R.id.btn_documents_rm)


            var activity : RMReAssignListingActivity=context as RMReAssignListingActivity


            root.setOnClickListener {

                val loanId = data[position].loanId
                activity.startActivity(Intent(activity, RmReassignNavActivity::class.java).apply {
                    putExtra("task", "RM_AssignList")
                    putExtra("loanId", loanId)
                })
                // activity.showPendingScreenList(data[position])

            }
            btnBusinessRm.setOnClickListener { 
                activity.showBusinessFragment(data[position].loanId)
            }
            btnIncomeRm.setOnClickListener { 
                activity.showIncomeFragment(data[position].loanId)
            }
            btnOthersRm.setOnClickListener { 
                activity.showOthersFragment(data[position].loanId)
            }
            btnDocumentsRm.setOnClickListener {

                activity.showDocumentsFragment(data[position].loanId)
            }
            //["Documents","Business","Income","Others"]
//            root.findViewById<TextView>(R.id.txt_stage).text= "Pending: ${data[position].pending}"
            for (doc in data[position].pending) {
                if (doc.equals("Documents", ignoreCase = true)) {
                    btnDocumentsRm.isEnabled = true
                    btnDocumentsRm.setBackgroundResource(R.drawable.curve_rect_btn_bg_enabled)
                    btnDocumentsRm.setTextColor(Color.parseColor("#ffffff"))
                }
                if (doc.equals("Business", ignoreCase = true)) {
                    btnBusinessRm.isEnabled = true
                    btnBusinessRm.setBackgroundResource(R.drawable.curve_rect_btn_bg_enabled)
                    btnBusinessRm.setTextColor(Color.parseColor("#ffffff"))

                }
                if (doc.equals("Income", ignoreCase = true)) {
                    btnIncomeRm.isEnabled = true
                    btnIncomeRm.setBackgroundResource(R.drawable.curve_rect_btn_bg_enabled)
                    btnIncomeRm.setTextColor(Color.parseColor("#ffffff"))

                }
                if (doc.equals("Others", ignoreCase = true)) {
                    btnOthersRm.isEnabled = true
                    btnOthersRm.setBackgroundResource(R.drawable.curve_rect_btn_bg_enabled)
                    btnOthersRm.setTextColor(Color.parseColor("#ffffff"))

                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
        ReassignVH(LayoutInflater.from(context).inflate(R.layout.row_reassignedby, parent, false))


    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ReassignVH, position: Int) {
        holder.bind(position)
    }
}