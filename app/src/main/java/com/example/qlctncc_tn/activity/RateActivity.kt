package com.example.qlctncc_tn.activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.qlctncc_tn.Model.Rate
import com.example.qlctncc_tn.Model.Report
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
class RateActivity : AppCompatActivity(), RateAdapter.RateAdapterListener {
    var listRate: MutableList<Rate> = mutableListOf()
    lateinit var btnAdd:Button
    lateinit var btnSave:Button
    lateinit var btnPrevious:ImageButton
    lateinit var edtCommentRate:EditText
    lateinit var linerlayoutAdd: LinearLayout
    var adapter : RateAdapter?=null
    var businessTripID: Int? = 0
    var checkAdd = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate)
        adapter = RateAdapter(this, listRate, this)
        businessTripID = intent.getIntExtra("businessTripID",-1)
        setControls()
        getListRatebyBusinessTripID(businessTripID!!)
    }

    private fun setEvent() {
        btnAdd.setOnClickListener(){
            checkAdd = true
            linerlayoutAdd.visibility = View.VISIBLE
            edtCommentRate.text.clear()
        }
        btnSave.setOnClickListener(){
            val comment = edtCommentRate.text.toString().trim()
            if (comment.isEmpty()) {
                val dialog = Dialog(this)
                val showDialog = ShowDialog(dialog, false, "Please enter Comment", this, LoginActivity::class.java, true)
                return@setOnClickListener
            }
            val businessTripID = BtDetailActivity.businessTrip?.businessTripId!!
            val userID = LoginActivity.userInfoLogin?.id!!
            val currentDate = Date()
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val formattedDate = formatter.format(currentDate)
            var rateNew: Rate = Rate(0,businessTripID,userID,comment,formattedDate)
            if (checkAdd){
                postRate(rateNew)
            }
        }
        btnPrevious.setOnClickListener(){
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
    private fun getListRatebyBusinessTripID(businessTripID: Int){
        RetrofitClient.apiService.getListRatebyBusinessTripID(businessTripID)
            .enqueue(object : Callback<List<Rate>>{
                override fun onResponse(call: Call<List<Rate>>, response: Response<List<Rate>>) {
                    if(response.isSuccessful){
                        listRate = response.body() as MutableList<Rate>
                        val listView = findViewById<ListView>(R.id.lvRate)
                        if(listRate.isNotEmpty()){
                             adapter = RateAdapter(this@RateActivity, listRate,this@RateActivity)
                        }else {
                            adapter = RateAdapter(this@RateActivity, emptyList(),this@RateActivity)
                        }
                        listView.adapter = adapter
                        setEvent()
                        println("getListRate by businessTripID Call API Successful ")
                    }else{
                        println("getListRate by businessTripID Call API ERROR ")
                    }
                }
                override fun onFailure(call: Call<List<Rate>>, t: Throwable) {
                    println("getListRate by businessTripID Call API ERROR " + t)
                }
            })
    }
    private fun postRate(rate:Rate){
        val token = LoginActivity.userInfoLogin?.token!!
        RetrofitClient.apiService.postRate(rate,token)
            .enqueue(object : Callback<Rate>{
                override fun onResponse(call: Call<Rate>, response: Response<Rate>) {
                    if(response.isSuccessful){
                        val rateNew = response.body()
                        if (rateNew != null) {
                            (listRate as MutableList<Rate>).add(rateNew)
                        }
                        linerlayoutAdd.visibility = View.GONE
                        adapter!!.notifyDataSetChanged()
                        Toast.makeText(this@RateActivity, "Add Rate Success!", Toast.LENGTH_SHORT).show()
                        println("Post Rate is successful")
                    }else{
                        println("Post Rate is ERROR")
                    }
                }
                override fun onFailure(call: Call<Rate>, t: Throwable) {
                    println("Post Rate is ERROR" + t)
                }

            })
    }
    override fun onEditRateClicked(position: Int) {
        linerlayoutAdd.visibility = View.VISIBLE
        checkAdd = false
        edtCommentRate.text.clear()
        var ratePositionAc = listRate[position]
        edtCommentRate.setText(ratePositionAc.commentRate)
        btnSave.setOnClickListener(){
            val comment = edtCommentRate.text.toString().trim()
            if (comment.isEmpty()) {
                val dialog = Dialog(this)
                val showDialog = ShowDialog(dialog, false, "Please enter Comment", this, LoginActivity::class.java, true)
                return@setOnClickListener
            }
            ratePositionAc.commentRate = comment
            if (!checkAdd){
                putRate(ratePositionAc.rateId,ratePositionAc)
            }
        }
        // Thực hiện các thay đổi khác liên quan đến việc nhấn nút Edit ở đây
    }
    private fun putRate(rateID:Int,rate:Rate){
        val token = LoginActivity.userInfoLogin?.token!!
        RetrofitClient.apiService.putRate(rateID,rate,token)
            .enqueue(object : Callback<Rate>{
                override fun onResponse(call: Call<Rate>, response: Response<Rate>) {
                    if(response.isSuccessful){
                        val rateNew = response.body()
                        linerlayoutAdd.visibility = View.GONE
                        for (i in listRate.indices) {
                            val rate = listRate[i]
                            if (rate.rateId == rateNew?.rateId) {
                                listRate[i] = rateNew // Gán phần tử có rateId trùng với rateNew bằng rateNew
                                break
                            }
                        }
                        adapter!!.notifyDataSetChanged()
                        Toast.makeText(this@RateActivity, "PUT Rate Success!", Toast.LENGTH_SHORT).show()
                        println("PUT Rate is successful")
                    }else{
                        println("PUT Rate is ERROR")
                    }
                }
                override fun onFailure(call: Call<Rate>, t: Throwable) {
                    println("PUT Rate is ERROR" + t)
                }
            })
    }

}