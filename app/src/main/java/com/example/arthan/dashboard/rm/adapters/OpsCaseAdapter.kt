package com.example.arthan.dashboard.rm.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.dashboard.rm.OpsCaseActivity
import com.example.arthan.dashboard.rm.PDTDocUploadActivity
import com.example.arthan.model.GetRMOpsCasesResponse
import com.example.arthan.model.ToDisbursedData

class OpsCaseAdapter(private val context: Context,
                     private val data: GetRMOpsCasesResponse): RecyclerView.Adapter<OpsCaseAdapter.ToBeDisbusuredVH>() {


    inner class ToBeDisbusuredVH(private val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(position: Int) {


            val activity=context as OpsCaseActivity
            root.setOnClickListener {

            }
            root.findViewById<TextView>(R.id.docName).text= data.documents[position].docName
            root.findViewById<RadioButton>(R.id.rbUpload).setOnCheckedChangeListener { _, isChecked ->

                if(isChecked)
                {
                        activity.updateList(position,"Upload")
                    activity.captureDoc(position)
                }
            }
            root.findViewById<RadioButton>(R.id.PDD).setOnCheckedChangeListener { _, isChecked ->
                if(isChecked)
                {
                    activity.updateList(position,"PDD")
                }
            }
            root.findViewById<RadioButton>(R.id.OTC).setOnCheckedChangeListener { _, isChecked ->

                if(isChecked)
                {
                    activity.updateList(position,"OTC")
                }
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
        ToBeDisbusuredVH(LayoutInflater.from(context).inflate(R.layout.ops_case_pattern, parent, false))


    override fun getItemCount() = data.documents.size

    override fun onBindViewHolder(holder: ToBeDisbusuredVH, position: Int) {
        holder.bind(position)
    }
}