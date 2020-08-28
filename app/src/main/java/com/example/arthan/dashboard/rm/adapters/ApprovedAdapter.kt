package com.example.arthan.dashboard.rm.adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.dashboard.bm.ApprovedCustomerLegalStatusActivity
import com.example.arthan.dashboard.bm.model.RejectedCaseResponse
import com.example.arthan.dashboard.rm.CommonApprovedListingActivity
import com.example.arthan.dashboard.rm.RMRequestWaiverActivity
import com.example.arthan.global.ArthanApp
import com.example.arthan.model.ApprovedCaseData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable

class ApprovedAdapter(private val context: Context,
                      private val from: String,
private var data: List<ApprovedCaseData>): RecyclerView.Adapter<ApprovedAdapter.ApprovedVH>(),Filterable {

    private val listoriginal:List<ApprovedCaseData>?=ArrayList(data)
    inner class ApprovedVH(private val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(position: Int) {

            when(from){
                "BM" -> {
                    root.setOnClickListener {
                        context.startActivity(Intent(context, ApprovedCustomerLegalStatusActivity::class.java).apply {
                            putExtra("FROM",from)
                            putExtra("Name",this@ApprovedAdapter.data.get(position).name)
                            putExtra("rcu",this@ApprovedAdapter.data.get(position).rcuStatus)
                            putExtra("legal",this@ApprovedAdapter.data.get(position).legalStatus)
                            putExtra("tech",this@ApprovedAdapter.data.get(position).techStatus)
                            putExtra("object",this@ApprovedAdapter.data.get(position) as Serializable)

                        })
                    }

                    root.findViewById<Button>(R.id.btn_rcu).visibility=View.GONE
                    root.findViewById<Button>(R.id.btn_legal).visibility=View.GONE
                    root.findViewById<Button>(R.id.btn_tech).visibility=View.GONE
                    root.findViewById<TextView>(R.id.txt_fee_paid).visibility=View.GONE
                    root.findViewById<Button>(R.id.btn_collect_fees).visibility=View.GONE
                    root.findViewById<Button>(R.id.btn_requestWaiver).visibility=View.VISIBLE
                    root.findViewById<ImageView>(R.id.iv_generate).visibility=View.GONE
                }
                "BCM"->{
                    root.setOnClickListener {
                        CoroutineScope(Dispatchers.IO).launch {
                            var res=RetrofitFactory.getApiService().checkRLTStatus(data[position].caseId)
                            if(res?.body()!=null)
                            {
                                if(res.body()!!.canDecide.equals("y",ignoreCase = true))
                                {
                                    withContext(Dispatchers.Main){
                                        context.startActivity(Intent(context, ApprovedCustomerLegalStatusActivity::class.java).apply {
                                            putExtra("FROM",from)
                                            putExtra("Name",this@ApprovedAdapter.data.get(position).name)
                                            putExtra("rcu",this@ApprovedAdapter.data.get(position).rcuStatus)
                                            putExtra("legal",this@ApprovedAdapter.data.get(position).legalStatus)
                                            putExtra("tech",this@ApprovedAdapter.data.get(position).techStatus)
                                            putExtra("object",this@ApprovedAdapter.data.get(position) as Serializable)

                                        })
                                    }
                                }else
                                {
                                    withContext(Dispatchers.Main)
                                    {
                                        Toast.makeText(context,"Reports are not yet ready",Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }

                    }

                    root.findViewById<Button>(R.id.btn_rcu).visibility=View.VISIBLE
                    root.findViewById<Button>(R.id.btn_legal).visibility=View.VISIBLE
                    root.findViewById<Button>(R.id.btn_tech).visibility=View.VISIBLE
                    root.findViewById<TextView>(R.id.txt_fee_paid).visibility=View.VISIBLE
                    root.findViewById<Button>(R.id.btn_collect_fees).visibility=View.GONE
                    root.findViewById<Button>(R.id.btn_requestWaiver).visibility=View.GONE
                    root.findViewById<ImageView>(R.id.iv_generate).visibility=View.VISIBLE
                }
                else -> {
                    root.setOnClickListener{

                        context.startActivity(Intent(context,RMRequestWaiverActivity::class.java).apply {
                            putExtra("loanId",this@ApprovedAdapter.data[position].caseId)
                        })
                        (context as Activity).finish()
                    }
                    root.findViewById<Button>(R.id.btn_rcu).visibility=View.GONE
                    root.findViewById<Button>(R.id.btn_legal).visibility=View.GONE
                    root.findViewById<Button>(R.id.btn_tech).visibility=View.GONE
                    root.findViewById<TextView>(R.id.txt_fee_paid).visibility=View.GONE
                    root.findViewById<ImageView>(R.id.iv_generate).visibility=View.GONE

                   /* root.findViewById<Button>(R.id.btn_collect_fees).setOnClickListener {

                        CoroutineScope(Dispatchers.IO).launch {
                            var res=RetrofitFactory.getApiService().sendPaymentLink(data[position].caseId)
                            if(res?.body()!=null)
                            {
                                withContext(Dispatchers.Main)
                                {
                                    Toast.makeText(context,"Payment link sent to customer successfully",Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }*/
                    var dailog:AlertDialog?=null
                    root.findViewById<Button>(R.id.btn_collect_fees).visibility=View.GONE
                    root.findViewById<Button>(R.id.btn_requestWaiver).visibility=View.GONE
                    }

            }
            root.findViewById<EditText>(R.id.remarks).visibility=View.GONE
            root.findViewById<EditText>(R.id.waiverAmt).visibility=View.GONE
            root.findViewById<Button>(R.id.submitWaiver).visibility=View.GONE

            root.findViewById<Button>(R.id.btn_requestWaiver).setOnClickListener {
              /*  root.findViewById<EditText>(R.id.remarks).visibility=View.VISIBLE
                root.findViewById<EditText>(R.id.waiverAmt).visibility=View.VISIBLE
                root.findViewById<Button>(R.id.submitWaiver).visibility=View.VISIBLE*/

                /*root.findViewById<Button>(R.id.submitWaiver).setOnClickListener {

                    val progressLoader = ProgrssLoader(context)
                    progressLoader.showLoading()
                    var map = HashMap<String, String>()
                    map["loanId"] = data[position].caseId
                    map["remarks"] = root.findViewById<EditText>(R.id.remarks).text.toString()
                    map["eId"] = "RM1"
                    map["userId"] = ArthanApp.getAppInstance().loginUser
                    map["waiveAmt"] = root.findViewById<EditText>(R.id.waiverAmt).text.toString()
                    CoroutineScope(Dispatchers.IO).launch {

                        var res = RetrofitFactory.getApiService().rmRequestWaiver(map)
                        if (res?.body() != null) {
                            withContext(Dispatchers.Main) {
                                progressLoader.dismmissLoading()

                                root.findViewById<EditText>(R.id.remarks).setText("")
                                root.findViewById<EditText>(R.id.waiverAmt).setText("")
                                root.findViewById<EditText>(R.id.remarks).visibility=View.GONE
                                root.findViewById<EditText>(R.id.waiverAmt).visibility=View.GONE
                                root.findViewById<Button>(R.id.submitWaiver).visibility=View.GONE
                                Toast.makeText(context, "Request successful", Toast.LENGTH_LONG)
                                    .show()
                                (context as CommonApprovedListingActivity).nnotifyAfterWaiver()
                            }
                        }
                    }
                }*/


                var layoutInflater: LayoutInflater =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                var view = layoutInflater.inflate(R.layout.remarks_popup, null)
                val dailog = AlertDialog.Builder(context).setView(view).create()
                if (from == "BM") {
                    dailog!!.show()
                }
                var etRemark = view.findViewById<EditText>(R.id.et_remarks)
                var btnSubmit = view.findViewById<Button>(R.id.btn_submit)
                btnSubmit.setOnClickListener {

                    val progressLoader = ProgrssLoader(context)
                    progressLoader.showLoading()
                    dailog!!.dismiss()
                    CoroutineScope(Dispatchers.IO).launch {
                        if (from == "RM") {

                            var map = HashMap<String, String>()
                            map["loanId"] = data[position].caseId
                            map["remarks"] = etRemark.text.toString()
                            map["eId"] = "RM1"
                            map["userId"]=ArthanApp.getAppInstance().loginUser
                            var res = RetrofitFactory.getApiService().rmRequestWaiver(map)
                            if (res?.body() != null) {
                                withContext(Dispatchers.Main) {
                                    progressLoader.dismmissLoading()
                                    Toast.makeText(context, "Request successful", Toast.LENGTH_LONG)
                                        .show()
                                    (context as CommonApprovedListingActivity).nnotifyAfterWaiver()
                                }
                            }
                        } else if (from == "BM") {
                            var map = HashMap<String, String>()
                            map["loanId"] = data[position].caseId
                            map["remarks"] = etRemark.text.toString()
                            map["eId"] = ArthanApp.getAppInstance().loginRole
                            map["userId"]=ArthanApp.getAppInstance().loginUser

                            var res = RetrofitFactory.getApiService().bmRequestWaiver(map)
                            if (res?.body() != null) {
                                withContext(Dispatchers.Main) {
                                    progressLoader.dismmissLoading()

                                    Toast.makeText(context, "Request successful", Toast.LENGTH_LONG)
                                        .show()
                                    (context as Activity).finish()
                                    context.startActivity(Intent(context,CommonApprovedListingActivity::class.java))

                                }
                            }

                        }
                        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)
                        btnCancel.setOnClickListener { dailog.dismiss() }
                    }
                }
            }

                root.findViewById<TextView>(R.id.txt_case_id).text= "CaseId: ${data[position].caseId}"
            root.findViewById<TextView>(R.id.txt_customer_name).text= "Customer Name: ${data[position].name}"
            root.findViewById<TextView>(R.id.txt_amount).text= "Approved Amount: ${data[position].approvedAmt}"
            root.findViewById<TextView>(R.id.txt_tenure).text= "Tenure: ${data[position].tenure}"
            root.findViewById<TextView>(R.id.txt_emi).text= "EMI: ${data[position].emi}"
            root.findViewById<TextView>(R.id.txt_loan_type).text= "Loan Type: ${data[position].loanType}"
            root.findViewById<TextView>(R.id.txt_login_date).text= "Login Date: ${data[position].loginDate}"
            root.findViewById<TextView>(R.id.txt_approved_date).text= "Approved Date: ${data[position].approvedDate}"

            var btnRcu=root.findViewById<Button>(R.id.btn_rcu)
            var btnLegal=root.findViewById<Button>(R.id.btn_legal)
            var btnTech=root.findViewById<Button>(R.id.btn_tech)

            if((from=="BM"||from=="BCM")&&data[position].rcuStatus!=null) {

                if (data[position].rcuStatus.toString().contentEquals("Y")) {
                    btnRcu.setBackgroundResource(R.drawable.curve_rect_btn_bg_enabled)
                    btnRcu.setTextColor(Color.WHITE)
                }
                if (data[position].legalStatus.toString().contentEquals("Y")) {
                    btnLegal.setBackgroundResource(R.drawable.curve_rect_btn_bg_enabled)
                    btnLegal.setTextColor(Color.WHITE)
                }
                if (data[position].techStatus.toString().contentEquals("Y")) {
                    btnTech.setBackgroundResource(R.drawable.curve_rect_btn_bg_enabled)
                    btnTech.setTextColor(Color.WHITE)
                }
                if (data[position].feePaidStatus!=null&&data[position].feePaidStatus.toString().contentEquals("Y")) {
                    root.findViewById<TextView>(R.id.txt_fee_paid)
                        .setTextColor(Color.parseColor("#43A047"))
                }
            }

        }

    }
    override  fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val query = charSequence.toString()
                //List<ScreeningData>
                var filtered = ArrayList<ApprovedCaseData>()
                if (query.isEmpty()) {
                    filtered.addAll(listoriginal!!.toList())
                } else {
                    for (name in data) {
                        if ((name as ApprovedCaseData).name.toLowerCase().startsWith(query.toLowerCase())) {
                            filtered.add(name)
                        }else if((name as ApprovedCaseData).customerId.startsWith(query)){
                            filtered.add(name)

                        }
                    }

                }

                val results = FilterResults()
                results.count = filtered.size
                results.values = filtered
                return results
            }

            override fun publishResults(
                charSequence: CharSequence,
                results: FilterResults
            ) {
                data= emptyList()
                var itemsFiltered = results.values as List<ApprovedCaseData>
                data=itemsFiltered
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
        ApprovedVH(LayoutInflater.from(context).inflate(R.layout.row_approved, parent, false))


    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ApprovedVH, position: Int) {
        holder.bind(position)
    }
}