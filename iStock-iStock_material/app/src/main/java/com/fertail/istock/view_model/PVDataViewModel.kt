package com.fertail.istock.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fertail.istock.Constant
import com.fertail.istock.api.iStockService
import com.fertail.istock.iStockApplication
import com.fertail.istock.model.PVData
import com.fertail.istock.util.NetworkUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*

class PVDataViewModel : ViewModel(), Constant {

    val mIStockService = iStockService().getUsersService()

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val showSuccess = MutableLiveData<ArrayList<PVData>>()
    val getCurrentLocSuccess = MutableLiveData<PVData>()
    val showError = MutableLiveData<String?>()
    val showLoading = MutableLiveData<Boolean>()

    fun getPVDataFromServer(){
        if (NetworkUtils.isConnected(iStockApplication.appController!!)) {
            showLoading.value = true
            job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                val response = mIStockService.getPVData(iStockApplication.appPreference.KEY_USER_NAME)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            iStockApplication.updatePVData(it).let {
                            showSuccess.value = it
                        } }

                    } else {
                        onError("Error : ${response.message()} ")
                    }

                    showLoading.value = false

                }
            }

        }else {
            onError("Error : $check_connection_for_download ")
        }
    }

    fun getPVData() {
        iStockApplication.pvDataModel.data.let {
            if (it.isEmpty()) {
               getPVDataFromServer()
            }else {
                showSuccess.value = it
            }
        }
    }

    fun getCurrentLoc() {
        if (NetworkUtils.isConnected(iStockApplication.appController!!))  {
            showLoading.value = true
            job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                val response = mIStockService.getCurrentLoc(iStockApplication.appPreference.KEY_USER_ID)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val gson = Gson()
                        iStockApplication.appPreference.KEY_PVData_master = gson.toJson(response.body())
                        getCurrentLocSuccess.value = response.body()
                        showLoading.value = false
                    } else {
                        onError("Error : ${response.message()}")
                    }
                }
            }
        }else {
            if ( iStockApplication.appPreference.KEY_PVData_master.equals("")){
                onError("Error : $check_connection_for_download")
            }else {
                val gson = Gson()
                val myType = object : TypeToken<PVData>() {}.type
                getCurrentLocSuccess.value = gson.fromJson<PVData>(iStockApplication.appPreference.KEY_PVData_master, myType)
            }
        }
    }

    private fun onError(message: String) {
        showError.postValue(message)
        showLoading.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}