package com.fertail.istock.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fertail.istock.api.iStockService
import com.fertail.istock.model.LoginRequest
import com.fertail.istock.model.LoginResponse
import kotlinx.coroutines.*

class LocalMasterViewModel  : ViewModel() {

    val mIStockService = iStockService().getUsersService()

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val localMasterResponse = MutableLiveData<List<LoginResponse>>()
    val localMasterError = MutableLiveData<String?>()
    val loading = MutableLiveData<Boolean>()

    fun getLocalMaster() {
        loading.postValue(true)
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mIStockService.getLocalMaster()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    localMasterResponse.value = response.body()
                    loading.postValue(false)
                } else {
                    onError("Error : ${response.message()} ")
                }

            }
        }
    }

    private fun onError(message: String) {
        localMasterError.postValue(message)
        loading.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}