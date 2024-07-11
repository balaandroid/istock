package com.fertail.istock.viewstate

import com.fertail.istock.model.LoginResponse

interface LoginViewState {

    fun showLoading()
    fun showSuccess(dlResponse: LoginResponse)
    fun showError(errorMsg: String)
}