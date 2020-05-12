package com.example.arthan.dashboard.bm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.lead.model.responsedata.Assets

class AssetsAdapter(
    private val context: Context,
    private val assetsList: ArrayList<Assets?>?
): RecyclerView.Adapter<AssetsAdapter.AssetVH>() {


    inner class AssetVH(val root: View): RecyclerView.ViewHolder(root){

        fun bind(position: Int) {

            val sp_asset=root.findViewById<Spinner>(R.id.sp_assetType)
            val et_OwnerName=root.findViewById<EditText>(R.id.et_OwnerName)
            val et_purchaseYear=root.findViewById<EditText>(R.id.et_purchaseYear)
           val  et_purchaseValue=root.findViewById<EditText>(R.id.et_purchaseValue)
            val et_CurrentValue=root.findViewById<EditText>(R.id.et_CurrentValue)
           val  et_Adress=root.findViewById<EditText>(R.id.et_Adress)
           val  et_assetType=root.findViewById<EditText>(R.id.et_assetType)

            et_OwnerName.setText(assetsList?.get(position)?.ownerName)
            et_purchaseYear.setText(assetsList?.get(position)?.purchaseYear)
            et_purchaseValue.setText(assetsList?.get(position)?.purchaseValue)
            et_CurrentValue.setText(assetsList?.get(position)?.currentValue)
            et_Adress.setText(assetsList?.get(position)?.assetAddress)
            et_assetType.setText(assetsList?.get(position)?.assetType)


            sp_asset.isEnabled=false
            sp_asset.visibility=View.GONE
            et_assetType.visibility=View.VISIBLE
            et_OwnerName.isFocusable=false
            et_purchaseYear.isFocusable=false
            et_purchaseValue.isFocusable=false
            et_CurrentValue.isFocusable=false
            et_Adress.isFocusable=false

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetVH {
//        val view= LayoutInflater.from(context).inflate(R.layout.row_asset,parent,false)
        val view= LayoutInflater.from(context).inflate(R.layout.assets_section,parent,false)
        return AssetVH(view)
    }

    override fun getItemCount()= assetsList!!.size

    override fun onBindViewHolder(holder: AssetVH, position: Int) {
        holder.bind(position)

    }
}