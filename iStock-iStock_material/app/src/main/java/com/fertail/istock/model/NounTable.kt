package com.fertail.istock.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class NounTable(

	@field:SerializedName("Characteristic")
	val characteristic: String? = null,

	@field:SerializedName("Definition")
	var definition: String? = null,
//
//	@field:SerializedName("Values")
//	val values: List<String?>? = null,

	@field:SerializedName("Abbrivation")
	val abbrivation: String? = null,

	@field:SerializedName("ShortSquence")
	val shortSquence: Int? = null,

//	@field:SerializedName("Uom")
//	val uom: List<String?>? = null,

	@field:SerializedName("UomMandatory")
	val uomMandatory: String? = null,

	@field:SerializedName("UpdatedOn")
	val updatedOn: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,


	@PrimaryKey(autoGenerate = true)
	@field:SerializedName("localId")
	val localId: Int? = null,

	@field:SerializedName("Squence")
	val squence: Int? = null,

	@field:SerializedName("Modifier")
	val modifier: String? = null,

	@field:SerializedName("Noun")
	val noun: String? = null,

	@field:SerializedName("Mandatory")
	val mandatory: String? = null
)
