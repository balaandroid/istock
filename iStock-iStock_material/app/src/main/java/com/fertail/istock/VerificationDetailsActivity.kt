package com.fertail.istock

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.fertail.istock.databinding.ActivityVerificationDetailsBinding
import com.fertail.istock.model.PVData

class VerificationDetailsActivity : BaseActivity() {
    lateinit var binding: ActivityVerificationDetailsBinding

    companion object {
         var mPVData = PVData()
        fun start(caller: Context, data: PVData){
            val intent = Intent(caller, VerificationDetailsActivity::class.java)
            mPVData = data
            caller.startActivity(intent)
            // test commit
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onPause() {
//        iStockApplication.saveCompletedItem(mPVData)
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}