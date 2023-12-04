package com.example.qlctncc_tn.Model;

import java.io.Serializable

data class Rate (
    var  rateId : Int,
    var  businessTripID: Int,
    var  userID: Int,
    var  commentRate : String,
    var  time_cre_rate : String,
) : Serializable
