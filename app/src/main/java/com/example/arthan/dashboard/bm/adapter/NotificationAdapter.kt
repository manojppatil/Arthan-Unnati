package com.example.arthan.dashboard.bm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R

class NotificationAdapter(private val context: Context): RecyclerView.Adapter<NotificationAdapter.NotificationVH>(){


    inner class NotificationVH(root: View): RecyclerView.ViewHolder(root){

        fun bind(position: Int){

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationVH {
        val view= LayoutInflater.from(context).inflate(R.layout.row_notification,parent,false)
        return NotificationVH(view)
    }

    override fun getItemCount()= 5

    override fun onBindViewHolder(holder: NotificationVH, position: Int) {
        holder.bind(position)
    }

}