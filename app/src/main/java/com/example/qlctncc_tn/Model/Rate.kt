package com.example.qlctncc_tn.Model;

import java.io.Serializable

data class Rate (
    var  RateId : Int,
    var  businessTripID: Int,
    var  userID: Int,
    var  commentRate : String,
    var  time_cre_rate : String,
) : Serializable
