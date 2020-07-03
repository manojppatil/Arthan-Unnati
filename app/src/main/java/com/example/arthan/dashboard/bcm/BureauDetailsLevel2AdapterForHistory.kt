package com.example.arthan.dashboard.bcm

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.dashboard.bm.adapter.BankingEntriesAdapter
import com.example.arthan.dashboard.bm.model.EntriesType
import com.example.arthan.dashboard.bm.model.InnerDetailsBanking
import com.example.arthan.dashboard.bm.model.Last6MonthsHistory

class BureauDetailsLevel2AdapterForHistory (context: Context,
                                            detailsList: ArrayList<Last6MonthsHistory>?
): RecyclerView.Adapter<BureauDetailsLevel2AdapterForHistory.BankingVH>() {
    private var context: Context = context
    private val mList: MutableList<Last6MonthsHistory>? = detailsList

    inner class BankingVH(root: View): RecyclerView.ViewHolder(root){

        fun bind(position:Int){

            if(mList?.size!!>0) {
                itemView.findViewById<TextView?>(R.id.remark)?.text = mList[position].remark
                itemView.findViewById<TextView?>(R.id.amountHistory)?.text = mList[position].amount
                itemView.findViewById<TextView?>(R.id.memName)?.text = mList[position].memberName
                itemView.findViewById<TextView?>(R.id.purpoe)?.text = mList[position].purpose
                itemView.findViewById<TextView?>(R.id.inqDate)?.text = mList[position].inquiryDate

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankingVH {
        val view= LayoutInflater.from(context).inflate(R.layout.sixmonthhistory,parent,false)
        return BankingVH(view)
    }

    override fun getItemCount(): Int = mList?.size!!

    override fun onBindViewHolder(holder: BankingVH, position: Int) {
        holder.bind(position)
    }
}