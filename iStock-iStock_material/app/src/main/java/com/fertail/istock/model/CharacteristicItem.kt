package com.fertail.istock.model

import com.google.gson.annotations.SerializedName

data class CharacteristicItem(

	@field:SerializedName("UOM")
	val uOM: String? = null,

	@field:SerializedName("ShortSquence")
	val shortSquence: Int? = null,

	@field:SerializedName("SourceUrl")
	val sourceUrl: String? = null,

	@field:SerializedName("Uom")
	val uom: List<String?>? = null,

	@field:SerializedName("Abbrevated")
	val abbrevated: String? = null,

	@field:SerializedName("Characteristic")
	val characteristic: String? = null,

	@field:SerializedName("UomMandatory")
	val uomMandatory: String? = null,

	@field:SerializedName("Value")
	val value: String? = null,

	@field:SerializedName("Squence")
	val squence: Int? = null,

	@field:SerializedName("Source")
	val source: String? = null,

	@field:SerializedName("Approve")
	val approve: Boolean? = null
)
