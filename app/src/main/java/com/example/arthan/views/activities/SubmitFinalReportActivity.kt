package com.example.arthan.views.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.example.arthan.R
import com.example.arthan.dashboard.bcm.BCMDashboardActivity
import com.example.arthan.dashboard.bm.BMDashboardActivity
import com.example.arthan.dashboard.bm.model.FinalReportPostData
import com.example.arthan.global.ArthanApp
import com.example.arthan.global.DOC_TYPE
import com.example.arthan.global.STATUS
import com.example.arthan.lead.UploadDocumentActivity
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.network.S3UploadFile
import com.example.arthan.network.S3Utility
import com.example.arthan.ocr.CardResponse
import com.example.arthan.utils.*
import com.example.arthan.views.adapters.DocumentAdapter
import com.example.arthan.views.adapters.SanctionAdapter
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import kotlinx.android.synthetic.main.activity_submit_final_report.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class SubmitFinalReportActivity : BaseActivity(), View.OnClickListener {

    val docList = mutableListOf<Uri>()
    var currentCapture: Uri? = null
    var mDocAdapter: DocumentAdapter? = null
    val fileList: MutableList<S3UploadFile> = mutableListOf()
    var sanctionList = ArrayList<String>()
    var docUrlList: String = ""
    var agreementUrl: String = ""
    var cocUrl: String = ""


    override fun contentView() = R.layout.activity_submit_final_report

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        btn_search.visibility = View.GONE
        btn_filter.visibility = View.GONE

        txt_change.setOnClickListener(this)
        btn_submit.setOnClickListener(this)
        ll_upload_document.setOnClickListener(this)

        txt_status.text = "Status: ${intent.getStringExtra(STATUS)}"
        txt_reason_msg.text = (resources.getString(
            R.string.state_the_reasons_for_the_approval_of_this_application,
            intent.getStringExtra(STATUS)
        ))
        tv_agreement.setOnClickListener(this)
        tv_coc.setOnClickListener(this)
        if (intent.getStringExtra(STATUS).contains("Recommended to CCM", ignoreCase = true)&&ArthanApp.getAppInstance().loginRole=="BM"&&
                intent.getStringExtra("recordType")=="AM") {

            tv_agreement.visibility = View.VISIBLE
            tv_coc.visibility = View.VISIBLE
        }else{
            tv_agreement.visibility = View.GONE
            tv_coc.visibility = View.GONE
        }

        if(ArthanApp.getAppInstance().loginRole=="BM"&&intent.getStringExtra("recordType")=="AM"&&intent.getStringExtra(STATUS).contains("Recommended to CCM", ignoreCase = true)) {
            val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                this.resources.getStringArray(R.array.recommendToCC)
            ) //selected item will look like a spinner set from XML

            rejectReason.adapter = spinnerArrayAdapter

        }
        if(ArthanApp.getAppInstance().loginRole=="BM"&&intent.getStringExtra("recordType")=="AM"&&intent.getStringExtra(STATUS).contains("Reject", ignoreCase = true))
        {

            CoroutineScope(Dispatchers.IO).launch {
                val res=RetrofitFactory.getApiService().bmAMReason()

                if(res?.body()!=null)
                {
                    withContext(Dispatchers.Main) {
                        rejectReason.adapter = getAdapter(res.body()?.data)
                    }
                }
            }
        }
        if (ArthanApp.getAppInstance().loginRole == "BCM") {
            sanctions.visibility = View.VISIBLE
        }
        addSanction.setOnClickListener {
            if (addSanction.text.toString() == "Add new") {
                addSanction.text = "Done"
                addNewSanctionField()
            } else {
                saveSanction()
            }
        }

        et_reason.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                checkForProceed()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        cb_sanction.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                addSanction.visibility = View.VISIBLE

            } else {
                addSanction.visibility = View.GONE
                sanctionList = ArrayList()
                sanctionRv.removeAllViews()
            }
        }


        // mDocAdapter = DocumentAdapter(this, docList,docUrlList)
        //   rv_docs.adapter=mDocAdapter

       /* if (intent.getStringExtra("recordType") == "AM") {
            tv_agreement.visibility = View.VISIBLE
            tv_coc.visibility = View.VISIBLE


        } else {
            tv_agreement.visibility = View.GONE
            tv_coc.visibility = View.GONE
        }*/
    }

    private fun saveSanction() {

        var view = sanctionRv.layoutManager?.findViewByPosition(sanctionList.size - 1)

        if (view != null) {
            var field = view?.findViewById<EditText>(R.id.field)
            if (field != null && field?.length()!! > 0) {
                sanctionList.removeAt(sanctionList.size - 1)
                sanctionList.add(field.text.toString())
                addSanction.text = "Add new"

            }
        }
    }
    private fun getAdapter(list: List<Data>?): DataSpinnerAdapter =
        DataSpinnerAdapter(this, list?.toMutableList() ?: mutableListOf()).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

    fun removeSanctionField(position: Int) {

        sanctionList
            .removeAt(position)

        sanctionRv.adapter?.notifyDataSetChanged()
    }

    private fun addNewSanctionField() {
        if (sanctionList.isEmpty()) {
            sanctionList.add("")
            sanctionRv.adapter = SanctionAdapter(this, sanctionList)
        } else {

            sanctionList.add("")
            var adapter: SanctionAdapter = sanctionRv.adapter as SanctionAdapter
            adapter.notifyDataSetChanged()

        }

    }

    override fun screenTitle() = "Final Report"

    private fun checkForProceed() {
        if (et_reason.text.isNullOrBlank()) {
            btn_submit.isEnabled = false
            btn_submit.setBackgroundResource(R.drawable.ic_next_disable)
            btn_submit.setTextColor(ContextCompat.getColor(this, R.color.disable_text))
        } else {
            btn_submit.isEnabled = true
            btn_submit.setBackgroundResource(R.drawable.ic_next_enabled)
            btn_submit.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.txt_change -> {
                finish()
            }

            R.id.btn_submit -> {
                if( intent.getStringExtra(STATUS).contains("Recommended to CCM", ignoreCase = true)&&intent.getStringExtra("recordType")=="AM"&&ArthanApp.getAppInstance().loginRole=="BM"&&( cocUrl.isEmpty() ||agreementUrl.isEmpty()||docUrlList.isEmpty()))
                {
                    Toast.makeText(this,"Document, Coc & Agreement are mandatory.",Toast.LENGTH_LONG).show()
                    return
                }
                val progressBar = ProgrssLoader(this)
                progressBar.showLoading()

                var decision = ""
                if (intent.getStringExtra(STATUS).contains("reject", ignoreCase = true)) {
                    rejectReason.visibility = View.VISIBLE
                    decision = (rejectReason.selectedItem as Data).value
                } else if (intent.getStringExtra(STATUS).contains("Approve", ignoreCase = true)) {
                    rejectReason.visibility = View.GONE
                    decision = "Approve"

                }
                var user = ""
                if (intent.getStringExtra("recordType") == "AM") {
                    user = intent.getStringExtra("amId")

                }

                var map = FinalReportPostData(
                    intent.getStringExtra("loanId"),
                    intent.getStringExtra("custId"),
                    intent.getStringExtra(STATUS),
                    decision,
                    et_reason.text.toString(),
                    docUrlList,
                    sanctionList, ArthanApp.getAppInstance().loginUser, user,
                    agreementUrl,
                    cocUrl
                )

                if (ArthanApp.getAppInstance().loginRole == "BM") {

                    CoroutineScope(Dispatchers.IO).launch {
                        var respo = if (intent.getStringExtra("recordType") == "AM") {
                            RetrofitFactory.getApiService().bmAmSubmit(
                                map
                            )
                        } else {
                            RetrofitFactory.getApiService().bmSubmit(
                                map
                            )
                        }

                        val result = respo.body()
                        if (respo.isSuccessful && respo.body() != null && result?.apiCode == "200") {

                            progressBar.dismmissLoading()
                            startActivity(Intent(
                                this@SubmitFinalReportActivity,
                                BMDashboardActivity::class.java
                            ).apply {
                                putExtra("FROM", "BM")
                            })
                            withContext(Dispatchers.Main) {
                                var msg =
                                    if (intent.getStringExtra(STATUS).contains(
                                            "reject",
                                            ignoreCase = true
                                        )
                                    ) {
                                        "Case is Rejected Successfully"
                                    } else {
                                        if(intent.getStringExtra("recordType") == "AM"){

                                            "Case successfully submitted to operations"
                                        }else {

                                            "Case is Successfully submitted to BCM"
                                        }
                                    }
                                Toast.makeText(
                                    this@SubmitFinalReportActivity,
                                    msg,
                                    Toast.LENGTH_LONG
                                ).show()
                                finish()
                            }

                        } else {
                            withContext(Dispatchers.Main) {

                                progressBar.dismmissLoading()
                                Toast.makeText(
                                    this@SubmitFinalReportActivity,
                                    "Please try again later",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }

                } else if (ArthanApp.getAppInstance().loginRole == "BCM") {
                    CoroutineScope(Dispatchers.IO).launch {
                        val respo = RetrofitFactory.getApiService().bcmSubmit(
                            map
                        )

                        val result = respo.body()
                        withContext(Dispatchers.Main) {
                            progressBar.dismmissLoading()
                            if (respo.isSuccessful && respo.body() != null && result?.apiCode == "200") {

                                var msg =
                                    when {
                                        intent.getStringExtra(STATUS).contains(
                                            "Approve",
                                            ignoreCase = true
                                        ) -> {
                                            "Case is Approved Successfully"
                                        }
                                        intent.getStringExtra(STATUS).contains(
                                            "reject",
                                            ignoreCase = true
                                        ) -> {
                                            "Case is Rejected Successfully"
                                        }
                                        else -> {

                                            "Case is Successfully Submitted to AA"
                                        }
                                    }
                                Toast.makeText(
                                    this@SubmitFinalReportActivity,
                                    msg,
                                    Toast.LENGTH_LONG
                                ).show()


                                startActivity(Intent(
                                    this@SubmitFinalReportActivity,
                                    BCMDashboardActivity::class.java
                                ).apply {
                                    putExtra("FROM", "BCM")
                                })
                                finish()

                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@SubmitFinalReportActivity,
                                        "Please try again later",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                        }
                    }

                }

                /*  val intent = Intent(this, BMDashboardActivity::class.java)
                  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                  startActivity(intent)*/
            }

            R.id.ll_upload_document -> capture()

            R.id.tv_agreement ->
                startActivityForResult(
                    Intent(
                        this@SubmitFinalReportActivity,
                        UploadDocumentActivity::class.java
                    ).apply {
                        putExtra(DOC_TYPE, RequestCode.Agreement)
                    }, RequestCode.Agreement
                )
            R.id.tv_coc ->
                startActivityForResult(
                    Intent(
                        this@SubmitFinalReportActivity,
                        UploadDocumentActivity::class.java
                    ).apply {
                        putExtra(DOC_TYPE, RequestCode.Coc)
                    }, RequestCode.Coc
                )
        }
    }

    fun capture() {
        val request = permissionsBuilder(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).build()
        request.listeners {
            onAccepted {
                navigateToCamera()
            }
            onDenied {

            }
            onPermanentlyDenied {

            }

        }
        request.send()
    }

    private fun getOutputMediaFile(): File {
        val dir = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "Arthan"
        )/* val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Arthan"
        )*/
        if (!dir.exists())
            dir.mkdirs()
        return File(
            dir.absolutePath + "/IMG_" + DateFormatUtil.currentTime("ddMMyyyy_HHmmssSS") + ".jpg"
        )
    }

    private fun navigateToCamera() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        currentCapture = FileProvider.getUriForFile(
            this, applicationContext.packageName + ".provider",
            getOutputMediaFile()
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentCapture)

        startActivityForResult(intent, 100)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            100 -> {
                ll_upload_document.visibility = View.GONE
                rv_docs.visibility = View.VISIBLE


                val loader = ProgrssLoader(context = this)
                loader.showLoading()
                loadImage(this, rv_docs, currentCapture!!, { filePath ->
                    try {
                        val file: File = File(filePath)
                        val url = file.name
                        val fileList: MutableList<S3UploadFile> = mutableListOf()
                        fileList.add(S3UploadFile(file, url))
                        S3Utility.getInstance(this)
                            .uploadFile(fileList,
                                {
                                    docUrlList = fileList[0].url ?: filePath
                                    println("docurlList-" + docUrlList)
                                    ThreadUtils.runOnUiThread { loader.dismmissLoading() }
                                }) {
                                ThreadUtils.runOnUiThread { loader.dismmissLoading() }
                            }
                    } catch (e: Exception) {
                        ThreadUtils.runOnUiThread { loader.dismmissLoading() }
                        e.printStackTrace()
                    }
                })
                //  docList.add(currentCapture!!)//used

                //      loader.showLoading()
                /*docList.add(currentCapture!!)
           if(mDocAdapter?.itemCount==0)
           {
               rv_docs.adapter=mDocAdapter
           }*/
//                mDocAdapter?.notifyDataSetChanged()
                //  mDocAdapter?.addNewDoc(currentCapture!!)//used
//               mDocAdapter?.notifyDataSetChanged()
//                capture.setImageURI(currentCapture)


/*

                val file = File(currentCapture!!.path)
                val url = file.name + file.extension
                fileList.add(S3UploadFile(file, url))
                S3Utility.getInstance(this)
                    .uploadFile(fileList,
                        {
                            //  MyProfileActivity.profileImage = fileList[0].url ?: filePath
                            ThreadUtils.runOnUiThread {
                                loader.dismmissLoading() }
                        }) {
                        ThreadUtils.runOnUiThread { loader.dismmissLoading() }
                    }
//                     Log.e("DOC SIZE","::: ${docList.size}")
*/


            }
            RequestCode.Agreement -> {
                data?.let {
                    tv_agreement.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_document_attached,
                        0,
                        0,
                        0
                    )

                    val applicantData: CardResponse? =
                        it.getParcelableExtra(ArgumentKey.Agreement) as? CardResponse

                    agreementUrl =
                        applicantData?.cardFrontUrl!!
                }
            }

            RequestCode.Coc -> {
                data?.let {
                    tv_coc.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_document_attached,
                        0,
                        0,
                        0
                    )
                    val applicantData: CardResponse? =
                        it.getParcelableExtra(ArgumentKey.Coc) as? CardResponse

                    cocUrl =  applicantData?.cardFrontUrl!!
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }

    }
}