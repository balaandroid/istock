package com.fertail.istock.model

import com.google.gson.annotations.SerializedName

data class LocalMasterResponse(

	@field:SerializedName("SiteId")
	val siteId: String? = null,

	@field:SerializedName("User")
	val user: Any? = null,

	@field:SerializedName("RegionCode")
	val regionCode: Any? = null,

	@field:SerializedName("SiteName")
	val siteName: String? = null,

	@field:SerializedName("BusinessCode")
	val businessCode: Any? = null,

	@field:SerializedName("EquipmentType")
	val equipmentType: String? = null,

	@field:SerializedName("Function")
	val function: String? = null,

	@field:SerializedName("IdentifierCode")
	val identifierCode: Any? = null,

	@field:SerializedName("EquipmentClass")
	val equipmentClass: String? = null,

	@field:SerializedName("Identifier")
	val identifier: String? = null,

	@field:SerializedName("SiteType")
	val siteType: String? = null,

	@field:SerializedName("CreatedOn")
	val createdOn: Any? = null,

	@field:SerializedName("SubArea")
	val subArea: String? = null,

	@field:SerializedName("Functional_Loc")
	val functionalLoc: String? = null,

	@field:SerializedName("Business")
	val business: String? = null,

	@field:SerializedName("FuncLoc_Hierarchy")
	val funcLocHierarchy: String? = null,

	@field:SerializedName("CreatedBy")
	val createdBy: Any? = null,

	@field:SerializedName("FunctionCode")
	val functionCode: Any? = null,

	@field:SerializedName("Objecttype")
	val objecttype: String? = null,

	@field:SerializedName("Islive")
	val islive: Boolean? = null,

	@field:SerializedName("Area")
	val area: String? = null,

	@field:SerializedName("Major")
	val major: String? = null,

	@field:SerializedName("SubAreaCode")
	val subAreaCode: Any? = null,

	@field:SerializedName("Region")
	val region: String? = null,

	@field:SerializedName("AreaCode")
	val areaCode: Any? = null,

	@field:SerializedName("EquipmentTypeCode")
	val equipmentTypeCode: Any? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("Minor")
	val minor: String? = null,

	@field:SerializedName("CompanyCode")
	val companyCode: String? = null
)
