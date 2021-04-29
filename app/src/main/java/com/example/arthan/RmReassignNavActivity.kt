package com.example.arthan

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.arthan.dashboard.rm.RMReAssignListingActivity
import com.example.arthan.dashboard.rm.adapters.RMReAssignNavAdapter
import com.example.arthan.lead.AddLeadStep2Activity
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.network.RmReAssignNavResponse
import com.example.arthan.utils.ProgrssLoader
import kotlinx.android.synthetic.main.activity_r_m_screening_navigation.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RmReassignNavActivity : AppCompatActivity() {
    private var task: String = ""
    private lateinit var responseData: RmReAssignNavResponse
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_r_m_screening_navigation)
        //   back_button.setOnClickListener { onBackPressed() }
//        toolbar_title.text = "Complete Loan Details"

        getRmAssignPendingScreenList()
        addnewApplicant.setOnClickListener {
            val dialog= AlertDialog.Builder(this)
            dialog.setTitle("Add new Applicant")
            dialog.setMessage("Select the type of Applicant you want to Add")
            dialog.setNegativeButton("Guarantor", DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
                startActivity(Intent(this, AddLeadStep2Activity::class.java).apply {
                    putExtra("screen","KYC_PA")
                    putExtra("type","G")
                    putExtra("loanId",responseData.loanId)
                    putExtra("custId",responseData.custId)
                    putExtra("task","RMAddCoRe")
                })
                finish()
            })
            dialog.setPositiveButton("Co-Applicant", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                startActivity(Intent(this, AddLeadStep2Activity::class.java).apply {
                    putExtra("screen","KYC_PA")
                    putExtra("type","CA")
                    putExtra("loanId",responseData.loanId)
                    putExtra("custId",responseData.custId)
                    putExtra("task","RMAddCoRe")
                })
                finish()
            })
            dialog.setNeutralButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            dialog.create().show()
        }
        if(intent?.getStringExtra("tile")=="AMCASES")
        {
            toolbar_title?.text = "AM Cases"

            submitAMcases.visibility=View.VISIBLE
            ressubmitReassign.visibility=View.GONE
        }else
        {
            submitAMcases.visibility=View.GONE
            ressubmitReassign.visibility=View.VISIBLE

        }
        ressubmitReassign.setOnClickListener {
            val progrssLoader=ProgrssLoader(this!!)
            progrssLoader.showLoading()
            val map=HashMap<String,String>()
            map["loanId"]=intent?.getStringExtra("loanId")!!

            CoroutineScope(Dispatchers.IO).launch {
                val response=RetrofitFactory.getApiService().rmReSubmit(map)
                if(response?.body()!=null)
                {
                    withContext(Dispatchers.Main){
                        progrssLoader.dismmissLoading()
                        Toast.makeText(this@RmReassignNavActivity,"Case is Re submitted successfully",Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@RmReassignNavActivity,RMReAssignListingActivity::class.java).apply {
                            putExtra("FROM","REASSIGN")
                        })
                    }
                }else
                {
                    withContext(Dispatchers.Main){
                        progrssLoader.dismmissLoading()
                        Toast.makeText(this@RmReassignNavActivity,"Error in Resubmission",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        submitAMcases.setOnClickListener {
            val loader=ProgrssLoader(this)
            loader.showLoading()
            CoroutineScope(Dispatchers.IO).launch {
                val res=RetrofitFactory.getApiService().submitAMCase(intent.getStringExtra("loanId"))
                if(res?.body()!=null)
                {
                    withContext(Dispatchers.Main) {
                        loader.dismmissLoading()
                        finish()
                        startActivity(Intent(this@RmReassignNavActivity, RMReAssignListingActivity::class.java).putExtra("tile","AMCASES"))
                        Toast.makeText(this@RmReassignNavActivity,"Submitted AM Case.",Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    withContext(Dispatchers.Main) {
                        loader.dismmissLoading()
                        Toast.makeText(this@RmReassignNavActivity," Please try again",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun getRmAssignPendingScreenList() {

        val loader = ProgrssLoader(this)
        loader.showLoading()

        CoroutineScope(Dispatchers.IO).launch {

            val response =when(intent?.getStringExtra("tile")){

            "AMCASES"-> {
                RetrofitFactory.getApiService()
                    .getAMScreenStatus(intent.getStringExtra("loanId")!!)
            }else->{
                    RetrofitFactory.getApiService()
                        .getRMReAssignedStatus(intent.getStringExtra("loanId")!!)
                }
            }

            if (response?.body() != null) {
                withContext(Dispatchers.Main) {
                    continueScreen.visibility = View.GONE
                    loader.dismmissLoading()
                    responseData=response.body()!!

                    rvScreeningList.adapter =
                        RMReAssignNavAdapter(
                            this@RmReassignNavActivity,
                            "ReScreen",
                            response.body()
                        )

                }
            } else {
                withContext(Dispatchers.Main)
                {
                    loader.dismmissLoading()
                }
            }
        }
    }


}
