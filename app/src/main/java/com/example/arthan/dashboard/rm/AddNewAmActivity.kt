package com.example.arthan.dashboard.rm

import android.widget.TextView
import android.widget.Toast
import com.example.arthan.R
import com.example.arthan.global.ArthanApp
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_add_new_am.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddNewAmActivity : BaseActivity() {


    override fun screenTitle() = "Add New AM"

    override fun contentView() = R.layout.activity_add_new_am
    override fun init() {

        experience_count.tag=0
        experience_count.text="0"

        minus_button_exp.setOnClickListener {
            updateCount(UpdateCountType.Decrement,experience_count)
        }
        plus_button_exp.setOnClickListener {
            updateCount(UpdateCountType.Increment,experience_count)
        }
        submit_button.setOnClickListener {
            if(am_name.text.toString().isNotEmpty()){
                var progress = ProgrssLoader(this)
                progress.showLoading()
                val map=HashMap<String,String>()
                map["name"]=am_name.text.toString()
                map["mobNo"]=am_mobile.text.toString()
                map["experience"]=experience_count.text.toString()
                map["pincode"]=am_pincode.text.toString()
                map["createdBy"]=ArthanApp.getAppInstance().loginUser
                CoroutineScope(Dispatchers.IO).launch {
                    val res=RetrofitFactory.getApiService().addAM(map)
                    if(res.body()!=null)
                    {
                        withContext(Dispatchers.Main)
                        {
                            progress.dismmissLoading()
                            Toast.makeText(this@AddNewAmActivity,"New AM Added.",Toast.LENGTH_LONG).show()
                            finish()

                        }
                    }else
                    {

                        withContext(Dispatchers.Main)
                        {
                            progress.dismmissLoading()
                            Toast.makeText(this@AddNewAmActivity,"Please try again later",Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    override fun onToolbarBackPressed() {
        finish()
    }
    private fun updateCount(
        updateType: UpdateCountType,
        countText: TextView?
    ) = when (updateType) {
        is UpdateCountType.Increment -> performIncrement(countText?.tag as? Int ?: 0)
        is UpdateCountType.Decrement -> performDecrement(countText?.tag as? Int ?: 0)
    }.also {
        countText?.text = "$it"
        countText?.tag = it
    }

    private fun performIncrement(initialCount: Int) = initialCount + 1

    sealed class UpdateCountType {
        object Increment : UpdateCountType()
        object Decrement : UpdateCountType()
    }

    private fun performDecrement(initialCount: Int): Int =
        if (initialCount - 1 < 0) 0 else initialCount - 1
}