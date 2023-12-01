package com.example.qlctncc_tn.activity

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.qlctncc_tn.R

import com.example.qlctncc_tn.Util.ShowDialog
import java.lang.String
import java.util.*

class ForgotPassword_EnterCode : AppCompatActivity() {
    lateinit var btnEnterCode: Button
    lateinit var txtEnterCode: EditText
    lateinit var btnPrevious: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password_enter_code)
        addControl();
        addEvent();
    }
    private fun addEvent() {
        btnEnterCode.setOnClickListener(View.OnClickListener {
            val code = txtEnterCode.text.toString().trim().lowercase()
            if (code.length == 0) {
                val dialog = Dialog(this@ForgotPassword_EnterCode)
                val showDialog = ShowDialog(dialog, false, "Please enter the verification code!",
                    this@ForgotPassword_EnterCode, ForgotPassword_SetPass::class.java, true)
                return@OnClickListener
            }
            if (code != String.valueOf(ForgotPassword_EnterEmail.otpCode) || code.length < 6) {
                val dialog = Dialog(this@ForgotPassword_EnterCode)
                val showDialog = ShowDialog(dialog, false, "Verification code is incorrect, please check again or get a new OTP!",
                    this@ForgotPassword_EnterCode, ForgotPassword_SetPass::class.java, true)
                return@OnClickListener
            }
            val intent = Intent(this@ForgotPassword_EnterCode, ForgotPassword_SetPass::class.java)
            startActivity(intent)
        })
        btnPrevious.setOnClickListener { finish() }
    }

    private fun addControl() {
        btnEnterCode = findViewById(R.id.btnEnterCode)
        txtEnterCode = findViewById(R.id.txtEnterCode)
        btnPrevious = findViewById(R.id.btnPreviousfp2)
    }
    override fun onStart() {

        super.onStart()
    }
    override fun onStop() {
        super.onStop()
    }

}