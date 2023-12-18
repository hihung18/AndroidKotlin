package com.example.qlctncc_tn.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.qlctncc_tn.R
import com.example.qlctncc_tn.Util.InputHandle
import java.util.*
import javax.mail.*
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@Suppress("DEPRECATION")
class ForgotPassword_EnterEmail : AppCompatActivity() {
    private lateinit var btnEnterEmail: Button
    private lateinit var btnPrevious: ImageButton
    private lateinit var txtEnterEmail: EditText
    private var checkSendEmail = false
    companion object {
        var otpCode = -1
        var emailInput = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password_enter_email)
        addControl();
        addEvent();
    }

    private fun addEvent() {
        btnEnterEmail.setOnClickListener(View.OnClickListener {
            val email =
                txtEnterEmail.text.toString().trim().lowercase(Locale.getDefault())
            if (email.length == 0) {
                openDialogEnterEmail(false, "Please enter your Email!")
                return@OnClickListener
            }
            if (!email.contains("@gmail.com")) {
                openDialogEnterEmail(false, "Invalid email input!")
                return@OnClickListener
            }
            val loadingDialog = LoadingDialog(this@ForgotPassword_EnterEmail)
            loadingDialog.startLoadingDialog()
            sendEmailOTP()
            loadingDialog.closeLoadingDialog()
            if (checkSendEmail) {
                openDialogEnterEmail(
                    true,
                    "OTP sent successfully!\n Please check your email address"
                )
            } else {
                openDialogEnterEmail(
                    false,
                    "OTP sending failed!\nPlease check your email address and try again"
                )
            }
        })

        btnPrevious.setOnClickListener { onBackPressed() }
    }

    private fun addControl() {
        btnEnterEmail = findViewById(R.id.btnEnterEmail)
        btnPrevious = findViewById(R.id.btnPreviousfp1)
        txtEnterEmail = findViewById(R.id.txtEnterEmail)
        txtEnterEmail.filters = arrayOf<InputFilter>(InputHandle.filter)
    }

    fun sendEmailOTP() {
        try {
            val email =
                txtEnterEmail.text.toString().trim ().lowercase(Locale.getDefault())
            val stringSenderEmail = "lequanghungb2@gmail.com"
            val stringPasswordSenderEmail = "pmmzptlksdyhgffy"
            val stringHost = "smtp.gmail.com"
            val properties = System.getProperties()
            properties["mail.smtp.host"] = stringHost
            properties["mail.smtp.port"] = "465"
            properties["mail.smtp.ssl.enable"] = "true"
            properties["mail.smtp.auth"] = "true"
            val session = Session.getInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail)
                }
            })
            val mimeMessage = MimeMessage(session)
            mimeMessage.addRecipient(Message.RecipientType.TO, InternetAddress(email))
            otpCode  = (100000..999999).random()
            mimeMessage.subject = "Mã xác thực OTP đổi mật khẩu"
            mimeMessage.setText(
                "Chào bạn,\n" + "Bạn đang thực hiện lấy lại mật khẩu mới trên ứng dụng Quản lý chuyến công tác ngoài cơ quan. Để tránh rủi ro, vui lòng không gửi OTP cho bất kỳ ai. Mã OTP: $otpCode"

                        + "Để được hỗ trợ thêm, vui lòng liên hệ n19dccn070@student.ptithcm.edu.vn" +

                        "Cảm ơn!."
            )
            val thread = Thread {
                try {
                    Transport.send(mimeMessage)
                    checkSendEmail = true
                    emailInput = email
                } catch (e: MessagingException) {
                    e.printStackTrace()
                    checkSendEmail = false
                }
            }
            thread.start()
            thread.join()
        } catch (e: AddressException) {
            e.printStackTrace()
        } catch (e: MessagingException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }


    fun openDialogEnterEmail(check: Boolean, text_content: String?) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_dialog_message)
        dialog.setCancelable(true)
        val window = dialog.window ?: return
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val windowAttributes = window.attributes
        window.attributes = windowAttributes
        val btnDialogOke = dialog.findViewById<Button>(R.id.btnDialogOke)
        val txtDialogContent = dialog.findViewById<TextView>(R.id.txtDialogContent)
        txtDialogContent.text = text_content
        btnDialogOke.setOnClickListener {
            dialog.dismiss()
            if (check) {
                val intent = Intent(
                    this@ForgotPassword_EnterEmail,
                    ForgotPassword_EnterCode::class.java
                )
                startActivity(intent)
            }
        }
        dialog.show()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }
}