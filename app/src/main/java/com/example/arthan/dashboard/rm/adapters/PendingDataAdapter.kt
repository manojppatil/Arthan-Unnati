package com.example.arthan.dashboard.rm.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R

class PendingDataAdapter(val context: Context): RecyclerView.Adapter<PendingDataAdapter.PendingDataVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingDataVH {
        val view= LayoutInflater.from(context).inflate(R.layout.row_pending_data,parent,false)
        return PendingDataVH(view)
    }

    override fun getItemCount()= 2

    override fun onBindViewHolder(holder: PendingDataVH, position: Int) {
        holder.bind(position)
    }

    inner class PendingDataVH(val root: View): RecyclerView.ViewHolder(root){

        fun bind(position: Int){
            if(position == 0){
                root.findViewById<TextView>(R.id.txt_label).text= "TurnOver"
            } else {
                root.findViewById<TextView>(R.id.txt_label).text= "PAN Number"
            }

        }
    }
}