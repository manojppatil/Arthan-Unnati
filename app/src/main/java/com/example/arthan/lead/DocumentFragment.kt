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
import com.example.arthan.ocr.CardResponse
import com.example.arthan.utils.ArgumentKey
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
    private var idProofUrl:String=""
    private var addrProofUrl:String=""
    private var businessCont:String=""
    private var offcAddrProof:String=""
    private var incomeProof:String=""
    private var propertyDoc:String=""

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
                call = "PROPDOC"
                spinner = spinner_propertyDocument
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitFactory.getApiService().getDocMstr(call)
                if (response?.body()?.errorCode == "200") {

                    withContext(Dispatchers.Main) {
                        if (call == "PROPDOC") {
                            progressLoader!!.dismmissLoading()
                        }
                        var data=response.body()?.data as ArrayList
                        data.add(0,Data("Select type","100","Select type"))

                        spinner?.adapter = getAdapter(data)
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
                    val progressBar = ProgrssLoader(context!!)
                    progressBar.showLoading()
                    var map = HashMap<String, String>()
                    map["loanId"] = arguments?.getString("loanId")!!
                    map["custId"] = arguments?.getString("custId")!!
                    map["userId"] = "RM1"
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCode.VoterCard -> {
                data?.let {
                    val VoterCard: CardResponse? =
                        it.extras?.getParcelable<CardResponse>(ArgumentKey.VoterDetails)
                    idProofUrl= VoterCard?.cardFrontUrl.toString()

                }
            }

            RequestCode.Passport -> {
                data?.let {
                    val passport: CardResponse? =
                        it.extras?.getParcelable<CardResponse>(ArgumentKey.Passport)
                    idProofUrl= passport?.cardFrontUrl.toString()

                }
            }

            RequestCode.PanCard -> {
                data?.let {
                    val passport: CardResponse? =
                        it.extras?.getParcelable<CardResponse>(ArgumentKey.PanDetails)
                    idProofUrl= passport?.cardFrontUrl.toString()

                }
            }

            RequestCode.AadharCard -> {
                data?.let {
                    val passport: CardResponse? =
                        it.extras?.getParcelable<CardResponse>(ArgumentKey.AadharDetails)
                    idProofUrl= passport?.cardFrontUrl.toString()
                }
            }

            RequestCode.DrivingLicense -> {
                data?.let {
                    val DrivingLicense: CardResponse? =
                        it.extras?.getParcelable<CardResponse>(ArgumentKey.DrivingLicense)
                    idProofUrl= DrivingLicense?.cardFrontUrl.toString()

                }
            }
            RequestCode.ApplicantPhoto -> {
                data?.let {
                    val ApplicantPhoto: CardResponse? =
                        it.extras?.getParcelable<CardResponse>(ArgumentKey.ApplicantPhoto)
                    idProofUrl= ApplicantPhoto?.cardFrontUrl.toString()

                }
            } RequestCode.PFP -> {
            data?.let {
                val PFP: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.PFP)
                idProofUrl= PFP?.cardFrontUrl.toString()

            }
        }
            RequestCode.waterBill -> {
                data?.let {
                    val waterBill: CardResponse? =
                        it.extras?.getParcelable<CardResponse>(ArgumentKey.waterBill)
                    addrProofUrl= waterBill?.cardFrontUrl.toString()

                }
            } RequestCode.electricityBill -> {
            data?.let {
                val electricityBill: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.electricityBill)
                addrProofUrl= electricityBill?.cardFrontUrl.toString()

            }
        }RequestCode.telephonebill -> {
            data?.let {
                val telephonebill: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.telephoneBill)
                addrProofUrl= telephonebill?.cardFrontUrl.toString()

            }
        }
            RequestCode.AadharCardAddrProof -> {
                data?.let {
                    val passport: CardResponse? =
                        it.extras?.getParcelable<CardResponse>(ArgumentKey.AadharCardAddrProof)
                    idProofUrl= passport?.cardFrontUrl.toString()
                }
            }


            RequestCode.SalesTaxRegistration -> {
                data?.let {
                    val SalesTaxRegistration: CardResponse? =
                        it.extras?.getParcelable<CardResponse>(ArgumentKey.SalesTaxRegistration)
                    businessCont= SalesTaxRegistration?.cardFrontUrl.toString()

                }
            } RequestCode.VatOrder -> {
            data?.let {
                val VatOrder: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.VatOrder)
                businessCont= VatOrder?.cardFrontUrl.toString()

            }
        }RequestCode.LicenseissuedunderShop -> {
            data?.let {
                val LicenseissuedunderShop: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.LicenseissuedunderShop)
                businessCont= LicenseissuedunderShop?.cardFrontUrl.toString()

            }
        }RequestCode.EstablishmentAct -> {
            data?.let {
                val EstablishmentAct: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.EstablishmentAct)
                businessCont= EstablishmentAct?.cardFrontUrl.toString()

            }
        } RequestCode.CST -> {
            data?.let {
                val CST: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.CST)
                businessCont= CST?.cardFrontUrl.toString()

            }
        }RequestCode.VAT -> {
            data?.let {
                val VAT: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.VAT)
                businessCont= VAT?.cardFrontUrl.toString()

            }
        }RequestCode.GSTCert -> {
            data?.let {
                val GSTCert: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.GSTCert)
                businessCont= GSTCert?.cardFrontUrl.toString()

            }
        } RequestCode.CurrentACofbankStmt -> {
            data?.let {
                val CurrentACofbankStmt: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.CurrentACofbankStmt)
                businessCont= CurrentACofbankStmt?.cardFrontUrl.toString()

            }
        }RequestCode.SSIcertificate -> {
            data?.let {
                val SSIcertificate: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.SSIcertificate)
                businessCont= SSIcertificate?.cardFrontUrl.toString()

            }
        }

            RequestCode.LatestTelephoneBill -> {
                data?.let {
                    val LatestTelephoneBill: CardResponse? =
                        it.extras?.getParcelable<CardResponse>(ArgumentKey.LatestTelephoneBill)
                    offcAddrProof= LatestTelephoneBill?.cardFrontUrl.toString()

                }
            } RequestCode.ElectricityBillOfcAdd -> {
            data?.let {
                val ElectricityBillOfcAdd: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.ElectricityBillOfcAdd)
                offcAddrProof= ElectricityBillOfcAdd?.cardFrontUrl.toString()

            }
        }RequestCode.BankStatement -> {
            data?.let {
                val BankStatement: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.BankStatement)
                offcAddrProof= BankStatement?.cardFrontUrl.toString()

            }
        }RequestCode.LeaveandLicenceagreement -> {
            data?.let {
                val LeaveandLicenceagreement: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.LeaveandLicenceagreement)
                offcAddrProof= LeaveandLicenceagreement?.cardFrontUrl.toString()

            }
        }
            RequestCode.Last2yearsITR -> {
                data?.let {
                    val Last2yearsITR: CardResponse? =
                        it.extras?.getParcelable<CardResponse>(ArgumentKey.Last2yearsITR)
                    incomeProof= Last2yearsITR?.cardFrontUrl.toString()

                }
            } RequestCode.Auditedbalancesheet -> {
            data?.let {
                val Auditedbalancesheet: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.Auditedbalancesheet)
                incomeProof= Auditedbalancesheet?.cardFrontUrl.toString()

            }
        }RequestCode.SaleDeed -> {
            data?.let {
                val SaleDeed: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.SaleDeed)
                propertyDoc= SaleDeed?.cardFrontUrl.toString()

            }
        }RequestCode.ChainDocument -> {
            data?.let {
                val ChainDocument: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.ChainDocument)
                propertyDoc= ChainDocument?.cardFrontUrl.toString()

            }
        } RequestCode.PropertyTaxReceipt -> {
            data?.let {
                val PropertyTaxReceipt: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.PropertyTaxReceipt)
                propertyDoc= PropertyTaxReceipt?.cardFrontUrl.toString()

            }
        } RequestCode.ROR -> {
            data?.let {
                val ROR: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.ROR)
                propertyDoc= ROR?.cardFrontUrl.toString()

            }
        }RequestCode.NOC -> {
            data?.let {
                val NOC: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.NOC)
                propertyDoc= NOC?.cardFrontUrl.toString()

            }
        }RequestCode._7by12 -> {
            data?.let {
                val _7by12: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey._7by12)
                propertyDoc= _7by12?.cardFrontUrl.toString()

            }
        }RequestCode.Mutation -> {
            data?.let {
                val Mutation: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.Mutation)
                propertyDoc= Mutation?.cardFrontUrl.toString()

            }
        }RequestCode.FerfarCertificate -> {
            data?.let {
                val FerfarCertificate: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.FerfarCertificate)
                propertyDoc= FerfarCertificate?.cardFrontUrl.toString()

            }
        }RequestCode.Others -> {
            data?.let {
                val Others: CardResponse? =
                    it.extras?.getParcelable<CardResponse>(ArgumentKey.Others)
                propertyDoc= Others?.cardFrontUrl.toString()

            }
        }

        }
    }
    private fun getAdapter(list: List<Data>?): DataSpinnerAdapter =
        DataSpinnerAdapter(context!!, list?.toMutableList() ?: mutableListOf()).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }


    override fun onNothingSelected(parent: AdapterView<*>?) {


    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {


        parent?.getItemAtPosition(position)?.let {
            if(check==0||check<=5) {
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
                                putExtra(DOC_TYPE,  RequestCode.Passport )
                            },  RequestCode.Passport )
                        }
                        "Voters ID card" -> {

                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.VoterCard )
                            },  RequestCode.VoterCard )
                        }
                        "Driving License" -> {

                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.DrivingLicense)
                            },  RequestCode.DrivingLicense )
                        }
                        "Pan Card" -> {

                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PanCard )
                            },  RequestCode.PanCard )
                        }
                        "Aadhaar Card" -> {

                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.AadharCard )
                            },  RequestCode.AadharCard )
                        }
                    }
                }
                spinner_AddProof.id -> {

                    when (list?.get(position)?.value) {
                        "Electricity bills " -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.electricityBill )
                            },  RequestCode.electricityBill )
                        }
                        "water bills" -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.waterBill )
                            },  RequestCode.waterBill )
                        }
                        "telephone bills" -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.telephonebill )
                            },  RequestCode.telephonebill )
                        }
                        "Aadhaar Card" -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.AadharCard )
                            },  RequestCode.AadharCard )
                        }

                    }
                }
                spinner_PFP.id -> {

                    when (list?.get(position)?.value) {
                        "VAT assessment order" -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.VatOrder )
                            },  RequestCode.VatOrder )
                        }
                        "Sales Tax Registration " -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.SalesTaxRegistration )
                            },  RequestCode.SalesTaxRegistration )
                        }
                        "License issued under Shop" -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.LicenseissuedunderShop )
                            },  RequestCode.LicenseissuedunderShop )
                        }
                        "Establishment Act" -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.EstablishmentAct )
                            },  RequestCode.EstablishmentAct )
                        }
                        "CST" -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.CST )
                            },  RequestCode.CST )
                        }
                        "VAT" -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.VAT )
                            },  RequestCode.VAT )
                        }
                        "GST Cert" -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.GSTCert )
                            },  RequestCode.GSTCert )
                        }
                        "Current AC of bank Stmt" -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.CurrentACofbankStmt )
                            },  RequestCode.CurrentACofbankStmt )
                        }
                        "SSI certificate" -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.SSIcertificate )
                            },  RequestCode.SSIcertificate )
                        }

                    }
                }
                spinner_idPBS.id -> {

                    when (list?.get(position)?.value) {
                        "Latest Telephone Bill" -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.LatestTelephoneBill )
                            },  RequestCode.LatestTelephoneBill )
                        }
                        "Electricity Bill" -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.ElectricityBillOfcAdd )
                            },  RequestCode.ElectricityBillOfcAdd )
                        }
                        "Bank Statement " -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.BankStatement )
                            },  RequestCode.BankStatement )
                        }
                        "Leave and licence agreement" -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.LeaveandLicenceagreement )
                            },  RequestCode.LeaveandLicenceagreement )
                        }
                    }
                }
                spinner_AFS.id -> {


                    when (list?.get(position)?.value) {
                        "Last 2 years ITR" -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.Last2yearsITR )
                            },  RequestCode.Last2yearsITR )
                        }
                        "Audited balance sheet" -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.Auditedbalancesheet )
                            },  RequestCode.Auditedbalancesheet )
                        }

                    }
                }
                spinner_propertyDocument.id -> {

                    when (list?.get(position)?.value) {
                        "Sale Deed"
                        -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.SaleDeed )
                            },  RequestCode.SaleDeed )
                        }
                        "Chain Document"
                        -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.ChainDocument )
                            },  RequestCode.ChainDocument )
                        }
                        "Property Tax Receipt"
                        -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.PropertyTaxReceipt )
                            },  RequestCode.PropertyTaxReceipt )
                        }
                        "ROR" -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.ROR )
                            },  RequestCode.ROR )
                        }
                        "NOC" -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.NOC )
                            },  RequestCode.NOC )
                        }
                        "7/12" -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode._7by12 )
                            },  RequestCode._7by12 )
                        }

                        "Mutation" -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.Mutation )
                            },  RequestCode.Mutation )
                        }
                        "Ferfar Certificate" -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.FerfarCertificate )
                            },  RequestCode.FerfarCertificate )
                        }
                        "Others" -> {
                              startActivityForResult(Intent(activity, UploadDocumentActivity::class.java).apply {
                                putExtra(DOC_TYPE,  RequestCode.Others )
                            },  RequestCode.Others )
                        }


                    }
                }
            }
        }
    }
}