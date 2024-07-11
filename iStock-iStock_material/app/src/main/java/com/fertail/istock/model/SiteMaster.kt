package com.fertail.istock.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class SiteMaster(

	@field:SerializedName("SiteId")
	val siteId: String? = null,

	@field:SerializedName("HighLevelLocation")
	var highLevelLocation: String? = null,

	@field:SerializedName("Label")
	val label: String? = null,

	@field:SerializedName("Cluster")
	val cluster: String? = null,

	@PrimaryKey
	@field:SerializedName("_id")
	var id: String = "",

	@field:SerializedName("Islive")
	val islive: Boolean? = null
)
