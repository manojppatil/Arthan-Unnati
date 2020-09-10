package com.example.arthan.dashboard.rm

import android.content.Intent
import android.widget.TextView
import com.example.arthan.R
import com.example.arthan.dashboard.rm.adapters.DocSubCategoryAdapter
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.utils.UploadActivityNew
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_document_sub_category.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DocumentSubCategoryActivity : BaseActivity() {
    override fun contentView(): Int {
        return R.layout.activity_document_sub_category
    }

    override fun init() {
        submitdocs.setOnClickListener {
            finish()
        }
        val category=intent.getStringExtra("cate")
        val loanId=intent.getStringExtra("loanId")
        val map=HashMap<String,String>()
        map["loanId"]=loanId
        map["categoryId"]=category
        val progress=ProgrssLoader(this)
        progress.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            val res=RetrofitFactory.getApiService().getDocSubCategories(map)

            if(res.body()!=null)
            {
                withContext(Dispatchers.Main)
                {
                    progress.dismmissLoading()
                    rvSubDocs.adapter=DocSubCategoryAdapter(this@DocumentSubCategoryActivity,"",res.body()!!)
                }

            }

        }

    }

    override fun onToolbarBackPressed() {
       finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 103&&data!=null) {
            val pos = data!!.getSerializableExtra("pos") as Int
            val docUrl = data!!.getSerializableExtra("docUrl").toString()
            val docName = data!!.getSerializableExtra("docName").toString()
            (rvSubDocs.adapter as DocSubCategoryAdapter).updateTile(pos,docName,docUrl,intent.getStringExtra("cate"))
            val view=rvSubDocs.layoutManager?.findViewByPosition(pos)
            if(view!=null)
            {
                view.findViewById<TextView>(R.id.capturedFileName).text=docName
            }

        }

    }
    override fun screenTitle(): String {
      return ""
    }

    fun invokeUploadActivity(position:Int,docName:String,loanId:String) {

        startActivityForResult(Intent(this, UploadActivityNew::class.java).apply {
            putExtra("pos",position)
            putExtra("docName",docName)
            putExtra("loanId",loanId)
        }, 103)
    }
}
