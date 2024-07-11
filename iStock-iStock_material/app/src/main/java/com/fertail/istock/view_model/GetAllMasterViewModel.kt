package com.fertail.istock.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.fertail.istock.api.iStockService
import com.fertail.istock.database.ProjectDeo
import com.fertail.istock.iStockApplication
import com.fertail.istock.model.AllMasterItem
import com.fertail.istock.ui.verification.adapter.MasterPagingSource
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class GetAllMasterViewModel @Inject constructor(private val database : ProjectDeo) : ViewModel() {

    val mIStockService = iStockService().getUsersService()

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val showSuccess = MutableLiveData<ArrayList<AllMasterItem>>()
    val showError = MutableLiveData<String?>()
    val showLoading = MutableLiveData<Boolean>()

    var queryTxt = "";

    fun setQueryText(query : String){
        queryTxt = query;
    }

    val data = Pager(
        PagingConfig(
            pageSize = 100,
            enablePlaceholders = false,
            initialLoadSize = 100
        ),
    ) {
        MasterPagingSource(database, queryTxt)
    }.flow.cachedIn(viewModelScope)

    fun getAllMaster() {
        showLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mIStockService.getAllMaster()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {


                    response.body()?.let {
                        database.insertAllMaster(it)
                    }


                    val gson = Gson()
                    val json = gson.toJson(response.body())
                    iStockApplication.appPreference.KEY_All_Master = json
                    showSuccess.postValue(response.body())
                    showLoading.postValue(false)
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