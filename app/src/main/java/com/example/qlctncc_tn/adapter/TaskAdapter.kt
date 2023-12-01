package com.example.qlctncc_tn.adapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.example.qlctncc_tn.Model.Task
import com.example.qlctncc_tn.R
import com.example.qlctncc_tn.Retrofit.RetrofitClient
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
        //anh xa
        val tvSTT_Task_item = rowView.findViewById<TextView>(R.id.tvSTT_Task_item)
        val tvTask_detail_item = rowView.findViewById<TextView>(R.id.tvTask_detail_item)
        val tvNameTask_item = rowView.findViewById<TextView>(R.id.tvNameTask_item)
        val tvUserTask_item = rowView.findViewById<TextView>(R.id.tvUserTask_item)
        val tvDateTask_cre_item = rowView.findViewById<TextView>(R.id.tvDateTask_cre_item)
        val btnHoanThanh = rowView.findViewById<Button>(R.id.btnHoanThanh)
//        set
        tvSTT_Task_item.text = (position+1).toString()
        tvTask_detail_item.text = taskPosition.detailTask
        tvNameTask_item.text = taskPosition.nameTask
        tvUserTask_item.text = taskPosition.userID.toString()
        tvDateTask_cre_item.text = convertDateFormat(taskPosition.time_cre_task)
        ///check userID
        if (taskPosition.userID == LoginActivity.userInfoLogin?.id){
            btnHoanThanh.visibility = View.VISIBLE
        }else{
            btnHoanThanh.visibility = View.GONE
        }
        //check status
        if (taskPosition.statusTask == 1){
            btnHoanThanh.setBackgroundColor(Color.RED)
        }else{
            btnHoanThanh.setBackgroundColor(Color.GREEN)
        }
        btnHoanThanh.setOnClickListener(){
            if (taskPosition.statusTask == 2) return@setOnClickListener
            val builder = AlertDialog.Builder(context)
            builder.setMessage("You have definitely completed it?")
            builder.setPositiveButton("Yes") { dialog, id ->
                taskPosition.statusTask=2
                btnHoanThanh.setBackgroundColor(Color.GREEN)
                putTask(taskPosition.taskId,taskPosition)
                notifyDataSetChanged()
            }
            builder.setNegativeButton(
                "No"
            ) { dialog, id -> dialog.dismiss() }
            val alertDialog = builder.create()
            alertDialog.show()
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
                        val task = response.body()
                        val builder = AlertDialog.Builder(context)
                        builder.setMessage("Task complete")
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
                    println("PutTask Call API Errol ")
                }
            })
    }

}