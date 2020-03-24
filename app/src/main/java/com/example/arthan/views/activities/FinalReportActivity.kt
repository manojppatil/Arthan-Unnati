package com.example.arthan.views.activities

import android.content.Intent
import android.view.View
import com.example.arthan.R
import com.example.arthan.global.APPROVE
import com.example.arthan.global.REJECT
import com.example.arthan.global.STATUS
import kotlinx.android.synthetic.main.activity_final_report.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FinalReportActivity: BaseActivity() {

    override fun contentView()= R.layout.activity_final_report

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        btn_search.visibility= View.GONE
        btn_filter.visibility= View.GONE

        pb_approve.isIndeterminate= false

        txt_approve.setOnClickListener {
            approve()
        }

        txt_reject.setOnClickListener {
            reject()
        }

    }

    override fun onResume() {
        super.onResume()

        ll_approve.setBackgroundResource(R.drawable.bg_white_rectangle)
        txt_approve.visibility= View.VISIBLE
        pb_approve.visibility= View.GONE
        txt_approved.visibility= View.GONE
        img_approved.visibility= View.GONE
        txt_approved.text= "Approve"

        ll_reject.setBackgroundResource(R.drawable.bg_white_rectangle)
        txt_reject.visibility= View.VISIBLE
        pb_reject.visibility= View.GONE
        txt_rejected.visibility= View.GONE
        img_rejected.visibility= View.GONE
        txt_rejected.text= "Reject"
    }

    private fun approve(){

        GlobalScope.launch(context = Dispatchers.Main) {

            ll_reject.isEnabled= false

            ll_approve.setBackgroundResource(R.drawable.bg_approve)

            txt_approve.visibility= View.GONE
            pb_approve.visibility= View.VISIBLE
            txt_approved.visibility= View.VISIBLE

            var progress= 0
            for(x in 0 .. 10){
                delay(1000)
                progress += 10
                pb_approve.progress = progress
            }

            if(pb_approve.progress == 100){

                pb_approve.visibility= View.GONE
                img_approved.visibility= View.VISIBLE
                txt_approved.text= "Approved"

                delay(1000)

                startActivity(Intent(this@FinalReportActivity,SubmitFinalReportActivity::class.java).apply {
                    putExtra(STATUS, APPROVE)
                })
            }
        }

    }


    private fun reject(){

        GlobalScope.launch(context = Dispatchers.Main) {

            ll_approve.isEnabled= false
            ll_reject.setBackgroundResource(R.drawable.bg_approve)

            txt_reject.visibility= View.GONE
            pb_reject.visibility= View.VISIBLE
            txt_rejected.visibility= View.VISIBLE

            var progress= 0
            for(x in 0 .. 10){
                delay(1000)
                progress += 10
                pb_reject.progress = progress
            }

            if(pb_reject.progress == 100){

                pb_reject.visibility= View.GONE
                img_rejected.visibility= View.VISIBLE
                txt_rejected.text= "Rejected"

                delay(1000)

                startActivity(Intent(this@FinalReportActivity,SubmitFinalReportActivity::class.java).apply {
                    putExtra(STATUS, REJECT)
                })
            }
        }

    }

    override fun screenTitle()= "Final Report"
}