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

class BureauDetailsLevel2Adapter (context: Context,
                                  detailsList: ArrayList<InnerDetailsBanking>?,private val typescreen:String
): RecyclerView.Adapter<BureauDetailsLevel2Adapter.BankingVH>() {
    private var context: Context = context
    private val mList: MutableList<InnerDetailsBanking>? = detailsList

    inner class BankingVH(root: View): RecyclerView.ViewHolder(root){

        fun bind(position:Int){

            if(mList?.size!!>0) {
                itemView.findViewById<TextView?>(R.id.amount)?.text = mList[position].amount
                itemView.findViewById<TextView?>(R.id.accnType)?.text = mList[position].accountType
                itemView.findViewById<TextView?>(R.id.date)?.text = mList[position].disbDate

                itemView.setOnClickListener {
                    context.startActivity(
                        Intent(
                            context,
                            BureauViewAllDetailsActivity::class.java
                        ).apply {
                            putExtra("data", mList[position])
                            putExtra("type", typescreen)
                        })
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankingVH {
        val view= LayoutInflater.from(context).inflate(R.layout.banking_details_level2_adapter_pattern,parent,false)
        return BankingVH(view)
    }

    override fun getItemCount(): Int = mList?.size!!

    override fun onBindViewHolder(holder: BankingVH, position: Int) {
        holder.bind(position)
    }
}