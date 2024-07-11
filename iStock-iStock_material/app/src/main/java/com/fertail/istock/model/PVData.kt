package com.fertail.istock.model

import com.google.gson.annotations.SerializedName

data class PVData(

    @field:SerializedName("Manufacture")
    var manufacture: String? = null,

    @field:SerializedName("MaximoAssetInfo")
    var maximoAssetInfo: String? = null,

    @field:SerializedName("Rework")
    var rework: String? = null,

    @field:SerializedName("Ownedby")
    var mOwnedBy: String? = null,

    @field:SerializedName("Rework_Remarks")
    var mReworkRemarks: String? = null,

    @field:SerializedName("Maintainer")
    var mMaintainer: String? = null,

    @field:SerializedName("Operatedby")
    var mOperatedBy: String? = null,

    @field:SerializedName("Nos_Of_AsestMaximo")
    var nosOfAsestMaximo: String? = null,

    @field:SerializedName("No_of_Contract")
    var noOfContract: String? = null,

    @field:SerializedName("BarcodeNumber")
    var barcodeNumber: String? = null,

    @field:SerializedName("ExistingBarCode")
    var existingBarCode: String? = null,


    @field:SerializedName("PresentLocation")
    var presentLocation: String? = null,

    @field:SerializedName("AvailabilityofAsset")
    var availabilityofAsset: String? = null,

    @field:SerializedName("pv_voice_record")
    var pvVoiceRecord: String? = null,

    @field:SerializedName("FixedAssetNo")
    var fixedAssetNo: String? = null,

    @field:SerializedName("Vendorsuppliers")
    val vendorsuppliers: Vendorsuppliers = Vendorsuppliers(),


    @field:SerializedName("Observations")
    val observations: ArrayList<Observations> =ArrayList(),

    @field:SerializedName("MainWork_Center")
    val mainWorkCenter: Any? = null,
    
    @field:SerializedName("VerificateionDate")
    var mVerificateionDate: String? = null,

    
    @field:SerializedName("VerifiedBy")
    var mVerifiedBy: String? = null,

    
    @field:SerializedName("FuncLocDesc")
    val funcLocDesc: String? = null,

    
    @field:SerializedName("Description")
    var equipmentDesc: String? = null,


    @field:SerializedName("Parent")
    var parent: String? = null,


    @field:SerializedName("Location")
    var location: String? = null,

    @field:SerializedName("LocationHierarchy")
    var locationHierarchy: String? = null,


    @field:SerializedName("ClassificationHierarchyDesc")
    var classificationHierarchyDesc: String? = null,


    
    @field:SerializedName("ItemStatus")
    val itemStatus: Int? = null,

    
    @field:SerializedName("SiteName")
    val siteName: String? = null,

    
    @field:SerializedName("AssetNo")
    var assetNo: String? = null,

    @field:SerializedName("pvassetNo")
    var pvassetNo: String? = null,

    
    @field:SerializedName("EquipmentType")
    var equipmentType: String? = null,

    
    @field:SerializedName("EquipmentTypeID")
    var equipmentTypeID: String? = null,

    
    @field:SerializedName("EquipmentTypeCode")
    var equipmentTypeCode: String? = null,

    
    @field:SerializedName("Depreciation_Year")
    val depreciationYear: Any? = null,

    
    @field:SerializedName("Department")
    val department: Any? = null,

    
    @field:SerializedName("Identifier")
    var identifier: String? = null,

    
    @field:SerializedName("IdentifierID")
    var identifierID: String? = null,

    
    @field:SerializedName("IdentifierCode")
    var identifierCode: String? = null,

    
    @field:SerializedName("NewTagNo")
    var newTagNo: String? = null,


    @field:SerializedName("C_Remark")
    var cRemarks: String? = null,

    @field:SerializedName("Remarks")
    var remarks: String? = null,

    @field:SerializedName("assetConditionRemarks")
    var assetConditionRemarks: String? = null,

    
    @field:SerializedName("UpdatedOn")
    val updatedOn: String? = null,

    
    @field:SerializedName("PlannerGroup")
    val plannerGroup: Any? = null,

    
    @field:SerializedName("Transaction_date")
    val transactionDate: String? = null,

    
    @field:SerializedName("ABC_Indicator")
    val aBCIndicator: Any? = null,

    
    @field:SerializedName("MaintanancePlant")
    val maintanancePlant: Any? = null,

    
    @field:SerializedName("SubArea")
    var subArea: String? = null,

    
    @field:SerializedName("SubAreaID")
    var subAreaID: String? = null,

    
    @field:SerializedName("SubAreaCode")
    var subAreaCode: String? = null,

    
    @field:SerializedName("Business")
    var business: String? = null,

    
    @field:SerializedName("businessCode")
    var businessCode: String? = null,

    
    @field:SerializedName("businessID")
    var businessID: String? = null,

    
    @field:SerializedName("UniqueId")
    var uniqueId: String? = null,

    
    @field:SerializedName("BOM")
    val bOM: String? = null,

    
    @field:SerializedName("SerialNo")
    var serialNo: String? = null,

    
    @field:SerializedName("Salvage_ResidualValue")
    val salvageResidualValue: Any? = null,

    
    @field:SerializedName("Addition")
    val addition: Any? = null,

    
    @field:SerializedName("PVuser")
    var pVuser: PVuser? = null,

    
    @field:SerializedName("YearOfMfr")
    val yearOfMfr: Any? = null,

    
    @field:SerializedName("AssetSubNo")
    val assetSubNo: String? = null,

    
    @field:SerializedName("City")
    var city: String? = null,


    
    @field:SerializedName("CityID")
    var cityID: String? = null,

    
    @field:SerializedName("CityICode")
    var cityCode: String? = null,

    
    @field:SerializedName("Warranty_ExpiryDate")
    val warrantyExpiryDate: String? = null,

    
    @field:SerializedName("GIS")
    var gIS: GIS = GIS(),

    
    @field:SerializedName("Area")
    var area: String? = null,

    
    @field:SerializedName("AreaID")
    var areaID: String? = null,

    
    @field:SerializedName("AreaCode")
    var areaCode: String? = null,

    
    @field:SerializedName("Barcode")
    val barcode: String? = null,

    
    @field:SerializedName("Equipment_Long")
    val equipmentLong: Any? = null,

    
    @field:SerializedName("Region")
    var region: String? = null,

    
    @field:SerializedName("regionID")
    var regionID: String? = null,

    
    @field:SerializedName("regionCode")
    var regionCode: String? = null,

    
    @field:SerializedName("_id")
    var id: String? = null,

    
    @field:SerializedName("OldTagNo")
    var oldTagNo: String? = null,

    
    @field:SerializedName("AssetCondition")
    var assetCondition: AssetCondition = AssetCondition(),

    
    @field:SerializedName("SiteId")
    var siteId: String? = null,

    
    @field:SerializedName("Depreciation_Startdate")
    val depreciationStartdate: String? = null,

    
    @field:SerializedName("OpeningBalance")
    val openingBalance: Any? = null,

    
    @field:SerializedName("Equipment_Short")
    val equipmentShort: Any? = null,

    
    @field:SerializedName("Idle_Operational")
    var idleOperational: String? = null,

    
    @field:SerializedName("Accumulated_Depreciation")
    val accumulatedDepreciation: Any? = null,

    
    @field:SerializedName("PlanningPlant")
    val planningPlant: Any? = null,

    
    @field:SerializedName("MinorClass")
    var minorClass: String? = null,

    
    @field:SerializedName("MinorClassID")
    var minorClassID: String? = null,

    
    @field:SerializedName("MinorClassCode")
    var minorClassCode: String? = null,

    
    @field:SerializedName("ObjectType")
    val objectType: String? = null,

    
    @field:SerializedName("Function")
    val function: String? = null,

    
    @field:SerializedName("EquipmentClass")
    var equipmentClass: String? = null,

    
    @field:SerializedName("EquipmentClassID")
    var equipmentClassID: String? = null,

    
    @field:SerializedName("EquipmentClassCode")
    var equipmentClassCode: String? = null,

    
    @field:SerializedName("Plant")
    val plant: Any? = null,

    
    @field:SerializedName("CostCenter")
    val costCenter: Any? = null,

    
    @field:SerializedName("SiteType")
    val siteType: String? = null,

    
    @field:SerializedName("SAP_Equipment")
    val sAPEquipment: String? = null,

    
    @field:SerializedName("FLOC_Code")
    val fLOCCode: String? = null,

    
    @field:SerializedName("YearOfInstall")
    val yearOfInstall: String? = null,

    
    @field:SerializedName("CreatedOn")
    val createdOn: String? = null,

    
    @field:SerializedName("Functional_Loc")
    var functionalLoc: String? = null,

    
    @field:SerializedName("Functional_LocID")
    var functionalLocID: String? = null,

    
    @field:SerializedName("Functional_LocCode")
    var functionalLocCode: String? = null,

    @field:SerializedName("Storage_Bin1")
    var stroageBin: String? = null,

    @field:SerializedName("Storage_Location1")
    var stroageLocation: String? = null,
    
    @field:SerializedName("AssetImages")
    var assetImages: AssetImages = AssetImages(),

    
    @field:SerializedName("Retirement")
    val retirement: Any? = null,

    
    @field:SerializedName("MajorClass")
    var majorClass: String? = null,

    
    @field:SerializedName("majorID")
    var majorID: String? = null,

    
    @field:SerializedName("majorCode")
    var majorCode: String? = null,

    
    @field:SerializedName("ObjectId")
    val objectId: String? = null,

    
    @field:SerializedName("Quantity")
    val quantity: String? = null,

    
    @field:SerializedName("ModelNo")
    var modelNo: String? = null,

    @field:SerializedName("ModelYear")
    var modelYear: String? = null,


    @field:SerializedName("AssCondition")
    var assCondition: String? = null,


    @field:SerializedName("Catalogue")
    val catalogue: Catalogue? = null,

    
    @field:SerializedName("PlantSection")
    val plantSection: Any? = null,


    
    @field:SerializedName("NetBook_Value")
    val netBookValue: Any? = null,

    
    @field:SerializedName("AssetBuilding")
    var assetBuilding: AssetBuilding = AssetBuilding(),

    
    @field:SerializedName("CompanyCode")
    val companyCode: String? = null,

    
    @field:SerializedName("itemType")
    var itemType: String = "Existing",

    
    @field:SerializedName("isNewOne")
    var isNewOne: Boolean = false,

    @field:SerializedName("isSelected")
    var isSelected: Boolean = false,

    @field:SerializedName("isSaved")
    var isSaved: Boolean = false,

    
    @field:SerializedName("tempbomModel")
    val bomModel: ArrayList<BOMModel> = ArrayList(),


    @field:SerializedName("Sysbal") // yes
    var sysbal: String? = null,

    @field:SerializedName("Pysbal") // yes
    var pysbal: String? = null,

    @field:SerializedName("PysicalObservationQty")
    var pysicalObservationQty: String? = null,

    @field:SerializedName("PysicalObservation")
    var pysicalObservation: String? = null,

    @field:SerializedName("ExpiredDate")
    var expiredDate: String? = null,

    @field:SerializedName("StockRemark")
    var stockRemark: String? = null,

    @field:SerializedName("UId") // yes
    val uId: Int? = null,

    @field:SerializedName("MaterialDesc") // yes
    var materialDesc: String? = null,

    @field:SerializedName("Storagebin") // yes
    var storagebin: String? = null,

    @field:SerializedName("Additioninfo")
    var additioninfo: String? = null,



    @field:SerializedName("Characteristics")
    val characteristics: ArrayList<AttributesItem> = ArrayList(),


    @field:SerializedName("BinUpdation")
    var binUpdation: String? = null,

    @field:SerializedName("Noun")
    var noun: String? = null,

    @field:SerializedName("Modifier")
    var modifier: String? = null,

    @field:SerializedName("QtyasonDate")
    var qtyasonDate: String? = null,

    @field:SerializedName("ExpiredQty")
    var expiredQty: String? = null,

    @field:SerializedName("Mode")
    val mode: String? = null,

    @field:SerializedName("Make")
    var make: String? = null,

    @field:SerializedName("Sapcode") // yes
    var sapcode: String? = null,

    @field:SerializedName("SelfLife")
    var selfLife: String? = null,

    @field:SerializedName("Itemcode") //Yes
    var itemcode: String? = null,

    @field:SerializedName("GRDate")
    var gRDate: String? = null,

    @field:SerializedName("UOM")
    var uOM: String? = null,



    @field:SerializedName("Username") // yes
    val username: String? = null,

    @field:SerializedName("DataCollection")
    val dataCollection: String? = null,

    @field:SerializedName("Model")
    var model: String? = null,


    @field:SerializedName("Status")
    var status: String? = null,

    @field:SerializedName("AssetType")
    var assetType: String? = null,

    @field:SerializedName("ReportGroup")
    var reportGroup: String? = null,

    @field:SerializedName("category")
    val category: String? = null,

    @field:SerializedName("Equipment")
    var equipment: EquipmentItem = EquipmentItem(),
    var action : String? = null
)

data class Vendorsuppliers(
    @field:SerializedName("Manufacture")
    var manufacture: String? = null,

    @field:SerializedName("PartNo")
    var partNo: String? = null,

    @field:SerializedName("ModelNo")
    var modelNo: String? = null,

    @field:SerializedName("SerialNo")
    var serialNo: String? = null,

    @field:SerializedName("TagNo")
    var tagNo: String? = null,
)

data class Observations (
    @field:SerializedName("Observation")
    var observation: String? = null,

    @field:SerializedName("Qty")
    var qty: String? = null,

    var isLast: Boolean = false,

    var id : Int = 0
)


data class PVuser(

    @field:SerializedName("UserId")
    var userId: String? = null,

    @field:SerializedName("UpdatedOn")
    val updatedOn: String? = null,

    @field:SerializedName("Name")
    var name: String? = null
)

data class Catalogue(

    @field:SerializedName("UserId")
    val userId: String? = null,

    @field:SerializedName("UpdatedOn")
    val updatedOn: String? = null,

    @field:SerializedName("Name")
    val name: String? = null
)

data class GIS(

    @field:SerializedName("LattitudeStart")
    var lattitudeStart: String? = null,

    @field:SerializedName("LattitudeEnd")
    var lattitudeEnd: String? = null,

    @field:SerializedName("Lat_LongLength")
    val latLongLength: Any? = null,

    @field:SerializedName("LongitudeStart")
    var longitudeStart: String? = null,

    @field:SerializedName("LongitudeEnd")
    var longitudeEnd: String? = null
)

data class AssetCondition(

    @field:SerializedName("Leakage")
    var leakage: String? = null,

    @field:SerializedName("Vibration")
    var vibration: String? = null,

    @field:SerializedName("CorrosionImage")
    val corrosionImage: ArrayList<String> = ArrayList(),


    @field:SerializedName("Corrosion")
    var corrosion: String? = null,

    @field:SerializedName("Noise")
    var noise: String? = null,

    @field:SerializedName("LeakageImage")
    val leakageImage: ArrayList<String> = ArrayList(),

    @field:SerializedName("DamageImage")
    val damageImage: ArrayList<String> = ArrayList(),


    @field:SerializedName("Rank")
    var rank: String? = null,

    @field:SerializedName("Damage")
    var damage: String? = null,

    @field:SerializedName("Temparature")
    var temparature: String? = null,


    @field:SerializedName("TemparatureImage")
    val temparatureImage: ArrayList<String> = ArrayList(),


    @field:SerializedName("AssetCondition")
    var assetCondition: String? = null,

    @field:SerializedName("Smell")
    var smell: String? = null


)

data class AssetImages(

    @field:SerializedName("NewTagImage")
    val newTagImage: ArrayList<String> = ArrayList(),

    @field:SerializedName("AssetImage")
    val assetImage: ArrayList<String> = ArrayList(),

    @field:SerializedName("NamePlateText")
    val namePlateText: ArrayList<String>? = ArrayList(),

    @field:SerializedName("NamePlateImge")
    val namePlateImge: ArrayList<String>? = ArrayList(),

    @field:SerializedName("NamePlateTextTwo")
    val namePlateTextTwo: ArrayList<String>? = ArrayList(),

    @field:SerializedName("NamePlateImgeTwo")
    var namePlateImgeTwo: ArrayList<String> = ArrayList(),

    @field:SerializedName("OldTagImage")
    val oldTagImage: ArrayList<String>? = ArrayList(),

    @field:SerializedName("MatImgs")
    var matImgs: ArrayList<String> = ArrayList(),

    @field:SerializedName("NameplateImgs")
    val nameplateImgs: ArrayList<String> = ArrayList(),


)

data class AssetBuilding(

    @field:SerializedName("Length")
    var length: String? = null,

    @field:SerializedName("BuildingName")
    var buildingName: String? = null,

    @field:SerializedName("BuildingImage")
    val buildingImage: ArrayList<String> = ArrayList(),

    @field:SerializedName("Height")
    var height: String? = null,

    @field:SerializedName("BuildingId")
    var buildingId: String? = null,

    @field:SerializedName("Width")
    var width: String? = null,

    @field:SerializedName("Location")
    var location: String? = null
)


