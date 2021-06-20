package com.example.arthan.global

//import `in`.finbox.mobileriskmanager.FinBox
import android.util.Log
import android.widget.Toast
import androidx.multidex.MultiDexApplication
import com.example.arthan.model.SubmitMultipleDocsRequest
import java.util.*
import java.util.logging.Logger

class ArthanApp : MultiDexApplication() {

    var loginUser:String=""
    var loginRole:String=""
    var empId:String=""
    var userName:String=""
    var appVersion="1.9"
    var onboarded=""
    var validAm = ""
    var submitDocs: SubmitMultipleDocsRequest? =null
    val clientKeyFinBox="nSjEi5etnf6xi4RyPA1Ph3TEWu3B07GT7BaQaF5v"
    var currentCustomerId:String?=null

    override fun onCreate() {
        super.onCreate()
        instance = this
        try {

            AppPreferences.init(this)
           // createCustomerFinBox()
        } catch (e: Exception) {
            e.printStackTrace()
            Crashlytics.log(e.message)

        }
        //FinBox.initLibrary(this)
    }

   /* private fun createCustomerFinBox() {
        FinBox.initLibrary(this)
        val customerId= UUID.randomUUID().toString().replace("-".toRegex(), "")
        Log.d("customerId ####",customerId)
        FinBox.createUser(clientKeyFinBox,customerId,object:FinBox.FinBoxAuthCallback{

            override fun onError(p0: Int) {

                Toast.makeText(applicationContext,"Error in creating new user",Toast.LENGTH_LONG).show()
            }

            override fun onSuccess(p0: String) {
                val finbox = FinBox()
                finbox.startPeriodicSync()
                Toast.makeText(applicationContext,"creating new user",Toast.LENGTH_LONG).show()

            }
        })
    }*/

    companion object {

        private var instance: ArthanApp? = null

        fun getAppInstance(): ArthanApp {
            if (instance == null)
                instance = ArthanApp()
            return instance!!
        }

    }

}