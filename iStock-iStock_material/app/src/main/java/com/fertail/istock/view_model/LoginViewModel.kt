package com.fertail.istock.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fertail.istock.api.iStockService
import com.fertail.istock.model.LoginRequest
import com.fertail.istock.model.LoginResponse
import com.fertail.istock.model.UserDetailsResponse
import kotlinx.coroutines.*
import javax.net.ssl.SSLHandshakeException

class LoginViewModel() : ViewModel() {

    val mIStockService = iStockService().getUsersService()

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loginResponse = MutableLiveData<LoginResponse>()
    val userDetailsResponse = MutableLiveData<UserDetailsResponse>()
    val loginError = MutableLiveData<String?>()
    val loading = MutableLiveData<Boolean>()

    fun login(request: LoginRequest) {
        loading.postValue(true)
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mIStockService.login(request.username!!, request.password!!, request.grantType!!)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    loginResponse.postValue(response.body())
                    loading.postValue(false)
                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }


    fun getUserInfo(userName: String) {
        loading.postValue(true)
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mIStockService.getUserInfo(userName)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    userDetailsResponse.postValue(response.body())
                    loading.postValue(false)
                } else {
                    onError("Error : ${response.message()} ")
                }

            }
        }
    }


    private fun onError(message: String) {
        loginError.postValue(message)
        loading.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}