package com.example.qlctncc_tn.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.example.qlctncc_tn.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.PolylineOptions


class LocationActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var longitudeTrip= 0.0
    var latitudeTrip = 0.0
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        latitudeTrip = intent.getDoubleExtra("latitudeTrip", 0.0)
        longitudeTrip = intent.getDoubleExtra("longitudeTrip", 0.0)
        val toolbar: Toolbar = findViewById(R.id.toolbarLocation)
        setSupportActionBar(toolbar)
        supportActionBar?.title = LoginActivity.userInfoLogin?.fullName
        //
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap
        val locationTrip = LatLng(latitudeTrip, longitudeTrip)
        googleMap.addMarker(MarkerOptions().position(locationTrip).title("Loaction Business Trip"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationTrip, 10f))
    }

    fun onGetCurrentLocationButtonClick(view: View) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    val locationTrip = LatLng(latitudeTrip, longitudeTrip)
                    googleMap.addMarker(
                        MarkerOptions().position(currentLatLng).title("Current Location")
                    )
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    if (latitudeTrip != 0.0 || longitudeTrip != 0.0){
                        val polylineOptions = PolylineOptions()
                        polylineOptions.add(currentLatLng, locationTrip)
                        polylineOptions.width(5f) //kich thuoc duong ke
                        polylineOptions.color(Color.BLUE)
                        val polyline = googleMap.addPolyline(polylineOptions)
                        displayDistanceBetweenLocations(currentLatLng,locationTrip)
                    }
                } ?: run {
                    Toast.makeText(
                        this,
                        "Couldn't get the location. Please make sure location is enabled on your device.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }
    fun displayDistanceBetweenLocations(location1: LatLng, location2: LatLng) {
        val results = FloatArray(1)
        Location.distanceBetween(
            location1.latitude,
            location1.longitude,
            location2.latitude,
            location2.longitude,
            results
        )
        val distance = results[0] // met
        val distanceklm = distance / 1000 // kilômét
        val distanceFormatted = String.format("%.2f", distanceklm)
        val distanceText = "Distance: $distanceFormatted km" // Chuỗi hiển thị khoảng cách
        val customTitle = findViewById<TextView>(R.id.tvdistance)
        customTitle.text = distanceText
    }
    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }
}

