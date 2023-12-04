package com.example.qlctncc_tn.activity

import android.app.Dialog
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.qlctncc_tn.Model.ChangePassword
import com.example.qlctncc_tn.R
import com.example.qlctncc_tn.Retrofit.RetrofitClient
import com.example.qlctncc_tn.Util.ShowDialog
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ForgotPassword_SetPass : AppCompatActivity() {
    private lateinit var btnEnterNewPassword: Button
    private lateinit var txtEnterNewPassword: EditText
    private lateinit var txtEnterReNewPassword:EditText
    private lateinit var btnPrevious: ImageButton
    private var passwordVisible = false
    private var rePasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password_set_pass)
        ForgotPassword_EnterEmail.otpCode = -1
        addControls()
        addEvents()
    }
    private fun addEvents() {
        btnEnterNewPassword.setOnClickListener(View.OnClickListener {
            val newPassword = txtEnterNewPassword.text.trim().toString()
            val reNewPassword = txtEnterReNewPassword.text.trim().toString()
            if (newPassword.length == 0) {
                val dialog = Dialog(this@ForgotPassword_SetPass)
                val showDialog = ShowDialog(dialog, false, "Please enter a new password!", this@ForgotPassword_SetPass
                    ,LoginActivity::class.java, true)
                return@OnClickListener
            } else if (newPassword.length < 6) {
                val dialog = Dialog(this@ForgotPassword_SetPass)
                val showDialog = ShowDialog(dialog, false, "Password needs 6 or more characters.", this@ForgotPassword_SetPass
                    , LoginActivity::class.java, true)
                return@OnClickListener
            } else if (reNewPassword.length == 0) {
                val dialog = Dialog(this@ForgotPassword_SetPass)
                val showDialog = ShowDialog(
                    dialog, false, "Please re-enter new password!", this@ForgotPassword_SetPass
                    ,LoginActivity::class.java, true
                )
                return@OnClickListener
            } else if (newPassword == reNewPassword == false) {
                val dialog = Dialog(this@ForgotPassword_SetPass)
                val showDialog = ShowDialog(dialog, false, "Hai mật khẩu không giống nhau! Vui lòng nhập lại.", this@ForgotPassword_SetPass
                    ,LoginActivity::class.java, true)
                return@OnClickListener
            }
            handleLoginApi()
        })
        btnPrevious.setOnClickListener(){
            onBackPressed()
        }


        // Show password
        txtEnterNewPassword.setOnTouchListener { view, motionEvent ->
            val Right = 2
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (motionEvent.rawX >= txtEnterNewPassword.right - txtEnterNewPassword.compoundDrawables[Right].bounds.width()) {
                    if (passwordVisible) {
                        txtEnterNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.show_password, 0)
                        txtEnterNewPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                        passwordVisible = false
                    } else {
                        txtEnterNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide_password, 0)
                        txtEnterNewPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        passwordVisible = true
                    }
                }
            }
            false
        }

        // Show RePassword
        txtEnterReNewPassword.setOnTouchListener { view, motionEvent ->
            val Right = 2
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (motionEvent.rawX >= txtEnterReNewPassword.right - txtEnterReNewPassword.compoundDrawables[Right].bounds.width()) {
                    if (rePasswordVisible) {
                        txtEnterReNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.show_password, 0)
                        txtEnterReNewPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                        rePasswordVisible = false
                    } else {
                        txtEnterReNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide_password, 0)
                        txtEnterReNewPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        rePasswordVisible = true
                    }
                }
            }
            false
        }
    }

    fun handleLoginApi() {
        val email: String = ForgotPassword_EnterEmail.emailInput
        val password = txtEnterNewPassword.text.toString().trim()
        if (email.length == 0) {
            val dialog = Dialog(this@ForgotPassword_SetPass)
            val showDialog = ShowDialog(dialog, false, "Email entered is not valid!\nPlease go back to entering your email address!",
                this@ForgotPassword_SetPass, LoginActivity::class.java, true)
            return
        }
        if (!email.contains("@gmail.com")) {
            val dialog = Dialog(this@ForgotPassword_SetPass)
            val showDialog = ShowDialog(dialog, false, "Email entered is not valid!\nPlease go back to entering your email address!",
                this@ForgotPassword_SetPass, LoginActivity::class.java, true)
            return
        }
        val loadingDialog = LoadingDialog(this@ForgotPassword_SetPass)
        loadingDialog.startLoadingDialog()
        Log.e("email", email)
        Log.e("txtPassword", password)
        val changePassword = ChangePassword(email, password)
        RetrofitClient.apiService.postChangePassByEmail(changePassword)?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        val dialog = Dialog(this@ForgotPassword_SetPass)
                        val showDialog = ShowDialog(dialog, true, "Password change successful!",
                            this@ForgotPassword_SetPass, LoginActivity::class.java, true)
                    }
                } else {
                    try {
                        var strResponseBody = ""
                        strResponseBody = response.errorBody()!!.string()
                        val messageObject = JSONObject(strResponseBody)
                        val dialog = Dialog(this@ForgotPassword_SetPass)
                        val showDialog = ShowDialog(dialog, false, """ Password change failed! ${messageObject["message"]} """.trim(), this@ForgotPassword_SetPass, LoginActivity::class.java, true)
                        Log.v("Error code 400", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        throw RuntimeException(e)
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                }
                loadingDialog.closeLoadingDialog()
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                val dialog = Dialog(this@ForgotPassword_SetPass)
                loadingDialog.closeLoadingDialog()
                val showDialog = ShowDialog(dialog, false, "Connection errors! Please try again later",
                    this@ForgotPassword_SetPass, LoginActivity::class.java, true
                )
            }
        })
    }

    private fun addControls() {
        btnEnterNewPassword = findViewById(R.id.btnEnterNewPassword)
        txtEnterNewPassword = findViewById(R.id.txtEnterNewPassword)
        txtEnterReNewPassword = findViewById(R.id.txtEnterReNewPassword)
        btnPrevious = findViewById(R.id.btnPreviousfp3)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

}