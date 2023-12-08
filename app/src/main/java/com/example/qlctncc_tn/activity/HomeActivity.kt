package com.example.qlctncc_tn.activity


import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.qlctncc_tn.Model.BusinessTrip
import com.example.qlctncc_tn.Model.UserDetail
import com.example.qlctncc_tn.R
import com.example.qlctncc_tn.Retrofit.RetrofitClient
import com.example.qlctncc_tn.adapter.BusinessTripAdapter
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {
    companion object {
        var listAllUser: List<UserDetail> = ArrayList<UserDetail>()
    }

    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    val userInfoLogin = LoginActivity.userInfoLogin
    var listBusinessTrip: List<BusinessTrip> = ArrayList<BusinessTrip>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        getAllUser()
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
                    val intent = Intent(applicationContext, LocationActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_personal -> {
                    val intent = Intent(applicationContext, PersonalActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_logOut -> {
                    finish()
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

    private fun getListBusinessTripbyUserID() {
        RetrofitClient.apiService.getListBusinessTripbyUserID(userInfoLogin!!.id)
            .enqueue(object : Callback<List<BusinessTrip>> {
                override fun onResponse(
                    call: Call<List<BusinessTrip>>,
                    response: Response<List<BusinessTrip>>
                ) {
                    if (response.isSuccessful) {
                        listBusinessTrip = response.body() ?: emptyList()
                        println("List BusinessTrip Call API ok")
                        var listBT: MutableList<BusinessTrip> = mutableListOf()
                        var listBTHistory: MutableList<BusinessTrip> = mutableListOf()
                        for (bt in listBusinessTrip) {
                            if (bt.statusBusinessTrip == 0 || bt.statusBusinessTrip == 1)
                                listBT.add(bt)
                            else listBTHistory.add(bt)
                        }
                        val listView = findViewById<ListView>(R.id.lvBusinessTrip)
                        val listViewHistory = findViewById<ListView>(R.id.lvBusinessTripHistory)
                        val adapter = BusinessTripAdapter(this@HomeActivity, listBT)
                        val adapterHistory = BusinessTripAdapter(this@HomeActivity, listBTHistory)
                        listView.adapter = adapter
                        listViewHistory.adapter = adapterHistory

                    } else {

                        println("BusinessTrip Call API ERROR ")
                    }
                }

                override fun onFailure(call: Call<List<BusinessTrip>>, t: Throwable) {

                    println("BusinessTrip Call API ERROR  $t")
                }
            })
    }

    private fun getAllUser() {
        RetrofitClient.apiService.getAllUSer()
            .enqueue(object : Callback<List<UserDetail>> {
                override fun onResponse(
                    call: Call<List<UserDetail>>, response: Response<List<UserDetail>>
                ) {
                    if (response.isSuccessful) {
                        listAllUser = response.body() ?: emptyList()
                        println("get All User Call API Success ")
                    } else {
                        println("get All User Call API ERROR ")
                    }
                }

                override fun onFailure(call: Call<List<UserDetail>>, t: Throwable) {
                    println("get All User Call API ERROR ")
                }
            })
    }

}