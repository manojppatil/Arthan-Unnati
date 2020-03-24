package com.example.arthan.views.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.views.activities.CustomerDetailActivity
import com.example.arthan.views.activities.LegalRcuApplicationInfoActivity

class ScreeningAdapter(private val context: Activity, val from: String) :
    RecyclerView.Adapter<ScreeningAdapter.ScreeningVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScreeningVH {
        val view =
            LayoutInflater.from(context).inflate(R.layout.row_screening_customers, parent, false)
        return ScreeningVH(view)
    }

    override fun getItemCount() = 5

    override fun onBindViewHolder(holder: ScreeningVH, position: Int) {
    }


    inner class ScreeningVH(root: View) : RecyclerView.ViewHolder(root) {

        init {
            root.setOnClickListener {
                if (from.equals("LEGAL", true) ||
                    from.equals("RCU", true)
                ) {
                    context.startActivity(
                        Intent(
                            context,
                            LegalRcuApplicationInfoActivity::class.java
                        ).apply {
                            putExtra("FROM", from)
                        })
                } else {
                    context.startActivity(
                        Intent(
                            context,
                            CustomerDetailActivity::class.java
                        ).apply {
                            putExtra("FROM", from)
                        })
                }


            }

        }

    }

}