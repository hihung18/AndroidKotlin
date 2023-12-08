package com.example.qlctncc_tn.Model;

import java.io.Serializable

data class Partner(
    var partnerId: Int,
    var name_pn: String,
    var email_pn: String,
    var phoneNum_pn: String,
    var address_pn: String,
    var status_pn: Int,
) : Serializable
