package com.example.arthan.dashboard.bm

import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.arthan.R
import com.example.arthan.views.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_document_preview_activty.*
import java.net.URL


class DocumentPreviewActivty : BaseActivity() {
    override fun contentView(): Int {
        return R.layout.activity_document_preview_activty
    }

    override fun init() {


        Glide.with(this)
            .load(intent.getStringExtra("url"))
            .skipMemoryCache(true)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e("TAG", "Error in loading image ---> ${e?.message}")

                    val url = URL(intent.getStringExtra("url"))
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    preview.setImageBitmap(bmp)
                    return false
                }


                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e("TAG", "Success in loading image ")
                    return false
                }
            })
            .into(preview!!)


    }

    override fun onToolbarBackPressed() {
        finish()
    }

    override fun screenTitle(): String {
        return "Document Preview"
    }


}