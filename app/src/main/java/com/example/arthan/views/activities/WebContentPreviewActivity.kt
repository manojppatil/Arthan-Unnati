package com.example.arthan.views.activities

import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.arthan.R
import kotlinx.android.synthetic.main.activity_web_content_preview.*

class WebContentPreviewActivity : BaseActivity() {

    override fun contentView() = R.layout.activity_web_content_preview

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {

        oka_button?.setOnClickListener {
            finish()
        }
        wv_preview?.settings?.javaScriptEnabled = true
        wv_preview?.settings?.pluginState = WebSettings.PluginState.ON
        wv_preview?.webViewClient = MyWebViewwClient()
        //wv_preview?.loadUrl("http://13.233.27.170:8080/JerseyDemos/Home?name=MSD&FileNo=MSD12345&contactNo=9876543211&acType=Saving&reqId=rk1234")
    }

    class MyWebViewwClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            view?.loadUrl(url!!)
            return true
        }
    }

    override fun screenTitle() = "Arthan"
}