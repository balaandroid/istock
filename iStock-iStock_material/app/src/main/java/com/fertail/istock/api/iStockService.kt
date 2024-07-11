package com.fertail.istock.api

import com.fertail.istock.iStockApplication
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class iStockService {

//    private val BASE_URL = "https://mdm.prosolonline.com/"
//    private val BASE_URL = "https://fewa.prosolonline.com/"
//    private val BASE_URL = "http://scm.codasol.net/"
//    private val BASE_URL = "https://qcm.prosolonline.com/"
//    private val BASE_URL = "https://acb.prosolonline.com/"
    private val BASE_URL = "https://adportsgroup.prosolonline.com/"

//    private val BASE_URL_WCB = "https://wcb.prosolonline.com/";
    private val BASE_URL_WCB = "https://adportsgroup.prosolonline.com/";
//    private val BASE_URL = "https://testing.prosolonline.com/";
    fun getUsersService(): iStockApi{

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(OAuthInterceptor("Bearer", iStockApplication.appPreference.KEY_ACCESS_TOKEN))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl( if (iStockApplication.isWCBURL) {BASE_URL_WCB}else {BASE_URL})
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(iStockApi::class.java)
    }


    fun getBaseUrl() : String{
        return  if (iStockApplication.isWCBURL) {BASE_URL_WCB}else {BASE_URL}
    }

}