package com.example.qlctncc_tn.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.qlctncc_tn.Model.Task
import com.example.qlctncc_tn.R
import com.example.qlctncc_tn.Retrofit.RetrofitClient
import com.example.qlctncc_tn.activity.CheckinActivity
import com.example.qlctncc_tn.activity.HomeActivity
import com.example.qlctncc_tn.activity.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(private val context: Context, private val listTasks: List<Task>) :
    ArrayAdapter<Task>(context, R.layout.list_item_task, listTasks) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.list_item_task, parent, false)
        var taskPosition = listTasks[position]
        val REQUEST_CODE_ACTIVITY_B = 123
        //anh xa
        val tvSTT_Task_item = rowView.findViewById<TextView>(R.id.tvSTT_Task_item)
        val tvTask_detail_item = rowView.findViewById<TextView>(R.id.tvTask_detail_item)
        val tvNameTask_item = rowView.findViewById<TextView>(R.id.tvNameTask_item)
        val tvUserTask_item = rowView.findViewById<TextView>(R.id.tvUserTask_item)
        val tvDateTask_cre_item = rowView.findViewById<TextView>(R.id.tvDateTask_cre_item)
        val btnCompleteTask = rowView.findViewById<Button>(R.id.btnCompleteTask)
        val btnCheckinTask = rowView.findViewById<Button>(R.id.btnCheckinTask)
        val btnConfirmTask = rowView.findViewById<Button>(R.id.btnConfirmTask)
//        set
        tvSTT_Task_item.text = (position+1).toString()
        tvTask_detail_item.text = taskPosition.detailTask
        tvNameTask_item.text = taskPosition.nameTask
        tvUserTask_item.text = taskPosition.userID.toString()
        tvDateTask_cre_item.text ="Ngày tạo: "+ convertDateFormat(taskPosition.time_cre_task)

        for (user in HomeActivity.listAllUser){
            if (user.userId == taskPosition.userID){
                if (user.fullName ==""){
                    tvUserTask_item.text = user.username
                    break
                }
                tvUserTask_item.text = user.fullName
                break
            }
        }
        ///check userID
        if (taskPosition.userID == LoginActivity.userInfoLogin?.id){
            btnCompleteTask.visibility = View.VISIBLE
            btnCheckinTask.visibility = View.VISIBLE
            btnConfirmTask.visibility = View.VISIBLE

        }else{
            btnCompleteTask.visibility = View.GONE
            btnCheckinTask.visibility = View.GONE
            btnConfirmTask.visibility = View.GONE
        }
        //check statusComplete
        if (taskPosition.statusComplete == 1){
            btnCompleteTask.setBackgroundColor(Color.RED)
        }else if (taskPosition.statusComplete == 3){
            btnCompleteTask.setBackgroundColor(Color.GREEN)
        }else if (taskPosition.statusComplete == 2){
            btnCompleteTask.setBackgroundColor(Color.YELLOW)
            btnCompleteTask.setText("CENSORING")
        }
        btnCompleteTask.setOnClickListener(){
            if (taskPosition.statusComplete == 2 ||taskPosition.statusComplete == 3) return@setOnClickListener
            val builder = AlertDialog.Builder(context)
            builder.setMessage("You have definitely completed it?")
            builder.setPositiveButton("Yes") { dialog, id ->
                taskPosition.statusComplete = 2
                btnCompleteTask.setBackgroundColor(Color.DKGRAY)
                btnCompleteTask.setText("CENSORING")
                putTask(taskPosition.taskId,taskPosition)
                notifyDataSetChanged()
            }
            builder.setNegativeButton(
                "No"
            ) { dialog, id -> dialog.dismiss() }
            val alertDialog = builder.create()
            alertDialog.show()
        }
        //check statusConfirm
        if (taskPosition.statusConfirm == 0){
            btnConfirmTask.setBackgroundColor(Color.RED)
        }else{
            btnConfirmTask.setBackgroundColor(Color.GREEN)
        }
        btnConfirmTask.setOnClickListener(){
            if (taskPosition.statusConfirm == 1) return@setOnClickListener
            val builder = AlertDialog.Builder(context)
            builder.setMessage("You definitely confirm the task?")
            builder.setPositiveButton("Yes") { dialog, id ->
                taskPosition.statusConfirm=1
                btnCompleteTask.setBackgroundColor(Color.GREEN)
                btnCompleteTask.setText("CENSORING")
                putTask(taskPosition.taskId,taskPosition)
                notifyDataSetChanged()
            }
            builder.setNegativeButton(
                "No"
            ) { dialog, id -> dialog.dismiss() }
            val alertDialog = builder.create()
            alertDialog.show()
        }
        btnCheckinTask.setOnClickListener {
            val intent = Intent(this, CheckinActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ACTIVITY_B)
        }
        return rowView
    }
    private fun convertDateFormat(inputDate: String): String {
        val cutString = inputDate.take(10)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val date: Date = inputFormat.parse(cutString) ?: Date()
        return outputFormat.format(date)
    }
    private fun putTask(taskID:Int, taskP:Task){
        val token = LoginActivity.userInfoLogin?.token!!
        RetrofitClient.apiService.putTask(taskID,taskP,token)
            .enqueue(object : Callback<Task>{
                override fun onResponse(call: Call<Task>, response: Response<Task>) {
                    if (response.isSuccessful){
                        val builder = AlertDialog.Builder(context)
                        builder.setMessage("Successful change!")
                        builder.setNegativeButton(
                            "Yes"
                        ) { dialog, id -> dialog.dismiss()
                        }
                        val alertDialog = builder.create()
                        alertDialog.show()
                        println("PutTask Call API SuccessFull ")
                    }
                }
                override fun onFailure(call: Call<Task>, t: Throwable) {
                    println("PutTask Call API ERROR ")
                }
            })
    }

}