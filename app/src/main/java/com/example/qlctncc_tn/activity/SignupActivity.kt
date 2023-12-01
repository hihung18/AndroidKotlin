package com.example.qlctncc_tn.activity

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.qlctncc_tn.Model.User
import com.example.qlctncc_tn.Model.UserSignup
import com.example.qlctncc_tn.R
import com.example.qlctncc_tn.Retrofit.RetrofitClient
import com.example.qlctncc_tn.Util.InputHandle
import com.example.qlctncc_tn.Util.ShowDialog
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class SignupActivity : AppCompatActivity() {

    private var checkSignup = false

    private lateinit var txtUsername: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var txtRePassword: EditText
    private lateinit var txtPhoneNumber: EditText
    private lateinit var txtFullName: EditText
    private lateinit var txtAddress: EditText
    private lateinit var btnSignup: Button
    private lateinit var haveAccount: TextView
    private lateinit var btnPrevious: ImageButton
    private var passwordVisible = false
    private var rePasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        addControls()
        addEvents()
    }

    private fun addEvents() {
        btnSignup.setOnClickListener {
            val username = txtUsername.text.toString().trim()
            val email = txtEmail.text.toString().trim()
            val password = txtPassword.text.toString().trim()
            val rePassword = txtRePassword.text.toString().trim()
            val fullName = txtFullName.text.toString().trim()
            val phoneNumber = txtPhoneNumber.text.toString().trim()
            val address = txtAddress.text.toString().trim()

            if (username.isEmpty()) {
                val dialog = Dialog(this@SignupActivity)
                val showDialog = ShowDialog(dialog, false, "Please enter username", this@SignupActivity, LoginActivity::class.java, true)
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                val dialog = Dialog(this@SignupActivity)
                val showDialog = ShowDialog(
                    dialog, false, "Please enter your email address", this@SignupActivity,
                    LoginActivity::class.java, true
                )
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                val dialog = Dialog(this@SignupActivity)
                val showDialog = ShowDialog(
                    dialog, false, "Please enter password", this@SignupActivity,
                    LoginActivity::class.java, true
                )
                return@setOnClickListener
            }

            if (rePassword.isEmpty()) {
                val dialog = Dialog(this@SignupActivity)
                val showDialog = ShowDialog(
                    dialog, false, "Please re-enter password", this@SignupActivity,
                    LoginActivity::class.java, true
                )
                return@setOnClickListener
            }

            if (rePassword != password) {
                val dialog = Dialog(this@SignupActivity)
                val showDialog = ShowDialog(
                    dialog, false, "Two passwords are not the same", this@SignupActivity,
                    LoginActivity::class.java, true
                )
                return@setOnClickListener
            }

            handleSignupApi()
        }

        haveAccount.setOnClickListener {
            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        btnPrevious.setOnClickListener {
            finish()
        }


        // Show password
        txtPassword.setOnTouchListener { view, motionEvent ->
            val Right = 2
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (motionEvent.rawX >= txtPassword.right - txtPassword.compoundDrawables[Right].bounds.width()) {
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
        // Show RePassword
        txtRePassword.setOnTouchListener { view, motionEvent ->
            val Right = 2
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (motionEvent.rawX >= txtRePassword.right - txtRePassword.compoundDrawables[Right].bounds.width()) {
                    if (rePasswordVisible) {
                        txtRePassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.user_password, 0, R.drawable.show_password, 0
                        )
                        txtRePassword.transformationMethod = PasswordTransformationMethod.getInstance()
                        rePasswordVisible = false
                    } else {
                        txtRePassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.user_password, 0, R.drawable.hide_password, 0)
                        txtRePassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        rePasswordVisible = true
                    }
                }
            }
            false
        }


    }

    private fun addControls() {
        txtUsername = findViewById(R.id.txtUsernameSignup)
        txtEmail = findViewById(R.id.txtEmailSignup)
        txtPassword = findViewById(R.id.txtPasswordSignup)
        txtRePassword = findViewById(R.id.txtRePasswordSignup)
        txtFullName = findViewById(R.id.txtfullNameSignup)
        txtPhoneNumber = findViewById(R.id.txtphoneNumSignup)
        txtAddress = findViewById(R.id.txtaddressSignup)
        btnSignup = findViewById(R.id.btnSignup)
        haveAccount = findViewById(R.id.haveAccount)
        btnPrevious = findViewById(R.id.btnPrevious)
        txtUsername.filters = arrayOf(InputHandle.filter)
        txtEmail.filters = arrayOf(InputHandle.filter)
    }

    private fun handleSignupApi() {
        val loadingDialog = LoadingDialog(this@SignupActivity)
        loadingDialog.startLoadingDialog()

        val uUsername = txtUsername.text.toString().trim().lowercase()
        val uEmail = txtEmail.text.toString().trim().lowercase()
        val uPassword = txtPassword.text.toString()
        val uFullname = txtFullName.text.toString()
        val uPhoneNum = txtPhoneNumber.text.toString()
        val uAddress = txtAddress.text.toString()
        val userapi = UserSignup(0, uEmail, uUsername, uPassword, uFullname, uPhoneNum, uAddress, "ROLE_NV")
        RetrofitClient.apiService.postSignUp(userapi).enqueue(object : Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                try {
                    if (response.isSuccessful) {
                        println("Successful account registration!")
                        val dialog = Dialog(this@SignupActivity)
                        val showDialog = ShowDialog(
                            dialog, true, "Successful account registration!"
                            , this@SignupActivity,LoginActivity::class.java, true)

                    } else {
                        println("Account registration failed!")
                        val dialog = Dialog(this@SignupActivity)
                        val showDialog = ShowDialog(dialog, false, "Account registration failed! "
                            ,this@SignupActivity, LoginActivity::class.java, true)

                    }
                } catch (e: IOException) {
                    Log.e("ERROR", "message: " + e.message)
                }
                loadingDialog.closeLoadingDialog()
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                val dialog = Dialog(this@SignupActivity)
                loadingDialog.closeLoadingDialog()
                val showDialog = ShowDialog(
                    dialog, false, "Connection errors! Please try again later"
                    , this@SignupActivity,LoginActivity::class.java, true
                )
            }
        })
    }

    override fun onStart() {
        Log.v("THEO DOI", "ON")
        super.onStart()
    }

    override fun onStop() {
        Log.v("THEO DOI", "OFF")
        super.onStop()
    }
}