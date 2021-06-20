package com.example.arthan.login

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.arthan.R
import com.example.arthan.global.Crashlytics
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.SplashActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.installations.FirebaseInstallations
import kotlinx.android.synthetic.main.activity_login_emp_id.*
import kotlinx.android.synthetic.main.activity_login_emp_id.et_role
import kotlinx.android.synthetic.main.activity_login_o_t_p.*
import kotlinx.android.synthetic.main.activity_login_o_t_p.btn_submit
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginOTPActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_o_t_p)

        if(intent.getStringExtra("role")=="Emp")
        {
            getOtp()

        }else {
          //  getOtpForNonEmp()

        }

        btn_submit.setOnClickListener {
            if (view_otp.otp.length == 6) {
                val progress=ProgrssLoader(this)
                progress.showLoading()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val map=HashMap<String,String>()
                        map["userId"]=intent.getStringExtra("empId")!!
                        map["otp"]=view_otp.otp.toString()
                        map["role"]=intent.getStringExtra("role")!!
                        val response=RetrofitFactory.getApiService().verifyOTPforEmp(map)
                        if(response.body()!=null)
                        {
                            if(response.body()!!.verifyStatus.equals("Success",ignoreCase = true))
                            {
                                withContext(Dispatchers.Main) {

                                    progress.dismmissLoading()
                                    sendTokenToServer()
                                    startActivity(Intent(this@LoginOTPActivity, SplashActivity::class.java).apply {
                                        putExtra("empId", intent.getStringExtra("empId"))
                                    })
                                    finish()
                                }
                            }else
                            {


                                withContext(Dispatchers.Main) {
                                    /* startActivity(Intent(this@LoginOTPActivity, SplashActivity::class.java).apply {
                                         putExtra("empId", intent.getStringExtra("empId"))
                                     })
                                     finish()*/
                                    progress.dismmissLoading()
                                    Toast.makeText(this@LoginOTPActivity,"Invalid OTP. Please try again",Toast.LENGTH_LONG).show()
                                }

                            }
                        }
                    }
                    catch (e:Exception)
                    {
                        withContext(Dispatchers.Main) {
                            progress.dismmissLoading()
                        }
                        Crashlytics.log(e.printStackTrace().toString())
                    }

                }

            }
        }
    }

    private fun sendTokenToServer() {

        let {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {

                    FirebaseInstallations.getInstance().getToken(true)
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
                                map["userId"] = intent.getStringExtra("empId")!!
                                map["token"] = token!!
                                val res = RetrofitFactory.getApiService()
                                    .sendToken(map = map)
                                Log.w("TAG", "getInstanceId :$token")


                            }

                        })
                }
            }
        }
    }

    private fun initiateOtpTimer()
    {
        object : CountDownTimer(45000, 1000) {
            // adjust the milli seconds here
            override fun onTick(millisUntilFinished: Long) {
                txt_otp_resend_msg.text = "" + String.format(
                    "OTP will be resent in %d seconds ",
                    millisUntilFinished/1000

                )
            }

            override fun onFinish() {
                Toast.makeText(this@LoginOTPActivity,"OTP resent ",Toast.LENGTH_LONG).show()

            //    getOtp()
                txt_otp_resend_msg.visibility=View.GONE
                cancel()


            }
        }.start()

    }
    private fun getOtpForNonEmp() {

        CoroutineScope(Dispatchers.IO).launch {
            val map=HashMap<String,String>()
            map["mobNo"]=intent.getStringExtra("mobNo")!!
            map["role"]="NonEmp"
            val response =
                RetrofitFactory.getAMService().sendOTP(map)
            if (response.body() != null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginOTPActivity,"OTP sent to registered mobile number",
                        Toast.LENGTH_LONG).show()

                }
            }
        }
    }
    private fun getOtp() {

        CoroutineScope(Dispatchers.IO).launch {
            val response =
                RetrofitFactory.getApiService().getOTPforEmp(intent.getStringExtra("empId"))
            if (response.body() != null) {
                withContext(Dispatchers.Main) {

                  //  initiateOtpTimer()
                    txt_otp_msg.text = "OTP sent to ${response.body()!!.mobNo}"
                    Toast.makeText(this@LoginOTPActivity,"OTP sent to registered mobile number",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}
