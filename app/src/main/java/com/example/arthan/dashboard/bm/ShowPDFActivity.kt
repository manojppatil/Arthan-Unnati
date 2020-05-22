package com.example.arthan.dashboard.bm

import android.app.ProgressDialog
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.arthan.R
import kotlinx.android.synthetic.main.activity_show_p_d_f.*
import kotlinx.android.synthetic.main.custom_toolbar.*


class ShowPDFActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_p_d_f)
//        webview.settings.javaScriptEnabled = true;
//        webview.loadUrl("http://www.google.com");
        var progressBar:ProgressDialog=ProgressDialog(this@ShowPDFActivity)
        var webSettings=webview.settings
        webSettings.javaScriptEnabled = true
        webSettings.setSupportMultipleWindows(true) // This forces ChromeClient enabled.


        webview.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(
                view: WebView,
                title: String
            ) {
                window.setTitle(title) //Set Activity tile to page title.
            }
        }

        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                url: String
            ): Boolean {
                view.loadUrl(url)
                return false
            }
        }
        val pdf = intent.getStringExtra("pdf_url")
        webview.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$pdf")

//        webview.webViewClient = HelloWebViewClient()

        back_button.setOnClickListener {
            finish()
        }
    }
}
