package com.example.arthan.dashboard.bm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.dashboard.bm.MultipleButtonClickListener
import com.example.arthan.dashboard.bm.model.Deviation

class DeviationsAdapter : RecyclerView.Adapter<DeviationsAdapter.ViewHolder>() {

     val mList: MutableList<Deviation> = mutableListOf()
    private var mButtonClickListener: MultipleButtonClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.row_deviation, parent, false)
        )

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViewHolder(mList[position], position)
    }

    fun updateListData(deviationRemark: String?, deviationDecision: String?, position: Int,deviationJustification:String) {
        mList[position].deviationRemark = deviationRemark
        mList[position].deviationDecision = deviationDecision
        mList[position].deviationJustification=deviationJustification
        notifyItemChanged(position)
    }

    fun updateList(list: List<Deviation>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    fun setButtonClickListener(multipleButtonClickListener: MultipleButtonClickListener) {
        mButtonClickListener = multipleButtonClickListener
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindViewHolder(item: Deviation, position: Int) {
            itemView?.findViewById<TextView?>(R.id.description)?.text = item.deviationDesc
            itemView?.findViewById<View?>(R.id.approve_button)
                ?.setOnClickListener { mButtonClickListener?.onFirstButtonClick(position) }
            itemView?.findViewById<View?>(R.id.reject_button)
                ?.setOnClickListener { mButtonClickListener?.onSecondButtonClick(position) }
            itemView?.findViewById<View?>(R.id.recommend_button)
                ?.setOnClickListener { mButtonClickListener?.onThirdButtonClick(position) }
        }
    }
}