package com.example.qlctncc_tn.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.qlctncc_tn.Model.Image
import com.example.qlctncc_tn.Model.Report
import com.example.qlctncc_tn.Model.Task
import com.example.qlctncc_tn.R
import com.example.qlctncc_tn.Retrofit.RetrofitClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@Suppress("DEPRECATION")
class CheckinActivity : AppCompatActivity() {
    lateinit var btnPrevious: ImageButton
    lateinit var btnTakePhoto: Button
    lateinit var btnCheckin: Button
    lateinit var ivPhotoCheckin: ImageView
    lateinit var currentPhotoPath: String
    var listImageUrl: MutableList<String> = mutableListOf()
    var taskPosition: Task? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var longitudeTrip= 0.0
    var latitudeTrip = 0.0
    var longitudeTripCurrent= 0.0
    var latitudeTripCurrent = 0.0
    var distanceMeter = 0.0
    val REQUEST_LOCATION_PERMISSION = 18

    companion object {
        val REQUEST_IMAGE_CAPTURE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkin)
        taskPosition = intent.getSerializableExtra("taskPosition") as Task
        currentPhotoPath = ""
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        onGetCurrentLocation()
        longitudeTrip = BtDetailActivity.businessTrip!!.longitudeTrip
        latitudeTrip = BtDetailActivity.businessTrip!!.latitudeTrip
        setControls()

    }

    private fun setEvents() {
        btnTakePhoto.setOnClickListener {
//            if (distanceMeter.toInt() > 100){
//                val builder = AlertDialog.Builder(this)
//                builder.setMessage("\n" +
//                        "The distance is too far from the businesstrip location!")
//                builder.setNegativeButton(
//                    "Yes"
//                ) { dialog, id -> dialog.dismiss()
//                }
//                val alertDialog = builder.create()
//                alertDialog.show()
//             } else {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    println("Tạo tệp tin không thành công, ERROR")
                    null
                }
                photoFile?.also {
                    val photoURI: Uri =
                        FileProvider.getUriForFile(this, "com.example.qlctncc_tn.fileprovider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
//            }
        }
        btnCheckin.setOnClickListener() {
            if (listImageUrl.isEmpty()){
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Please take a photo!")
                builder.setNegativeButton(
                    "Yes"
                ) { dialog, id -> dialog.dismiss()
                }
                val alertDialog = builder.create()
                alertDialog.show()
            }else {
                val currentTime = Date()
                val formatterTime = SimpleDateFormat("HH:mm:ss") // Định dạng giờ theo ý muốn
                val formattedTime = formatterTime.format(currentTime)
                val distanceString = String.format("%.1f", distanceMeter)
                var detail = "CHECK-IN: " + taskPosition!!.nameTask + "\nDistance: " + distanceString +" m"+
                        "\nLocation: " + latitudeTripCurrent.toString() + " - " + longitudeTripCurrent.toString() +
                        "\nTime: " + formattedTime.toString()
                val formatter = SimpleDateFormat("yyyy-MM-dd")
                val formattedDate = formatter.format(currentTime)
                var reportNew = Report(
                    0,
                    taskPosition!!.taskId,
                    detail,
                    formattedDate,
                    listImageUrl
                )
                postReport(reportNew)
            }

        }
        btnPrevious.setOnClickListener() {
            onBackPressed()
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
            .apply { currentPhotoPath = absolutePath }
    }

    private fun setControls() {
        btnCheckin = findViewById(R.id.btnCheckin)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        btnPrevious = findViewById(R.id.btnPreviousCheckinActivity)
        ivPhotoCheckin = findViewById(R.id.ivPhotoCheckin)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val filee = File(currentPhotoPath)
            val storageRef = Firebase.storage.reference
            val imagesRef = storageRef.child("images/${filee.name}")
            val uploadTask = imagesRef.putFile(Uri.fromFile(filee))
            uploadTask.addOnSuccessListener { taskSnapshot ->
                imagesRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    listImageUrl.add(imageUrl)
                    Glide.with(this)
                        .load(imageUrl)
                        .centerCrop()
                        .into(ivPhotoCheckin)
                    print("SUCCESS")
                }
            }.addOnFailureListener { e ->
                print("ERROR")
            }
        }
    }

    private fun postReport(reportNew: Report) {
        RetrofitClient.apiService.postReport(reportNew, LoginActivity.userInfoLogin!!.token)
            .enqueue(object : Callback<Report> {
                override fun onResponse(call: Call<Report>, response: Response<Report>) {
                    if (response.isSuccessful) {
                        val rp = response.body()
                        var imageNew = Image(0, "", rp!!.reportId)
                        for (url in listImageUrl) {
                            imageNew.imageUrl = url
                            postImage(imageNew)
                            taskPosition!!.statusCheckIn = 1
                            putTask(taskPosition!!.taskId, taskPosition!!)
                        }
                        println("Post Report is successful")
                    } else {
                        println("Post Report is ERROR")

                    }
                }

                override fun onFailure(call: Call<Report>, t: Throwable) {
                    println("Post Report is ERROR" + t)
                }
            })
    }

    private fun postImage(image: Image) {
        RetrofitClient.apiService.postImage(image, LoginActivity.userInfoLogin!!.token)
            .enqueue(object : Callback<Image> {
                override fun onResponse(call: Call<Image>, response: Response<Image>) {
                    if (response.isSuccessful) {
                        println("POST Image is successful")
                    } else {
                        println("Post Image is ERROR")
                    }
                }
                override fun onFailure(call: Call<Image>, t: Throwable) {
                    println("Post Image is ERROR" + t)
                }
            })
    }

    private fun putTask(taskID: Int, taskPut: Task) {
        println("taskPut" + taskPut)
        RetrofitClient.apiService.putTask(taskID, taskPut, LoginActivity.userInfoLogin!!.token)
            .enqueue(object : Callback<Task> {
                override fun onResponse(call: Call<Task>, response: Response<Task>) {
                    if (response.isSuccessful) {
                        val taskR = response.body()
                        println("taskR" + taskR)
                        println("PutTask checkin Call API SuccessFull ")
                        val resultIntent = Intent()
                        resultIntent.putExtra("taskDataPut", taskR)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    } else {
                        println("PutTask checkin Call API ERROR ")
                    }
                }

                override fun onFailure(call: Call<Task>, t: Throwable) {
                    println("PutTask checkin Call API ERROR " + t)
                }
            })
    }
    private fun onGetCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            return
        }else{
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        latitudeTripCurrent = location.latitude
                        longitudeTripCurrent = location.longitude
                        println("ok lấy location")
                        val locationTrip = LatLng(latitudeTrip, longitudeTrip)
                        displayDistanceBetweenLocations(currentLatLng,locationTrip)
                    } ?: run {
                        Toast.makeText(
                            this,
                            "Couldn't get the location. Please make sure location is enabled on your device.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

    }
    private fun displayDistanceBetweenLocations(location1: LatLng, location2: LatLng) {
        val results = FloatArray(1)
        Location.distanceBetween(
            location1.latitude,
            location1.longitude,
            location2.latitude,
            location2.longitude,
            results
        )
        distanceMeter = results[0].toDouble() // met
        setEvents();
    }
}