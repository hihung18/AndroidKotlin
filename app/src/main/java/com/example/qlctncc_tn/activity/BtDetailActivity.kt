package com.example.qlctncc_tn.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import com.example.qlctncc_tn.Model.*
import com.example.qlctncc_tn.R
import com.example.qlctncc_tn.Retrofit.RetrofitClient
import com.example.qlctncc_tn.adapter.TaskAdapter
import com.example.qlctncc_tn.adapter.TaskAdapterListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class BtDetailActivity : AppCompatActivity(), TaskAdapterListener {
    lateinit var tvNameBT_detail: TextView
    lateinit var tvPartner_detail: TextView
    lateinit var tvLocationBT_detail: TextView
    lateinit var tvManagerBT_detail: TextView
    lateinit var tvDetaiBT_detail: TextView
    lateinit var tvDateBeginBT_detail: TextView
    lateinit var tvDateEndBT_detail: TextView
    lateinit var tvDateCreateBT_detail: TextView
    lateinit var btnRate: Button
    lateinit var btnReport: Button
    lateinit var btnPrevious: ImageButton
    lateinit var btnMapBTdetial: ImageButton
    val REQUEST_CODE_ACTIVITY_CHECKIN = 123
    val REQUEST_CODE_ACTIVITY_RESUFE = 234

    companion object {
        var businessTrip: BusinessTrip? = null
        var listTask: MutableList<Task> = mutableListOf()
    }

    var nameManager: String? = null
    var namePartner: String? = null
    var adapter: TaskAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bt_detail)
        businessTrip = intent.getSerializableExtra("businessTrip") as BusinessTrip
        getPartnerByID(businessTrip!!.partnerID)
        setControl()
        setEvent()
    }
    private fun setEvent() {
        tvNameBT_detail.text = businessTrip!!.name_trip
        tvLocationBT_detail.text = businessTrip!!.location_trip
        tvDetaiBT_detail.text = businessTrip!!.detail_trip
        tvDateBeginBT_detail.text = convertDateFormat(businessTrip!!.time_begin_trip)
        tvDateEndBT_detail.text = convertDateFormat(businessTrip!!.time_end_trip)
        tvDateCreateBT_detail.text = convertDateFormat(businessTrip!!.time_cre_trip)
        tvPartner_detail.text = namePartner
        tvManagerBT_detail.text = nameManager

        btnRate.setOnClickListener() {
            val intent = Intent(applicationContext, RateActivity::class.java)
            intent.putExtra("businessTripID", businessTrip?.businessTripId)
            startActivity(intent)
        }
        btnReport.setOnClickListener() {
            val intent = Intent(applicationContext, ReportActivity::class.java)
            intent.putExtra("businessTripID", businessTrip?.businessTripId)
            startActivity(intent)
        }
        btnMapBTdetial.setOnClickListener(){
            val intent = Intent(applicationContext, LocationActivity::class.java)
            intent.putExtra("latitudeTrip", businessTrip?.latitudeTrip)
            intent.putExtra("longitudeTrip", businessTrip?.longitudeTrip)
            startActivity(intent)
        }
        btnPrevious.setOnClickListener() {
            onBackPressed()
        }

    }

    private fun setControl() {
        tvNameBT_detail = findViewById(R.id.tvNameBT_detail)
        tvPartner_detail = findViewById(R.id.tvPartner_detail)
        tvLocationBT_detail = findViewById(R.id.tvLocationBT_detail)
        tvManagerBT_detail = findViewById(R.id.tvManagerBT_detail)
        tvDetaiBT_detail = findViewById(R.id.tvDetaiBT_detail)
        tvDateBeginBT_detail = findViewById(R.id.tvDateBeginBT_detail)
        tvDateEndBT_detail = findViewById(R.id.tvDateEndBT_detail)
        tvDateCreateBT_detail = findViewById(R.id.tvDateCreateBT_detail)
        btnPrevious = findViewById(R.id.btnPreviousTripDetail)
        btnMapBTdetial = findViewById(R.id.btnMapBTdetial)
        btnRate = findViewById(R.id.btnRate)
        btnReport = findViewById(R.id.btnReport)

    }

    private fun convertDateFormat(inputDate: String): String {
        val cutString = inputDate.take(10)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date: Date = inputFormat.parse(cutString) ?: Date()
        return outputFormat.format(date)
    }

    private fun getPartnerByID(partnerID: Int) {
        RetrofitClient.apiService.getPartnerByID(partnerID)
            .enqueue(object : Callback<Partner> {
                override fun onResponse(call: Call<Partner>, response: Response<Partner>) {
                    if (response.isSuccessful) {
                        println("getPartner by ID Call API SuccessFul ")
                        val partner = response.body()
                        namePartner = partner?.name_pn
                        getNameManagerByID(businessTrip!!.managerID)
                    } else {

                        println("getPartner by ID Call API ERROR ")
                    }
                }
                override fun onFailure(call: Call<Partner>, t: Throwable) {

                    println("getPartner by ID Call API ERROR ")
                }
            })
    }

    private fun getNameManagerByID(managerID: Int) {
        RetrofitClient.apiService.getUserDetailByID(managerID)
            .enqueue(object : Callback<UserDetail> {
                override fun onResponse(call: Call<UserDetail>, response: Response<UserDetail>) {
                    if (response.isSuccessful) {
                        val userDetail = response.body()
                        nameManager = userDetail?.fullName
                        getListTaskbyBusinessTripID(businessTrip!!.businessTripId)
                    } else {

                        println("getName Manager by ID Call API ERROR ")
                    }
                }
                override fun onFailure(call: Call<UserDetail>, t: Throwable) {

                    println("getName Manager by ID Call API ERROR ")
                }
            })
    }

    private fun getListTaskbyBusinessTripID(businessTripID: Int) {
        RetrofitClient.apiService.getListTaskbyBusinessTripID(businessTripID)
            .enqueue(object : Callback<List<Task>> {
                override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                    if (response.isSuccessful) {
                        listTask = response.body() as MutableList<Task>
                        val listView = findViewById<ListView>(R.id.lvTaskBT)
                        setEvent()
                        if (listTask.isNotEmpty()) {
                            adapter =
                                TaskAdapter(this@BtDetailActivity, listTask, this@BtDetailActivity,
                                    businessTrip!!
                                )
                        } else {
                            adapter = TaskAdapter(this@BtDetailActivity, emptyList(), this@BtDetailActivity
                            , businessTrip!!)
                        }
                        listView.adapter = adapter
                    } else {

                        println("get List Task by business ID Call API ERROR ")
                    }
                }
                override fun onFailure(call: Call<List<Task>>, t: Throwable) {

                    println("get List Task by business ID Call API ERROR ")
                }
            })
    }

    override fun onRefuseClicked(taskPosition: Task) {
        val intent = Intent(this, ReportActivity::class.java)
        intent.putExtra("taskPosition", taskPosition)
        intent.putExtra("RefuseStatus", 1)
        startActivityForResult(intent, REQUEST_CODE_ACTIVITY_RESUFE)
    }

    override fun onCheckinClicked(taskPosition: Task) {
        val intent = Intent(this, CheckinActivity::class.java)
        intent.putExtra("taskPosition", taskPosition)
        startActivityForResult(intent, REQUEST_CODE_ACTIVITY_CHECKIN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ACTIVITY_CHECKIN || requestCode == REQUEST_CODE_ACTIVITY_RESUFE) {
            if (resultCode == Activity.RESULT_OK) {
                val taskDataPut = data?.getSerializableExtra("taskDataPut") as Task
                listTask.forEach { task ->
                    if (task.taskId == taskDataPut.taskId) {
                        task.statusCheckIn = taskDataPut.statusCheckIn
                    }
                }
                adapter!!.notifyDataSetChanged()
            }
        }
    }

}