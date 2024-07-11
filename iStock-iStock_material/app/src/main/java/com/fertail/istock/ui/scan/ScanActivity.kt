package com.fertail.istock.ui.scan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.fertail.istock.BaseActivity
import com.fertail.istock.DashboardActivity
import com.fertail.istock.VerificationDetailsActivity
import com.fertail.istock.databinding.ActivityDashboardBinding
import com.fertail.istock.databinding.ActivityScanBinding
import com.fertail.istock.ui.ScannerActivity
import com.fertail.istock.ui.dataclass.actionSelected

class ScanActivity : BaseActivity()  {

    private lateinit var binding: ActivityScanBinding

    companion object {
        fun start(caller: Context){
            val intent = Intent(caller, ScanActivity::class.java)
            caller.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initOnCreate()
    }

    private fun initOnCreate(){
        binding.idCreateNew.setOnClickListener {
            ScannerActivity.start(this, object : actionSelected {
                override fun optionChosen(action: String, from: String) {

                }
            })
        }
    }
}