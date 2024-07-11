package com.fertail.istock.api

import okhttp3.Interceptor

class OAuthInterceptor(private val tokenType: String, private val accessToken: String) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        var request = chain.request()
        request = request.newBuilder().header("Authorization", "Bearer $accessToken").build()
        return chain.proceed(request)
    }
}