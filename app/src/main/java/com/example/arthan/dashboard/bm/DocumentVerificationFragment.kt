package com.example.arthan.dashboard.bm

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.arthan.R
import com.example.arthan.lead.model.responsedata.DocDetails
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_document_verification.*

class DocumentVerificationFragment : BaseFragment() {

    override fun contentView() = R.layout.fragment_document_verification

    override fun init() {
    }

    fun updateData(docDetails: DocDetails?) {
        context?.let {
            Glide.with(it)
                .load(docDetails?.panUrl)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Error in loading image ---> ${e?.message}")
                        return false
                    }


                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Success in loading image ")
                        return false
                    }
                })
                .into(img_pan)
            Glide.with(it)
                .load(docDetails?.aadharFrontUrl)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Error in loading image ---> ${e?.message}")
                        return false
                    }


                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Success in loading image ")
                        return false
                    }
                })
                .into(img_aadhaar)
            Glide.with(it)
                .load(docDetails?.aadharBackUrl)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Error in loading image ---> ${e?.message}")
                        return false
                    }


                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Success in loading image ")
                        return false
                    }
                })
                .into(img_aadhaar_back)
            Glide.with(it)
                .load(docDetails?.voterUrl)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Error in loading image ---> ${e?.message}")
                        return false
                    }


                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Success in loading image ")
                        return false
                    }
                })
                .into(img_voter)
            Glide.with(it)
                .load(docDetails?.paApplicantPhoto)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Error in loading image ---> ${e?.message}")
                        return false
                    }


                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Success in loading image ")
                        return false
                    }
                })
                .into(img_applicant)
        }
    }

    companion object {
        const val TAG = "DocumentVerifFragment"
    }
}