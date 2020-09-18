package com.example.arthan.dashboard.rm.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.views.ApprovalDialog

class PDTDocAdapter(val context: Context,private val mListener: OnUploadListener): RecyclerView.Adapter<PDTDocAdapter.PDTDocVH>(),View.OnClickListener {

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_upload -> mListener.onUpload()
            R.id.btn_take_approval -> ApprovalDialog(context,0).show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PDTDocVH {
        val view= LayoutInflater.from(context).inflate(R.layout.row_pdt_doc,parent,false)
        return PDTDocVH(view)
    }

    override fun getItemCount()= 3

    override fun onBindViewHolder(holder: PDTDocVH, position: Int) {
        holder.bind(position)
    }

    inner class PDTDocVH(root: View): RecyclerView.ViewHolder(root){

        init {
            root.findViewById<Button>(R.id.btn_upload).setOnClickListener(this@PDTDocAdapter)
            root.findViewById<Button>(R.id.btn_take_approval).setOnClickListener(this@PDTDocAdapter)
        }

        fun bind(position: Int){

        }
    }

    fun onUploadSuccess(){

    }

    interface OnUploadListener{
         fun onUpload()
    }
}