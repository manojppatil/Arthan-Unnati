package com.example.arthan.login

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.example.arthan.R
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_login_o_t_p.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginOTPActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_o_t_p)
        getOtp()

        btn_submit.setOnClickListener {
            if (view_otp.otp.length == 6) {
                val progress=ProgrssLoader(this)
                progress.showLoading()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val map=HashMap<String,String>()
                        map["userId"]=intent.getStringExtra("empId")!!
                        map["otp"]=view_otp.otp.toString()
                        val response=RetrofitFactory.getApiService().verifyOTPforEmp(map)
                        if(response.body()!=null)
                        {
                            if(response.body()!!.verifyStatus.equals("Success",ignoreCase = true))
                            {
                                withContext(Dispatchers.Main) {

                                    progress.dismmissLoading()
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

    private fun getOtp() {

        CoroutineScope(Dispatchers.IO).launch {
            val response =
                RetrofitFactory.getApiService().getOTPforEmp(intent.getStringExtra("empId"))
            if (response.body() != null) {
                withContext(Dispatchers.Main) {

                  //  initiateOtpTimer()
                    Toast.makeText(this@LoginOTPActivity,"OTP sent to registered mobile number",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}
