package com.example.arthan.lead

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.arthan.R
import com.example.arthan.dashboard.rm.RMDashboardActivity
import com.example.arthan.dashboard.rm.RMScreeningNavigationActivity
import com.example.arthan.global.*
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.model.Docs
import com.example.arthan.model.PresanctionDocsRequestData
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.ocr.CardResponse
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.ProgrssLoader
import com.example.arthan.utils.RequestCode
import com.example.arthan.views.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_documents.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.Manifest

import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import java.io.ByteArrayOutputStream
import java.io.File

private const val MY_CAMERA_PERMISSION_CODE = 100
private const val CAMERA_REQUEST = 1888
private const val AADHAR_FRONT = 101
private const val AADHAR_BACK = 102
private const val PASSPORT = 103
private const val PANCARD = 104
private const val VOTERID_FRONT = 105
private const val VOTERID_BACK = 106

class DocumentFragment2: AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var uploadDocSpinner: Spinner? = null
    var uploadDocListView: ListView? = null
    var uploadedList: ArrayList<String>? = null
    var listAdapter: ArrayAdapter<String>? = null


    val docTypes = arrayOf<String>("Select Document Type to Upload","AadharCard Front","AadharCard Back","Passport","PanCard","VoterId Front","VoterId Back")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_documents2)
        uploadDocSpinner = findViewById<Spinner>(R.id.document_select_spinner)
        uploadDocListView = findViewById<ListView>(R.id.list_uploaddocs)
        uploadedList = ArrayList<String>()

        uploadDocSpinner!!.onItemSelectedListener = this

        val dataAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            docTypes
        )
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        uploadDocSpinner!!.adapter = dataAdapter


        listAdapter = MyListAdapter(this,uploadedList)
        uploadDocListView!!.adapter = listAdapter

        uploadDocListView!!.setOnItemClickListener(){adapterView, view, position, id ->
            val itemAtPos = adapterView.getItemAtPosition(position)
            val itemIdAtPos = adapterView.getItemIdAtPosition(position)
//            Toast.makeText(this, "Click on item at $itemAtPos its item id $itemIdAtPos", Toast.LENGTH_LONG).show()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        val spinnerType = parent?.getItemAtPosition(position).toString()

        if(uploadedList?.contains(spinnerType)!!) {
            Toast.makeText(this, "Your selected document image is already present please remove and select again", Toast.LENGTH_LONG).show()
        } else {
            var selectedDocType = 0
            when (spinnerType) {
                "AadharCard Front" ->  selectedDocType = AADHAR_FRONT
                "AadharCard Back" -> selectedDocType = AADHAR_BACK
                "Passport" -> selectedDocType = PASSPORT
                "PanCard" -> selectedDocType = PANCARD
                "VoterId Front" -> selectedDocType = VOTERID_FRONT
                "VoterId Back" -> selectedDocType = VOTERID_BACK
                else -> { selectedDocType = 0
                    return }
            }

            if (requestPermission()) {
                launchCamera(selectedDocType)
            }
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun getImageUri(inContext: Activity, inImage: Bitmap?): Uri {
        val bytes = ByteArrayOutputStream()
        inImage!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            val photo = data!!.extras!!["data"] as Bitmap?
            val newUri = getImageUri(this, photo)
            var docName = ""
            when (requestCode) {
                AADHAR_FRONT -> docName = "AadharCard Front"
                AADHAR_BACK -> docName = "AadharCard Back"
                PASSPORT -> docName = "Passport"
                PANCARD -> docName = "PanCard"
                VOTERID_FRONT -> docName = "VoterId Front"
                VOTERID_BACK -> docName = "VoterId Back"
                else -> {
                    return
                }

            }
            if(!docName.isEmpty()) {
                uploadS3(newUri, docName)
            }
        }
    }
    private fun uploadS3(image: Uri, docName: String) {
        try {
            val filePathColumn =
                arrayOf(MediaStore.Images.Media.DATA)
            var picturePath = ""
            if (image != null) {
                val cursor = contentResolver.query(
                    image,
                    filePathColumn, null, null, null
                )
                cursor!!.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                picturePath = cursor.getString(columnIndex)
                cursor.close()
            }
            val filter = IntentFilter()
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            registerReceiver(
                TransferNetworkLossHandler.getInstance(this),
                filter
            )
            val credentialsProvider: CognitoCachingCredentialsProvider =
                CognitoCachingCredentialsProvider(
                    applicationContext,
                    "us-east-2:6182f6ea-79cd-4f6c-a747-beb8186cf602",  // Identity pool ID
                    Regions.US_EAST_2
                )
            val s3 = AmazonS3Client(
                credentialsProvider,
                Region.getRegion(Regions.AP_SOUTH_1)
            )
            val transferUtility = TransferUtility.builder()
                .context(applicationContext)
                .awsConfiguration(AWSMobileClient.getInstance().configuration)
                .s3Client(s3)
                .build()
            val imageName = "1233" + "/" + docName + ".png"
            val file = File(picturePath)
            val uploadObserver = transferUtility.upload(
                "test-doc-repo",  //this is the bucket name on S3
                imageName,  //this is the path and name
                File(picturePath),  //path to the file locally
                CannedAccessControlList.PublicRead //to make the file public
            )
            val docUrl = s3.getUrl("test-doc-repo", imageName).toString()

            //move this code to transferstate cmpleted once the upload get success
            uploadedList?.add(docName)
            listAdapter?.notifyDataSetChanged();

            uploadObserver.setTransferListener(object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState) {
                    if (TransferState.COMPLETED == state) {
                        Toast.makeText(getApplicationContext(), "uploaded successfully" + state,
                            Toast.LENGTH_SHORT).show();

                        // Handle a completed download.
                    }
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                    val percentDonef =
                        bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                    val percentDone = percentDonef.toInt()
                    Toast.makeText(getApplicationContext(), "Progress in %" + percentDonef,
                        Toast.LENGTH_SHORT).show();
                }

                override fun onError(id: Int, ex: Exception) {
                    // Handle errors
                    Log.e("error", "error" + ex.message)
                }
            })
            if (TransferState.COMPLETED == uploadObserver.state) {
                // Handle a completed upload.
            }
        } catch (exc: Exception) {
            Log.e("EXCEPTION", "text")
        }
    }

    private fun launchCamera(cameraRequest: Int) {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, cameraRequest)
    }

    private fun requestPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), MY_CAMERA_PERMISSION_CODE
                )
            } else {
                return true
            }
        }
        return false
    }


}


class MyListAdapter(private val context: Activity, private val docList: ArrayList<String>?)
    : ArrayAdapter<String>(context, R.layout.activity_doc_detailsview, docList!!) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.activity_doc_detailsview, null, true)

        val titleText = rowView.findViewById(R.id.title) as TextView
        val closeButton = rowView.findViewById(R.id.btn_remove) as Button

        titleText.text = docList!![position]

        closeButton!!.setOnClickListener {
            docList.removeAt(position)
            notifyDataSetChanged()

        }

        return rowView
    }
}