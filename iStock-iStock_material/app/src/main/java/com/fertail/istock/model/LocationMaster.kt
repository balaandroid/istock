package com.fertail.istock.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class LocationMaster(

	@field:SerializedName("IsActive")
	val isActive: Boolean? = null,

	@field:SerializedName("Area_Id")
	val areaId: String? = null,

	@field:SerializedName("LocationCode")
	val locationCode: String? = null,

	@field:SerializedName("LocationHierarchy")
	val locationHierarchy: String? = null,

	@PrimaryKey
	@field:SerializedName("_id")
	val id: String = "",

	@field:SerializedName("Location")
	val location: String? = null,

	@field:SerializedName("Islive")
	val islive: Boolean? = null
)
