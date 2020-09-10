package com.example.arthan.global

import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.example.arthan.model.SubmitMultipleDocsRequest

class ArthanApp : MultiDexApplication() {

    var loginUser:String=""
    var loginRole:String=""
    var empId:String=""
    var appVersion="Version:1.1"
    var onboarded=""
    var validAm = ""
    var submitDocs: SubmitMultipleDocsRequest? =null

    override fun onCreate() {
        super.onCreate()
        instance = this
        try {
            AppPreferences.init(this)
        } catch (e: Exception) {
            e.printStackTrace()
            Crashlytics.log(e.message)

        }
        //FinBox.initLibrary(this)
    }

    companion object {

        private var instance: ArthanApp? = null

        fun getAppInstance(): ArthanApp {
            if (instance == null)
                instance = ArthanApp()
            return instance!!
        }

    }

}