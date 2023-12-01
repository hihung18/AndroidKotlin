package com.example.qlctncc_tn.Util

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.example.qlctncc_tn.R

class ShowDialog {
    private var isSuccess: Boolean = false
    private var text_content: String? = null
    private var currentContext: Context? = null
    private var newClass: Class<*>? = null
    private var showSuccessNotify: Boolean = false

    constructor(dialog: Dialog, isSuccess: Boolean, text_content: String, currentContext: Context, newClass: Class<*>, showSuccessNotify: Boolean) {
        this.isSuccess = isSuccess
        this.text_content = text_content
        this.currentContext = currentContext
        this.newClass = newClass
        this.showSuccessNotify = showSuccessNotify

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_dialog_message)
        dialog.setCancelable(true)

        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttributes = window?.attributes
        windowAttributes?.let { window.attributes = it }

        val btnDialogOke = dialog.findViewById<Button>(R.id.btnDialogOke)
                val txtDialogContent = dialog.findViewById<TextView>(R.id.txtDialogContent)
                txtDialogContent.text = text_content

        btnDialogOke.setOnClickListener {
            if (isSuccess && showSuccessNotify) {
                val intent = Intent(currentContext, newClass)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                currentContext.startActivity(intent)
            } else {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    constructor(dialog: Dialog, text_content: String, currentContext: Context) {
        this.text_content = text_content
        this.currentContext = currentContext
    }

    fun CreateDialog(dialog: Dialog): Dialog? {
        dialog.setCancelable(false)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_dialog_message)

        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val windowAttributes = window?.attributes
        windowAttributes?.let { window.attributes = it }

        val btnDialogOke = dialog.findViewById<Button>(R.id.btnDialogOke)
                val txtDialogContent = dialog.findViewById<TextView>(R.id.txtDialogContent)
                txtDialogContent.text = text_content

        btnDialogOke.setOnClickListener {
            val connectivityManager = currentContext?.applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val networkInfo: NetworkInfo? = connectivityManager?.activeNetworkInfo
            if (networkInfo == null) {
                dialog.dismiss()
                dialog.show()
            } else {
                dialog.dismiss()
            }
        }
        return dialog
    }
}
