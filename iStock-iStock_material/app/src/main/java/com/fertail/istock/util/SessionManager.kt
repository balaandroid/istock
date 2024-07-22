package com.fertail.istock.util

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SessionManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MySession", Context.MODE_PRIVATE)


    fun setUriArrayList(key: String, arrayList: MutableList<Uri>) {
        val gson = Gson()
        val uriStrings = arrayList.map { it.toString() }
        val json = gson.toJson(uriStrings)
        sharedPreferences.edit().putString(key, json).apply()
    }

    fun getUriArrayList(key: String): MutableList<Uri>? {
        val gson = Gson()
        val json = sharedPreferences.getString(key, null)
        val uriStringList: MutableList<String>? = gson.fromJson(json, object : TypeToken<MutableList<String>>() {}.type)
        return uriStringList?.map { Uri.parse(it) }?.toMutableList()
    }


    fun setUriArrayListEquipment(key: String, arrayList: MutableList<Uri>) {
        val gson = Gson()
        val uriStrings = arrayList.map { it.toString() }
        val json = gson.toJson(uriStrings)
        sharedPreferences.edit().putString(key, json).apply()
    }

    fun getUriArrayListEquipment(key: String): MutableList<Uri>? {
        val gson = Gson()
        val json = sharedPreferences.getString(key, null)
        val uriStringList: MutableList<String>? = gson.fromJson(json, object : TypeToken<MutableList<String>>() {}.type)
        return uriStringList?.map { Uri.parse(it) }?.toMutableList()
    }

}