package com.fertail.istock.internetChecking

interface NetworkCallBack {
    fun onReceive(isInternetPresent: Boolean)
}