package com.example.qlctncc_tn.Model;

import java.io.Serializable
import java.util.ArrayList;

data class User(
    var token: String,
    var id: Int,
    var username: String,
    var email: String,
    var fullName: String,
    var phoneNumber: String,
    var address: String,
    var tokenDevice: String,
    var roles: ArrayList<String>,
) : Serializable




