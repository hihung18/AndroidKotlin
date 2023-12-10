package com.example.qlctncc_tn.Model

import java.io.Serializable

class NotificationFCM (
    var notification: MutableMap<String, String> = mutableMapOf(),
    var to: String = ""
) : Serializable {

    fun addToNotification(key: String, value: String) {
        notification[key] = value
    }
}

