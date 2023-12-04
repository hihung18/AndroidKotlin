package com.example.qlctncc_tn.activity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.qlctncc_tn.MainActivity
import com.example.qlctncc_tn.Model.User
import com.example.qlctncc_tn.Model.UserDetail
import com.example.qlctncc_tn.R
import com.example.qlctncc_tn.Retrofit.RetrofitClient
import com.example.qlctncc_tn.Util.ShowDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonalActivity : AppCompatActivity() {
    lateinit var btnSave: Button
    lateinit var btnUpdate: Button
    lateinit var edtUserName:EditText
    lateinit var edtEmail:EditText
    lateinit var edtFullname:EditText
    lateinit var edtPhoneNum:EditText
    lateinit var edtAddress:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal)
        setControls()
        setEvents()
    }

    private fun setEvents() {
        val userInfo = LoginActivity.userInfoLogin
        edtUserName.setText(userInfo?.username)
        edtEmail.setText(userInfo?.email)
        edtFullname.setText(userInfo?.fullName)
        edtPhoneNum.setText(userInfo?.phoneNumber)
        edtAddress.setText(userInfo?.address)
        btnUpdate.setOnClickListener(){
            edtFullname.isEnabled = true
            edtPhoneNum.isEnabled = true
            edtAddress.isEnabled = true
            btnUpdate.visibility = View.GONE
            btnSave.visibility = View.VISIBLE
        }
        btnSave.setOnClickListener(){
            if (edtFullname.text.toString().trim().isEmpty()) {
                val dialog = Dialog(this@PersonalActivity)
                ShowDialog(dialog, false, "Please enter FullName", this@PersonalActivity, MainActivity::class.java, true)
                return@setOnClickListener
            }
            if (edtPhoneNum.text.toString().trim().isEmpty()) {
                val dialog = Dialog(this@PersonalActivity)
                ShowDialog(dialog, false, "Please enter PhoneNumber", this@PersonalActivity, MainActivity::class.java, true)
                return@setOnClickListener
            }
            if (edtAddress.text.toString().trim().isEmpty()) {
                val dialog = Dialog(this@PersonalActivity)
                ShowDialog(dialog, false, "Please enter Address", this@PersonalActivity, MainActivity::class.java, true)
                return@setOnClickListener
            }
            var userDetailNew = UserDetail(userInfo!!.id,userInfo.email,userInfo.username
                ,"",userInfo.fullName,userInfo.phoneNumber,userInfo.address,"ROLE_NV")
            putUserDetail(userDetailNew.userId,userDetailNew)
        }
    }

    private fun setControls() {
        btnSave = findViewById(R.id.btnSaveP)
        btnUpdate = findViewById(R.id.btnUpdateP)
        edtUserName = findViewById(R.id.edtUsernameP)
        edtEmail = findViewById(R.id.edtEmailP)
        edtFullname = findViewById(R.id.edtFullNameP)
        edtPhoneNum = findViewById(R.id.edtPhoneP)
        edtAddress = findViewById(R.id.edtAddressP)
        edtUserName.isEnabled = false
        edtEmail.isEnabled = false
        edtFullname.isEnabled = false
        edtPhoneNum.isEnabled = false
        edtAddress.isEnabled = false
        btnSave.visibility = View.GONE
    }
    private fun putUserDetail(userId:Int, userDetail: UserDetail){
        RetrofitClient.apiService.putUserDetail(userId,userDetail, LoginActivity.userInfoLogin!!.token)
            .enqueue(object : Callback<UserDetail>{
                override fun onResponse(call: Call<UserDetail>, response: Response<UserDetail>) {
                    if (response.isSuccessful){
                        val userDetail = response.body()
                        btnSave.visibility = View.GONE
                        btnUpdate.visibility = View.VISIBLE
                        edtFullname.isEnabled = false
                        edtPhoneNum.isEnabled = false
                        edtAddress.isEnabled = false
                        println("Put UserDetail is successful")
                    }else {
                        Toast.makeText(this@PersonalActivity, "Put UserDetail Success!", Toast.LENGTH_SHORT).show()
                        println("Put UserDetail is successful")
                    }
                }
                override fun onFailure(call: Call<UserDetail>, t: Throwable) {
                    Toast.makeText(this@PersonalActivity, "Put UserDetail Success!", Toast.LENGTH_SHORT).show()
                    println("Put UserDetail")
                }
            })
    }

}