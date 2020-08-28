package com.example.arthan.dashboard.rm.adapters

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
import com.example.arthan.dashboard.rm.OpsCaseActivity
import com.example.arthan.dashboard.rm.PendingInfoActivity
import com.example.arthan.dashboard.rm.RMReAssignListingActivity
import com.example.arthan.global.ArthanApp
import com.example.arthan.model.ReassignLeadData

class ReassignAdapter(
    private val context: Context,
    private val from: String,
    private val data: List<ReassignLeadData>,
    private val tile: String?
): RecyclerView.Adapter<ReassignAdapter.ReassignVH>() {

    inner class ReassignVH(private val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(position: Int) {

            if(tile=="AMCASES")
            {
                root.findViewById<ConstraintLayout>(R.id.clparent).visibility=View.GONE
                root.findViewById<LinearLayout>(R.id.amCasesLL).visibility=View.VISIBLE
                var amCname=root.findViewById<TextView>(R.id.cnameAM)
                var amlDate=root.findViewById<TextView>(R.id.amLdate)
                var amltype=root.findViewById<TextView>(R.id.amLType)
                var amlamt=root.findViewById<TextView>(R.id.amLAmt)
                var amName=root.findViewById<TextView>(R.id.amAMName)
                amCname.text="Customer Name: "+data[position].cname
                amlDate.text="Login Date:"+data[position].loginDate
                amlamt.text="Loan Amt:"+data[position].loanAmt
                amltype.text="Loan Type"+data[position].loanType
                amName.text="AM: "+data[position].amName

            }else
            {
                root.findViewById<ConstraintLayout>(R.id.clparent).visibility=View.VISIBLE
                root.findViewById<LinearLayout>(R.id.amCasesLL).visibility=View.GONE

            }
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
            root.findViewById<TextView>(R.id.txt_loanamount).text= "Loan Amt: "+data[position].loanAmt
            root.findViewById<TextView>(R.id.txt_phone).text= "Mobile No.: "+data[position].mobNo
            var btnBusinessRm=root.findViewById<Button>(R.id.btn_business_rm)
            var btnIncomeRm=root.findViewById<Button>(R.id.btn_income_rm)
            var btnOthersRm=root.findViewById<Button>(R.id.btn_others_rm)
            var btnDocumentsRm=root.findViewById<Button>(R.id.btn_documents_rm)




            var activity : RMReAssignListingActivity=context as RMReAssignListingActivity


            root.setOnClickListener {


                if( data[position].opsCase.equals("yes",ignoreCase = true)&&ArthanApp.getAppInstance().loginRole=="RM")
                    {
                        val loanId = data[position].loanId
                        activity.startActivity(
                            Intent(
                                activity,
                                OpsCaseActivity::class.java
                            ).apply {
                                putExtra("task", "RM_AssignList")
                                putExtra("loanId", loanId)
                                putExtra("tile", tile)

                            })

                    }else {
                    val loanId = data[position].loanId
                    activity.startActivity(
                        Intent(
                            activity,
                            RmReassignNavActivity::class.java
                        ).apply {
                            putExtra("task", "RM_AssignList")
                            putExtra("loanId", loanId)
                            putExtra("tile", tile)

                        })
                }
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