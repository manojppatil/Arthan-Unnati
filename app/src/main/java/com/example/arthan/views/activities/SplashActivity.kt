package com.example.arthan.views.activities

import `in`.finbox.mobileriskmanager.FinBox
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.example.arthan.CBO.CBODashboardActivity
import com.example.arthan.R
import com.example.arthan.dashboard.am.AMOnboardingAtivity
import com.example.arthan.dashboard.bcm.BCMDashboardActivity
import com.example.arthan.dashboard.bm.BMDashboardActivity
import com.example.arthan.dashboard.legal.LegalDashboardActivity
import com.example.arthan.dashboard.ops.OpsDashboardActivity
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.global.AppPreferences
import com.example.arthan.global.ArthanApp
import com.example.arthan.lead.OTPValidationActivity
import com.example.arthan.login.LoginEmpIdActivity
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        version.text = ArthanApp.getAppInstance().appVersion


        /*GlobalScope.launch(context = Dispatchers.Main) {
            delay(500)
            startActivity(
                Intent(this@SplashActivity,
                RMDashboardActivity::class.java)
            )
            finish()
        }*/

        btn_submit.setOnClickListener {


            val progressBar = ProgrssLoader(this)
            progressBar.showLoading()
            var map: HashMap<String, String> = HashMap()
            map["userId"] = ArthanApp.getAppInstance().loginUser
//            map["userId"] =et_role.text.toString()
            map["mPin"] = et_role.otp.toString()
//            ArthanApp.getAppInstance().loginUser=et_role.otp.toString()
            CoroutineScope(Dispatchers.IO).launch {


                val mapPin = HashMap<String, String>()

                mapPin["userId"] = ArthanApp.getAppInstance().loginUser
                mapPin["mpin"] = et_role.otp.toString()
                val res = RetrofitFactory.getApiService().storeMpin(map)
                if (res.body() != null) {
                    var response = RetrofitFactory.getApiService().getUserRole(map)
                    if (response.body() != null) {



                        var prefs = getSharedPreferences("user", Context.MODE_PRIVATE)
                        var editor = prefs.edit()
                        if (prefs.getString("empId", "") == "") {
                            editor.putString("empId", ArthanApp.getAppInstance().loginUser).apply()
                        }

                        progressBar.dismmissLoading()
//                    ArthanApp.getAppInstance().loginUser =  intent.getStringExtra("empId")
                        ArthanApp.getAppInstance().loginRole = response.body()!!.role
                        ArthanApp.getAppInstance().onboarded = res.body()!!.onboarded
                        var user = response.body()!!.role

                        withContext(Dispatchers.Main) {
                            if (!et_role.otp.isNullOrBlank()) {

                                when (user) {
                                    "BM" -> {
                                        AppPreferences.getInstance()
                                            .remove(AppPreferences.Key.LoginType)
                                        AppPreferences.getInstance()
                                            .addString(AppPreferences.Key.LoginType, "BM")
                                        startActivity(
                                            Intent(
                                                this@SplashActivity,
                                                BMDashboardActivity::class.java
                                            )
                                        )
                                        finish()
                                    }
                                    "RM" -> {
                                        AppPreferences.getInstance()
                                            .remove(AppPreferences.Key.LoginType)
                                        AppPreferences.getInstance()
                                            .addString(AppPreferences.Key.LoginType, "RM1")
                                        startActivity(
                                            Intent(
                                                this@SplashActivity,
                                                RMDashboardActivity::class.java
                                            )
                                        )
                                        finish()
                                    } "AM" -> {


                                    if (ArthanApp.getAppInstance().onboarded.toLowerCase() == "yes") {

                                        AppPreferences.getInstance()
                                            .remove(AppPreferences.Key.LoginType)
                                        AppPreferences.getInstance()
                                            .addString(AppPreferences.Key.LoginType, "RM1")
                                        startActivity(
                                            Intent(
                                                this@SplashActivity,
                                                RMDashboardActivity::class.java
                                            )
                                        )
                                        finish()
                                    } else {
                                        AppPreferences.getInstance()
                                            .remove(AppPreferences.Key.LoginType)
                                        AppPreferences.getInstance()
                                            .addString(AppPreferences.Key.LoginType, "AM")
                                        startActivity(
                                            Intent(
                                                this@SplashActivity,
                                                AMOnboardingAtivity::class.java
                                            )
                                        )
                                        finish()
                                    }
                                }
                                    "legal" -> {
                                        AppPreferences.getInstance()
                                            .remove(AppPreferences.Key.LoginType)
                                        AppPreferences.getInstance()
                                            .addString(AppPreferences.Key.LoginType, "legal")
                                        startActivity(
                                            Intent(
                                                this@SplashActivity,
                                                LegalDashboardActivity::class.java
                                            ).apply {
                                                putExtra("FROM", "LEGAL")
                                            })
                                        finish()
                                    }
                                    "RCU" -> {
                                        AppPreferences.getInstance()
                                            .remove(AppPreferences.Key.LoginType)
                                        AppPreferences.getInstance()
                                            .addString(AppPreferences.Key.LoginType, "RCU")
                                        startActivity(
                                            Intent(
                                                this@SplashActivity,
                                                LegalDashboardActivity::class.java
                                            ).apply {
                                                putExtra("FROM", "RCU")
                                            }
                                        )
                                        finish()
                                    }
                                    "OPS" -> {
                                        AppPreferences.getInstance()
                                            .remove(AppPreferences.Key.LoginType)
                                        AppPreferences.getInstance()
                                            .addString(AppPreferences.Key.LoginType, "OPS")
                                        OpsDashboardActivity.startMe(this@SplashActivity)
                                    }
                                    "BCM" -> {
                                        AppPreferences.getInstance()
                                            .remove(AppPreferences.Key.LoginType)
                                        AppPreferences.getInstance()
                                            .addString(AppPreferences.Key.LoginType, "BCM")
                                        startActivity(
                                            Intent(
                                                this@SplashActivity,
                                                BCMDashboardActivity::class.java
                                            )
                                        )
                                        finish()
                                    }  "CBO" -> {
                                        AppPreferences.getInstance()
                                            .remove(AppPreferences.Key.LoginType)
                                        AppPreferences.getInstance()
                                            .addString(AppPreferences.Key.LoginType, "BCM")
                                        startActivity(
                                            Intent(
                                                this@SplashActivity,
                                                CBODashboardActivity::class.java
                                            )
                                        )
                                        finish()
                                    }
                                    else -> {
                                        Toast.makeText(
                                            this@SplashActivity,
                                            "Invalid login user",
                                            Toast.LENGTH_LONG
                                        )
                                            .show()
                                        /*  startActivity(
                                          Intent(
                                              this@SplashActivity,
                                              BCMDashboardActivity::class.java
                                          )
                                      )
                                      finish()*/
                                    }

                                }/*let {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        withContext(Dispatchers.Main) {

                                            FirebaseInstanceId.getInstance().instanceId
                                                .addOnCompleteListener(OnCompleteListener { task ->
                                                    if (!task.isSuccessful) {
                                                        Log.w(
                                                            "TAG",
                                                            "getInstanceId failed",
                                                            task.exception
                                                        )
                                                        return@OnCompleteListener
                                                    }

                                                    // Get new Instance ID token
                                                    val token = task.result?.token

                                                    CoroutineScope(Dispatchers.IO).launch {

                                                        var map = HashMap<String, String>()
                                                        map["userId"] = et_role?.otp?.toString()!!
                                                        map["token"] = token!!
                                                        val res = RetrofitFactory.getApiService()
                                                            .sendToken(map = map)
                                                        Log.w("TAG", "getInstanceId :$token")


                                                    }

                                                })
                                        }
                                    }
                                }*/
                            }
                        }

                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@SplashActivity,
                                "Something went wrong",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                }
            }


            /*var user:String=""
            if(et_role.text.toString()=="1234")
            {
                user="RM1"
                ArthanApp.getAppInstance().loginRole=user
            }else if(et_role.text.toString()=="3456")
            {
                user="BM"
                ArthanApp.getAppInstance().loginUser=et_role.text.toString()
                ArthanApp.getAppInstance().loginRole=user
            }else if(et_role.text.toString()=="5678")
            {
                user="BCM"
                ArthanApp.getAppInstance().loginUser=et_role.text.toString()
                ArthanApp.getAppInstance().loginRole=user
            }
*/
            /*startActivity(
                Intent(
                    this@SplashActivity,
                    OTPValidationActivity::class.java
                ).apply {
                    putExtra("FROM", "LEGAL")
                })
            finish()*/

            /*FinBox.createUser("nSjEi5etnf6xi4RyPA1Ph3TEWu3B07GT7BaQaF5v", "finbox_test_user",
                object : FinBox.FinBoxAuthCallback{
                    override fun onSuccess(accessToken: String) {
                        Log.e("USER CREATED",":: $accessToken")
                        val finbox = FinBox()
                        finbox.startPeriodicSync()
                    }

                    override fun onError(error: Int) {
                        Log.e("USER CREATED ERROR",":: $error")
                    }
                })*/

            //val result= fizzBuzz(et_num.text.toString().toInt())
        }
        switchUser.setOnClickListener {
            getSharedPreferences("user", Context.MODE_PRIVATE).edit().putString("empId", "").apply()
            startActivity(Intent(this, LoginEmpIdActivity::class.java))
            finish()
        }

//        et_role?.setText("rm")
//        btn_submit?.performClick()

        /*val arr= mutableListOf<Int>()

        arr.add(6)
        arr.add(5)
        arr.add(1)
        arr.add(3)
        arr.add(4)
        arr.add(6)
        arr.add(2)

        calFinalPrize(arr)*/

        //perfectSubstring("1020122",2)

    }

    /*fun perfectSubstring(s: String, k: Int): Int {
        // Write your code here

        var countR=0

        for(i in 1..s.length){
            for(j in i..s.length){
                val str= s.substring(0,i)
                val distict= str.toCharArray().distinct()

                var correct: Boolean= true
                distict.forEach { c->
                    val count= str.toList().count {
                        it == c
                    }
                    correct = count == 2
                }

                if(correct){
                    Log.e("SUB STRING","::: $str")
                    countR++
                }


            }

        }

        return countR
    }*/


    /*fun calFinalPrize(arr: MutableList<Int>){

        var finalPrice= 0
        var fullPaid= ""

        arr.forEachIndexed { index, i ->

            Log.e("ARRA","$index : $i ::: ${arr.size}")

            val tempArr= mutableListOf<Int>()

            if(index < arr.size) {
                tempArr.addAll(arr.toMutableList().subList(index + 1, arr.size ))

                val minValue = tempArr.min()
                if(minValue != null){
                    if(minValue <= i){
                        finalPrice += (i - minValue)
                        Log.e("MIN VALUE IF" ,"$i :::  $tempArr :::::::  ${tempArr.min()} ::: " +
                                "${(i - minValue)} :: $finalPrice")
                    } else {
                        finalPrice += i
                        fullPaid = if(fullPaid.isNullOrBlank())
                            "$index"
                        else
                            "$fullPaid $index"
                        Log.e("MIN VALUE ELSE" ,"$i :::  $tempArr :::::::  ${tempArr.min()} :: $finalPrice")
                    }
                } else {
                    finalPrice += i
                    fullPaid = "$fullPaid $index"
                }
            }
        }

        Log.e("FINAL VALUE" ,"$finalPrice ${fullPaid.trim()} ")
    }*/

    /*fun fizzBuzz(n: Int): Unit {


        // Write your code here
        for(i in 1..n) {

            Log.e("RESULT"," $i :::${
            when {
                isMultipleofNumber(i, 3) && isMultipleofNumber(i, 5) -> "FIzzBuzz\n"
                isMultipleofNumber(i, 3) && !isMultipleofNumber(i, 5) -> "Fizz\n"
                !isMultipleofNumber(i, 3) && isMultipleofNumber(i, 5) -> "Buzz\n"
                !isMultipleofNumber(i, 3) && !isMultipleofNumber(i, 5) -> "$i\n"
                else -> ""
            }
            }")
        }
    }

    private fun isMultipleofNumber(enteredNum: Int,multipleOf: Int): Boolean{
        return (enteredNum % multipleOf) == 0
    }*/


}