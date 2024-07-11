package com.fertail.istock.model

import com.google.gson.annotations.SerializedName

data class NounItem(

	@field:SerializedName("uomlist")
	val uomlist: Any,

	@field:SerializedName("Similaritems")
	val similaritems: Any,

	@field:SerializedName("ModifierDefinition")
	val modifierDefinition: String,

	@field:SerializedName("Nounabv")
	val nounabv: String,

	@field:SerializedName("Formatted")
	val formatted: Int,

	@field:SerializedName("ModifierEqu")
	val modifierEqu: Any,

	@field:SerializedName("ImageId")
	val imageId: String,

	@field:SerializedName("UpdatedOn")
	val updatedOn: String,

	@field:SerializedName("_id")
	val id: String,

	@field:SerializedName("Modifierabv")
	val modifierabv: Any,

	@field:SerializedName("NounEqu")
	val nounEqu: Any,

	@field:SerializedName("Modifier")
	val modifier: String,

	@field:SerializedName("FileData")
	val fileData: Any,

	@field:SerializedName("NounDefinition")
	val nounDefinition: Any,

	@field:SerializedName("RP")
	val rP: Any,

	@field:SerializedName("Noun")
	val noun: String
)
