package com.fertail.istock.model

import com.google.gson.annotations.SerializedName

data class EquipmentItem(

	@field:SerializedName("Modelno")
	var modelno: String = "",

	@field:SerializedName("Serialno")
	var serialno: String = "",

	@field:SerializedName("Tagno")
	var tagno: String = "",

	@field:SerializedName("Additionalinfo")
	var additionalinfo: String = "",

	@field:SerializedName("Manufacturer")
	var manufacturer: String = "",

	@field:SerializedName("EMS")
	val eMS: Int? = null,

	@field:SerializedName("ENS")
	val eNS: Int? = null,

	@field:SerializedName("Name")
    var name: String = ""
)
