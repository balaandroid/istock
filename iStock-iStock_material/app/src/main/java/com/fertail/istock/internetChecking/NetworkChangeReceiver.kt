package com.fertail.istock.internetChecking

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log


class NetworkChangeReceiver(private val mNetworkCallBack: NetworkCallBack): BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            mNetworkCallBack.onReceive(isConnectedToInternet(it))
        }
    }


    private fun isConnectedToInternet(context: Context): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected == true
        } catch (e: Exception) {
            Log.e(NetworkChangeReceiver::class.java.name, e.message ?: "Unknown error")
            false
        }
    }

}