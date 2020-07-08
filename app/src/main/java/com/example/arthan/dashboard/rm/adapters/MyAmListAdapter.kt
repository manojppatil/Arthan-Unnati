package com.example.arthan.dashboard.rm.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.dashboard.rm.PDTDocUploadActivity
import com.example.arthan.model.AmListModel
import com.example.arthan.model.ToDisbursedData

class MyAmListAdapter (private val context: Context,
                       private val data: List<AmListModel>): RecyclerView.Adapter<MyAmListAdapter.AmList>() {


    inner class AmList(private val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(position: Int) {
                root.findViewById<TextView>(R.id.nameValue).text=data[position].name
                root.findViewById<TextView>(R.id.dateValue).text=data[position].submittedDate
                root.findViewById<TextView>(R.id.statusValue).text=data[position].status
                root.findViewById<Button>(R.id.resend).setOnClickListener {
                    Toast.makeText(context,"Link resent",Toast.LENGTH_LONG).show()
                }

          }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
        AmList(LayoutInflater.from(context).inflate(R.layout.am_adapter_pattern, parent, false))


    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: AmList, position: Int) {
        holder.bind(position)
    }
}