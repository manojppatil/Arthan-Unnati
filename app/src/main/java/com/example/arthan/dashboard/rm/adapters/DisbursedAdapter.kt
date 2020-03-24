package com.example.arthan.dashboard.rm.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R

class DisbursedAdapter(private val context: Context): RecyclerView.Adapter<DisbursedAdapter.DisbursedVH>() {


    inner class DisbursedVH(root: View) : RecyclerView.ViewHolder(root) {

        fun bind(position: Int) {

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisbursedVH {
        val view = LayoutInflater.from(context).inflate(R.layout.row_disbursed, parent, false)
        return DisbursedVH(view)
    }

    override fun getItemCount() = 5

    override fun onBindViewHolder(holder: DisbursedVH, position: Int) {
    }
}