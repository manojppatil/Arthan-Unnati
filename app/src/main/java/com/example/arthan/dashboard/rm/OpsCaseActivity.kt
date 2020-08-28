package com.example.arthan.dashboard.rm

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.crashlytics.android.Crashlytics
import com.example.arthan.R
import com.example.arthan.dashboard.rm.adapters.OpsCaseAdapter
import com.example.arthan.model.DocumentsList
import com.example.arthan.model.GetRMOpsCasesResponse
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.network.S3UploadFile
import com.example.arthan.network.S3Utility
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.utils.loadImage
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_ops_case.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class OpsCaseActivity : BaseActivity() {
    override fun contentView(): Int {
        return R.layout.activity_ops_case
    }
    lateinit var response:GetRMOpsCasesResponse

    override fun init() {

        loadData()
        submitOpsCases.setOnClickListener {

           sendToServer()
        }
    }

    private fun sendToServer() {

        val progrssLoader=ProgrssLoader(this)
        progrssLoader.dismmissLoading()
        val map=HashMap<String,Any?>()
        map["loanId"]=response.loanId
        map["documents"]=response.documents
        CoroutineScope(Dispatchers.IO).launch {
            val response=RetrofitFactory.getApiService().submitRMOpsCases(map)
            if(response.body()!=null)
            {
                withContext(Dispatchers.Main){
                    progrssLoader.dismmissLoading()
                    Toast.makeText(this@OpsCaseActivity,"Submitted successfully",Toast.LENGTH_LONG).show()
                }
            }
            else{
                withContext(Dispatchers.Main){
                    progrssLoader.dismmissLoading()
                    Toast.makeText(this@OpsCaseActivity,"Please try again later",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun updateList(position:Int,status:String)
    {
        response.documents[position].docStatus=status
        if(status!="Upload")
        {
            response.documents[position].docUrl=""
        }
    }



    private fun loadData() {

        val progrssLoader=ProgrssLoader(this)
        progrssLoader.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            val response=RetrofitFactory.getApiService().getRMOpsCases(intent.getStringExtra("loanId"))
            if(response.body()!=null)
            {
                withContext(Dispatchers.Main){
                    progrssLoader.dismmissLoading()
                   this@OpsCaseActivity.response=response.body()!!
                    RvOpsCases.adapter=OpsCaseAdapter(this@OpsCaseActivity,response.body()!!)
                }
            }
        }

    }

    override fun onToolbarBackPressed() {
       finish()
    }


    override fun screenTitle(): String {
      return "OpsCase Details"
    }

    fun captureDoc(position:Int) {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra("docType",response.documents[position].docName)
        intent.putExtra("position",position)
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),4000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
                if (data == null || data.data == null) {
                    return
                }
                val loader = ProgrssLoader(this)
                loader.showLoading()
            loadImage(this, uselessView, data.data!!, { filePath ->
                try {
                    val file: File = File(filePath)
                    val url = file.name
                    val fileList: MutableList<S3UploadFile> = mutableListOf()
                    fileList.add(S3UploadFile(file, url))
                    S3Utility.getInstance(this)
                        .uploadFile(fileList,
                            {
//                                shopUrl = fileList[0].url ?: filePath
                                val  resultUrl = fileList[0].url ?: filePath
                                val pos=data.getIntExtra("position",0)
//                                    val name=data.getStringExtra("docType")
                                response.documents[pos].docUrl=resultUrl
                                ThreadUtils.runOnUiThread { loader.dismmissLoading() }
                            }) {
                            ThreadUtils.runOnUiThread { loader.dismmissLoading() }
                        }
                } catch (e: Exception) {
                    Crashlytics.log(e.message)
                    ThreadUtils.runOnUiThread { loader.dismmissLoading() }
                    e.printStackTrace()
                }
            })
            }
        }
}
