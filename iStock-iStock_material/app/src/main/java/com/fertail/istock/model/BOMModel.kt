package com.fertail.istock.model

import com.google.gson.annotations.SerializedName

data class BOMModel(

	@field:SerializedName("UniqueId")
	var uniqueId: String? = null,

	@field:SerializedName("Description")
	val description: String? = null,

	@field:SerializedName("EquipmentId")
	var equipmentId: String? = null,

	@field:SerializedName("BOMName")
	var bOMName: String? = null,

	@field:SerializedName("UOM")
	var uOM: String? = null,

	@field:SerializedName("TagNo")
	var tagNo: String? = null,

	@field:SerializedName("Barcode")
	var barcode: String? = null,

	@field:SerializedName("Qty")
	var qty: String? = null,

	@field:SerializedName("OldTagImg")
	var oldTagImg: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("NamePlateImg")
	var namePlateImg: String? = null,

	@field:SerializedName("NamePlateText")
	var namePlateText: String? = null,

	@field:SerializedName("OldTag")
	var oldTag: String? = null,

	@field:SerializedName("BarCodeImg")
	var barCodeImg: String? = null,

	@field:SerializedName("BOMImg")
	var bOMImg: String? = null
)
