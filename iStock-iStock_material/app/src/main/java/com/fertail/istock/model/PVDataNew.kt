package com.fertail.istock.model

import com.google.gson.annotations.SerializedName

data class PVDataNew(

	@field:SerializedName("Sysbal")
	val sysbal: String? = null,

	@field:SerializedName("Pysbal")
	val pysbal: String? = null,

	@field:SerializedName("PysicalObservationQty")
	val pysicalObservationQty: String? = null,

	@field:SerializedName("PysicalObservation")
	val pysicalObservation: String? = null,

	@field:SerializedName("ExpiredDate")
	val expiredDate: String? = null,

	@field:SerializedName("StockRemark")
	val stockRemark: String? = null,

	@field:SerializedName("UId")
	val uId: Int? = null,

	@field:SerializedName("MaterialDesc")
	val materialDesc: String? = null,

	@field:SerializedName("Storagebin")
	val storagebin: String? = null,

	@field:SerializedName("Additioninfo")
	val additioninfo: String? = null,

	@field:SerializedName("CreatedOn")
	val createdOn: String? = null,

	@field:SerializedName("NameplateImgs")
	val nameplateImgs: List<String?>? = null,

	@field:SerializedName("BinUpdation")
	val binUpdation: String? = null,

	@field:SerializedName("QtyasonDate")
	val qtyasonDate: String? = null,

	@field:SerializedName("ExpiredQty")
	val expiredQty: String? = null,

	@field:SerializedName("Mode")
	val mode: String? = null,

	@field:SerializedName("Make")
	val make: String? = null,

	@field:SerializedName("Sapcode")
	val sapcode: String? = null,

	@field:SerializedName("SelfLife")
	val selfLife: String? = null,

	@field:SerializedName("Itemcode")
	val itemcode: String? = null,

	@field:SerializedName("GRDate")
	val gRDate: String? = null,

	@field:SerializedName("UOM")
	val uOM: String? = null,

	@field:SerializedName("NameplateText")
	val nameplateText: List<String?>? = null,

	@field:SerializedName("Username")
	val username: String? = null,

	@field:SerializedName("DataCollection")
	val dataCollection: String? = null,

	@field:SerializedName("Model")
	val model: String? = null,

	@field:SerializedName("MatImgs")
	val matImgs: List<String?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)
