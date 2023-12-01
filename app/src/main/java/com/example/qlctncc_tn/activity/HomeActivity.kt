package com.example.qlctncc_tn.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.qlctncc_tn.Model.BusinessTrip
import com.example.qlctncc_tn.Model.User
import com.example.qlctncc_tn.R
import com.example.qlctncc_tn.Retrofit.RetrofitClient
import com.example.qlctncc_tn.adapter.BusinessTripAdapter
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    val userInfoLogin = LoginActivity.userInfoLogin
    var businessTrip: List<BusinessTrip> = ArrayList<BusinessTrip>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setControl()
        ActionBar()
        getListBusinessTripbyUserID()
    }
    private fun ActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size)
        toolbar.setNavigationOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        navigationView.setNavigationItemSelectedListener { item ->
            val bundle = Bundle()
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(applicationContext, HomeActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_location -> {
                }
                R.id.nav_personal -> {
                    val intent = Intent(applicationContext, PersonalActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_logOut -> {
                    val logout = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(logout)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            false
        }
    }
    private fun setControl() {
        toolbar = findViewById(R.id.toolbarhome)
        navigationView = findViewById(R.id.navigationview)
        drawerLayout = findViewById(R.id.drawerlayout)
    }
    private fun getListBusinessTripbyUserID(){

        RetrofitClient.apiService.getListBusinessTripbyUserID(userInfoLogin!!.id)
            .enqueue(object : Callback<List<BusinessTrip>> {
            override fun onResponse(call: Call<List<BusinessTrip>>, response: Response<List<BusinessTrip>>) {
                if (response.isSuccessful) {
                    businessTrip = response.body()?: emptyList()
                    println("List BusinessTrip Call API ok")
                    val listView = findViewById<ListView>(R.id.lvBusinessTrip)
                    val adapter = BusinessTripAdapter(this@HomeActivity, businessTrip)
                    listView.adapter = adapter

                } else {
                    Toast.makeText(this@HomeActivity, "Call API Errol", Toast.LENGTH_SHORT).show()
                    println("BusinessTrip Call API Errol ")
                }
            }

            override fun onFailure(call: Call<List<BusinessTrip>>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Call API Errol  $t", Toast.LENGTH_SHORT).show()
                println("BusinessTrip Call API Errol  $t")
            }
        })
    }

}