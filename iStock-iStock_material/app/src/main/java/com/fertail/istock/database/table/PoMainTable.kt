package com.fertail.istock.database.table

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tbl_po_main")
data class PoMainTable (

    @PrimaryKey
    @ColumnInfo(name = "po_id")
    var po_id: String,

    @ColumnInfo(name = "po_name")
    var po_name: String
)
