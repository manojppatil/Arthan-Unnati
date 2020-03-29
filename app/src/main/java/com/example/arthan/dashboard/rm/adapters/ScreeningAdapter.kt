package com.example.arthan.dashboard.rm.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.dashboard.rm.RMScreeningListingActivity
import com.example.arthan.lead.AddLeadActivity
import com.example.arthan.lead.LeadInfoCaptureActivity
import com.example.arthan.lead.LoanDetailActivity
import com.example.arthan.model.*
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.getPixelFromDP
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class ScreeningAdapter(private val context: Context,private val data: List<ScreeningData>) :
    RecyclerView.Adapter<ScreeningAdapter.ScreeningVH>() {

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
          data[position].id

            itemView.findViewById<TextView>(R.id.txt_loan_id).text= "Loan ID: ${data[position].loanId}"
            itemView.findViewById<TextView>(R.id.txt_mobile).text= "Mobile NO: ${data[position].mobileNo}"
            itemView.findViewById<TextView>(R.id.txt_branch).text= "Branch: ${data[position].branch}"
            itemView.setOnClickListener {

                getScreenDetails(itemView.findViewById<TextView>(R.id.txt_loan_id).text.toString().replace("Loan ID: ",""))
            }
        }

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
}