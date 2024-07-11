package com.fertail.istock.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fertail.istock.database.table.*
import com.fertail.istock.model.*


@Database(
    version = 3,
    entities = [AttributesItem::class, NounTable::class, PoMainTable::class ,
        AllMasterItem::class,
        SiteMaster::class,
        LocationMaster::class,
        AssetTypeMaster::class,
        ClassificationHierarchyModel::class,
        PoSubTable::class, AssertImageTable::class,
        NamePlateTextTable::class, NamePlateImageTable::class,
        subpoandAssertImageCrossRef::class, subPoandNamePlateCrossRef::class,
        subPoandNamePlateTextCrossRef::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDictionary(): ProjectDeo
}