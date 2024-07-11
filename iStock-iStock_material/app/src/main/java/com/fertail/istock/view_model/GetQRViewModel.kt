package com.fertail.istock.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.fertail.istock.Constant
import com.fertail.istock.api.iStockService
import com.fertail.istock.database.ProjectDeo
import com.fertail.istock.iStockApplication
import com.fertail.istock.model.AttributesItem
import com.fertail.istock.model.ModifierItem
import com.fertail.istock.model.NounItem
import com.fertail.istock.model.NounTable
import com.fertail.istock.model.PVData
import com.fertail.istock.ui.verification.adapter.MyPagingSource
import com.fertail.istock.util.NetworkUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class GetQRViewModel @Inject constructor(private val database : ProjectDeo) : ViewModel() ,
    Constant {

    val mIStockService = iStockService().getUsersService()

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val showSuccess = MutableLiveData<PVData>()
    val showError = MutableLiveData<String?>()
    val showLoading = MutableLiveData<Boolean>()

    fun getQRData(code: String) {
        if (!NetworkUtils.isConnected(iStockApplication.appController!!)) {
            onError("Error : $check_connection_for_download ")
            return
        }

        showLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mIStockService.getScannerData(code)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        showSuccess.value = it
                    }
                } else {
                    onError("Error : ${response.message()} ")
                }

                showLoading.value = false

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