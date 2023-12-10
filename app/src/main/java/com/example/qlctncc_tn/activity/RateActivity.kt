package com.example.qlctncc_tn.activity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.qlctncc_tn.Model.*
import com.example.qlctncc_tn.R
import com.example.qlctncc_tn.Retrofit.RetrofitClient
import com.example.qlctncc_tn.Util.ShowDialog
import com.example.qlctncc_tn.adapter.RateAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class RateActivity : AppCompatActivity() {
    var listRate: MutableList<Rate> = mutableListOf()
    lateinit var btnAdd: Button
    lateinit var btnSave: Button
    lateinit var btnPrevious: ImageButton
    lateinit var edtCommentRate: EditText
    lateinit var linerlayoutAdd: LinearLayout
    var adapter: RateAdapter? = null
    var businessTripID: Int? = 0
    var managerID: Int? = 0
    var businessTripName: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate)
        adapter = RateAdapter(this, listRate)
        businessTripID = intent.getIntExtra("businessTripID", -1)
        businessTripName = intent.getStringExtra("businessTripName")
        managerID = intent.getIntExtra("managerID", -1)
        setControls()
        getListRatebyBusinessTripID(businessTripID!!)
    }

    private fun setEvent() {
        btnAdd.setOnClickListener() {
            linerlayoutAdd.visibility = View.VISIBLE
            edtCommentRate.text.clear()
        }
        btnSave.setOnClickListener() {
            val comment = edtCommentRate.text.toString().trim()
            if (comment.isEmpty()) {
                val dialog = Dialog(this)
                val showDialog = ShowDialog(
                    dialog,
                    false,
                    "Please enter Comment",
                    this,
                    LoginActivity::class.java,
                    true
                )
                return@setOnClickListener
            }
            val businessTripID = BtDetailActivity.businessTrip?.businessTripId!!
            val userID = LoginActivity.userInfoLogin?.id!!
            val currentDate = Date()
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val formattedDate = formatter.format(currentDate)
            var rateNew = Rate(0, businessTripID, userID, comment, formattedDate)
            postRate(rateNew)
        }
        btnPrevious.setOnClickListener() {
            super.onBackPressed()
        }
    }

    private fun setControls() {
        btnAdd = findViewById(R.id.btnAdd_rate)
        btnSave = findViewById(R.id.btnSaveRate)
        linerlayoutAdd = findViewById(R.id.lnAdd)
        linerlayoutAdd.visibility = View.GONE
        edtCommentRate = findViewById(R.id.edtCommentRate)
        btnPrevious = findViewById(R.id.btnPreviousRate)
    }

    private fun getListRatebyBusinessTripID(businessTripID: Int) {
        RetrofitClient.apiService.getListRatebyBusinessTripID(businessTripID)
            .enqueue(object : Callback<List<Rate>> {
                override fun onResponse(call: Call<List<Rate>>, response: Response<List<Rate>>) {
                    if (response.isSuccessful) {
                        listRate = response.body() as MutableList<Rate>
                        val listView = findViewById<ListView>(R.id.lvRate)
                        if (listRate.isNotEmpty()) {
                            adapter = RateAdapter(this@RateActivity, listRate)
                        } else {
                            adapter = RateAdapter(this@RateActivity, emptyList())
                        }

                        listView.adapter = adapter
                        setEvent()
                        println("getListRate by businessTripID Call API Successful ")
                    } else {
                        println("getListRate by businessTripID Call API ERROR ")
                    }
                }

                override fun onFailure(call: Call<List<Rate>>, t: Throwable) {
                    println("getListRate by businessTripID Call API ERROR " + t)
                }
            })
    }

    private fun postRate(rate: Rate) {
        val token = LoginActivity.userInfoLogin?.token!!
        RetrofitClient.apiService.postRate(rate, token)
            .enqueue(object : Callback<Rate> {
                override fun onResponse(call: Call<Rate>, response: Response<Rate>) {
                    if (response.isSuccessful) {
                        val rateNew = response.body()
                        if (rateNew != null) {
                            listRate.add(rateNew)
                        }
                        linerlayoutAdd.visibility = View.GONE
                        adapter!!.notifyDataSetChanged()
                        val lisUserDetial = HomeActivity.listAllUser
                        var manager:UserDetail? = null
                        for (user in lisUserDetial){
                            if (user.userId == managerID) {
                                manager = user
                                break
                            }
                        }
                        val notificationFCM = NotificationFCM()
                        notificationFCM.addToNotification("title", "Hi "+ manager!!.fullName+ " , You have a new Rate")
                        notificationFCM.addToNotification("body", "From "+ LoginActivity.userInfoLogin!!.fullName + " in Trip: "+ businessTripName)
                        notificationFCM.to = manager!!.tokeDevice
                        sendNotificationFCM(notificationFCM)
                        println("Post Rate is successful")
                    } else {
                        println("Post Rate is ERROR")
                    }
                }

                override fun onFailure(call: Call<Rate>, t: Throwable) {
                    println("Post Rate is ERROR" + t)
                }

            })
    }
    private fun sendNotificationFCM(notificationFCM: NotificationFCM) {
        val serverKey = "key=AAAAnkR0nsA:APA91bEKaDDItAac3jKQB4xw3Tmmv5Xj9Cw8y08iebT6e0RmFELBg1nx6uUNfEXGDSr7-K1DgdBGoYuF6yTTX8YAx3gc_PPneLMVyPFRdSDI-oFjJbZgchcmNXNZf6-oKb_xX25Vl_96"
        val contentType = "application/json"

        RetrofitClient.fcm.senNotification(notificationFCM, serverKey, contentType)
            .enqueue(object : Callback<NotificationFCM> {
                override fun onResponse(call: Call<NotificationFCM>, response: Response<NotificationFCM>) {
                    if (response.isSuccessful) {
                        val noti = response.body()
                        println(noti)
                        println("POST Notification is success")
                    } else {
                        println("response: " + response)
                        println("POST Notification is ERRORRRR")
                    }
                }
                override fun onFailure(call: Call<NotificationFCM>, t: Throwable) {
                    println("POST Notification is ERROR + $t")
                }
            })
    }
}