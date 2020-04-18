package com.example.arthan.dashboard.rm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.arthan.R
import com.example.arthan.dashboard.rm.adapters.PendingDataAdapter
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.activity_lisiting.*
import kotlinx.android.synthetic.main.activity_lisiting.toolbar
import kotlinx.android.synthetic.main.custom_toolbar.*

class PendingDataFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_lisiting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        back_button?.setOnClickListener { activity?.onBackPressed() }
        toolbar.visibility = View.GONE
        rv_listing.adapter = PendingDataAdapter(activity!!)
    }
}