package com.example.arthan.dashboard.bcm

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.example.arthan.R
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.lead.model.postdata.TradeRefDetail
import com.example.arthan.lead.model.responsedata.DetailsResponseData

class UploadStatmentsDialog (context: Context,
                             detailsList: ArrayList<String>?
): RecyclerView.Adapter<UploadStatmentsDialog.TradeRefVH>() {
    private var context: Context = context
    private val mList: MutableList<String>? = detailsList

    inner class TradeRefVH(root: View): RecyclerView.ViewHolder(root) {

        fun bind(pos: Int) {

            if (mList?.size!! > 0) {

                itemView.findViewById<TextView>(R.id.tvStmt).text = mList[pos].toString()
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TradeRefVH {
        val view= LayoutInflater.from(context).inflate(R.layout.statement_upload_list_pattern,parent,false)
        return TradeRefVH(view)
    }

    override fun getItemCount(): Int = mList?.size!!

    override fun onBindViewHolder(holder: TradeRefVH, position: Int) {
        holder.bind(position)
    }
    private fun getAdapter(list: List<Data>?): DataSpinnerAdapter? =
        if (context != null)
            DataSpinnerAdapter(context!!, list?.toMutableList() ?: mutableListOf()).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            } else null

}