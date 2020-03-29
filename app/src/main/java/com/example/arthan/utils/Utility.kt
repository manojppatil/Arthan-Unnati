package com.example.arthan.utils

import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.OpenableColumns
import android.util.TypedValue
import android.widget.EditText
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import java.io.*
import java.util.*

object Constant {
    const val BUFFER_SIZE = 1024 * 2
    const val COGNITO_POOL_ID = "us-east-2:6182f6ea-79cd-4f6c-a747-beb8186cf602"
    const val COGNITO_POOL_REGION = "us-east-2"
    const val S3_BASE_URL = "https://s3-us-east-2.amazonaws.com/"
    //        const val BUCKET_NAME = "test-doc-repo"
    //        const val BUCKET_REGION = "ap-south-1"
    const val BUCKET_NAME = "test-doc-repo"
    const val BUCKET_REGION = "us-east-2"
}

fun getDPFromPixel(pixel: Float, context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    pixel,
    context.resources.displayMetrics
)

fun getPixelFromDP(dp: Float, context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_PX,
    dp,
    context.resources.displayMetrics
)

fun getSymbol(context: Context?, symbol: String, textSize: Float, color: Int): Drawable {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.textSize = textSize
    paint.color = color
    paint.textAlign = Paint.Align.LEFT
    val baseline = -paint.ascent() // ascent() is negative
    val image = Bitmap.createBitmap(
        (paint.measureText(symbol) + 0.5f).toInt(),
        (baseline + paint.descent() + 0.5f).toInt(),
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(image)
    canvas.drawText(symbol, 0f, baseline, paint)
    return BitmapDrawable(context?.resources, image)
}

fun getRupeeSymbol(context: Context?, textSize: Float, color: Int): Drawable =
    getSymbol(context, "₹", textSize, color)

fun dateSelection(context: Context, editText: EditText?) {
    val c = Calendar.getInstance()
    DatePickerDialog(
        context,
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val date = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
            editText?.setText(date)
        },
        c.get(Calendar.YEAR),
        c.get(Calendar.MONTH),
        c.get(Calendar.DAY_OF_MONTH)
    ).show()
}

fun loadImage(
    context: Context,
    imageView: ImageView,
    uri: Uri,
    onSuccess: ((filePath: String) -> Unit)? = null,
    onError: ((error: String?) -> Unit)? = null
) {
    val file = copyFile(context, uri)
    var path = ""
    if (file != null) {
        path = file.absolutePath
    }
    Glide.with(context)
        .load(path)
        .addListener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                onError?.invoke("Something went wrong...")
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                onSuccess?.invoke(path)
                return false
            }
        })
        .into(imageView)
}

private fun getFileName(contentResolver: ContentResolver?, uri: Uri?): String? {
    if (uri == null) return null
    var fileName: String? = null
    uri?.let { returnUri ->
        contentResolver?.query(returnUri, null, null, null, null)
    }?.use { cursor ->
        cursor.moveToFirst()
        fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
    }
    return fileName
}

fun copyFile(context: Context, uri: Uri): File? {
    val tempDirectory = File("${context.filesDir}/temp")
    if (!tempDirectory.exists()) {
        tempDirectory.mkdirs()
    }
    val fileName = getFileName(context?.contentResolver, uri) ?: return null
    val copiedFile: File = File(tempDirectory, fileName.replace(":", "")) ?: return null
    copiedFile.createNewFile()
    try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val outPutStream: OutputStream = FileOutputStream(copiedFile)
        val buffer = ByteArray(Constant.BUFFER_SIZE)
        val input = BufferedInputStream(inputStream, Constant.BUFFER_SIZE);
        val out = BufferedOutputStream(outPutStream, Constant.BUFFER_SIZE);
        var count = 0
        var n = input.read(buffer, 0, Constant.BUFFER_SIZE)
        while (n != -1) {
            out.write(buffer, 0, n)
            count += n
            n = input.read(buffer, 0, Constant.BUFFER_SIZE)
        }
        out.close()
        input.close()
        inputStream.close()
        outPutStream.close()
        return copiedFile
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return null
}

object ArgumentKey {
    const val FilePath="FilePath"
    const val FROM = "FROM"
    const val BranchLaunchType = "branch_launch_type"
    const val LeadId = "lead_id"
    const val LoanId = "loan_id"
    const val CustomerId = "customer_id"
    const val InPrincipleAmount = "in_principle_amount"
    const val PanDetails = "pan_details"
    const val AadharDetails = "aadhar_details"
    const val VoterDetails = "voter_details"
    const val ApplicantPhoto= "applicant_photo"
    const val PDData = "pd_data"
    const val Eligibility="Eligibility"
}

object ConstantValue {
    const val BCM = "bcm";

    object CardStatus {
        const val Valid = "VALID"
        const val Ok = "OK"
    }
}

object RequestCode {
    const val GSTDetailsActivity = 0x0000
    const val BillsDetailsActivity = 0x0001
    const val ObligationsDetailsActivity = 0x0002
    const val BankingDetailsActivity = 0x0003
    const val PanCard = 0x0004
    const val VoterCard = 0x0005
    const val AadharCard = 0x0006
    const val AadharFrontCard = 0x0007
    const val AadharBackCard = 0x0008
    const val ApplicantPhoto = 0x0009
    const val PropertyDocument = 0x0010
    const val Passport = 0x000A
}
