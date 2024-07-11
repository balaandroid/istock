package com.fertail.istock.database

import android.content.Context
import com.fertail.istock.iStockApplication
import com.fertail.istock.model.BOMModel
import com.fertail.istock.model.PVData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject


class BOMListModel @Inject constructor(context: Context) {

    private val mJSon : String
    var data = ArrayList<BOMModel>()

    companion object {
        private val PREFERENCE_NAME = "iStock_PREFERENCE"
    }

    init {
        mJSon = iStockApplication.appPreference.KEY_BOM_List

        if (!mJSon.equals("")) {

            val gson = Gson()
            val myType = object : TypeToken<ArrayList<BOMModel>>() {}.type
            data = gson.fromJson<ArrayList<BOMModel>>(mJSon, myType)

        }

    }
}