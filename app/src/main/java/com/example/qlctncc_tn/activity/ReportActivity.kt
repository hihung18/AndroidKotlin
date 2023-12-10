package com.example.qlctncc_tn.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlctncc_tn.Model.Image
import com.example.qlctncc_tn.Model.Report
import com.example.qlctncc_tn.Model.Task
import com.example.qlctncc_tn.R
import com.example.qlctncc_tn.Retrofit.RetrofitClient
import com.example.qlctncc_tn.Util.ShowDialog
import com.example.qlctncc_tn.adapter.ImageAdapter
import com.example.qlctncc_tn.adapter.ReportAdapter
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.storage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class ReportActivity : AppCompatActivity() {
    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    var listImageUrl: MutableList<String> = mutableListOf()
    var listReport: MutableList<Report> = mutableListOf()
    var adapterReImage: ImageAdapter? = null
    lateinit var btnAdd: Button
    lateinit var btnSave: Button
    lateinit var recyclerView: RecyclerView
    lateinit var btnAddImage: Button
    lateinit var btnPrevious: ImageButton
    lateinit var reportDetail: EditText
    lateinit var linerlayoutAdd: LinearLayout
    var adapter: ReportAdapter? = null
    var businessTripID: Int? = 0
    var checkRefuse = false
    var taskPosition: Task? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_report)
        businessTripID = intent.getIntExtra("businessTripID", -1)
        if (intent.hasExtra("taskPosition")) {
            taskPosition = intent.getSerializableExtra("taskPosition") as? Task
            if (taskPosition != null) checkRefuse = true
        }
        setControls()
        getListRatebyBusinessTripID(businessTripID!!)
    }

    private fun setEvent() {
        btnAdd.setOnClickListener() {
            linerlayoutAdd.visibility = View.VISIBLE
            reportDetail.text.clear()
        }
        btnAddImage.setOnClickListener() {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
        btnSave.setOnClickListener() {
            var detail = reportDetail.text.toString().trim()
            if (checkRefuse) detail = "REFUSE: " + taskPosition!!.nameTask + "\n" + detail
            if (detail.isEmpty()) {
                val dialog = Dialog(this)
                val showDialog = ShowDialog(
                    dialog,
                    false,
                    "Please enter rate detail",
                    this,
                    LoginActivity::class.java,
                    true
                )
                return@setOnClickListener
            }
            val currentDate = Date()
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val formattedDate = formatter.format(currentDate)
            var reportNew = Report(
                0,
                LoginActivity.userInfoLogin!!.id,
                businessTripID!!,
                detail,
                formattedDate,
                listImageUrl
            )
            postReport(reportNew)
        }
        btnPrevious.setOnClickListener(){
            onBackPressed()
        }
    }

    private fun setControls() {
        btnAdd = findViewById(R.id.btnAdd_Report)
        btnAddImage = findViewById(R.id.btnAddImage)
        btnSave = findViewById(R.id.btnSaveReport)
        linerlayoutAdd = findViewById(R.id.lnAddReport)
        linerlayoutAdd.visibility = View.GONE
        reportDetail = findViewById(R.id.edtReportDetail)
        btnPrevious = findViewById(R.id.btnPreviousReport)
        ///////////
        recyclerView = findViewById(R.id.recyclerViewListImage)
        val layoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager = layoutManager
        adapterReImage = ImageAdapter(listImageUrl)
        recyclerView.adapter = adapterReImage
    }

    private fun getListRatebyBusinessTripID(businessTripID: Int) {
        RetrofitClient.apiService.getListReportbyBusinessTripID(businessTripID)
            .enqueue(object : Callback<List<Report>> {
                override fun onResponse(
                    call: Call<List<Report>>,
                    response: Response<List<Report>>
                ) {
                    if (response.isSuccessful) {
                        listReport = response.body() as MutableList<Report>
                        val listView = findViewById<ListView>(R.id.lvReport)
                        if (listReport.isNotEmpty()) {
                            adapter = ReportAdapter(this@ReportActivity, listReport)
                        } else {
                            adapter = ReportAdapter(this@ReportActivity, emptyList())
                        }
                        listView.adapter = adapter
                        setEvent()
                        println("getReport by businessTripID Call API Successful ")
                    } else {
                        println("getReport by businessTripID Call API ERROR ")
                    }
                }

                override fun onFailure(call: Call<List<Report>>, t: Throwable) {
                    println("getReport by businessTripID Call API ERROR " + t)
                }
            })
    }

    private fun postReport(reportNew: Report) {
        RetrofitClient.apiService.postReport(reportNew, LoginActivity.userInfoLogin!!.token)
            .enqueue(object : Callback<Report> {
                override fun onResponse(call: Call<Report>, response: Response<Report>) {
                    if (response.isSuccessful) {
                        val rp = response.body()
                        if (rp != null) {
                            rp.imageUrls = listImageUrl
                            listReport.add(rp)
                        }
                        linerlayoutAdd.visibility = View.GONE
                        adapter!!.notifyDataSetChanged()
                        var imageNew: Image = Image(0, "", rp!!.reportId)
                        for (url in listImageUrl) {
                            imageNew.imageUrl = url
                            postImage(imageNew)
                        }
                        if (checkRefuse) {
                            taskPosition!!.statusConfirm = -1
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!
            val storageRef = Firebase.storage.reference
            val imagesRef = storageRef.child("images/${UUID.randomUUID()}.jpg")
            val uploadTask = imagesRef.putFile(imageUri)
            uploadTask.addOnSuccessListener { taskSnapshot ->
                imagesRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    listImageUrl.add(imageUrl)
                    adapterReImage!!.notifyDataSetChanged()

                    println("Upload Image Success")
                }.addOnFailureListener {

                    println("Upload ERROR")
                }
            }.addOnFailureListener { e ->
                println("Upload ERROR")
            }
        }
    }

    private fun putTask(taskID: Int, taskPut: Task) {
        RetrofitClient.apiService.putTask(taskID, taskPut, LoginActivity.userInfoLogin!!.token)
            .enqueue(object : Callback<Task> {
                override fun onResponse(call: Call<Task>, response: Response<Task>) {
                    if (response.isSuccessful) {
                        val taskR = response.body()
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