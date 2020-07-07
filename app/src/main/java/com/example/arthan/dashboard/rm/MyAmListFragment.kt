package com.example.arthan.dashboard.rm

import com.example.arthan.R
import com.example.arthan.dashboard.rm.adapters.MyAmListAdapter
import com.example.arthan.model.AmListModel
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.am_list_fragment.*

class MyAmListFragment:BaseFragment() {
    override fun contentView(): Int {

        return R.layout.am_list_fragment
    }

    override fun init() {


        var list=ArrayList<AmListModel>()
        list.add(AmListModel("Abc","19-07-2020","Completed"))
        list.add(AmListModel("Abc","19-07-2020","Completed"))
        list.add(AmListModel("Abc","19-07-2020","Completed"))
        list.add(AmListModel("Abc","19-07-2020","Completed"))
        list.add(AmListModel("Abc","19-07-2020","Completed"))
        list.add(AmListModel("Abc","19-07-2020","Completed"))
        list.add(AmListModel("Abc","19-07-2020","Completed"))

        rv_ams.adapter=MyAmListAdapter(context!!,list)
    }
}