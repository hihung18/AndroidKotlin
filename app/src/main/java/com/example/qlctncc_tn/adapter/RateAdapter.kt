package com.example.qlctncc_tn.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.example.qlctncc_tn.Model.Rate
import com.example.qlctncc_tn.R
import com.example.qlctncc_tn.activity.HomeActivity
import java.text.SimpleDateFormat
import java.util.*

class RateAdapter(private val context: Context, private val listRate: List<Rate>) :
    ArrayAdapter<Rate>(context, R.layout.list_item_rate, listRate) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.list_item_rate, parent, false)
        val ratePosition = listRate[position]
        //anh xa
        val tvSTT_Rate_item = rowView.findViewById<TextView>(R.id.tvSTT_Rate_item)
        val tvRate_comment_item = rowView.findViewById<TextView>(R.id.tvRate_comment_item)
        val tvUserRate_item = rowView.findViewById<TextView>(R.id.tvUserRate_item)
        val tvdateRate_cre_item = rowView.findViewById<TextView>(R.id.tvdateRate_cre_item)
        val lnRate = rowView.findViewById<LinearLayout>(R.id.lnRate)
        //set
//        val listUser = HomeActivity.listAllUser
//        for (user in listUser){
//            if (user.userId == ratePosition.userID && user.roleName == "ROLE_NV")
//                lnRate.setBackgroundColor(R.drawable.bg_rate_user)
//        }
        tvSTT_Rate_item.text = (position+1).toString()
        tvRate_comment_item.text = ratePosition.commentRate
        tvdateRate_cre_item.text = "Ngày tạo: "+convertDateFormat(ratePosition.time_cre_rate)


        for (user in HomeActivity.listAllUser){
            if (user.userId == ratePosition.userID){
                if (user.fullName ==""){
                    tvUserRate_item.text = user.username
                    break
                }
                tvUserRate_item.text = user.fullName
                break
            }
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
}
