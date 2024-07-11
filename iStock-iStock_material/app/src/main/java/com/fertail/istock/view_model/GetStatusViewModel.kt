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
import com.fertail.istock.model.SiteMaster
import com.fertail.istock.ui.verification.adapter.SitePagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class GetStatusViewModel @Inject constructor(private val database : ProjectDeo) : ViewModel() {

    val mIStockService = iStockService().getUsersService()

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val showSuccess = MutableLiveData<ArrayList<SiteMaster>>()
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
        SitePagingSource(database, queryTxt)
    }.flow.cachedIn(viewModelScope)

    fun getAllSiteMaster() {
        showLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val allMasterItem: ArrayList<SiteMaster> = ArrayList()

            var model = SiteMaster();
            model.id = "1"
            model.highLevelLocation = "Active"

            allMasterItem.add(model)

            model = SiteMaster();
            model.id = "2"
            model.highLevelLocation = "Inactive"

            allMasterItem.add(model)

            database.insertSiteMaster(allMasterItem)

            iStockApplication.appPreference.KEY_SITE_MAster = "Added"

            showSuccess.postValue(allMasterItem)
            showLoading.postValue(false)

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