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
import com.fertail.istock.model.AttributesItem
import com.fertail.istock.model.ModifierItem
import com.fertail.istock.model.NounItem
import com.fertail.istock.model.NounTable
import com.fertail.istock.ui.verification.adapter.MyPagingSource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class GetNounViewModel @Inject constructor(private val database : ProjectDeo) : ViewModel() {

    val mIStockService = iStockService().getUsersService()

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val showSuccess = MutableLiveData<ArrayList<NounItem>>()
    val showSuccessModifier = MutableLiveData<ArrayList<ModifierItem>>()
    val showSuccessAttributes = MutableLiveData<ArrayList<AttributesItem>>()
    val showError = MutableLiveData<String?>()
    val showLoading = MutableLiveData<Boolean>()

    fun getAllDictionary(): Flow<List<NounTable>> {
       return database.observeAllDictionary()
    }

    var queryTxt = ""

    fun setQueryText(query : String){
        queryTxt = query
    }


    val data = Pager(
        PagingConfig(
            pageSize = 100,
            enablePlaceholders = false,
            initialLoadSize = 100
        ),
    ) {
        MyPagingSource(database, queryTxt)
    }.flow.cachedIn(viewModelScope)

    fun getAllMaster() {
        showLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mIStockService.getNoun()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
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

    fun getModifier(noun: String) : ArrayList<AttributesItem>{
        val item : ArrayList<AttributesItem> = ArrayList()
        showLoading.value = true

        database.getModifier(noun).let {
            item.addAll(it)
        }

        showLoading.postValue(false)

        return item

//        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
//            val response = mIStockService.getModifier(noun)
//            withContext(Dispatchers.Main) {
//                if (response.isSuccessful) {
//                    val gson = Gson()
//                    val json = gson.toJson(response.body())
//                    iStockApplication.appPreference.KEY_All_Master = json
//                    showSuccessModifier.postValue(response.body())
//                    showLoading.postValue(false)
//                } else {
//                    onError("Error : ${response.message()} ")
//                }
//
//            }
//        }
    }

    fun getAttributes(noun: String, modifier:String) {
        showLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            database.getAttribute(noun, modifier).let {
                val item : ArrayList<AttributesItem> = ArrayList()
                item.addAll(it)
                showSuccessAttributes.postValue(item)
                showLoading.postValue(false)
            }

//            val response = mIStockService.getAttributes(noun, modifier)
//            withContext(Dispatchers.Main) {
//                if (response.isSuccessful) {
//                    val gson = Gson()
//                    val json = gson.toJson(response.body())
//                    iStockApplication.appPreference.KEY_All_Master = json
//                    showSuccessAttributes.postValue(response.body())
//                    showLoading.postValue(false)
//                } else {
//                    onError("Error : ${response.message()} ")
//                }
//
//            }
        }
    }

    fun getAllClasificationHierarchy() {
        showLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mIStockService.getAssetTypeMaster()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    response.body()?.let {
                        database.insertClassificationHierarchyMaster(it)
                    }

                    val gson = Gson()
                    val json = gson.toJson(response.body())
                    iStockApplication.appPreference.AssetTypeMaster = json
//                    showSuccess.postValue(response.body())
                    showLoading.postValue(false)
                } else {
                    onError("Error : ${response.message()} ")
                }

            }
        }
    }


    fun getAllLocationHierarchy() {
        showLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mIStockService.getLocationHierarchyMaster()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    response.body()?.let {
                        database.insertLocationHierarchyMaster(it)
                    }

                    val gson = Gson()
                    val json = gson.toJson(response.body())
                    iStockApplication.appPreference.LocationHierarchy = json
//                    showSuccess.postValue(response.body())
                    showLoading.postValue(false)
                } else {
                    onError("Error : ${response.message()} ")
                }

            }
        }
    }



    fun getDictionary() {
        showLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mIStockService.getDictionary()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
//                    val gson = Gson()
//                    val json = gson.toJson(response.body())


                    val gson = Gson()
                    val json = gson.toJson(response.body())
                    val myType = object : TypeToken<ArrayList<NounTable>>() {}.type
                    val nounItem = gson.fromJson<ArrayList<NounTable>>(
                        json,
                        myType
                    )

                    nounItem.let {
                        val tempList :  ArrayList<NounTable> = ArrayList()

                        it.forEach {

                            val test =  tempList.filter {   s -> (s.noun.equals(it.noun))}

                            if (test.isEmpty()){
                                tempList.add(it)
                            }
                        }

                        database.insertNounItem(tempList)
                    }




//                    iStockApplication.appPreference.KEY_All_Master = json
//                    showSuccessAttributes.postValue(response.body())
                    showLoading.postValue(false)
                    response.body()?.let {
                        database.insertDictionary(it)
                        }
                } else {
                    onError("Error : ${response.message()} ")
                }

            }
        }
    }

    fun getAllMasterDetail() {
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
//                    showSuccess.postValue(response.body())
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