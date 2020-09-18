package com.example.arthan.dashboard.rm.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.dashboard.rm.DocumentSubCategoryActivity
import com.example.arthan.global.ArthanApp
import com.example.arthan.model.DocSubCategories
import com.example.arthan.model.DocsRequest

class DocSubCategoryAdapter(
    private val context: Context,
    private val from: String,
    private val responseData: DocSubCategories?,
    private val category: String?
) : RecyclerView.Adapter<DocSubCategoryAdapter.ScreenNav>() {


    inner class ScreenNav(private val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(position: Int) {
            root.findViewById<TextView>(R.id.screenValue).text =
                responseData!!.docCategories[position]

            root.findViewById<ImageView>(R.id.delete).setOnClickListener {

                removeFileAtPos(position, category)
                root.findViewById<TextView>(R.id.capturedFileName).text =
                    "No File Selected"
                root.findViewById<ImageView>(R.id.delete).visibility = View.GONE

            }
            root.findViewById<TextView>(R.id.capturedFileName).text =
                "No File Selected"

            root.setOnClickListener {

                (context as DocumentSubCategoryActivity).invokeUploadActivity(
                    position,
                    responseData.docCategories[position].replace(" ", "_"),
                    responseData.loanId
                )

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ScreenNav(
            LayoutInflater.from(context).inflate(
                R.layout.doc_upload_sub_cat,
                parent,
                false
            )
        )

    override fun getItemCount() = responseData?.docCategories!!.size

    override fun onBindViewHolder(holder: ScreenNav, position: Int) {
        holder.bind(position)
    }

    fun removeFileAtPos(pos: Int, category: String?) {
        if (ArthanApp.getAppInstance().submitDocs != null) {
            when (category) {
                "Business Proof and Stabilty" -> {
                    ArthanApp.getAppInstance().submitDocs?.businessDocs?.removeAt(pos)


                }
                "Other KYC" -> {
                    ArthanApp.getAppInstance().submitDocs?.kycDocs?.removeAt(pos)


                }
                "Business Premises" -> {
                    ArthanApp.getAppInstance().submitDocs?.bussPremisesDocs?.removeAt(pos)


                }
                "Residential Premises" -> {
                    ArthanApp.getAppInstance().submitDocs?.residentialDocs?.removeAt(pos)


                }

            }
        }
    }

    fun updateTile(
        pos: Int,
        docName: String?,
        docUrl: String?,
        category: String?
    ) {

        when (category) {
            "Business Proof and Stabilty" -> {
                if (ArthanApp.getAppInstance().submitDocs?.businessDocs?.size!! > 0&&ArthanApp.getAppInstance().submitDocs?.businessDocs?.get(pos)?.docUrl!!.isNotEmpty()) {

                    ArthanApp.getAppInstance().submitDocs?.businessDocs?.removeAt(pos)
                }

                    ArthanApp.getAppInstance().submitDocs?.businessDocs?.add(
                        DocsRequest(
                            pos.toString(),
                            docName!!,
                            docUrl!!
                        )
                    )

            }
            "Other KYC" -> {
                if (ArthanApp.getAppInstance().submitDocs?.kycDocs?.size!! > 0&&ArthanApp.getAppInstance().submitDocs?.kycDocs?.get(pos)?.docUrl!!.isNotEmpty()) {

                    ArthanApp.getAppInstance().submitDocs?.kycDocs?.removeAt(pos)

                }
                    ArthanApp.getAppInstance().submitDocs?.kycDocs?.add(
                        DocsRequest(
                            pos.toString(),
                            docName!!,
                            docUrl!!
                        )
                    )


            }
            "Business Premises" -> {
                if (ArthanApp.getAppInstance().submitDocs?.bussPremisesDocs?.size!! > 0&&ArthanApp.getAppInstance().submitDocs?.bussPremisesDocs?.get(pos)?.docUrl!!.isNotEmpty()) {

                    ArthanApp.getAppInstance().submitDocs?.bussPremisesDocs?.removeAt(pos)

                }

                    ArthanApp.getAppInstance().submitDocs?.bussPremisesDocs?.add(
                        DocsRequest(
                            pos.toString(),
                            docName!!,
                            docUrl!!
                        )
                    )


            }
            "Residential Premises" -> {
                if (ArthanApp.getAppInstance().submitDocs?.residentialDocs?.size!! > 0&&ArthanApp.getAppInstance().submitDocs?.residentialDocs?.get(pos)?.docUrl!!.isNotEmpty()) {

                    ArthanApp.getAppInstance().submitDocs?.residentialDocs?.removeAt(pos)

                }

                    ArthanApp.getAppInstance().submitDocs?.residentialDocs?.add(
                        DocsRequest(
                            pos.toString(),
                            docName!!,
                            docUrl!!
                        )
                    )
                }


        }

    }
}