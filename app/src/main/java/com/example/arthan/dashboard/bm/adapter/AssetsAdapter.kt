package com.example.arthan.dashboard.bm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R

class AssetsAdapter(private val context: Context): RecyclerView.Adapter<AssetsAdapter.AssetVH>() {


    inner class AssetVH(root: View): RecyclerView.ViewHolder(root){

        fun bind(){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetVH {
        val view= LayoutInflater.from(context).inflate(R.layout.row_asset,parent,false)
        return AssetVH(view)
    }

    override fun getItemCount()= 2

    override fun onBindViewHolder(holder: AssetVH, position: Int) {
    }
}