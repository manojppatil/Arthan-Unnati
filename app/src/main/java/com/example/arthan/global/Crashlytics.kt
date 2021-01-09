package com.example.arthan.global

import com.google.firebase.crashlytics.FirebaseCrashlytics

class Crashlytics {

    companion object{
        fun  log(message:String?)
        {
            FirebaseCrashlytics.getInstance().log(message!!)
        }
    }

}
