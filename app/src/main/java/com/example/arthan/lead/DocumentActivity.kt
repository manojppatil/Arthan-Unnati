package com.example.arthan.lead

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.arthan.R
import com.example.arthan.global.AppPreferences
import com.example.arthan.utils.DocumentUploadFragment
import kotlinx.android.synthetic.main.activity_document.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class DocumentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document)
        toolbar_title.setText("Documents")
        // ToDo set a title Documents
//        var documentFragment=DocumentFragment()
        var documentFragment=DocumentUploadFragment()
        var b=Bundle()
        b.putString("loanId",intent.getStringExtra("loanId"))
        b.putString("custId",intent.getStringExtra("custId"))
        AppPreferences.getInstance().addString(AppPreferences.Key.LoanId,intent.getStringExtra("loanId"))
        documentFragment.arguments=b
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, documentFragment)
            .commit()
    }
}
