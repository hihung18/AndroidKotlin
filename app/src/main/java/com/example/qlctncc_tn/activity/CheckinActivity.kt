package com.example.qlctncc_tn.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.qlctncc_tn.Model.Image
import com.example.qlctncc_tn.Model.Report
import com.example.qlctncc_tn.Model.Task
import com.example.qlctncc_tn.R
import com.example.qlctncc_tn.Retrofit.RetrofitClient
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
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

    companion object {
        val REQUEST_IMAGE_CAPTURE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkin)
        taskPosition = intent.getSerializableExtra("taskPosition") as Task
        currentPhotoPath = ""
        setControls()
        setEventls()
    }

    private fun setEventls() {
        btnTakePhoto.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                println("Tạo tệp tin không thành công, ERROR")
                null
            }
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(this, "com.example.qlctncc_tn.fileprovider", it)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
        btnCheckin.setOnClickListener() {
            var detail = "CHECK-IN: " + taskPosition!!.nameTask
            val currentDate = Date()
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val formattedDate = formatter.format(currentDate)
            var reportNew = Report(
                0,
                taskPosition!!.taskId,
                detail,
                formattedDate,
                listImageUrl
            )
            postReport(reportNew)

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
}