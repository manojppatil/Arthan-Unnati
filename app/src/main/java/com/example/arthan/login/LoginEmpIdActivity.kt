package com.example.arthan.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import com.example.arthan.R
import com.example.arthan.global.ArthanApp
import com.example.arthan.views.activities.SplashActivity
import kotlinx.android.synthetic.main.activity_login_emp_id.*
import kotlinx.android.synthetic.main.activity_submit_final_report.*

class LoginEmpIdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_emp_id)
        var btn_Submit=findViewById<Button>(R.id.btn_submit)
        if (getSharedPreferences("user", Context.MODE_PRIVATE).getString("empId", "") != "") {

            ArthanApp.getAppInstance().loginUser =
                getSharedPreferences("user", Context.MODE_PRIVATE).getString("empId", "")!!
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        }
        btn_Submit.setOnClickListener {
            if (getSharedPreferences("user", Context.MODE_PRIVATE).getString("empId", "") == "") {
                ArthanApp.getAppInstance().loginUser = et_role.text.toString()
                startActivity(Intent(this, LoginOTPActivity::class.java).apply {
                    putExtra("empId", et_role.text.toString())
                })
                finish()
            }
        }
        et_role.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(et_role.length()>1)
                {
                    btn_Submit.isEnabled=true
                    btn_Submit.setBackgroundResource(R.drawable.ic_next_enabled)
                    btn_Submit.setTextColor(resources.getColor(R.color.white))

                }else
                {
                    btn_Submit.isEnabled=false
                    btn_Submit.setBackgroundResource(R.drawable.ic_next_disable)
                    btn_Submit.setTextColor(resources.getColor(R.color.black))


                }
            }
        })
    }
}