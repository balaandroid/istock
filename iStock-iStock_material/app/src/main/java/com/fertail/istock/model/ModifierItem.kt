package com.fertail.istock.model

import com.google.gson.annotations.SerializedName

data class ModifierItem(

	@field:SerializedName("uomlist")
	val uomlist: Any? = null,

	@field:SerializedName("Similaritems")
	val similaritems: Any? = null,

	@field:SerializedName("ModifierDefinition")
	val modifierDefinition: String? = null,

	@field:SerializedName("Nounabv")
	val nounabv: String? = null,

	@field:SerializedName("Formatted")
	val formatted: Int? = null,

	@field:SerializedName("ModifierEqu")
	val modifierEqu: Any? = null,

	@field:SerializedName("ImageId")
	val imageId: String? = null,

	@field:SerializedName("UpdatedOn")
	val updatedOn: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("Modifierabv")
	val modifierabv: Any? = null,

	@field:SerializedName("NounEqu")
	val nounEqu: Any? = null,

	@field:SerializedName("Modifier")
	val modifier: String? = null,

	@field:SerializedName("FileData")
	val fileData: Any? = null,

	@field:SerializedName("NounDefinition")
	val nounDefinition: Any? = null,

	@field:SerializedName("RP")
	val rP: Any? = null,

	@field:SerializedName("Noun")
	val noun: String? = null
)
