package com.example.arthan.views.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.arthan.R
import kotlinx.android.synthetic.main.activity_rm_dashboard.*


abstract class BaseActivity : AppCompatActivity() {

    abstract fun contentView(): Int

    abstract fun init()

    abstract fun onToolbarBackPressed()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(contentView())
        setUpToolbar()
        init()
    }

    private fun setUpToolbar() {

        if (toolbar != null) {
            setSupportActionBar(toolbar as Toolbar)
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        if (findViewById<TextView>(R.id.txt_title) != null)
            findViewById<TextView>(R.id.txt_title).text = screenTitle()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId === android.R.id.home) {
            onToolbarBackPressed()
            true
        } else
            true
        //else mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }


    abstract fun screenTitle(): String

}