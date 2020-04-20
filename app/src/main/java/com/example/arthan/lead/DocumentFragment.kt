package com.example.arthan.lead

import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.arthan.R
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.global.*
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.utils.RequestCode
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.collateral_section.*
import kotlinx.android.synthetic.main.fragment_documents.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DocumentFragment : BaseFragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    override fun contentView() = R.layout.fragment_documents
    private var progressLoader: ProgrssLoader? = null
    private var check:Int=0

    override fun init() {
        progressLoader = ProgrssLoader(context!!)
        progressLoader!!.showLoading()
        loadData("ID")
        loadData("ADDRESS")
        loadData("CONTBUSS")
        loadData("OFCADDR")
        loadData("INCPROOF")
        loadData("PROPDOC")

        btn_submit.setOnClickListener(this)

        spinner_idProof.onItemSelectedListener = this
        spinner_AddProof.onItemSelectedListener = this
        spinner_idPBS.onItemSelectedListener = this
        spinner_AFS.onItemSelectedListener = this
        spinner_PFP.onItemSelectedListener = this
        spinner_propertyDocument.onItemSelectedListener = this

    }

    private fun loadData(value: String) {

        //ID/ADDRESS/CONTBUSS/OFCADDR/INCPROOF/PROPDOC
        var call: String = ""
        var spinner: Spinner? = null
        when (value) {
            "ID" -> {
                call = "ID"
                spinner = spinner_idProof
            }
            "ADDRESS" -> {
                call = "ADDRESS"
                spinner = spinner_AddProof
            }
            "CONTBUSS" -> {
                call = "CONTBUSS"
                spinner = spinner_PFP
            }
            "OFCADDR" -> {
                call = "OFCADDR"
                spinner = spinner_idPBS
            }
            "INCPROOF" -> {
                call = "INCPROOF"
                spinner = spinner_AFS
            }
            "PROPDOC" -> {
                call = "INCPROOF"
                spinner = spinner_propertyDocument
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitFactory.getApiService().getDocMstr(call)
                if (response?.body()?.errorCode == "200") {

                    withContext(Dispatchers.Main) {
                        if (call == "INCPROOF") {
                            progressLoader!!.dismmissLoading()
                        }
                        spinner?.adapter = getAdapter(response.body()?.data)
                    }
                }

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            /*   R.id.txt_pfp_card -> {
                   startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                       putExtra(DOC_TYPE,  RequestCode.PFP )
                   },  RequestCode.PFP )
               }
               R.id.txt_balSheet_card -> {
                   startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                       putExtra(DOC_TYPE, RequestCode.VoterCard)
                   }, RequestCode.VoterCard)
               }
               R.id.txt_finStatement_id -> {
                   startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                       putExtra(DOC_TYPE, RequestCode.VoterCard)
                   }, RequestCode.VoterCard)
               }

               R.id.txt_property_doc -> {
                   startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                       putExtra(DOC_TYPE, RequestCode.VoterCard)
                   }, RequestCode.VoterCard)
               }*/
            R.id.btn_submit -> {

                if (arguments != null) {
                    var map = HashMap<String, String>()
                    map["loanId"] = arguments?.getString("loanId")!!
                    map["custId"] = arguments?.getString("custId")!!
                    CoroutineScope(Dispatchers.IO).launch {
                        val respo = RetrofitFactory.getApiService().submitPresanctionDocs(
                            map
                        )

                        val result = respo.body()

                        if (respo.isSuccessful && respo.body() != null && result?.apiCode == "200") {

                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    activity,
                                    "Case is Successfully submitted to BM",
                                    Toast.LENGTH_LONG
                                ).show()

                                val intent = Intent(activity, RMDashboardActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        } else {
                            Toast.makeText(activity, "Please try again later", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }

            }
        }
    }

    private fun getAdapter(list: List<Data>?): DataSpinnerAdapter =
        DataSpinnerAdapter(context!!, list?.toMutableList() ?: mutableListOf()).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

    /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
     }*/

    override fun onNothingSelected(parent: AdapterView<*>?) {


    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {


        parent?.getItemAtPosition(position)?.let {
            if(check==0) {
                check++
                return
            }

            val list =
                (parent.adapter as? DataSpinnerAdapter)?.list

            when (parent.id) {
                spinner_idProof.id -> {


                    when (list?.get(position)?.value) {
                        "Passport" -> {

                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "Voters ID card" -> {

                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "Driving License" -> {

                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "Pan Card" -> {

                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "Aadhaar Card" -> {

                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                    }
                }
                spinner_AddProof.id -> {

                    when (list?.get(position)?.value) {
                        "Electricity bills " -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "water bills" -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "telephone bills" -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "Aadhaar Card" -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }

                    }
                }
                spinner_PFP.id -> {

                    when (list?.get(position)?.value) {
                        "VAT assessment order" -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "Sales Tax Registration " -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "License issued under Shop" -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "Establishment Act" -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "CST" -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "VAT" -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "GST Cert" -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "Current AC of bank Stmt" -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "SSI certificate" -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }

                    }
                }
                spinner_idPBS.id -> {

                    when (list?.get(position)?.value) {
                        "Latest Telephone Bill" -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "Electricity Bill" -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "Bank Statement " -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "Leave and licence agreement" -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                    }
                }
                spinner_AFS.id -> {

                    when (list?.get(position)?.value) {
                        "Last 2 years ITR" -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "Audited balance sheet" -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }

                    }
                }
                spinner_propertyDocument.id -> {

                    when (list?.get(position)?.value) {
                        "Sale Deed"
                        -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "Chain Document"
                        -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "Property Tax Receipt"
                        -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "ROR" -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "NOC" -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "7/12" -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }

                        "Mutation" -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "Ferfar Certificate" -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }
                        "Others" -> {
                            startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PFP )
                            },  RequestCode.PFP )
                        }


                    }
                }
            }
        }
    }
}