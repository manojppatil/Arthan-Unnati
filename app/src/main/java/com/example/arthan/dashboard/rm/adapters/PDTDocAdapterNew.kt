package com.example.arthan.dashboard.rm.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.dashboard.rm.PDTDocUploadActivity
import com.example.arthan.model.RMDocs
import com.example.arthan.views.ApprovalDialog

class PDTDocAdapterNew(
    val context: Context,
    private val mList: ArrayList<RMDocs>,
    loanId: String
): RecyclerView.Adapter<PDTDocAdapterNew.PDTDocVH>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PDTDocVH {
        val view= LayoutInflater.from(context).inflate(R.layout.row_pdt_doc,parent,false)
        return PDTDocVH(view)
    }

    override fun getItemCount()= mList.size

    override fun onBindViewHolder(holder: PDTDocVH, position: Int) {
        holder.bind(position)
    }

    inner class PDTDocVH(root: View): RecyclerView.ViewHolder(root){

        fun bind(position: Int){
            itemView.findViewById<Button>(R.id.btn_upload).setOnClickListener{

                (context as PDTDocUploadActivity).onUpload(position)
            }
            itemView.findViewById<Button>(R.id.btn_take_approval).setOnClickListener{

                ApprovalDialog(context,position).show()
            }
            itemView.findViewById<TextView>(R.id.txt_doc_name).text=mList[position].docName
        }
    }

    fun onUploadSuccess(data: Intent?) {

        (context as PDTDocUploadActivity).sendToS3()
    }

    interface OnUploadListener{
         fun onUpload()
    }
}