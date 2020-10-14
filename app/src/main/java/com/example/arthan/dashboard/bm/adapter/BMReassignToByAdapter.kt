package com.example.arthan.dashboard.bm.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.model.BMCasesData


class BMReassignToByAdapter(
    private val context: Context,
    private val id: String,
   private val mList: ArrayList<BMCasesData>
) : RecyclerView.Adapter<BMReassignToByAdapter.ViewHolder>() {


//     val mList: MutableList<BMCasesData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater
                .from(context)
                .inflate(R.layout.bm_reassign_to, parent, false)
        )

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViewHolder(mList[position], position)
    }



    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindViewHolder(item: BMCasesData, position: Int) {
            itemView?.findViewById<TextView?>(R.id.caseId)?.text = item.caseId
            itemView?.findViewById<TextView?>(R.id.cname)?.text = item.customerName
            itemView?.findViewById<TextView?>(R.id.loanAmnt)?.text = item.loanAmount
            itemView?.findViewById<TextView?>(R.id.assignedBy)?.text=item.assignedBy
            itemView?.findViewById<TextView?>(R.id.assignedOn)?.text=item.assignedOn
//            itemView?.findViewById<TextView?>(R.id.remarks)?.text=item.remarks

           /* itemView?.findViewById<TextView?>(R.id.remarks)?.isClickable = true
            itemView?.findViewById<TextView?>(R.id.remarks)?.movementMethod = LinkMovementMethod.getInstance()
            val text = "click to view"
            itemView?.findViewById<TextView?>(R.id.remarks)?.text = text*/
            itemView?.findViewById<TextView?>(R.id.loginDate)?.text=item.loginDate

            if(item.remarks!=null&&item.remarks!="") {
                val udata = "Click to View"
                val content = SpannableString(udata)
                content.setSpan(UnderlineSpan(), 0, udata.length, 0)
                itemView?.findViewById<TextView?>(R.id.remarks)?.text = (content)
                itemView?.findViewById<TextView?>(R.id.remarks)?.setOnClickListener {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Remarks")
                    builder.setMessage(item.remarks)
                    builder.setPositiveButton(
                        "Ok",
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        })
                    builder.create().show()
                }
            }else
            {
                itemView?.findViewById<TextView?>(R.id.remarks)?.text = "No Comments found"

            }

        }
    }
}