package com.example.qlctncc_tn.Model;

import java.io.Serializable

data class Task(
    var taskId: Int,
    var businessTripID: Int,
    var userID: Int,
    var nameTask: String,
    var detailTask: String,
    var statusConfirm: Int,
    var statusCheckIn: Int,
    var statusComplete: Int,
    var time_cre_task: String,
) : Serializable
