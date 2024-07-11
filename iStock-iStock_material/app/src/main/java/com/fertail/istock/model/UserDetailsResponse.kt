package com.fertail.istock.model

import com.google.gson.annotations.SerializedName

data class UserDetailsResponse(

	@field:SerializedName("EmailId")
	val emailId: String? = null,

	@field:SerializedName("UserName")
	val userName: String? = null,

	@field:SerializedName("FirstName")
	val firstName: String? = null,

	@field:SerializedName("Modules")
	val modules: Any? = null,

	@field:SerializedName("Roles")
	val roles: Any? = null,

	@field:SerializedName("Mobile")
	val mobile: String? = null,

	@field:SerializedName("Islive")
	val islive: String? = null,

	@field:SerializedName("Departmentname")
	val departmentname: String? = null,

	@field:SerializedName("Lastlogin")
	val lastlogin: String? = null,

	@field:SerializedName("Userid")
	val userid: String? = null,

	@field:SerializedName("ImageId")
	val imageId: Any? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("LastName")
	val lastName: String? = null,

	@field:SerializedName("Createdon")
	val createdon: String? = null,

	@field:SerializedName("Plantcode")
	val plantcode: Any? = null,

	@field:SerializedName("FileData")
	val fileData: Any? = null,

	@field:SerializedName("Password")
	val password: Any? = null
)
