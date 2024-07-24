package com.fertail.istock.util

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException


class SessionManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MySession", Context.MODE_PRIVATE)


    fun setUriArrayListEquipmentNew(key: String, arrayList: List<UriWithDate>) {
        val gson = createGson()
        val json = gson.toJson(arrayList)
        sharedPreferences.edit().putString(key, json).apply()
    }


    fun getUriArrayListEquipmentNew(key: String): MutableList<UriWithDate>? {
        val gson = createGson()
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<UriWithDate>>() {}.type
            gson.fromJson<MutableList<UriWithDate>>(json, type)?.toMutableList()
        } else {
            null
        }
    }


    fun setUriArrayListNew( key: String, list: List<UriWithDate>) {
        val gson = createGson()
        val json = gson.toJson(list)
        sharedPreferences.edit().putString(key, json).apply()
    }


    fun getUriArrayListNew(key: String): MutableList<UriWithDate>? {
        val gson = createGson()
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<UriWithDate>>() {}.type
            gson.fromJson<MutableList<UriWithDate>>(json, type)?.toMutableList()
        } else {
            null
        }
    }

    private fun createGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(Uri::class.java, UriTypeAdapter())
            .create()
    }

}


data class UriWithDate(val uri: String, val date: String)

class UriTypeAdapter : TypeAdapter<Uri>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Uri?) {
        out.value(value?.toString())
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): Uri? {
        return Uri.parse(`in`.nextString())
    }
}



