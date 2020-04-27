package com.example.arthan.dashboard.bcm

import android.content.Context
import android.content.Intent
import android.view.View
import com.example.arthan.R
import com.example.arthan.lead.model.responsedata.PdX
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.layout_bm_toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class PDActivity : BaseActivity(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main
    private val custName:String?=""
    private var mPDData: PdX? = null

    override fun contentView()=R.layout.activity_pd

    override fun init() {

        if (intent?.hasExtra(ArgumentKey.PDData) == true) {
            mPDData = intent?.getParcelableExtra(ArgumentKey.PDData) as? PdX
        }
        btn_search.visibility = View.GONE
        btn_filter.visibility = View.GONE
    }

    override fun onToolbarBackPressed() {
        onBackPressed()
    }


    override fun screenTitle()="PD * "+intent.getStringExtra("cname")
    companion object {
        fun startMe(
            context: Context?,
            pdData: PdX?,
            custName: String?
        ) {
            custName
            context?.startActivity(Intent(context, PDActivity::class.java).apply {
                putExtra(ArgumentKey.PDData, pdData)
                putExtra("cname",custName)
            })
        }
    }
}

