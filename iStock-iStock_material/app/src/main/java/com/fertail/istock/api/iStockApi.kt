package com.fertail.istock.api

import com.fertail.istock.model.*
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*

interface iStockApi {

    @FormUrlEncoded
    @POST("istock/api/GetToken")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grantType: String
    ): Response<LoginResponse>

    @GET("istock/api/FunLoc/GetLocMaster")
    suspend fun getLocalMaster(): Response<ArrayList<LoginResponse>>

    @GET("istock/api/GetPVdata")
    suspend fun getPVData(@Query("UserName") userID: String): Response<ArrayList<PVData>>

    @GET("istock/api/GetUserInfo")
    suspend fun getUserInfo(@Query("UserName") username: String): Response<UserDetailsResponse>

    @GET("istock/api/getCurrentLoc")
    suspend fun getCurrentLoc(@Query("UserID") userID: String): Response<PVData>

    @GET("istock/api/GetAllMaster")
    suspend fun getAllMaster(): Response<ArrayList<AllMasterItem>>

    @GET("istock/api/getSiteMaster")
    suspend fun getSiteMaster(): Response<ArrayList<SiteMaster>>

    @GET("istock/api/getLocationMaster")
    suspend fun getLocationHierarchyMaster(): Response<ArrayList<LocationMaster>>


    @GET("istock/api/getAssetTypeMaster")
    suspend fun getAssetTypeMaster(): Response<ArrayList<ClassificationHierarchyModel>>

    @POST("istock/api/Savedata")
    suspend fun saveEquipment(@Body data: ArrayList<PVData>): Response<String>

    @GET("istock/api/GetBOMList")
    suspend fun getBOMList(@Query("UserID") userID: String): Response<ArrayList<BOMModel>>


    @GET("istock/api/Getnoun")
    suspend fun getNoun(): Response<ArrayList<NounItem>>

    @GET("istock/api/GetModifier")
    suspend fun getModifier(@Query("Noun") noun: String): Response<ArrayList<ModifierItem>>

    @GET("istock/api/GetAttributes")
    suspend fun getAttributes(
        @Query("Noun") noun: String,
        @Query("Modifier") modifier: String
    ): Response<ArrayList<AttributesItem>>

    @GET("istock/api/getDictionary")
    suspend fun getDictionary(
    ): Response<ArrayList<AttributesItem>>


    @GET("istock/api/GetQrData")
    suspend fun getScannerData(
        @Query("qrCode") code: String
    ): Response<PVData>

}