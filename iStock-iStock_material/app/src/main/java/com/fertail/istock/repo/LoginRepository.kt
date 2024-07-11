package com.fertail.istock.repo

import com.fertail.istock.BaseUseCase
import com.fertail.istock.api.iStockApi
import com.fertail.istock.model.LoginRequest
import com.fertail.istock.model.LoginResponse
import io.reactivex.Observable
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class LoginRepository @Inject constructor(private val iStockApi: iStockApi): BaseUseCase<LoginResponse>() {
//
//    fun login(data: LoginRequest): Observable<LoginResponse> {
////        return iS/ockApi.login(data)
//    }

    override fun createObservable(data: HashMap<String, Any>?): Observable<LoginResponse> {
        TODO("Not yet implemented")
    }
}