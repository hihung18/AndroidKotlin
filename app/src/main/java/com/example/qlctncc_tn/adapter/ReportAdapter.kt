package com.example.qlctncc_tn.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlctncc_tn.Model.Report
import com.example.qlctncc_tn.R
import com.example.qlctncc_tn.activity.HomeActivity
import java.text.SimpleDateFormat
import java.util.*

class ReportAdapter(private val context: Context, private val listReport: List<Report>) :
    ArrayAdapter<Report>(context, R.layout.list_item_report, listReport) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.list_item_report, parent, false)
        var reportPosition = listReport[position]
        //anhxa
        val tvSTT_Report_item = rowView.findViewById<TextView>(R.id.tvSTT_Report_item)
        val tvReport_detail_item = rowView.findViewById<TextView>(R.id.tvReport_detail_item)
        val tvUserReport_item = rowView.findViewById<TextView>(R.id.tvUserReport_item)
        val tvdateReport_cre_item = rowView.findViewById<TextView>(R.id.tvdateReport_cre_item)
        val rvImageRpItem = rowView.findViewById<RecyclerView>(R.id.rvImageRpItem)
        // set
        tvSTT_Report_item.text = (position + 1).toString()
        tvReport_detail_item.text = reportPosition.report_detail
        tvdateReport_cre_item.text = "Ngày tạo: " + convertDateFormat(reportPosition.time_cre_rp)

        for (user in HomeActivity.listAllUser) {
            if (user.userId == reportPosition.userID) {
                if (user.fullName == "") {
                    tvUserReport_item.text = user.username
                    break
                }
                tvUserReport_item.text = user.fullName
                break
            }
        }

        val layoutManager = GridLayoutManager(context, 4)
        rvImageRpItem.layoutManager = layoutManager
        var adapterReImage = ImageSmallAdapter(reportPosition.imageUrls)
        rvImageRpItem.adapter = adapterReImage
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