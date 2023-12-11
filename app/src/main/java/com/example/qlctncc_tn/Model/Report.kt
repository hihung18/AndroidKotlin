package com.example.qlctncc_tn.Model;

import java.io.Serializable


data class Report(
    var reportId: Int,
    var taskID: Int,
    var report_detail: String,
    var time_cre_rp: String,
    var imageUrls: List<String>,
) : Serializable
