package com.fertail.istock.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("access_token")
	val accessToken: String? = null,

	@field:SerializedName("token_type")
	val tokenType: String? = null,

	@field:SerializedName("expires_in")
	val expiresIn: Int? = null,

	@field:SerializedName("error")
	val error: String? = null,

	@field:SerializedName("error_description")
	val errorDescription: String? = null
)
