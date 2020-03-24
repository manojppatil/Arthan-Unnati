package com.example.arthan.utils

import com.example.arthan.global.ArthanApp
import java.io.File

class FileUtil {

    companion object{

        fun createImgFile(): File {
            val dir = File(ArthanApp.getAppInstance().filesDir?.absolutePath, "images")
            if (!dir.exists())
                dir.mkdirs()
            return File(
                dir.absolutePath + "/IMG_" + DateFormatUtil.currentTime( "ddMMyyyy_HHmmssSS") + ".jpg"
            )
        }

         fun createImgFile(name: String): File {
            val dir = File(ArthanApp.getAppInstance().filesDir?.absolutePath, "images")
            if (!dir.exists())
                dir.mkdirs()
            return File(dir.absolutePath + "/" + name + ".jpg")
        }

    }



}