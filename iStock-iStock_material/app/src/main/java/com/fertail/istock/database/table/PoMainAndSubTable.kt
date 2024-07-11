package com.fertail.istock.database.table

import androidx.room.Embedded
import androidx.room.Relation

data class PoMainAndSubTable(
    @Embedded val poMain: PoMainTable,
    @Relation(
        parentColumn = "po_id",
        entityColumn = "po_id"
    )
    val poSubList: List<PoSubTable>
)
