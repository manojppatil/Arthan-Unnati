package com.example.arthan.utils

import android.content.Intent
import android.widget.Toast
import com.example.arthan.R
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.dashboard.rm.RMScreeningNavigationActivity
import com.example.arthan.global.ArthanApp
import com.example.arthan.lead.DocumentActivity
import com.example.arthan.model.SubmitMultipleDocsRequest
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.documnet_upload_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DocumentUploadFragment : BaseFragment() {
    override fun contentView(): Int {
      return  R.layout.documnet_upload_fragment
    }

    override fun init() {
        var loanId=arguments?.getString("loanId")
        var custId= arguments?.getString("custId")
        if(loanId== null)
        {
            loanId=activity?.intent?.getStringExtra("loanId")
            custId=activity?.intent?.getStringExtra("custId")
        }
        submitDocs.setOnClickListener {
            val progressBar=ProgrssLoader(activity!!)
            progressBar.showLoading()
            submitDocs.isEnabled=false
            CoroutineScope(Dispatchers.Main).launch {
                val res=RetrofitFactory.getApiService().submitMultipleDocs(ArthanApp.getAppInstance().submitDocs!!)
                if(res?.body()!=null)
                {

                    val result = res.body()

                    if (res.isSuccessful && res.body() != null && result?.apiCode == "200") {


                        if (activity?.intent?.getStringExtra("task") == "RMreJourney") {
                            withContext(Dispatchers.Main) {

                                progressBar.dismmissLoading()
                                startActivity(
                                    Intent(
                                        activity,
                                        RMScreeningNavigationActivity::class.java
                                    ).apply {
                                        putExtra("loanId", loanId)
                                    }
                                )
                                (context as DocumentActivity).finish()
                            }
                        }else{
                            withContext(Dispatchers.Main) {
                                progressBar.dismmissLoading()

                                Toast.makeText(
                                    activity,
                                    "Case is Successfully submitted to BM",
                                    Toast.LENGTH_LONG
                                ).show()

                                val intent = Intent(activity, RMDashboardActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            progressBar.dismmissLoading()

                            submitDocs.isEnabled=true

                            Toast.makeText(
                                activity,
                                "Please try again later",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                }
            }
        }

        val progrssLoader=ProgrssLoader(activity!!)
        progrssLoader.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            val res=RetrofitFactory.getApiService().getDocCategories(loanId)
            if(res?.body()!=null)
            {
                withContext(Dispatchers.Main){
                    progrssLoader.dismmissLoading()
                    ArthanApp.getAppInstance().submitDocs= SubmitMultipleDocsRequest()
                    rvDocsListMain.adapter=DocCategoryAdapter(context!!,"",res.body()!!)
                    ArthanApp.getAppInstance().submitDocs?.loanId=res.body()!!.loanId
                    ArthanApp.getAppInstance().submitDocs?.userId=ArthanApp.getAppInstance().loginUser

                }

            }else
            {
                withContext(Dispatchers.Main){
                    progrssLoader.dismmissLoading()
                }
            }
        }
    }
}