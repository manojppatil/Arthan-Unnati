package com.example.arthan.dashboard.bm

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import com.example.arthan.R
import com.example.arthan.dashboard.bcm.BCMDashboardActivity
import com.example.arthan.dashboard.bm.adapter.DeviationsAdapter
import com.example.arthan.dashboard.bm.model.DeviationsResponseData
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.global.ArthanApp
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.SplashActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.activity_deviations.*
import kotlinx.android.synthetic.main.bottom_dialog_remark.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class DeviationsActivity : AppCompatActivity(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deviations)

        toolbar_title?.text = "Deviations"
        back_button?.setOnClickListener { onBackPressed() }

        loadInitialData()
        recycler_list?.adapter = DeviationsAdapter().also { adapter ->
            adapter.setButtonClickListener(object : MultipleButtonClickListener {
                override fun onFirstButtonClick(position: Int) {
                    openBottomDialog {
                        Toast.makeText(
                                this@DeviationsActivity, "Text ----> $it",
                                Toast.LENGTH_LONG
                            )
                            .show()
                        (recycler_list?.adapter as? DeviationsAdapter)?.updateListData(
                            it!!.data, "Approve", position, it!!.justiification
                        )
                    }
                }

                override fun onSecondButtonClick(position: Int) {
                    openBottomDialog {
                        Toast.makeText(
                                this@DeviationsActivity, "Text ----> $it",
                                Toast.LENGTH_LONG
                            )
                            .show()
                        (recycler_list?.adapter as? DeviationsAdapter)?.updateListData(
                            it!!.data, "Reject", position,it!!.justiification
                        )
                    }
                }

                override fun onThirdButtonClick(position: Int) {
                    openBottomDialog {
                        Toast.makeText(
                                this@DeviationsActivity, "Text ----> $it",
                                Toast.LENGTH_LONG
                            )
                            .show()
                        (recycler_list?.adapter as? DeviationsAdapter)?.updateListData(
                            it!!.data, "Recommend", position,it.justiification
                        )
                    }
                }
            })
        }
        submit_button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val adapter=recycler_list.adapter as DeviationsAdapter
                val list=adapter.mList
                for(i in list)
                {
                    if(i.deviationRemark==null)
                        i.deviationRemark=""
                    if(i.deviationDecision== null)
                        i.deviationDecision=""
                }
                var response=RetrofitFactory.getApiService().updateDeviations(DeviationsResponseData( list,intent.getStringExtra("loanId"),cb_TriggerDeviations.isChecked.toString()))
                if(response.body()!=null&&response.body()?.apiCode=="200")
                {
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@DeviationsActivity,"Data submitted successfully",Toast.LENGTH_LONG).show()
                        finish()

                    }
                }
            }
        }
    }

    private fun openBottomDialog(onSave: ((data: DataTopass?) -> Unit)? = null) {
         RemarkDialogFragment().also {
            it.setOnSaveClick(onSave)
        }.show(supportFragmentManager, RemarkDialogFragment.TAG)
    }

    private suspend fun stopLoading(progressBar: ProgrssLoader, message: String?) {
        withContext(uiContext) {
            progressBar.dismmissLoading()
            message?.let {
                Toast.makeText(this@DeviationsActivity, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadInitialData() {
        val progressBar = ProgrssLoader(this)
        progressBar.showLoading()
        CoroutineScope(ioContext).launch {
            try {
                val apiResponse = RetrofitFactory.getApiService()
                    .getDeviations(intent.getStringExtra("loanId"))
                if (apiResponse?.isSuccessful == true) {
                    val result = apiResponse.body()
                    withContext(uiContext) {
                        if (result?.deviations != null) {
                            (recycler_list?.adapter as? DeviationsAdapter)
                                ?.updateList(result.deviations)
                            when(result.triggerDevFlag){
                                "true"->cb_TriggerDeviations.isChecked=true
                                "false"->cb_TriggerDeviations.isChecked=false
                            }
                        }
                        stopLoading(progressBar, null)
                    }
                } else {
                    stopLoading(progressBar, "Something went wrong!!")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                stopLoading(progressBar, "Something went wrong!!")
            }
        }
    }

    companion object {
        fun startMe(context: Context?, loanId: String?) =
            context?.startActivity(Intent(context, DeviationsActivity::class.java).apply {
                putExtra(ArgumentKey.LoanId, loanId)
            })
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {


        menuInflater.inflate(R.menu.more,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.homeMenu->{
                finish()
                when (ArthanApp.getAppInstance().loginRole) {
                    "RM" -> {
                        startActivity(Intent(this, RMDashboardActivity::class.java))
                    }
                    "BCM" -> {
                        startActivity(Intent(this, BCMDashboardActivity::class.java))

                    }
                    "BM" -> {
                        startActivity(Intent(this,BMDashboardActivity::class.java))

                    }
                }
            }
            R.id.logoutMenu->
            {
                finish()
                startActivity(Intent(this, SplashActivity::class.java))
            }

        }
        return super.onOptionsItemSelected(item)
    }
}

interface MultipleButtonClickListener {
    fun onFirstButtonClick(position: Int)
    fun onSecondButtonClick(position: Int)
    fun onThirdButtonClick(position: Int)
}

class RemarkDialogFragment : BottomSheetDialogFragment() {

    private var mOnSave: ((data: DataTopass?) -> Unit)? = null
    var justification:String=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_dialog_remark, container, false);
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        justification=Justification_input.text.toString()

        save_button?.setOnClickListener {
            var data=DataTopass( remark_input?.text?.toString(),Justification_input.text.toString())
            mOnSave?.invoke(
                data)
            dismiss()
        }
        cancel_button?.setOnClickListener { dismiss() }
        close_button?.setOnClickListener { dismiss() }
    }

    fun setOnSaveClick(onSave: ((data: DataTopass?) -> Unit)? = null) {
        mOnSave = onSave
    }

    companion object {
        const val TAG = "RemarkDialogFragment"
    }

}
data class DataTopass(
    val data:String?,
    val justiification:String
)
