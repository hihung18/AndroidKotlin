package com.example.qlctncc_tn.Model;

import java.io.Serializable


data class UserDetail (
    var userId : Int,
    var email : String,
    var username : String,
    var password : String,
    var fullName : String,
    var phoneNumber : String,
    var address : String,
    var tokeDevice : String,
    var roleName : String,
): Serializable

