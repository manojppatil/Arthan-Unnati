package com.example.arthan.views.activities

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.example.arthan.R
import com.example.arthan.global.DOC_TYPE
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.BitmapUtils
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraUtils
import kotlinx.android.synthetic.main.activity_custom_camera.*
import java.io.File

class FrontCameraActivity: BaseActivity() {

    override fun contentView() = R.layout.activity_front_camera

    override fun onToolbarBackPressed() = onBackPressed()

    override fun init() {
        camera.layoutParams.height = (resources.displayMetrics.heightPixels * 0.40).toInt()

        camera.clearCameraListeners()
        camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(jpeg: ByteArray) {
                super.onPictureTaken(jpeg)

                CameraUtils.decodeBitmap(
                    jpeg, resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels
                ) { b ->
                    BitmapUtils.saveBitmapToFile(b, getOutputMediaFile())
                        .subscribe({ filePath ->
                            run {
                                val resultIntent = Intent()
                                resultIntent.putExtra(ArgumentKey.FilePath, filePath)
                                resultIntent.putExtra(DOC_TYPE, intent.getIntExtra(DOC_TYPE, 0))
                                setResult(Activity.RESULT_OK, resultIntent)
                                finish()
                            }
                        }, { error -> Log.e("ERROR::", error.message!!) })
                }
            }
        })

        btn_capture.setOnClickListener {
            camera.capturePicture()
        }
    }

    private fun getOutputMediaFile(): File {
        return File(intent.getStringExtra(ArgumentKey.FilePath) ?: "")
    }

    override fun onResume() {
        super.onResume()
        try {
            camera.start()
        } catch (e: Exception) {
            camera.start()
        }
    }

    override fun onPause() {
        super.onPause()
        camera.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        camera.destroy()
    }

    override fun screenTitle() = "Capture"
}