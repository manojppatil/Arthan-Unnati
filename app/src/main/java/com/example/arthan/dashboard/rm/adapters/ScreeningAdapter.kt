package com.example.arthan.dashboard.rm.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.dashboard.rm.RMScreeningNavigationActivity
import com.example.arthan.lead.AddLeadActivity
import com.example.arthan.model.ScreenDetailsToNavigateData
import com.example.arthan.model.ScreeningData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.getPixelFromDP
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScreeningAdapter(private val context: Context,private var data: List<ScreeningData>) :
    RecyclerView.Adapter<ScreeningAdapter.ScreeningVH>() , Filterable {

    private val listoriginal:List<ScreeningData>?=ArrayList(data)
    inner class ScreeningVH(root: View) : RecyclerView.ViewHolder(root) {

        private val businessType: TextView = itemView.findViewById(R.id.txt_business)
        private val customerName: TextView = itemView.findViewById(R.id.txt_customer_name)
        private val date: TextView = itemView.findViewById(R.id.txt_date)
        private val amount: TextView = itemView.findViewById(R.id.txt_amount)
        fun bind(position: Int) {
            if (position == 5) {
                val lp = itemView.layoutParams as? ViewGroup.MarginLayoutParams
                lp?.bottomMargin = getPixelFromDP(20f, itemView.context)?.toInt() ?: 0
                lp?.let { itemView.layoutParams = it }
            }

            /*amount.setCompoundDrawablesWithIntrinsicBounds(
                Utility.getRupeeSymbol(
                    itemView.context,
                    amount.textSize ?: 14f,
                    amount.currentTextColor ?: Color.rgb(105, 105, 105)
                ), null, null, null
            )*/

            val color = if (position % 2 == 0) Color.rgb(11, 65, 175) else Color.rgb(140, 192, 252)
            businessType.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.screening_item_indicator
                )?.also {
                    DrawableCompat.setTint(
                        DrawableCompat.wrap(it),
                        color
                    )
                }, null, null, null)
            businessType.setTextColor(color)
            businessType.text= data[position].segment
            customerName.text= data[position].name
            date.text= data[position].leadDate
            amount.text= "Amount: ₹${data[position].loanAmt}"
//          data[position]

            itemView.findViewById<TextView>(R.id.txt_loan_id).text= "Loan ID: ${data[position].loanId}"
            itemView.findViewById<TextView>(R.id.txt_mobile).text= "Mobile NO: ${data[position].mobileNo}"
            itemView.findViewById<TextView>(R.id.txt_branch).text= "Branch: ${data[position].branch}"
            if(data[position].recordType=="AM")
            {
                itemView.findViewById<TextView>(R.id.am_txt).visibility=View.VISIBLE
            }else
            {
                itemView.findViewById<TextView>(R.id.am_txt).visibility=View.GONE

            }
            itemView.setOnClickListener {

                completeScreeningList(itemView.findViewById<TextView>(R.id.txt_loan_id).text.toString().replace("Loan ID: ",""),data[position].id)
//                getScreenDetails(itemView.findViewById<TextView>(R.id.txt_loan_id).text.toString().replace("Loan ID: ",""))
            }
        }

    }

    private fun completeScreeningList(loanId:String,custId:String)
    {

        context.startActivity(Intent(context,RMScreeningNavigationActivity::class.java).apply {
            putExtra("loanId",loanId)
            putExtra("custId",custId)
        })
    }
    private fun getScreenDetails(loanId:String):MutableLiveData<ScreenDetailsToNavigateData?> {

            val response= MutableLiveData<ScreenDetailsToNavigateData?>()


            try {

               var map=HashMap<String,String>()
                map.put("loanId",loanId)
                CoroutineScope(Dispatchers.IO).launch {
                    val respo = RetrofitFactory.getApiService().getScreenDetails(
                        map
                    )
                    if (respo.isSuccessful && respo.body() != null) {
                        withContext(Dispatchers.Main) {
                            response.value = respo.body()
                            context.startActivity(Intent(context,AddLeadActivity::class.java).apply {
                                putExtra("screen",respo.body()!!.screenId)
                                putExtra("leadId",respo.body()!!.leadId)
                                putExtra("loanId",respo.body()!!.loanId)
                                putExtra("custId",respo.body()!!.customerId)
                            })

                            // moveToScreen(respo.body()!!.screenId)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            response.value = null
                        }
                    }
                }
            } catch (e: Exception){
                response.value = null
            }

            return response
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScreeningVH {
        return ScreeningVH(LayoutInflater.from(context).inflate(R.layout.row_screening_customers, parent, false))
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ScreeningVH, position: Int) {
        holder.bind(position)
    }
    override  fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val query = charSequence.toString()
                //List<ScreeningData>
                var filtered = ArrayList<ScreeningData>()
                if (query.isEmpty()) {
                    filtered.addAll(listoriginal!!.toList())
                } else {
                    for (name in data) {
                        if ((name as ScreeningData).name.toLowerCase().startsWith(query.toLowerCase())) {
                            filtered.add(name)
                        }else if((name as ScreeningData).mobileNo.startsWith(query)){
                            filtered.add(name)

                        }
                    }

                }

                val results = FilterResults()
                results.count = filtered.size
                results.values = filtered
                return results
            }

            override fun publishResults(
                charSequence: CharSequence,
                results: FilterResults
            ) {
                data= emptyList()
                var itemsFiltered = results.values as List<ScreeningData>
                data=itemsFiltered
                notifyDataSetChanged()
            }
        }
    }

}