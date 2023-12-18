package com.example.qlctncc_tn.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.qlctncc_tn.Model.BusinessTrip
import com.example.qlctncc_tn.R
import com.example.qlctncc_tn.activity.BtDetailActivity
import java.text.SimpleDateFormat
import java.util.*

class BusinessTripAdapter(private val context: Context, private val businessTrips: List<BusinessTrip>):
    ArrayAdapter<BusinessTrip>(context, R.layout.list_item_businesstrip, businessTrips) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.list_item_businesstrip, parent, false)
        val businessTrip = businessTrips[position]
        //anh xa
        val tvSttBtItem = rowView.findViewById<TextView>(R.id.tvSTT_BT_item)
        val tvNameBtItem = rowView.findViewById<TextView>(R.id.tvNameBT_item)
        val tvDateBtItem = rowView.findViewById<TextView>(R.id.tvDateBT_item)
        val tvstatusBTItem = rowView.findViewById<TextView>(R.id.tvstatusBT_item)
        //set
        tvSttBtItem.text = (position+1).toString()
        tvNameBtItem.text = businessTrip.name_trip
        tvDateBtItem.text = convertDateFormat(businessTrip.time_begin_trip) + " - "+ convertDateFormat(businessTrip.time_end_trip)
        when (businessTrip.statusBusinessTrip){
            0-> {tvstatusBTItem.text = "PREPARING"
                tvstatusBTItem.setBackgroundColor(ContextCompat.getColor(context,R.color.yellow))
            }
            1-> {
                tvstatusBTItem.text = "READY"
                tvstatusBTItem.setBackgroundColor(ContextCompat.getColor(context,R.color.green))
            }
            2-> {
                tvstatusBTItem.text = "PROCESSING"
                tvstatusBTItem.setBackgroundColor(ContextCompat.getColor(context,R.color.orange))
            }
            3-> {
                tvstatusBTItem.text = "FINISHED"
                tvstatusBTItem.setBackgroundColor(ContextCompat.getColor(context,R.color.yellow_orange))
            }
            4-> {
                tvstatusBTItem.text = "COMPLETE"
                tvstatusBTItem.setBackgroundColor(ContextCompat.getColor(context,R.color.greenyblack))
            }
            5-> {
                tvstatusBTItem.text = "POSTPONED"
                tvstatusBTItem.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
            }
        }
        rowView.setOnClickListener {
            val intent = Intent(context, BtDetailActivity::class.java)
            intent.putExtra("businessTrip", businessTrip)
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