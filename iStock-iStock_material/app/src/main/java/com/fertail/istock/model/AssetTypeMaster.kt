package com.fertail.istock.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class AssetTypeMaster(

	@field:SerializedName("AssetType")
	val assetType: String? = null,

	@field:SerializedName("ClassificationHierarchyDesc")
	val classificationHierarchyDesc: String? = null,

	@field:SerializedName("Label")
	val label: String? = null,

	@field:SerializedName("FailureCode")
	val failureCode: String? = null,

	@PrimaryKey
	@field:SerializedName("_id")
	val id: String = "",

	@field:SerializedName("Islive")
	val islive: Boolean? = null
)
