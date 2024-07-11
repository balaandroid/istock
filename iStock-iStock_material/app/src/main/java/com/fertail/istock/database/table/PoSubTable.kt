package com.fertail.istock.database.table


import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.*
import com.google.gson.annotations.SerializedName

@Entity
data class PoSubTable(

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String,

    @ColumnInfo(name = "type")
    var type: String? = null,

    @ColumnInfo(name = "po_id")
    var po_id: String? = null,

    @ColumnInfo(name = "material")
    var material: String? = null,

    @ColumnInfo(name = "noOfItem")
    var noOfItem: String? = null,

    @ColumnInfo(name = "equipmentName")
    var equipmentName: String? = null,

    @ColumnInfo(name = "equipmentModelNo")
    var equipmentModelNo: String? = null,

    @ColumnInfo(name = "equipmentManufacture")
    var equipmentManufacture: String? = null,

    @ColumnInfo(name = "equipmentTagNo")
    var equipmentTagNo: String? = null,

    @ColumnInfo(name = "equipmentSerialNo")
    var equipmentSerialNo: String? = null,

    @ColumnInfo(name = "equipmentAdditionalInformation")
    var equipmentAdditionalInformation: String? = null,


    @ColumnInfo(name = "vendorName")
    var vendorName: String? = null,

    @ColumnInfo(name = "vendorPartNo")
    var vendorPartNo: String? = null,

    @ColumnInfo(name = "vendorModelNo")
    var vendorModelNo: String? = null,

    @ColumnInfo(name = "description")
    var description: String? = null

)

@Entity
data class AssertImageTable(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "imgid")
    var id: Int? = null,

    @ColumnInfo(name = "po_id")
    var po_id: String? = null,

    @ColumnInfo(name = "image_url")
    var image_url: String? = null
)

@Entity
data class NamePlateImageTable(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "imgid")
    var id: Int? = null,

    @ColumnInfo(name = "po_id")
    var po_id: String? = null,

    @ColumnInfo(name = "image_url")
    var image_url: String? = null
)

@Entity
data class NamePlateTextTable(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "imgid")
    var id: Int? = null,

    @ColumnInfo(name = "po_id")
    var po_id: String? = null,

    @ColumnInfo(name = "text")
    var text: String? = null
)

@Entity(primaryKeys = ["id", "imgid"])
data class subpoandAssertImageCrossRef(
    val id: Long,
    val imgid: Long
)

@Entity(primaryKeys = ["id", "imgid"])
data class subPoandNamePlateCrossRef(
    val id: Long,
    val imgid: Long
)

@Entity(primaryKeys = ["id", "imgid"])
data class subPoandNamePlateTextCrossRef(
    val id: Long,
    val imgid: Long
)

data class SubPoWithAsserts(
    @Embedded val subPo: PoSubTable,
    @Relation(
        parentColumn = "id",
        entityColumn = "imgid",
        associateBy = Junction(subpoandAssertImageCrossRef::class)
    )
    val assertImage: List<AssertImageTable>,

    @Relation(
        parentColumn = "id",
        entityColumn = "imgid",
        associateBy = Junction(subPoandNamePlateCrossRef::class)
    )
    val namePlateImage: List<NamePlateImageTable>,

    @Relation(
        parentColumn = "id",
        entityColumn = "imgid",
        associateBy = Junction(subPoandNamePlateTextCrossRef::class)
    )
    val namePlateText: List<NamePlateTextTable>,
)