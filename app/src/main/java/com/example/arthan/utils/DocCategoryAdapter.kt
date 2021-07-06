package com.example.arthan.utils

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.RmReassignNavActivity
import com.example.arthan.dashboard.rm.DocumentSubCategoryActivity
import com.example.arthan.dashboard.rm.RMScreeningNavigationActivity
import com.example.arthan.dashboard.rm.ReUsableFragmentSpace
import com.example.arthan.global.ArthanApp
import com.example.arthan.lead.AddLeadActivity
import com.example.arthan.lead.AddLeadStep2Activity
import com.example.arthan.lead.LeadInfoCaptureActivity
import com.example.arthan.lead.PersonalInformationActivity
import com.example.arthan.model.DocCategoriesList
import com.example.arthan.model.SubmitMultipleDocsRequest
import com.example.arthan.network.RmReAssignNavResponse

class DocCategoryAdapter(
    private val context: Context,
    private val from: String,
    private val responseData: DocCategoriesList?
) : RecyclerView.Adapter<DocCategoryAdapter.ScreenNav>() {


    inner class ScreenNav(private val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(position: Int) {
            root.findViewById<TextView>(R.id.screenValue).text =
                responseData!!.docCategories[position]
            root.setOnClickListener {

                if(responseData.docCategories[position].startsWith("Co-Applicant"))
                {
                    when(responseData.docCategories[position]){

                        "Co-Applicant 1"->{
                           context.startActivity(Intent(context, AddLeadStep2Activity::class.java).apply {
                               putExtra("screen","KYC_PA")
                               putExtra("type","CA1")
                               putExtra("loanId",responseData.loanId)
                               putExtra("task","documentsAddCo")
                           })
                        }
                        "Co-Applicant 2"->{
                            context.startActivity(Intent(context, AddLeadStep2Activity::class.java).apply {
                                putExtra("screen","KYC_PA")
                                putExtra("type","CA2")
                                putExtra("loanId",responseData.loanId)
                                putExtra("task","documentsAddCo")
                            })
                        }
                        "Co-Applicant 3"->{
                            context.startActivity(Intent(context, AddLeadStep2Activity::class.java).apply {
                                putExtra("screen","KYC_PA")
                                putExtra("type","CA3")
                                putExtra("loanId",responseData.loanId)
                                putExtra("task","documentsAddCo")
                            })
                        }
                        "Co-Applicant 4"->{
                            context.startActivity(Intent(context, AddLeadStep2Activity::class.java).apply {
                                putExtra("screen","KYC_PA")
                                putExtra("type","CA4")
                                putExtra("loanId",responseData.loanId)
                                putExtra("task","documentsAddCo")
                            })
                        }
                        "Co-Applicant 5"->{
                            context.startActivity(Intent(context, AddLeadStep2Activity::class.java).apply {
                                putExtra("screen","KYC_PA")
                                putExtra("type","CA5")
                                putExtra("loanId",responseData.loanId)
                                putExtra("task","documentsAddCo")
                            })
                        }
                        "Guarantor"->{
                            context.startActivity(Intent(context, AddLeadStep2Activity::class.java).apply {
                                putExtra("screen","KYC_PA")
                                putExtra("type","G")
                                putExtra("loanId",responseData.loanId)
                                putExtra("task","documentsAddCo")
                            })
                        }
                    }


                }else {

                    context.startActivity(
                        Intent(
                            context,
                            DocumentSubCategoryActivity::class.java
                        ).apply {
                            putExtra("cate", responseData.docCategories[position])
                            putExtra("loanId", responseData.loanId)
                            putExtra("task", "RM_AssignList")
//                        ArthanApp.getAppInstance().submitDocs=null

                        })
                }


              /*  when (responseData.docCategories[position]) {

                    "Business Proof and Stabilty" -> {
                        context.startActivity(
                            Intent(
                                context,
                                DocumentSubCategoryActivity::class.java
                            ).apply {
                                putExtra("cate", responseData.docCategories[position])
                                putExtra("loanId", responseData.loanId)
                                putExtra("task", "RM_AssignList")
                            })

                    }
                    "Other KYC" -> {
                        context.startActivity(
                            Intent(
                                context,
                                ReUsableFragmentSpace::class.java
                            ).apply {
                                putExtra("screen", "LOAN")
                                putExtra("loanId", responseData.loanId)
                                putExtra("task", "RM_AssignList")
                            })
                    }
                    "Business Premises" -> {
                        context.startActivity(Intent(context, PersonalInformationActivity::class.java)
                            .apply {
                                putExtra("screen", "PERSONAL_PA")
                                putExtra("loanId", responseData.loanId)
                                putExtra("task", "RM_AssignList")
                            })


                    }

                }*/
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

    override fun getItemCount() = responseData?.docCategories!!.size

    override fun onBindViewHolder(holder: ScreenNav, position: Int) {
        holder.bind(position)
    }
}