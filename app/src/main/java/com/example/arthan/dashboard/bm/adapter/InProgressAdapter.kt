package com.example.arthan.dashboard.bm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R

class InProgressAdapter(private val context: Context): RecyclerView.Adapter<InProgressAdapter.InProgressVH>() {


    inner class InProgressVH(private val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(position: Int) {

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InProgressVH {
        val view = LayoutInflater.from(context).inflate(R.layout.row_in_progress, parent, false)
        return InProgressVH(view)
    }

    override fun getItemCount() = 5

    override fun onBindViewHolder(holder: InProgressVH, position: Int) {
    }
}