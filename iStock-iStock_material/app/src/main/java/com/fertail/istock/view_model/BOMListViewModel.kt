package com.fertail.istock.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fertail.istock.Constant
import com.fertail.istock.api.iStockService
import com.fertail.istock.iStockApplication
import com.fertail.istock.model.BOMModel
import com.fertail.istock.util.NetworkUtils
import kotlinx.coroutines.*

class BOMListViewModel : ViewModel(), Constant {

    val mIStockService = iStockService().getUsersService()

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val showSuccess = MutableLiveData<ArrayList<BOMModel>>()
    val showError = MutableLiveData<String?>()
    val showLoading = MutableLiveData<Boolean>()

    fun getBOMListFromServer(){
        if (NetworkUtils.isConnected(iStockApplication.appController!!)) {
            showLoading.value = true
            job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                val response = mIStockService.getBOMList(iStockApplication.appPreference.KEY_USER_ID)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            showSuccess.value = it
                            iStockApplication.updateBOMList(it)
                        }
                    } else {
                        onError("Error : ${response.message()} ")
                    }

                    showLoading.value = false

                }
            }

        }else {
            onError("Error : ${check_connection_for_download} ")
        }
    }

    fun getBOMList() {
        iStockApplication.bOMListModel.data.let {
            if (it.isEmpty()) {
                getBOMListFromServer()
            }else {
                showSuccess.value = it
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