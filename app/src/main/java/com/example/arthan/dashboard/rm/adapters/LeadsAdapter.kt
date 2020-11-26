package com.example.arthan.dashboard.rm.adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.global.ArthanApp
import com.example.arthan.lead.LoanDetailActivity
import com.example.arthan.model.LeadData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LeadsAdapter(private val context: Context,
                   private val from: String,
private val data: List<LeadData>): RecyclerView.Adapter<LeadsAdapter.LeadVH>() {


    inner class LeadVH(private val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(position: Int) {
            if(from == "RM") {
                root.findViewById<Button>(R.id.btn_reopen).visibility = View.VISIBLE
            } else {
                root.findViewById<Button>(R.id.btn_reopen).visibility = View.VISIBLE

            }
            root.findViewById<Button>(R.id.btn_reopen).setOnClickListener {

                val progress=ProgrssLoader(context)
                progress.showLoading()

                CoroutineScope(Dispatchers.IO).launch {
                    val map=HashMap<String,String>()
                    map["leadId"]=data[position].id
                    map["userId"]=ArthanApp.getAppInstance().loginUser
                    val res=RetrofitFactory.getApiService().moveToScreening(map)
                    if(res.body()!=null)
                    {
                        withContext(Dispatchers.Main){
                            Toast.makeText(context,"Lead Re-opened",Toast.LENGTH_LONG).show()
                            progress.dismmissLoading()
                            context.startActivity(Intent(context,LoanDetailActivity::class.java).apply {
                                putExtra("leadId",this@LeadsAdapter.data[position].id)
                            })
                            (context as Activity).finish()
                        }
                    }
                }

               /* AlertDialog.Builder(context)
                    .setMessage("${data[position].name}, loan rejected Successfully")
                    .setPositiveButton("Ok") { dialogInterface, i ->
                        dialogInterface.dismiss()
                    }.create().show()*/
            }
            root.findViewById<ImageView>(R.id.call_button).setOnClickListener {
                val uri = "tel:" + data[position].mobileNo.trim() ;
                val intent =Intent(Intent.ACTION_DIAL);
                intent.data = Uri.parse(uri)
                context.startActivity(intent)
            }
            root.findViewById<ImageView>(R.id.whats_app_button).setOnClickListener {

              /*  val url = "https://api.whatsapp.com/send?phone=${data[position].mobileNo}"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                context.startActivity(i)*/
                openWhatsApp(data[position].mobileNo,"this is a sample whatsapp message",data[position].name)
            }
            root.findViewById<TextView>(R.id.txt_occupation).text= data[position].segment
            root.findViewById<TextView>(R.id.txt_customer_name).text= "Customer Name: ${data[position].name}"
            root.findViewById<TextView>(R.id.txt_loan_id).text= "Lead Id: ${data[position].id}"
            root.findViewById<TextView>(R.id.txt_loan_amount).text= "Later Date: ${data[position].laterDate}"
            root.findViewById<TextView>(R.id.txt_contact_number).text= "${data[position].mobileNo}"
            root.findViewById<TextView>(R.id.txt_stage).text= "Branch: ${data[position].branch}"



        }
    }

    private fun openWhatsApp(smsNumber: String,msg:String,name:String) {
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.type = "text/plain"
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hi, This is $msg"
        )
        sendIntent.putExtra(name, "$smsNumber@s.whatsapp.net") //phone number without "+" prefix
        sendIntent.setPackage("com.whatsapp")
        if (sendIntent.resolveActivity(context.packageManager) == null) {
            Toast.makeText(context, "Cannot find whatsApp to open", Toast.LENGTH_SHORT).show()
            return
        }
        context.startActivity(sendIntent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
        LeadVH(LayoutInflater.from(context).inflate(R.layout.row_lead, parent, false))

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: LeadVH, position: Int) {
        holder.bind(position)
    }
}