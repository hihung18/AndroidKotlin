package com.example.qlctncc_tn.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.example.qlctncc_tn.R

class CheckinActivity : AppCompatActivity() {
    lateinit var btnPrevious: ImageButton
    lateinit var btnTakePhoto: Button
    lateinit var btnCheckin: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkin)

        setControls()
        setEventls()
    }

    private fun setEventls() {

    }

    private fun setControls() {
        btnCheckin = findViewById(R.id.btnCheckin)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        btnPrevious = findViewById(R.id.btnPreviousCheckinActivity)
    }

}