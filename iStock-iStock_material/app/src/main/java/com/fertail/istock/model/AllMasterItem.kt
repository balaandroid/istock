package com.fertail.istock.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class AllMasterItem(

	@field:SerializedName("AssetDesc")
	val assetDesc: String? = null,

	@field:SerializedName("Label")
	val label: String? = null,

	@field:SerializedName("Region")
	val region: String? = null,

	@PrimaryKey
	@field:SerializedName("_id")
	val id: String = "",

	@field:SerializedName("FARId")
	val fARId: String? = null,

	@field:SerializedName("Islive")
	val islive: Boolean? = null
)
