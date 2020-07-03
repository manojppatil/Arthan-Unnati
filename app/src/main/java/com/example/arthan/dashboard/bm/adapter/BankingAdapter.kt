package com.example.arthan.dashboard.bm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.dashboard.bm.model.Banking360DetailsResponseData

class BankingAdapter(
    context: Context,
    detailsList: ArrayList<Banking360DetailsResponseData>
): RecyclerView.Adapter<BankingAdapter.BankingVH>() {
    private val mList: MutableList<Banking360DetailsResponseData> = detailsList
    private var context:Context = context

    inner class BankingVH(root: View): RecyclerView.ViewHolder(root){

        fun bind(position:Int){

                itemView.findViewById<TextView?>(R.id.txt_bank_name)?.text =
                    mList[position].bankName
                itemView.findViewById<TextView?>(R.id.txt_avg_bal_amt)?.text =
                    mList[position].avgBankBal
                itemView.findViewById<TextView?>(R.id.txt_credit_summation)?.text ="Total Net Credits - "+ mList[position].totalNetCredits
                itemView.findViewById<TextView?>(R.id.txt_bounces)?.text ="Total Cash Deposit - "+ mList[position].totalCashDeposit
                itemView.findViewById<TextView?>(R.id.txt_debit_summation)?.text ="Inward Bounce - "+ mList[position].inwardBounce
                itemView.findViewById<TextView?>(R.id.txt_abb)?.text = "Outward Bounce - "+ mList[position].outwardBounce
                itemView.findViewById<TextView?>(R.id.txt_no_of_credit_entries)?.text = "EMI Count - " +mList[position].emiCount
                itemView.findViewById<TextView?>(R.id.emiAmt)?.text = "EMI Amt - "+mList[position].emiAmt

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankingVH {
        val view= LayoutInflater.from(context).inflate(R.layout.row_banking,parent,false)
        return BankingVH(view)
    }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: BankingVH, position: Int) {
        holder.bind(position)
    }

    fun updateList(details: List<Banking360DetailsResponseData>) {
        mList.clear()
        mList.addAll(details)
        notifyDataSetChanged()
    }
}