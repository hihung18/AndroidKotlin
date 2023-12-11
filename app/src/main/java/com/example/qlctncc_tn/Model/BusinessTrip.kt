package com.example.qlctncc_tn.Model;

import java.io.Serializable


data class BusinessTrip(
    var businessTripId: Int,
    var managerID: Int,
    var partnerID: Int,
    var name_trip: String,
    var detail_trip: String,
    var location_trip: String,
    var latitudeTrip: Double,
    var longitudeTrip: Double,
    var time_begin_trip: String,
    var time_end_trip: String,
    var time_cre_trip: String,
    var statusBusinessTrip: Int,
) : Serializable


