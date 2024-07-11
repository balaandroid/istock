package com.fertail.istock.model

import com.google.gson.annotations.SerializedName

data class GetAllMasterDataResponse(

	@field:SerializedName("MajorClasses")
	val majorClasses: List<MajorClassesItem> = ArrayList(),


	@field:SerializedName("PhysicalObs")
	val physicalObs: List<PhysicalObsItem> = ArrayList(),

	@field:SerializedName("MinorClasses")
	val minorClasses: List<MinorClassesItem> = ArrayList(),

	@field:SerializedName("SubAreas")
	val subAreas: List<SubAreasItem> = ArrayList(),

	@field:SerializedName("Locations")
	val locations: List<LocationsItem> = ArrayList(),

	@field:SerializedName("Regions")
	val regions: List<RegionsItem> = ArrayList(),

	@field:SerializedName("Businesses")
	val businesses: List<BusinessesItem> = ArrayList(),

	@field:SerializedName("EquipmentClasses")
	val equipmentClasses: List<EquipmentClasses> = ArrayList(),

	@field:SerializedName("EquipmentTypes")
	val equipmentTypes: List<EquipmentTypes> = ArrayList(),

	@field:SerializedName("Cities")
	val cities: List<CitiesItem> = ArrayList(),

	@field:SerializedName("Identifiers")
	val identifiers: List<IdentifiersItem> = ArrayList(),

	@field:SerializedName("Areas")
	val areas: List<AreasItem> = ArrayList(),
)

data class PhysicalObsItem(

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("Physical_Observation")
	val physicalObservation: String? = null,

)

data class SubAreasItem(

	@field:SerializedName("SubAreaCode")
	val subAreaCode: String? = null,

	@field:SerializedName("IsActive")
	val isActive: Any? = null,

	@field:SerializedName("Area_Id")
	val areaId: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("SubArea")
	val subArea: String? = null
)

data class IdentifiersItem(

	@field:SerializedName("IdentifierCode")
	val identifierCode: String? = null,

	@field:SerializedName("Identifier")
	val identifier: String? = null,

	@field:SerializedName("IsActive")
	val isActive: Any? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("fClass_Id")
	val fClassId: String? = null
)

data class BusinessesItem(

	@field:SerializedName("BusinessName")
	val businessName: String? = null,

	@field:SerializedName("IsActive")
	val isActive: Any? = null,

	@field:SerializedName("BusinessCode")
	val businessCode: String? = null,

	@field:SerializedName("_id")
	val id: String? = null
)

data class EquipmentClasses(

	@field:SerializedName("EquipmentCode")
	val equipmentCode: String? = null,

	@field:SerializedName("IsActive")
	val isActive: Any? = null,

	@field:SerializedName("EquipmentClass")
	val equipmentClass: String? = null,

	@field:SerializedName("_id")
	val id: String? = null
)

data class EquipmentTypes(

	@field:SerializedName("EquClass_Id")
	val equClass_Id: String? = null,

	@field:SerializedName("IsActive")
	val isActive: Any? = null,

	@field:SerializedName("EquTypeCode")
	val equTypeCode: String? = null,

	@field:SerializedName("EquipmentType")
	val equipmentType: String? = null,

	@field:SerializedName("_id")
	val id: String? = null
)

data class RegionsItem(

	@field:SerializedName("RegionCode")
	val regionCode: String? = null,

	@field:SerializedName("IsActive")
	val isActive: Any? = null,

	@field:SerializedName("Region")
	val region: String? = null,

	@field:SerializedName("_id")
	val id: String? = null
)

data class MinorClassesItem(

	@field:SerializedName("IsActive")
	val isActive: Any? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("Major_id")
	val majorId: String? = null,

	@field:SerializedName("MinorClass")
	val minorClass: String? = null,

	@field:SerializedName("MinorCode")
	val minorCode: String? = null
)

data class CitiesItem(

	@field:SerializedName("CityCode")
	val cityCode: String? = null,

	@field:SerializedName("Region_Id")
	val regionId: String? = null,

	@field:SerializedName("IsActive")
	val isActive: Any? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("City")
	val city: String? = null
)

data class MajorClassesItem(

	@field:SerializedName("IsActive")
	val isActive: Any? = null,

	@field:SerializedName("MajorCode")
	val majorCode: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("Business_id")
	val businessId: String? = null,

	@field:SerializedName("MajorClass")
	val majorClass: String? = null
)

data class LocationsItem(

	@field:SerializedName("IsActive")
	val isActive: Any? = null,

	@field:SerializedName("Area_Id")
	val areaId: String? = null,

	@field:SerializedName("LocationCode")
	val locationCode: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("Location")
	val location: String? = null
)

data class AreasItem(

	@field:SerializedName("Area")
	val area: String? = null,

	@field:SerializedName("City_Id")
	val cityId: String? = null,

	@field:SerializedName("IsActive")
	val isActive: Any? = null,

	@field:SerializedName("AreaCode")
	val areaCode: String? = null,

	@field:SerializedName("_id")
	val id: String? = null
)
