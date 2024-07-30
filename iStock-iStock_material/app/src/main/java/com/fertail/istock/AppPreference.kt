package com.fertail.istock

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.fertail.istock.model.PVData
import com.google.gson.Gson
import javax.inject.Inject

class AppPreference @Inject constructor(context: Context) {

    private val preference: SharedPreferences

    companion object {
        private val PREFERENCE_NAME = "iStock_PREFERENCE"
        private val _KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN"
        private val _KEY_ACCESS_TOKEN_TYPE = "KEY_ACCESS_TOKEN_TYPE"
        private val _KEY_IS_LOGGED_IN = "KEY_IS_LOGGED_IN"
        private val _KEY_PVData = "_KEY_PVData"
        private val _KEY_PVData_Completed_Item = "_KEY_PVData_Completed_Item"
        private val _KEY_USER_NAME = "_KEY_USER_NAME"
        private val _KEY_USER_PASSWORD = "_KEY_USER_PASSWORD"
        private val _KEY_USER_ID = "_KEY_USER_ID"
        private val _KEY_All_Master = "KEY_All_Master"
        private val _KEY_USER_DETAILS = "_KEY_USER_DETAILS"
        private val _KEY_PVData_master = "_KEY_PVData_master"
        private val _KEY_BOM_List = "_KEY_BOM_List"
        private val _KEY_SITE_MAster = "_KEY_SITE_MAster"
        private val _LocationHierarchy = "LocationHierarchy"
        private val _AssetTypeMaster = "AssetTypeMaster"


    }

    init {
        preference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }


    /*---------------------------------------------------------Clear Preference -----------------------------------------------------------*/
    fun clearAppPreference() {

        preference.edit().putString(_KEY_ACCESS_TOKEN, "").apply()
        preference.edit().putString(_KEY_USER_ID, "").apply()
        preference.edit().putBoolean(_KEY_IS_LOGGED_IN, false).apply()
        preference.edit().putString(_KEY_USER_NAME, "").apply()
        preference.edit().putString(_KEY_USER_PASSWORD, "").apply()
        preference.edit().putString(_KEY_PVData, "").apply()
        preference.edit().putString(_KEY_PVData_Completed_Item, "").apply()
        preference.edit().putString(_KEY_All_Master, "").apply()
        preference.edit().putString(_KEY_PVData_master, "").apply()
        preference.edit().putString(KEY_ACCESS_TOKEN_TYPE, "").apply()


        iStockApplication.pvDataCompletedModel.data.clear()
        iStockApplication.pvDataModel.data.clear()
        iStockApplication.bOMListModel.data.clear()

        preference.edit().clear().apply()
        preference.edit().commit()
    }

    var KEY_ACCESS_TOKEN: String
        set(value) {
            preference.edit().putString(_KEY_ACCESS_TOKEN, value).apply()
        }
        get() {
            return preference.getString(_KEY_ACCESS_TOKEN, "")!!
        }





    var KEY_USER_ID: String
        set(value) {
            preference.edit().putString(_KEY_USER_ID, value).apply()
        }
        get() {
            return preference.getString(_KEY_USER_ID, "")!!
        }

    var KEY_ACCESS_TOKEN_TYPE: String
        set(value) {
            preference.edit().putString(_KEY_ACCESS_TOKEN_TYPE, value).apply()
        }
        get() {
            return preference.getString(_KEY_ACCESS_TOKEN_TYPE, "")!!
        }

    var KEY_IS_LOGGED_IN: Boolean
        set(value) {
            preference.edit().putBoolean(_KEY_IS_LOGGED_IN, value).apply()
        }
        get() {
            return preference.getBoolean(_KEY_IS_LOGGED_IN, false)
        }


    var KEY_USER_NAME: String
        set(value) {
            preference.edit().putString(_KEY_USER_NAME, value).apply()
        }
        get() {
            return preference.getString(_KEY_USER_NAME, "")!!
        }


    var KEY_USER_PASSWORD: String
        set(value) {
            preference.edit().putString(_KEY_USER_PASSWORD, value).apply()
        }
        get() {
            return preference.getString(_KEY_USER_PASSWORD, "")!!
        }


    var KEY_PVData: String
        set(value) {
            preference.edit().putString(_KEY_PVData, value).apply()
        }
        get() {
            return preference.getString(_KEY_PVData, "")!!
        }

    var KEY_PVData_Completed_Item: String
        set(value) {
            preference.edit().putString(_KEY_PVData_Completed_Item, value).apply()
        }
        get() {
            return preference.getString(_KEY_PVData_Completed_Item, "")!!
        }


    var KEY_USER_DETAILS: String
        set(value) {
            preference.edit().putString(_KEY_USER_DETAILS, value).apply()
        }
        get() {
            return preference.getString(_KEY_USER_DETAILS, "")!!
        }

    var KEY_All_Master: String
        set(value) {
            preference.edit().putString(_KEY_All_Master, value).apply()
        }
        get() {
            return preference.getString(_KEY_All_Master, "")!!
        }

    var KEY_PVData_master: String
        set(value) {
            preference.edit().putString(_KEY_PVData_master, value).apply()
        }
        get() {
            return preference.getString(_KEY_PVData_master, "")!!
        }

    var KEY_BOM_List: String
        set(value) {
            preference.edit().putString(_KEY_BOM_List, value).apply()
        }
        get() {
            return preference.getString(_KEY_BOM_List, "")!!
        }



    var KEY_SITE_MAster: String
        set(value) {
            preference.edit().putString(_KEY_SITE_MAster, value).apply()
        }
        get() {
            return preference.getString(_KEY_SITE_MAster, "")!!
        }



    var LocationHierarchy: String
        set(value) {
            preference.edit().putString(_LocationHierarchy, value).apply()
        }
        get() {
            return preference.getString(_LocationHierarchy, "")!!
        }

    var AssetTypeMaster: String
        set(value) {
            preference.edit().putString(_AssetTypeMaster, value).apply()
        }
        get() {
            return preference.getString(_AssetTypeMaster, "")!!
        }


    //*------------------------------------ Locale ---------------------------------------------------------------*//


}