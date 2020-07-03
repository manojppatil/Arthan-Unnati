package com.example.arthan.dashboard.rm.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.dashboard.bm.model.RejectedCaseResponse
import com.example.arthan.network.RetrofitFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RejectedAdapter(private val context: Context,private val from: String,
private val data: List<RejectedCaseResponse>): RecyclerView.Adapter<RejectedAdapter.RejectedVH>() {


    inner class RejectedVH(private val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(position: Int) {

            if(from == "RM"){
                root.findViewById<Button>(R.id.btn_reopen).visibility= View.GONE
            }else {
                root.findViewById<Button>(R.id.btn_reopen).visibility= View.VISIBLE
                root.findViewById<Button>(R.id.btn_reopen).setOnClickListener {

                    CoroutineScope(Dispatchers.IO).launch {
                        var response=RetrofitFactory.getApiService().saveBMReopen(data[position].caseId)
                        withContext(Dispatchers.Main) {

                            if (response?.body()?.apiCode == "200") {
                                AlertDialog.Builder(context)
                                    .setMessage("Case Reopened Successfully")
                                    .setPositiveButton("Ok") { dialogInterface, _ ->
                                        dialogInterface.dismiss()
                                    }.create().show()

                            } else {
                                Toast.makeText(context, "Failed to update", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }

                }
            }

            root.findViewById<TextView>(R.id.txt_case_id).text= "Case Id: ${data[position].caseId}"
            root.findViewById<TextView>(R.id.txt_customer_name).text= "Customer Name: ${data[position].cname}"
            root.findViewById<TextView>(R.id.txt_amount).text= "Loan Amount: ₹ ${data[position].loanAmt}"
            root.findViewById<TextView>(R.id.txt_rejection_reason).text= "Rejection Reason: ${data[position].rejectReason}"
            root.findViewById<TextView>(R.id.txt_login_date).text= "Login Date: ${data[position].loginDate}"
            root.findViewById<TextView>(R.id.txt_rejection_date).text= "Rejected Date: ${data[position].rejectDate}"
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
        RejectedVH(LayoutInflater.from(context).inflate(R.layout.row_rejected, parent, false))

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: RejectedVH, position: Int) {
        holder.bind(position)
    }
}