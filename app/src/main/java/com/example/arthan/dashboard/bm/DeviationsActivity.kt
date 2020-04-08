package com.example.arthan.dashboard.bm

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.arthan.R
import com.example.arthan.dashboard.bm.adapter.DeviationsAdapter
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.ProgrssLoader
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
                            it, "Approve", position
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
                            it, "Reject", position
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
                            it, "Recommend", position
                        )
                    }
                }
            })
        }
    }

    private fun openBottomDialog(onSave: ((data: String?) -> Unit)? = null) {
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
}

interface MultipleButtonClickListener {
    fun onFirstButtonClick(position: Int)
    fun onSecondButtonClick(position: Int)
    fun onThirdButtonClick(position: Int)
}

class RemarkDialogFragment : BottomSheetDialogFragment() {

    private var mOnSave: ((data: String?) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_dialog_remark, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        save_button?.setOnClickListener {
            mOnSave?.invoke(remark_input?.text?.toString())
            dismiss()
        }
        cancel_button?.setOnClickListener { dismiss() }
        close_button?.setOnClickListener { dismiss() }
    }

    fun setOnSaveClick(onSave: ((data: String?) -> Unit)? = null) {
        mOnSave = onSave
    }

    companion object {
        const val TAG = "RemarkDialogFragment"
    }
}
