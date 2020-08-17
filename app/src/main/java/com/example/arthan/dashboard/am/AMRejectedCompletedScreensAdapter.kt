package com.example.arthan.dashboard.am

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.model.AmCompletedScreens

class AMRejectedCompletedScreensAdapter(private val context: Context,
                                        private val from: String,
                                        private val responseData: AmCompletedScreens
): RecyclerView.Adapter<AMRejectedCompletedScreensAdapter.ScreenNav>() {


    inner class ScreenNav(private val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(position: Int) {
            root.findViewById<TextView>(R.id.screenValue).text=responseData.completedScreens[position]
            root.setOnClickListener {

               context.startActivity(Intent(context, AMRejectedScreenNavActivity::class.java).apply {
                   putExtra("screen", responseData.completedScreens[position])
                   putExtra("amId",responseData.amId)

               })
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
        ScreenNav(LayoutInflater.from(context).inflate(R.layout.screen_nav_adapter_row, parent, false))

    override fun getItemCount() = responseData?.completedScreens!!.size

    override fun onBindViewHolder(holder: ScreenNav, position: Int) {
        holder.bind(position)
    }
}