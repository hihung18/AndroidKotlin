package com.example.qlctncc_tn.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.example.qlctncc_tn.Model.*
import com.example.qlctncc_tn.R
import com.example.qlctncc_tn.Retrofit.RetrofitClient
import com.example.qlctncc_tn.adapter.BusinessTripAdapter
import com.example.qlctncc_tn.adapter.TaskAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class BtDetailActivity : AppCompatActivity() {
    lateinit var tvNameBT_detail: TextView
    lateinit var tvPartner_detail: TextView
    lateinit var tvLocationBT_detail: TextView
    lateinit var tvManagerBT_detail: TextView
    lateinit var tvDetaiBT_detail: TextView
    lateinit var tvDateBeginBT_detail: TextView
    lateinit var tvDateEndBT_detail: TextView
    lateinit var tvDateCreateBT_detail: TextView
    lateinit var btnPrevious: ImageButton
    var businessTrip: BusinessTrip? = null
    var nameManager: String? = null
    var namePartner: String? = null
    var listTask: List<Task> = ArrayList<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bt_detail)
        businessTrip = intent.getSerializableExtra("businessTrip") as BusinessTrip
//        managerID = intent.getIntExtra("managerID",-1)
//        partnerID = intent.getIntExtra("partnerID",-1)
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
    }
    private fun convertDateFormat(inputDate: String): String {
        val cutString = inputDate.take(10)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val date: Date = inputFormat.parse(cutString) ?: Date()
        return outputFormat.format(date)
    }
    private fun getPartnerByID(partnerID : Int) {
        RetrofitClient.apiService.getPartnerByID(partnerID)
            .enqueue(object : Callback<Partner> {
                override fun onResponse(call: Call<Partner>, response: Response<Partner>) {
                    if (response.isSuccessful) {
                        val partner = response.body()
                        namePartner = partner?.name_pn
                        getNameManagerByID(businessTrip!!.managerID)
                    } else {
                        Toast.makeText(this@BtDetailActivity, "getPartner by ID Call API Errol", Toast.LENGTH_SHORT).show()
                        println("getPartner by ID Call API Errol ")
                    }
                }
                override fun onFailure(call: Call<Partner>, t: Throwable) {
                    Toast.makeText(this@BtDetailActivity, "getPartner by ID Call API Errol", Toast.LENGTH_SHORT).show()
                    println("getPartner by ID Call API Errol ")
                }
            })
    }
    private fun getNameManagerByID(managerID : Int) {
        RetrofitClient.apiService.getUserDetailByID(managerID)
            .enqueue(object : Callback<UserDetail> {
                override fun onResponse(call: Call<UserDetail>, response: Response<UserDetail>) {
                    if (response.isSuccessful) {
                        val userDetail = response.body()
                        nameManager = userDetail?.fullName
                        getListTaskbyBusinessTripID(businessTrip!!.businessTripId)
                    } else {
                        Toast.makeText(this@BtDetailActivity, "getName Manager by ID Call API Errol", Toast.LENGTH_SHORT).show()
                        println("getName Manager by ID Call API Errol ")
                    }
                }
                override fun onFailure(call: Call<UserDetail>, t: Throwable) {
                    Toast.makeText(this@BtDetailActivity, "getName Manager by ID Call API Errol", Toast.LENGTH_SHORT).show()
                    println("getName Manager by ID Call API Errol ")
                }
            })
    }
    private fun getListTaskbyBusinessTripID(businessTripID: Int){
        RetrofitClient.apiService.getListTaskbyBusinessTripID(businessTripID)
            .enqueue(object : Callback<List<Task>>{
                override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                    if (response.isSuccessful){
                        listTask = response.body()?: emptyList()
                        val listView = findViewById<ListView>(R.id.lvTaskBT)
                        setEvent()
                        val adapter = TaskAdapter(this@BtDetailActivity, listTask)
                        listView.adapter = adapter
                    }else {
                        Toast.makeText(this@BtDetailActivity, "get List Task by business ID Call API Errol", Toast.LENGTH_SHORT).show()
                        println("get List Task by business ID Call API Errol ")
                    }
                }
                override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                    Toast.makeText(this@BtDetailActivity, "get List Task by business ID Call API Errol", Toast.LENGTH_SHORT).show()
                    println("get List Task by business ID Call API Errol ")
                }
            })
    }
}