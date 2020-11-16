package com.example.arthan.dashboard.bcm

import android.content.Context
import android.content.Intent
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

class TradeRefDetailsAdapter (context: Context,
                              detailsList: ArrayList<TradeRefDetail>?, private val typescreen:String,private val tradeAdapterResponse: DetailsResponseData
): RecyclerView.Adapter<TradeRefDetailsAdapter.TradeRefVH>() {
    private var context: Context = context
    private val mList: MutableList<TradeRefDetail>? = detailsList

    var counter:Int=2
    inner class TradeRefVH(root: View): RecyclerView.ViewHolder(root){

        fun bind(pos:Int){

            if(mList?.size!!>0) {

                itemView.findViewById<TextView>(R.id.trade_reference_more_text_view).text = "Trade Reference ${counter+1}"
                itemView.findViewById<EditText>(R.id.trade_reference_more_firm_name_input)?.setText(mList?.get(pos)?.firmName)
                itemView.findViewById<EditText>(R.id.trade_reference_more_person_name_dealing_with_input)?.setText(mList?.get(pos)?.nameofPersonDealingWith)

                itemView.findViewById<Spinner>(R.id.trade_reference_2_relationship_with_applicant_spinner)?.adapter=getAdapter(tradeAdapterResponse.data)
                var position = -1
                val list =
                    (itemView.findViewById<Spinner>(R.id.trade_reference_2_relationship_with_applicant_spinner)?.adapter as? DataSpinnerAdapter)?.list
                for (index in 0 until (list?.size ?: 0)) {
                    if (list?.get(index)?.value == mList?.get(pos)?.rshipWithApplicant) {
                        position = index
                    }
                }
                if (position != -1) {
                    Handler(Looper.getMainLooper()).postDelayed(Runnable {

                        itemView.findViewById<Spinner>(R.id.trade_reference_2_relationship_with_applicant_spinner)?.setSelection(
                            position
                        )
                    },1000)
                }
                itemView.findViewById<EditText>(R.id.trade_reference_2_contact_details_input)?.setText(mList?.get(pos)?.contactDetails)
               // trade_reference_more_product_purchase_sale_input?.setText(tradeRefDetails?.get(1)?.productPurchaseSale)
                try {
                    itemView.findViewById<TextView>(R.id.trade_reference_more_years_working_with_count)?.tag =
                        mList?.get(pos)?.noOfYrsWorkingWith?.toInt()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Crashlytics.log(e.message)

                }
                itemView.findViewById<TextView>(R.id.trade_reference_more_years_working_with_count)?.text =
                    "${mList?.get(pos)?.noOfYrsWorkingWith}"
            }

        }
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TradeRefDetailsAdapter.TradeRefVH {
        val view= LayoutInflater.from(context).inflate(R.layout.trade_ref_details_bcm_bm,parent,false)
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