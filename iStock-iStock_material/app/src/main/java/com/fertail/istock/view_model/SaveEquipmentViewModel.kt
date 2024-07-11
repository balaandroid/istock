package com.fertail.istock.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fertail.istock.api.iStockService
import com.fertail.istock.iStockApplication
import com.fertail.istock.model.*
import com.google.gson.Gson
import kotlinx.coroutines.*

class SaveEquipmentViewModel : ViewModel() {

    val mIStockService = iStockService().getUsersService()

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val showSuccess = MutableLiveData<String>()
    val showError = MutableLiveData<String?>()
    val showLoading = MutableLiveData<Boolean>()

    fun saveEquipment(body : ArrayList<PVData>) {
        showLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mIStockService.saveEquipment(body)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
//                    val gson = Gson()
//                    val json = gson.toJson(response.body())
//                    iStockApplication.appPreference.KEY_All_Master = json
                    showSuccess.value = response.body()
                    showLoading.value = false
                } else {
                    onError("Error : ${response.message()} ")
                }

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