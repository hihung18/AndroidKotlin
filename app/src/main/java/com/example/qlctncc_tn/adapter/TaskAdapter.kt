package com.example.qlctncc_tn.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import com.example.qlctncc_tn.Model.Rate
import com.example.qlctncc_tn.Model.Task
import com.example.qlctncc_tn.R
import com.example.qlctncc_tn.Retrofit.RetrofitClient
import com.example.qlctncc_tn.Util.ShowDialog
import com.example.qlctncc_tn.activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
interface TaskAdapterListener {
    fun onRefuseClicked(taskPosition: Task)
    fun onCheckinClicked(taskPosition:Task)
}
class TaskAdapter(private val context: Context, private val listTasks: List<Task>
,private val listener: TaskAdapterListener,statusBT: Int) :
    ArrayAdapter<Task>(context, R.layout.list_item_task, listTasks) {
    lateinit var  btnConfirmTask : Button
    lateinit var  btnRefuseTask : Button
    lateinit var  btnCompleteTask : Button
    lateinit var  btnCheckinTask : Button
    lateinit var  btnReportTask : Button
    lateinit var  btnRateTask : Button
    lateinit var  tvRefused : TextView
    val statusBusinessTrip = statusBT
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
        tvRefused = rowView.findViewById(R.id.tvRefused)
        btnCompleteTask = rowView.findViewById(R.id.btnCompleteTask)
        btnCheckinTask = rowView.findViewById(R.id.btnCheckinTask)
        btnConfirmTask = rowView.findViewById(R.id.btnConfirmTask)
        btnRefuseTask = rowView.findViewById(R.id.btnRefuseTask)
        btnReportTask = rowView.findViewById(R.id.btnReportTask)
        btnRateTask = rowView.findViewById(R.id.btnRateTask)

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
        tvRefused.visibility = View.GONE
        btnConfirmTask.visibility = View.GONE
        btnRefuseTask.visibility = View.GONE
        btnCheckinTask.visibility = View.GONE
        btnCompleteTask.visibility = View.GONE
        btnRateTask.visibility = View.GONE
        btnReportTask.visibility = View.GONE

        ///check userID
        if (taskPosition.userID == LoginActivity.userInfoLogin?.id){
            btnRateTask.visibility = View.VISIBLE
            btnReportTask.visibility = View.VISIBLE
            if (taskPosition.statusConfirm == -1){
                tvRefused.visibility = View.VISIBLE
                btnRateTask.visibility = View.GONE
                btnReportTask.visibility = View.GONE

            }
            else if (taskPosition.statusConfirm == 0){
                btnConfirmTask.visibility = View.VISIBLE
                btnRefuseTask.visibility = View.VISIBLE
            }
            else{
                btnConfirmTask.visibility = View.VISIBLE
                btnCheckinTask.visibility = View.VISIBLE
            }
            if (taskPosition.statusCheckIn == 1)
                btnCompleteTask.visibility = View.VISIBLE
        }
        if (taskPosition.statusComplete == 1){
            btnCompleteTask.setBackgroundColor(Color.RED)
        }else if (taskPosition.statusComplete == 3){
            btnCompleteTask.setBackgroundColor(Color.GREEN)
        }else if (taskPosition.statusComplete == 2){
            btnCompleteTask.setBackgroundColor(Color.YELLOW)
            btnCompleteTask.setText("CENSORING")
        }
        btnCompleteTask.setOnClickListener(){
            if (taskPosition.statusComplete == 2 ||taskPosition.statusComplete == 3 || (statusBusinessTrip != 0 && statusBusinessTrip != 1)) return@setOnClickListener
            val builder = AlertDialog.Builder(context)
            builder.setMessage("You have definitely completed it?")
            builder.setPositiveButton("Yes") { dialog, id ->
                taskPosition.statusComplete = 2
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
        if (taskPosition.statusConfirm == 1)
            btnConfirmTask.setBackgroundColor(Color.GREEN)

        btnConfirmTask.setOnClickListener(){
            if (taskPosition.statusConfirm == 1 || (statusBusinessTrip != 0 && statusBusinessTrip != 1)) return@setOnClickListener
            val builder = AlertDialog.Builder(context)
            builder.setMessage("You definitely confirm the task?")
            builder.setPositiveButton("Yes") { dialog, id ->
                taskPosition.statusConfirm = 1
                putTask(taskPosition.taskId, taskPosition)
                notifyDataSetChanged()
            }
            builder.setNegativeButton(
                "No"
            ) { dialog, id -> dialog.dismiss() }
            val alertDialog = builder.create()
            alertDialog.show()
        }
        //check-in
        if (taskPosition.statusCheckIn == 0){
            btnCheckinTask.setBackgroundColor(Color.RED)
        }else if (taskPosition.statusCheckIn == 1){
            btnCheckinTask.setBackgroundColor(Color.GREEN)
        }
        btnCheckinTask.setOnClickListener {
            if (taskPosition.statusCheckIn == 1 || (statusBusinessTrip != 0 && statusBusinessTrip != 1)) return@setOnClickListener
            println("click btn")
            listener.onCheckinClicked(taskPosition)
        }
        btnRefuseTask.setOnClickListener(){
            if (statusBusinessTrip != 0 && statusBusinessTrip != 1) return@setOnClickListener
            listener.onRefuseClicked(taskPosition)
        }
        btnReportTask.setOnClickListener() {
            val intent = Intent(context, ReportActivity::class.java)
            intent.putExtra("taskPosition",taskPosition )
            intent.putExtra("newReportStatus",1 )
            context.startActivity(intent)
        }
        btnRateTask.setOnClickListener() {
            val intent = Intent(context, RateActivity::class.java)
            intent.putExtra("taskPosition",taskPosition )
            context.startActivity(intent)
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