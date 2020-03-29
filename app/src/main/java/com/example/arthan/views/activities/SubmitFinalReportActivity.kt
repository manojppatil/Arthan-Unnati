package com.example.arthan.views.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.arthan.R
import com.example.arthan.dashboard.bm.BMDashboardActivity
import com.example.arthan.dashboard.bm.BMScreeningReportActivity
import com.example.arthan.global.STATUS
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.DateFormatUtil
import com.example.arthan.views.adapters.DocumentAdapter
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import kotlinx.android.synthetic.main.activity_submit_final_report.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class SubmitFinalReportActivity : BaseActivity(), View.OnClickListener {

    val docList = mutableListOf<Uri>()
    var currentCapture: Uri? = null
    var mDocAdapter: DocumentAdapter? = null

    override fun contentView() = R.layout.activity_submit_final_report

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        btn_search.visibility = View.GONE
        btn_filter.visibility = View.GONE

        txt_change.setOnClickListener(this)
        btn_submit.setOnClickListener(this)
        ll_upload_document.setOnClickListener(this)

        txt_status.text = "Status: ${intent.getStringExtra(STATUS)}"
        txt_reason_msg.text=(resources.getString(R.string.state_the_reasons_for_the_approval_of_this_application,intent.getStringExtra(STATUS)))

        et_reason.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                checkForProceed()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        mDocAdapter = DocumentAdapter(this, docList)
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

                var rejection=""
                if(intent.getStringExtra(STATUS).contains("reject",ignoreCase = true))
                {
                    rejectReason.visibility=View.VISIBLE
                   rejection= rejectReason.selectedItem.toString()
                }
                var map=HashMap<String,String>()
                map["loanId"] = intent.getStringExtra("loanId")
                map["custId"] = intent.getStringExtra("custId")
                map["bmDecision"] = intent.getStringExtra(STATUS)
                map["rejectReason"] = rejection
                map["remarks"] = et_reason.text.toString()
                map["supportingDoc"] = ""

                CoroutineScope(Dispatchers.IO).launch {
                    val respo = RetrofitFactory.getApiService().bmSubmit(
                        map
                    )

                    val result = respo.body()
                    if (respo.isSuccessful && respo.body() != null && result?.apiCode == "200") {

                        startActivity(Intent(
                            this@SubmitFinalReportActivity,
                            PendingCustomersActivity::class.java
                        ).apply {
                            putExtra("FROM", "BM")
                        })
                        finish()

                    } else {
                        Toast.makeText(this@SubmitFinalReportActivity, "Please try again later", Toast.LENGTH_LONG).show()
                    }
                }

              /*  val intent = Intent(this, BMDashboardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)*/
            }

            R.id.ll_upload_document -> capture()
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
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Arthan"
        )
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
                /* if (resultCode == Activity.RESULT_OK){
                     ll_upload_document.visibility= View.GONE
                     rv_docs.visibility= View.VISIBLE

                     docList.add(currentCapture!!)
                     mDocAdapter?.addNewDoc(currentCapture!!)

                     Log.e("DOC SIZE","::: ${docList.size}")

                 }*/
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }

    }
}