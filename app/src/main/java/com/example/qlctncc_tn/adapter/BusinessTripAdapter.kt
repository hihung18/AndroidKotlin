package com.example.qlctncc_tn.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.qlctncc_tn.Model.BusinessTrip
import com.example.qlctncc_tn.R
import com.example.qlctncc_tn.activity.BtDetailActivity
import java.text.SimpleDateFormat
import java.util.*

class BusinessTripAdapter(private val context: Context, private val businessTrips: List<BusinessTrip>) :
    ArrayAdapter<BusinessTrip>(context, R.layout.list_item_businesstrip, businessTrips) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.list_item_businesstrip, parent, false)
        val businessTrip = businessTrips[position]
        //anh xa
        val tvSttBtItem = rowView.findViewById<TextView>(R.id.tvSTT_BT_item)
        val tvNameBtItem = rowView.findViewById<TextView>(R.id.tvNameBT_item)
        val tvDateBtItem = rowView.findViewById<TextView>(R.id.tvDateBT_item)
        //set
        tvSttBtItem.text = (position+1).toString()
        tvNameBtItem.text = businessTrip.name_trip
        tvDateBtItem.text = convertDateFormat(businessTrip.time_begin_trip)

        rowView.setOnClickListener {
            val intent = Intent(context, BtDetailActivity::class.java)
            intent.putExtra("businessTrip", businessTrip)
//            intent.putExtra("managerID", businessTrip.managerID)
//            intent.putExtra("partnerID", businessTrip.partnerID)
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
}