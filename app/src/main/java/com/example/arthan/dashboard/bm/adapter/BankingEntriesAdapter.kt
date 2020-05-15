package com.example.arthan.dashboard.bm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.dashboard.bm.model.EntriesType

class BankingEntriesAdapter(context: Context,
                            detailsList: ArrayList<EntriesType>
): RecyclerView.Adapter<BankingEntriesAdapter.BankingVH>() {
    private var context:Context = context
    private val mList: MutableList<EntriesType> = detailsList

    inner class BankingVH(root: View): RecyclerView.ViewHolder(root){

        fun bind(position:Int){

            itemView.findViewById<TextView?>(R.id.txnDate)?.text =  mList[position].txnDate
            itemView.findViewById<TextView?>(R.id.narration)?.text =mList[position].narration
            itemView.findViewById<TextView?>(R.id.amount)?.text = mList[position].amount

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankingVH {
        val view= LayoutInflater.from(context).inflate(R.layout.banking_entries_pattern,parent,false)
        return BankingVH(view)
    }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: BankingVH, position: Int) {
        holder.bind(position)
    }
}