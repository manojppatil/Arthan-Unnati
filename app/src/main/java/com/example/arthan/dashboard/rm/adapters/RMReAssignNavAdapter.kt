package com.example.arthan.dashboard.rm.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.RmReassignNavActivity
import com.example.arthan.dashboard.rm.RMScreeningNavigationActivity
import com.example.arthan.dashboard.rm.ReUsableFragmentSpace
import com.example.arthan.lead.AddLeadActivity
import com.example.arthan.lead.AddLeadStep2Activity
import com.example.arthan.lead.LeadInfoCaptureActivity
import com.example.arthan.lead.PersonalInformationActivity
import com.example.arthan.network.RmReAssignNavResponse

class RMReAssignNavAdapter(
    private val context: Context,
    private val from: String,
    private val responseData: RmReAssignNavResponse?
) : RecyclerView.Adapter<RMReAssignNavAdapter.ScreenNav>() {


    inner class ScreenNav(private val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(position: Int) {
            root.findViewById<TextView>(R.id.screenValue).text =
                responseData!!.completedScreens[position]
            root.setOnClickListener {

                when (responseData.completedScreens[position]) {

                    "LOAN" -> {
                        context.startActivity(
                            Intent(
                                context,
                                AddLeadActivity::class.java
                            ).apply {
                                putExtra("screen", "LOAN")
                                putExtra("loanId", responseData.loanId)
                                putExtra("custId", responseData.customerId)
                                putExtra("task", "RM_AssignList")
                            })
                        (context as RmReassignNavActivity).finish()

                    }
                    "ELIGIBILITY" -> {
                        context.startActivity(
                            Intent(
                                context,
                                ReUsableFragmentSpace::class.java
                            ).apply {
                                putExtra("screen", "LOAN")
                                putExtra("loanId", responseData.loanId)
                                putExtra("custId", responseData.customerId)
                                putExtra("task", "RM_AssignList")
                            })
                        (context as RmReassignNavActivity).finish()
                    }
                    "PERSONAL_PA" -> {
                        context.startActivity(Intent(context, PersonalInformationActivity::class.java)
                            .apply {
                                putExtra("screen", "PERSONAL_PA")
                                putExtra("loanId", responseData.loanId)
                                putExtra("custId", responseData.customerId)
                                putExtra("task", "RM_AssignList")
                            })
                        (context as RmReassignNavActivity).finish()

                    }
                    "PERSONAL_CA" -> {
                        context.startActivity(Intent(context, PersonalInformationActivity::class.java)
                            .apply {
                                putExtra("screen", "PERSONAL_CA")
                                putExtra("loanId", responseData.loanId)
                                putExtra("custId", responseData.customerId)
                                putExtra("task", "RM_AssignList")
                            })
                        (context as RmReassignNavActivity).finish()

                    }
                    "PERSONAL_G" -> {
                        context.startActivity(Intent(context, PersonalInformationActivity::class.java)
                            .apply {
                                putExtra("screen", "PERSONAL_G")
                                putExtra("loanId", responseData.loanId)
                                putExtra("custId", responseData.customerId)
                                putExtra("task", "RM_AssignList")
                            })
                        (context as RmReassignNavActivity).finish()

                    }
                    "BUSINESS" -> {
                        context.startActivity(
                            Intent(
                                context,
                                ReUsableFragmentSpace::class.java
                            ).apply {
                                putExtra("screen", "Business")
                                putExtra("loanId", responseData.loanId)
                                putExtra("custId", responseData.customerId)
                                putExtra("task", "RM_AssignList")
                                putExtra("from", "rmbusiness")

                            })
                        (context as RmReassignNavActivity).finish()

                    }
                    "INCOME" -> {
                        context.startActivity(
                            Intent(
                                context,
                                ReUsableFragmentSpace::class.java
                            ).apply {
                                putExtra("screen", "Income")
                                putExtra("loanId", responseData.loanId)
                                putExtra("custId", responseData.customerId)
                                putExtra("task", "RM_AssignList")
                                putExtra("from", "rmincome")

                            })
                        (context as RmReassignNavActivity).finish()


                    }
                    "OTHERS", "OTHERS_TRADE" -> {
                        context.startActivity(
                            Intent(
                                context,
                                ReUsableFragmentSpace::class.java
                            ).apply {
                                putExtra("screen", "OTHERS_TRADE")
                                putExtra("loanId", responseData.loanId)
                                putExtra("custId", responseData.customerId)
                                putExtra("task", "RM_AssignList")
                                putExtra("from", "rmothers")

                            })
                        (context as RmReassignNavActivity).finish()


                    }
                    "OTHERS_SECURITY" -> {
                        context.startActivity(
                            Intent(
                                context,
                                ReUsableFragmentSpace::class.java
                            ).apply {
                                putExtra("screen", "OTHERS_SECURITY")
                                putExtra("loanId", responseData.loanId)
                                putExtra("custId", responseData.customerId)
                                putExtra("task", "RM_AssignList")
                                putExtra("from", "rmothers")

                            })
                        (context as RmReassignNavActivity).finish()


                    }
                    "KYC_PA" -> {
                        context.startActivity(
                            Intent(
                                context,
                                AddLeadStep2Activity::class.java
                            ).apply {
                                putExtra("screen", "KYC_PA")
                                putExtra("loanId", responseData.loanId)
                                putExtra("custId", responseData.customerId)
                                putExtra("task", "RM_AssignList")
                            })
                        (context as RmReassignNavActivity).finish()


                    }
                    "CONSENT" -> {
                        context.startActivity(
                            Intent(
                                context,
                                ReUsableFragmentSpace::class.java
                            ).apply {
                                putExtra("screen", "CONSENT")
                                putExtra("loanId", responseData.loanId)
                                putExtra("custId", responseData.customerId)
                                putExtra("task", "RM_AssignList")
                            })
                        (context as RmReassignNavActivity).finish()


                    }
                    "DOCUMENTS" -> {
                        context.startActivity(
                            Intent(
                                context,
                                ReUsableFragmentSpace::class.java
                            ).apply {
                                putExtra("screen", "Documents")
                                putExtra("loanId", responseData.loanId)
                                putExtra("custId", responseData.customerId)
                                putExtra("task", "RM_AssignList")
                            })
                        (context as RmReassignNavActivity).finish()


                    }

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ScreenNav(
            LayoutInflater.from(context).inflate(
                R.layout.screen_nav_adapter_row,
                parent,
                false
            )
        )

    override fun getItemCount() = responseData?.completedScreens!!.size

    override fun onBindViewHolder(holder: ScreenNav, position: Int) {
        holder.bind(position)
    }
}