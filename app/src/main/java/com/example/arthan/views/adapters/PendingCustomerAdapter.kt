package com.example.arthan.views.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.dashboard.bm.BMDocumentVerificationActivity
import com.example.arthan.dashboard.bm.BMScreeningReportActivity
import com.example.arthan.dashboard.bm.Customer360Activity
import com.example.arthan.model.Customer
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.utils.getPixelFromDP
import com.example.arthan.utils.getRupeeSymbol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class PendingCustomerAdapter(private val mContext: Context, private val from: String) :
    RecyclerView.Adapter<PendingCustomerAdapter.PendingCustomerVH>() {

    private val list: MutableList<Customer> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingCustomerVH {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.row_pending_customer, parent, false)
        return PendingCustomerVH(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: PendingCustomerVH, position: Int) {
        holder.bind(position, list[position])
    }

    fun updateList(myQueue: List<Customer>?) {
        list.addAll(myQueue?.toMutableList() ?: return)
    }

    inner class PendingCustomerVH(private val root: View) : RecyclerView.ViewHolder(root) {

        private val mAmount: TextView = itemView.findViewById(R.id.txt_amount)
        private val mViewCustomer360: View = itemView.findViewById(R.id.cl_customer360)
        private val mDocuments: View = itemView.findViewById(R.id.cl_view_document)
        private val mTakeDecision: View = itemView.findViewById(R.id.cl_take_decision)
        fun bind(position: Int, customer: Customer) {

            if (position == 4) {
                val lp = itemView.layoutParams as? ViewGroup.MarginLayoutParams
                    ?: ViewGroup.MarginLayoutParams(
                        ViewGroup.MarginLayoutParams.MATCH_PARENT,
                        ViewGroup.MarginLayoutParams.WRAP_CONTENT
                    )
                lp?.bottomMargin = getPixelFromDP(20f, itemView.context)?.toInt() ?: 0
                lp?.let { itemView.layoutParams = it }
            } else if (position == 0) {
                val lp = itemView.layoutParams as? ViewGroup.MarginLayoutParams
                    ?: ViewGroup.MarginLayoutParams(
                        ViewGroup.MarginLayoutParams.MATCH_PARENT,
                        ViewGroup.MarginLayoutParams.WRAP_CONTENT
                    )
                lp?.topMargin = getPixelFromDP(40f, itemView.context)?.toInt() ?: 0
                lp?.let { itemView.layoutParams = it }
            }

            when (position % 3) {
                0 -> {
                    itemView.findViewById<ImageView?>(R.id.img_customer360)
                        ?.let {
                            it.setImageResource(R.drawable.ic_clock)
                            it.setColorFilter(Color.rgb(11, 65, 175))
                        }
                    itemView.findViewById<View?>(R.id.img_customer360_next)?.visibility =
                        View.VISIBLE
                    itemView.findViewById<TextView?>(R.id.txt_customer360)
                        ?.setTextColor(Color.rgb(11, 65, 175))
                    mViewCustomer360.setBackgroundColor(Color.rgb(238, 248, 250))
                }
                1 -> {
                    itemView.findViewById<View?>(R.id.img_view_document_next)?.visibility =
                        View.VISIBLE
                    itemView.findViewById<TextView?>(R.id.txt_customer360)
                        ?.setTextColor(Color.rgb(4, 194, 0))
                    itemView.findViewById<TextView?>(R.id.txt_view_document)
                        ?.setTextColor(Color.rgb(11, 65, 175))
                    itemView.findViewById<ImageView?>(R.id.img_customer360)
                        ?.setImageResource(R.drawable.ic_analysis_complete)
                    itemView.findViewById<ImageView?>(R.id.img_view_document)
                        ?.let {
                            it.setImageResource(R.drawable.ic_clock)
                            it.setColorFilter(Color.rgb(11, 65, 175))
                        }
                    mDocuments.setBackgroundColor(Color.rgb(238, 248, 250))
                }
                2 -> {
                    itemView.findViewById<ImageView?>(R.id.img_decision_stage)
                        ?.let {
                            it.background = null
                            it.setImageResource(R.drawable.ic_clock)
                            it.setColorFilter(Color.rgb(11, 65, 175))
                        }
                    itemView.findViewById<View?>(R.id.img_decision_next)?.visibility =
                        View.VISIBLE
                    itemView.findViewById<View?>(R.id.take_decision_message)?.visibility =
                        View.VISIBLE
                    itemView.findViewById<TextView?>(R.id.txt_customer360)
                        ?.setTextColor(Color.rgb(4, 194, 0))
                    itemView.findViewById<TextView?>(R.id.txt_view_document)
                        ?.setTextColor(Color.rgb(4, 194, 0))
                    itemView.findViewById<TextView?>(R.id.txt_take_decision)
                        ?.setTextColor(Color.rgb(11, 65, 175))
                    itemView.findViewById<ImageView?>(R.id.img_customer360)
                        ?.setImageResource(R.drawable.ic_analysis_complete)
                    itemView.findViewById<ImageView?>(R.id.img_view_document)
                        ?.setImageResource(R.drawable.ic_analysis_complete)
                    mTakeDecision.setBackgroundColor(Color.rgb(238, 248, 250))
                }
                3 -> {
                }
            }

            itemView.findViewById<TextView?>(R.id.txt_industry)?.text = customer?.indSeg
            itemView.findViewById<TextView?>(R.id.txt_date)?.text = customer?.loginDate
            itemView.findViewById<TextView?>(R.id.txt_customer_name)?.text = customer?.customerName
            mAmount?.text = customer?.loanAmt

            mAmount.setCompoundDrawablesWithIntrinsicBounds(
                getRupeeSymbol(
                    itemView.context,
                    mAmount.textSize ?: 14f,
                    mAmount.currentTextColor ?: Color.rgb(105, 105, 105)
                ), null, null, null
            )

            mViewCustomer360.setOnClickListener {
                mContext.startActivity(Intent(mContext, Customer360Activity::class.java).apply {
                putExtra("indSeg",customer.indSeg)
                putExtra("loginDate",customer.loginDate)
                putExtra("loanId",customer.loanId)
                putExtra("loanAmt",customer.loanAmt)
                putExtra("cname",customer.customerName)
                putExtra("custId",customer.customerId)
                putExtra("loanType",customer.loanType)

            })
            }

            root.findViewById<ConstraintLayout>(R.id.cl_view_document).setOnClickListener {
                BMDocumentVerificationActivity.startMe(
                    itemView.context,
                    customer.loanId,
                    customer.customerId,
                    from
                )
            }

            root.findViewById<ConstraintLayout>(R.id.cl_take_decision).setOnClickListener {

                if(from=="BM") {
                    val progressLoader = ProgrssLoader(mContext)
                    progressLoader.showLoading()
                    CoroutineScope(Dispatchers.IO).launch {
                        val res = RetrofitFactory.getApiService().bmDecision(customer.loanId)
                        if (res?.body() != null) {
                            progressLoader.dismmissLoading()

                            val canDecide= res.body()!!.canDecide
                            withContext(Dispatchers.Main) {
                                if (canDecide == "N") {

                                    Toast.makeText(mContext,"Please verify Documents & Data before taking a decision",Toast.LENGTH_LONG).show()
                                } else {
                                    mContext.startActivity(
                                        Intent(
                                            mContext,
                                            BMScreeningReportActivity::class.java
                                        ).apply {
                                            putExtra("indSeg", customer.indSeg)
                                            putExtra("loginDate", customer.loginDate)
                                            putExtra("loanId", customer.loanId)
                                            putExtra("loanAmt", customer.loanAmt)
                                            putExtra("cname", customer.customerName)
                                            putExtra("custId", customer.customerId)
                                            putExtra("FROM", "BM")

                                        })
                                }
                            }
                        }else
                        {
                            progressLoader.dismmissLoading()
                        }
                    }
                }else
                {
                    mContext.startActivity(
                        Intent(
                            mContext,
                            BMScreeningReportActivity::class.java
                        ).apply {
                            putExtra("indSeg", customer.indSeg)
                            putExtra("loginDate", customer.loginDate)
                            putExtra("loanId", customer.loanId)
                            putExtra("loanAmt", customer.loanAmt)
                            putExtra("cname", customer.customerName)
                            putExtra("custId", customer.customerId)
                            putExtra("FROM", from)

                        })
                }


            }

           /* if (from == "BCM") {

            } else {

            }*/
        }

    }

}