package com.fertail.istock

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fertail.istock.databinding.ActivityDashboardBinding
import com.fertail.istock.util.CommonUtils
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity()  {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var binding: ActivityDashboardBinding
    private lateinit var navController: NavController

    companion object {
        fun start(caller: Context){
            val intent = Intent(caller, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            caller.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        val navView: NavigationView = binding.navView

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            binding.appBarMain.includedMain.navigation.menu.findItem(destination.id)?.isChecked = true
        }

        binding.consLogout.setOnClickListener {
            CommonUtils.showLogotPopup(this@DashboardActivity)
        }



        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_profile, R.id.nav_verification,
                R.id.nav_report_list),binding.drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.appBarMain.includedMain.navigation.setupWithNavController(navController)
        binding.appBarMain.includedMain.navigation.itemIconTintList = null



        binding.appBarMain.includedMain.navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    navController.navigate(R.id.nav_home)
                    true
                }
                R.id.nav_profile -> {
                    navController.navigate(R.id.nav_profile)
                    true
                }
                R.id.nav_verification -> {
                    navController.navigate(R.id.nav_verification)
                    true
                }
                R.id.nav_report_list -> {
                    navController.navigate(R.id.nav_report_list)
                    true
                }
                else -> false
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    fun closeDrawer(){
        binding.drawerLayout.openDrawer(GravityCompat.END)
    }

    fun backClicked(){
        onBackPressed()
    }


    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.nav_home) {
            super.onBackPressed()
        } else {
            navController.navigate(R.id.nav_home)
        }
    }


}