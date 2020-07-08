package com.example.arthan.dashboard.rm

import com.example.arthan.R
import com.example.arthan.dashboard.rm.adapters.MyAmListAdapter
import com.example.arthan.global.ArthanApp
import com.example.arthan.model.AmListModel
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.am_list_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyAmListFragment:BaseFragment() {
    override fun contentView(): Int {

        return R.layout.am_list_fragment
    }

    override fun init() {

        val progess = ProgrssLoader(context!!)

        progess.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            val res=RetrofitFactory.getApiService().getAMs(ArthanApp.getAppInstance().loginUser)
            if(res!=null)
            {
                withContext(Dispatchers.Main){
                    progess.dismmissLoading()
                    rv_ams.adapter=MyAmListAdapter(context!!,res)


                }
            }else
            {
                withContext(Dispatchers.Main){
                    progess.dismmissLoading()
                }
            }
        }

      /*  var list=ArrayList<AmListModel>()
        list.add(AmListModel("Abc","19-07-2020","Completed"))
        list.add(AmListModel("Abc","19-07-2020","Completed"))
        list.add(AmListModel("Abc","19-07-2020","Completed"))
        list.add(AmListModel("Abc","19-07-2020","Completed"))
        list.add(AmListModel("Abc","19-07-2020","Completed"))
        list.add(AmListModel("Abc","19-07-2020","Completed"))
        list.add(AmListModel("Abc","19-07-2020","Completed"))*/

    }
}