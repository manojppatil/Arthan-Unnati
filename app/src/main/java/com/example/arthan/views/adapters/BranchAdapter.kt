package com.example.arthan.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.model.Branch
import com.example.arthan.utils.getRupeeSymbol
import com.example.arthan.views.activities.PendingCustomersActivity

class BranchAdapter : RecyclerView.Adapter<BranchAdapter.BranchViewHolder>() {

    val list: MutableList<Branch> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BranchViewHolder =
        BranchViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_screening_customers,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: BranchViewHolder, position: Int) {
        holder.bind(position, list[position])
    }

    fun updateList(branchList: List<Branch>) {
        list.addAll(branchList)
    }

    inner class BranchViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
        init {
            itemView.findViewById<TextView?>(R.id.txt_business)?.text = ""
            itemView.findViewById<TextView?>(R.id.txt_business)?.visibility = View.GONE
        }

        fun bind(position: Int, branch: Branch) {
            itemView.findViewById<TextView?>(R.id.txt_customer_name)?.apply {
                text = branch.branchName
            }
            itemView.findViewById<TextView?>(R.id.txt_amount)?.apply {
                text = "${branch.amount} Lakh"
                setCompoundDrawables(
                    getRupeeSymbol(
                        itemView.context,
                        textSize,
                        currentTextColor
                    ), null, null, null
                )
            }
            itemView.setOnClickListener {
                PendingCustomersActivity.startMe(itemView.context, "OPS")
            }
        }
    }
}