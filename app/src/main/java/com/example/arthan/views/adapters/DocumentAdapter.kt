package com.example.arthan.views.adapters

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.arthan.R
import com.example.arthan.views.activities.SubmitFinalReportActivity

class DocumentAdapter(private val mContext:SubmitFinalReportActivity, private val docs: MutableList<Uri>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val HEADER_VIEW= 0
    val DOC_VIEW=1

    init {

        Log.e("DocumentAdapter SIZE","::: ${docs.size}")
    }

    override fun getItemViewType(position: Int): Int {
        return if(position==0)
            HEADER_VIEW
        else DOC_VIEW
    }

    fun addNewDoc(doc : Uri){
        docs.add(doc)
        notifyDataSetChanged()

        Log.e("addNewDoc SIZE","::: ${docs.size}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == HEADER_VIEW){
            val view= LayoutInflater.from(mContext).inflate(R.layout.layout_document_header,parent,false)
            HeaderVH(view)
        }else {
            val view= LayoutInflater.from(mContext).inflate(R.layout.row_document,parent,false)
            DocumentVH(view)
        }
    }

    override fun getItemCount()= docs.size+1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is HeaderVH)
            holder.bind()
        else
            (holder as DocumentVH).bind(position)
    }

    inner class DocumentVH(private val root: View): RecyclerView.ViewHolder(root){

        fun bind(position: Int){
            if(position!=0)
            Glide.with(mContext).load(docs[position-1]).into(root.findViewById(R.id.img_doc))
        }
    }

    inner class HeaderVH(private val root: View): RecyclerView.ViewHolder(root){

        fun bind(){
            root.setOnClickListener {
                mContext.capture()
            }
        }


    }

}