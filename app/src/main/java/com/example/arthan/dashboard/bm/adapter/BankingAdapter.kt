package com.example.arthan.dashboard.bm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R

class BankingAdapter(private val context: Context): RecyclerView.Adapter<BankingAdapter.BankingVH>() {


    inner class BankingVH(root: View): RecyclerView.ViewHolder(root){

        fun bind(position:Int){

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankingVH {
        val view= LayoutInflater.from(context).inflate(R.layout.row_banking,parent,false)
        return BankingVH(view)
    }

    override fun getItemCount()=2

    override fun onBindViewHolder(holder: BankingVH, position: Int) {
    }
}