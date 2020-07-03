package com.example.arthan.dashboard.rm.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.dashboard.rm.RMScreeningNavigationActivity
import com.example.arthan.lead.*
import com.example.arthan.model.ScreeningNavDataResponse
import com.example.arthan.utils.ArgumentKey

class RMScreeningNavAdapter(private val context: Context,
                            private val from: String,
                            private val responseData: ScreeningNavDataResponse?
): RecyclerView.Adapter<RMScreeningNavAdapter.ScreenNav>() {


    inner class ScreenNav(private val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(position: Int) {
            root.findViewById<TextView>(R.id.screenValue).text = responseData!!.completedScreens[position]
            root.setOnClickListener {

                when(responseData.completedScreens[position])
                {
                    "LOAN"->{
                        context.startActivity(Intent(context, LoanDetailActivity::class.java).apply {
                            putExtra("screen","LOAN")
                            putExtra("loanId",responseData.loanId)
                            putExtra("custId",responseData.customerId)
                            putExtra("task","RMreJourney")
                        })
                        (context as RMScreeningNavigationActivity).finish()
                    }
                    "ELIGIBILITY"->{
                        context.startActivity(Intent(context, LeadEligibilityActivity::class.java).apply {
                            putExtra("screen","LOAN")
                            putExtra("loanId",responseData.loanId)
                            putExtra("custId",responseData.customerId)
                            putExtra("leadId",responseData.leadId)
                            putExtra("task","RMreJourney")
                        })
                        (context as RMScreeningNavigationActivity).finish()
                    }
                    "PERSONAL_PA"->{
                        context.startActivity(Intent(context, PersonalInformationActivity::class.java).apply {
                            putExtra("screen","PERSONAL_PA")
                            putExtra("loanId",responseData.loanId)
                            putExtra("custId",responseData.customerId)
                            putExtra("task","RMreJourney")
                        })
                        (context as RMScreeningNavigationActivity).finish()
                    }
                    "PERSONAL_CA"->{
                        context.startActivity(Intent(context, PersonalInformationActivity::class.java).apply {
                            putExtra("screen","PERSONAL_CA")
                            putExtra("loanId",responseData.loanId)
                            putExtra("custId",responseData.customerId)
                            putExtra("task","RMreJourney")
                        })
                        (context as RMScreeningNavigationActivity).finish()
                    }
                    "PERSONAL_G"->{
                        context.startActivity(Intent(context, PersonalInformationActivity::class.java).apply {
                            putExtra("screen","PERSONAL_G")
                            putExtra("loanId",responseData.loanId)
                            putExtra("custId",responseData.customerId)
                            putExtra("task","RMreJourney")
                        })
                        (context as RMScreeningNavigationActivity).finish()
                    }
                    "BUSINESS"->{
                        context.startActivity(Intent(context, AddLeadActivity::class.java).apply {
                            putExtra("screen","business")
                            putExtra("loanId",responseData.loanId)
                            putExtra("custId",responseData.customerId)
                            putExtra("task","RMreJourney")
                        })
                        (context as RMScreeningNavigationActivity).finish()
                    }
                    "INCOME"->{
                        context.startActivity(Intent(context, AddLeadActivity::class.java).apply {
                            putExtra("screen","income")
                            putExtra("loanId",responseData.loanId)
                            putExtra("custId",responseData.customerId)
                            putExtra("task","RMreJourney")
                        })
                        (context as RMScreeningNavigationActivity).finish()

                    }
                    "OTHERS","OTHERS_TRADE","OTHERS_SECURITY"->{
                        context.startActivity(Intent(context, AddLeadActivity::class.java).apply {
                            putExtra("screen","others")
                            putExtra("loanId",responseData.loanId)
                            putExtra("custId",responseData.customerId)
                            putExtra("task","RMreJourney")
                        })
                        (context as RMScreeningNavigationActivity).finish()

                    }
                    "KYC_PA"->{
                        context.startActivity(Intent(context, AddLeadStep2Activity::class.java).apply {
                            putExtra("screen","KYC_PA")
                            putExtra("loanId",responseData.loanId)
                            putExtra("custId",responseData.customerId)
                            putExtra("task","RMreJourney")
                        })
                        (context as RMScreeningNavigationActivity).finish()

                    }
                    "CONSENT"->{
                        context.startActivity(Intent(context, ConsentActivity::class.java).apply {
                            putExtra("screen","CONSENT")
                            putExtra("loanId",responseData.loanId)
                            putExtra("custId",responseData.customerId)
                            putExtra(ArgumentKey.InPrincipleAmount,responseData.inPrincpAmt)
                            putExtra("task","RMreJourney")
                        })
                        (context as RMScreeningNavigationActivity).finish()

                    }
                    "DOCUMENTS"->{
                        context.startActivity(Intent(context, DocumentActivity::class.java).apply {
                            putExtra("screen","DOCUMENTS")
                            putExtra("loanId",responseData.loanId)
                            putExtra("custId",responseData.customerId)
                            putExtra("task","RMreJourney")
                        })
                        (context as RMScreeningNavigationActivity).finish()

                    }

                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
        ScreenNav(LayoutInflater.from(context).inflate(R.layout.screen_nav_adapter_row, parent, false))

    override fun getItemCount() = responseData?.completedScreens!!.size

    override fun onBindViewHolder(holder: ScreenNav, position: Int) {
        holder.bind(position)
    }
}