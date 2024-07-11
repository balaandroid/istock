package com.fertail.istock

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.core.app.ActivityCompat.startActivityForResult
import com.fertail.istock.database.BOMListModel
import com.fertail.istock.database.PVDataCompletedModel
import com.fertail.istock.database.PVDataModel
import com.fertail.istock.internetChecking.NetworkCallBack
import com.fertail.istock.internetChecking.NetworkChangeReceiver
import com.fertail.istock.model.BOMModel
import com.fertail.istock.model.PVData
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import org.greenrobot.eventbus.EventBus


@HiltAndroidApp
class iStockApplication : Application() {

    val ACTIVITY_REQUEST_SEND_LOG = 9578
    private lateinit var mBroadcastReceiver: BroadcastReceiver

    companion object {
        var appController: iStockApplication? = null
        lateinit var bOMListModel: BOMListModel
        lateinit var pvDataModel: PVDataModel
        lateinit var pvDataCompletedModel: PVDataCompletedModel
        lateinit var appPreference: AppPreference
        val isWCBURL: Boolean = false // false == acb ||true == wcb


        fun updateBOMList(data: ArrayList<BOMModel>) {
            val gson = Gson()
            val json = gson.toJson(data)
            appPreference.KEY_BOM_List = json
            bOMListModel = BOMListModel(getInstance())
        }

        fun updatePVData(data: ArrayList<PVData>): ArrayList<PVData> {

            val tempList = ArrayList<PVData>()

            tempList.addAll(pvDataModel.data)
            tempList.addAll(pvDataCompletedModel.data)

            for (item in data){

                if (item.noun == null && item.modifier == null){

                    if (item.characteristics != null && item.characteristics.isNotEmpty()){
                        item.characteristics.clear()
                    }
                }

            }

            for (item in data) {
                val newItem: List<PVData> =
                    tempList.filter { s -> s.assetNo == item.assetNo }
                if (newItem.isEmpty()) {
                    pvDataModel.data.add(item)
                }
            }

            val gson = Gson()
            val json = gson.toJson(pvDataModel.data)
            appPreference.KEY_PVData = json
            pvDataModel = PVDataModel(getInstance())

            return pvDataModel.data
        }


        fun saveData(data: ArrayList<PVData>) {
            val gson = Gson()
            val json = gson.toJson(data)
            appPreference.KEY_PVData = json
            pvDataModel = PVDataModel(getInstance())

        }

        fun saveNonCompletedItem(data: PVData){
            var pos = -1
            for (i in 0 until pvDataModel.data.size) {
                if (pvDataModel.data[i].assetNo == data.assetNo) {
                    pos = i
                    break
                }
            }

            if (pos != -1) {
                pvDataModel.data[pos] = data
                val gson = Gson()
                val json = gson.toJson(pvDataModel.data)
                appPreference.KEY_PVData = json
                pvDataModel = PVDataModel(getInstance())
            }
        }

        fun saveCompletedItem(data: PVData) {
            var pos = -1

            for (i in 0 until pvDataCompletedModel.data.size) {
                if (pvDataCompletedModel.data[i].assetNo == data.assetNo) {
                    pos = i
                    break
                }
            }

            if (pos == -1) {
                pvDataCompletedModel.data.add(data)
            } else {
                pvDataCompletedModel.data.set(pos, data)
            }

            val gson = Gson()
            val json = gson.toJson(pvDataCompletedModel.data)
            appPreference.KEY_PVData_Completed_Item = json
            pvDataCompletedModel = PVDataCompletedModel(getInstance())

        }

        fun removeCompletedItem(data: PVData) {

            for (i in 0 until pvDataCompletedModel.data.size) {
                if (pvDataCompletedModel.data[i].assetNo == data.assetNo) {
                    pvDataCompletedModel.data.removeAt(i)
                    break
                }
            }

            val gson = Gson()
            val json = gson.toJson(pvDataCompletedModel.data)
            appPreference.KEY_PVData_Completed_Item = json
            pvDataCompletedModel = PVDataCompletedModel(getInstance())


        }

        @Synchronized
        fun getInstance(): iStockApplication {
            return appController!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        appController = this
        appPreference = AppPreference(this)

        pvDataModel = PVDataModel(this)
        pvDataCompletedModel = PVDataCompletedModel(this)
        bOMListModel = BOMListModel(this)

        mBroadcastReceiver = NetworkChangeReceiver(
            object : NetworkCallBack {
                override fun onReceive(isInernetPresent: Boolean) {
                    if (isInernetPresent) {
                        EventBus.getDefault().post("true")
                    } else {
                        EventBus.getDefault().post("false")
                    }
                }

            })
        registerReceiver(mBroadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        
//        setGlobuleException()
    }

    private fun setGlobuleException() {
        Thread.setDefaultUncaughtExceptionHandler { thread, e ->
            handleUncaughtException(
                thread,
                e
            )
        }
    }

    private fun handleUncaughtException(thread: Thread, e: Throwable) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "plain/text"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("mahamadhavan0105@gmail.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "log file")
        intent.putExtra(Intent.EXTRA_TEXT, e.message)


//        startActivityForResult(intent, ACTIVITY_REQUEST_SEND_LOG)


//        System.exit(1); // kill off the crashed app
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ACTIVITY_REQUEST_SEND_LOG) System.exit(1)
    }






}