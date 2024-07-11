package com.fertail.istock.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(

    @field:SerializedName("username")
    var username: String? = null,

    @field:SerializedName("password")
    var password: String? = null,

    @field:SerializedName("grant_type")
    var grantType: String? = null

)