package com.example.arthan.lead

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.arthan.R
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.global.*
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_documents.*
import kotlinx.android.synthetic.main.fragment_documents.txt_balSheet_card
import kotlinx.android.synthetic.main.fragment_documents.txt_pfp_card
import kotlinx.android.synthetic.main.fragment_documents.txt_finStatement_id
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DocumentFragment : BaseFragment(), View.OnClickListener {

    override fun contentView() = R.layout.fragment_documents

    override fun init() {

        txt_pfp_card.setOnClickListener(this)
        txt_balSheet_card.setOnClickListener(this)
        txt_finStatement_id.setOnClickListener(this)
        txt_property_doc.setOnClickListener(this)

        btn_submit.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.txt_pfp_card -> {
                startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                    putExtra(DOC_TYPE, PFP)
                }, PFP)
            }
            R.id.txt_balSheet_card -> {
                startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                    putExtra(DOC_TYPE, BALSHEET)
                }, BALSHEET)
            }
            R.id.txt_finStatement_id -> {
                startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                    putExtra(DOC_TYPE, FINSTATEMENT)
                }, FINSTATEMENT)
            }
            
            R.id.txt_property_doc -> {
                startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                    putExtra(DOC_TYPE, PROPERTY_DOC)
                }, VOTER_ID_REQ_CODE)
            }
            R.id.btn_submit -> {

                if(arguments!=null) {
                    var map = HashMap<String, String>()
                    map["loanId"] = arguments?.getString("loanId")!!
                    map["custId"] = arguments?.getString("custId")!!
                    CoroutineScope(Dispatchers.IO).launch {
                        val respo = RetrofitFactory.getApiService().submitPresanctionDocs(
                            map
                        )

                        val result = respo.body()

                        if (respo.isSuccessful && respo.body() != null&&result?.apiCode=="200") {

                            val intent = Intent(activity, RMDashboardActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }else
                        {
                            Toast.makeText(activity,"Please try again later",Toast.LENGTH_LONG).show()
                        }
                    }
                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PAN_CARD_REQ_CODE -> {
                txt_pfp_card.tag = "ashja"
                txt_pfp_card.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_document_attached,
                    0,
                    0,
                    0
                )
                txt_pfp_card.setTextColor(ContextCompat.getColor(activity!!, R.color.black))
            }
            AADHAR_CARD_REQ_CODE -> {
                txt_balSheet_card.tag = "dgfsd"
                txt_balSheet_card.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_document_attached,
                    0,
                    0,
                    0
                )
                txt_balSheet_card.setTextColor(ContextCompat.getColor(activity!!, R.color.black))
            }
            VOTER_ID_REQ_CODE -> {
                txt_finStatement_id.tag = "shfkds"
                txt_finStatement_id.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_document_attached,
                    0,
                    0,
                    0
                )
                txt_finStatement_id.setTextColor(ContextCompat.getColor(activity!!, R.color.black))
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
}