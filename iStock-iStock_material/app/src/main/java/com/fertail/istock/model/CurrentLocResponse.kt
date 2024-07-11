package com.fertail.istock.model

import com.google.gson.annotations.SerializedName

data class CurrentLocResponse(

	@field:SerializedName("CityCode")
	val cityCode: Any? = null,

	@field:SerializedName("RegionCode")
	val regionCode: Any? = null,

	@field:SerializedName("BusinessCode")
	val businessCode: Any? = null,

	@field:SerializedName("FunctionCode")
	val functionCode: Any? = null,

	@field:SerializedName("City")
	val city: Any? = null,

	@field:SerializedName("EquipmentType")
	val equipmentType: Any? = null,

	@field:SerializedName("Function")
	val function: String? = null,

	@field:SerializedName("IdentifierCode")
	val identifierCode: Any? = null,

	@field:SerializedName("Area")
	val area: String? = null,

	@field:SerializedName("EquipmentClass")
	val equipmentClass: Any? = null,

	@field:SerializedName("Major")
	val major: String? = null,

	@field:SerializedName("Identifier")
	val identifier: String? = null,

	@field:SerializedName("SubAreaCode")
	val subAreaCode: Any? = null,

	@field:SerializedName("Region")
	val region: String? = null,

	@field:SerializedName("AreaCode")
	val areaCode: Any? = null,

	@field:SerializedName("EquipmentTypeCode")
	val equipmentTypeCode: Any? = null,

	@field:SerializedName("Minor")
	val minor: Any? = null,

	@field:SerializedName("SubArea")
	val subArea: String? = null,

	@field:SerializedName("Business")
	val business: String? = null
)
