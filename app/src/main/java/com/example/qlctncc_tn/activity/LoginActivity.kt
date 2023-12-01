package com.example.qlctncc_tn.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.qlctncc_tn.MainActivity
import com.example.qlctncc_tn.Model.User
import com.example.qlctncc_tn.Model.UserLogin
import com.example.qlctncc_tn.R
import com.example.qlctncc_tn.Retrofit.RetrofitClient
import com.example.qlctncc_tn.Util.InputHandle
import com.example.qlctncc_tn.Util.ShowDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    companion object{
      var userInfoLogin: User? = null
    }
    private var username_save = ""
    private var password_save = ""
    private var checkBox = false

    lateinit var btnSignIn: Button
    var checkLogin = false
    lateinit var txtUsername: EditText
    lateinit var txtPassword: EditText
    lateinit var createAccount: TextView
    lateinit var forgotPassword: TextView
    lateinit var isRemember: CheckBox
    var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        addControls()
        addEvents()
    }

    private fun addControls() {
        btnSignIn = findViewById(R.id.btnSignIn)
        txtUsername = findViewById(R.id.txtUsername)
        txtPassword = findViewById(R.id.txtPassword)
        createAccount = findViewById(R.id.createAccount)
        forgotPassword = findViewById(R.id.forgotPassword)
        isRemember = findViewById(R.id.isRemember)
        txtUsername.setText(username_save)
        txtPassword.setText(password_save)
        isRemember.isChecked = checkBox
        txtUsername.filters = arrayOf(InputHandle.filter)
    }

    private fun addEvents() {
        btnSignIn.setOnClickListener {
            val username = txtUsername.text.toString().trim()
            val password = txtPassword.text.toString().trim()
            if (username.isEmpty()) {
                val dialog = Dialog(this@LoginActivity)
                ShowDialog(dialog, false, "Please enter username", this@LoginActivity, MainActivity::class.java, true)
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                val dialog = Dialog(this@LoginActivity)
                ShowDialog(dialog, false, "Please enter password", this@LoginActivity, MainActivity::class.java, true)
                return@setOnClickListener
            }
            handleLoginApi()
        }

        txtPassword.setOnTouchListener { _, motionEvent ->
            val right = 2
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (motionEvent.rawX >= txtPassword.right - txtPassword.compoundDrawables[right].bounds.width()) {
                    if (passwordVisible) {
                        txtPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.user_password, 0, R.drawable.show_password, 0)
                        txtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                        passwordVisible = false
                    } else {
                        txtPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.user_password, 0, R.drawable.hide_password, 0)
                        txtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        passwordVisible = true
                    }
                }
            }
            false
        }

        createAccount.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
        }

        forgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPassword_EnterEmail::class.java)
            startActivity(intent)
        }
    }

    private fun handleLoginApi() {
        val loadingDialog = LoadingDialog(this@LoginActivity)
        loadingDialog.startLoadingDialog()
        val userName = txtUsername.text.toString().trim().lowercase()
        val userPassword = txtPassword.text.toString().lowercase()
        val user = UserLogin(userName, userPassword)
        RetrofitClient.apiService.postLogin(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.code() == 200) {
                    checkLogin = true
                    userInfoLogin = response.body()

                    if (isRemember.isChecked) {
                        username_save = userName
                        password_save = userPassword
                        checkBox = true
                    } else {
                        username_save = ""
                        password_save = ""
                        checkBox = false
                    }

                    loadingDialog.closeLoadingDialog()
                    userInfoLogin?.token = "Bearer " + userInfoLogin?.token
                    val intent = Intent(applicationContext, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    checkLogin = false
                    userInfoLogin = null
                    val dialog = Dialog(this@LoginActivity)
                    ShowDialog(dialog, false, "Incorrect account or password!", this@LoginActivity, MainActivity::class.java, true)
                }
                loadingDialog.closeLoadingDialog()
            }

            @Override
            override fun onFailure(call: Call<User>, t: Throwable) {
                checkLogin = false
                val dialog = Dialog(this@LoginActivity)
                loadingDialog.closeLoadingDialog()
                ShowDialog(dialog, false, "Connection errors! Please try again later", this@LoginActivity, MainActivity::class.java, true)
            }
        })
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }
}
