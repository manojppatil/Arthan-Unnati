package com.example.arthan.dashboard.rm.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.example.arthan.R
import com.example.arthan.model.AmListModel
import com.example.arthan.network.ApiService
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MyAmListAdapter (private val context: Context,
                       private val data: List<AmListModel>): RecyclerView.Adapter<MyAmListAdapter.AmList>() , CoroutineScope{

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val ioContext: CoroutineContext
        get() = Dispatchers.IO

    private val uiContext: CoroutineContext
        get() = Dispatchers.Main

    inner class AmList(private val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(position: Int) {
            root.findViewById<TextView>(R.id.nameValue).text=data[position].name
            root.findViewById<TextView>(R.id.dateValue).text=data[position].submittedDate
            root.findViewById<TextView>(R.id.statusValue).text=data[position].status
            root.findViewById<TextView>(R.id.mobile_number).text = data[position].amMobNo
            val progressBar: ProgrssLoader? = if (context != null) ProgrssLoader(context!!) else null
            root.findViewById<Button>(R.id.resend).setOnClickListener {

                CoroutineScope(ioContext).launch {
                    try {
                        Log.d("Amid","amid"+data[position].amId)
                        val response = RetrofitFactory.getApiService().reSendAMLink(data[position].amId)
                        if (response?.isSuccessful == true) {
                            val result = response.body()
                            if (result?.apiCode == "200") {
                                withContext(uiContext) {
                                    progressBar?.dismmissLoading()
                                    Toast.makeText(context,"Link resent Successfully",Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            progressBar?.dismmissLoading()
                            Toast.makeText(
                                context,
                                response?.body()?.apiCode + "    Something went wrong. Please try later!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: Exception) {
                        if (progressBar != null) {
                            stopLoading(progressBar, "Something went wrong. Please try later!")
                        }
                        e.printStackTrace()
                        Crashlytics.log(e.message)

                    }
                }

            }

        }


    }
    private suspend fun stopLoading(progressBar: ProgrssLoader, message: String?) {
        withContext(uiContext) {
            progressBar.dismmissLoading()
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
        AmList(LayoutInflater.from(context).inflate(R.layout.am_adapter_pattern, parent, false))


    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: AmList, position: Int) {
        holder.bind(position)
    }
}