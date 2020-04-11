package com.example.arthan.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.views.activities.SubmitFinalReportActivity

class SanctionAdapter(private val context: Context,private val sanctionList:ArrayList<String>) : RecyclerView.Adapter<SanctionAdapter.SanctionVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
         SanctionVH(LayoutInflater.from(context).inflate(R.layout.sanction_row, parent, false))


    override fun getItemCount(): Int =sanctionList.size
    override fun onBindViewHolder(holder: SanctionVH, position: Int) {
        holder.bind(position)
    }
    inner class SanctionVH( private val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(position: Int) {

            root.findViewById<Button>(R.id.remove).setOnClickListener {
                var activity:SubmitFinalReportActivity=context as SubmitFinalReportActivity
                activity.removeSanctionField(position)
            }
        }
    }
}
