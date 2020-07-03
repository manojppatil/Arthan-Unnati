package com.example.arthan.global

import androidx.multidex.MultiDexApplication

class ArthanApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        try {
            AppPreferences.init(this)
        } catch (e: Exception) {
            e.printStackTrace()
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