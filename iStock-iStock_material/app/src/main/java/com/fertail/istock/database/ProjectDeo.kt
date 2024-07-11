package com.fertail.istock.database

import androidx.room.*
import com.fertail.istock.database.table.*
import com.fertail.istock.model.*
import kotlinx.coroutines.flow.Flow


@Dao
public interface ProjectDeo {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDictionary(dictionary: List<AttributesItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMaster(allMasterItem: List<AllMasterItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSiteMaster(allMasterItem: List<SiteMaster>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocationHierarchyMaster(allMasterItem: List<LocationMaster>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassificationHierarchyMaster(allMasterItem: List<ClassificationHierarchyModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNounItem(dictionary: List<NounTable>)

    @Delete
    suspend fun deleteDictionary(dictionary: List<AttributesItem>)

    @Delete
    suspend fun deleteAllDictionary(dictionary: List<AttributesItem>)

    @Query("SELECT * from AttributesItem LIMIT 100")
    fun observeAllDictionary(): Flow<List<NounTable>>


    @Query("SELECT * from AttributesItem WHERE Noun = :noune ORDER BY modifier ASC" )
    fun getModifier(noune : String): List<AttributesItem>


    @Query("SELECT * from AttributesItem WHERE Noun = :noune AND Modifier = :modifier" )
    fun getAttribute(noune : String, modifier : String): List<AttributesItem>


    @Query("SELECT * FROM AttributesItem ORDER BY localId ASC LIMIT :limit OFFSET :offset")
    suspend fun getDictionary(limit: Int, offset: Int): List<AttributesItem>

    @Query("SELECT * FROM NounTable WHERE noun LIKE '%' || :noun || '%' ORDER BY noun ASC LIMIT :limit OFFSET :offset")
    suspend fun getNoun(limit: Int, offset: Int, noun: String): List<NounTable>


    @Query("SELECT * FROM AllMasterItem WHERE fARId LIKE '%' || :noun || '%' ORDER BY fARId ASC LIMIT :limit OFFSET :offset")
    suspend fun getAllMaster(limit: Int, offset: Int, noun: String): List<AllMasterItem>


    @Query("SELECT * FROM SiteMaster WHERE highLevelLocation LIKE '%' || :noun || '%' ORDER BY highLevelLocation ASC LIMIT :limit OFFSET :offset")
    suspend fun getAllSiteMaster(limit: Int, offset: Int, noun: String): List<SiteMaster>


    @Query("SELECT * FROM LocationMaster WHERE locationHierarchy LIKE '%' || :noun || '%' ORDER BY locationHierarchy ASC LIMIT :limit OFFSET :offset")
    suspend fun getAllLocationHierarchy(limit: Int, offset: Int, noun: String): List<LocationMaster>


    @Query("SELECT * FROM ClassificationHierarchyModel WHERE classificationHierarchyDesc LIKE '%' || :noun || '%' ORDER BY classificationHierarchyDesc ASC LIMIT :limit OFFSET :offset")
    suspend fun getclassificationHierarchyDesc(limit: Int, offset: Int, noun: String): List<ClassificationHierarchyModel>


    @Transaction
    @Query("SELECT * FROM tbl_po_main")
     fun getPoList(): Flow<List<PoMainAndSubTable>>

    @Transaction
    @Query("SELECT * FROM PoSubTable WHERE po_id = :poID")
    fun getSubPoList(poID : String): Flow<List<PoSubTable>>


    @Transaction
    @Query("SELECT * FROM posubtable WHERE id = :poID")
    fun getSubPOById(poID : String): SubPoWithAsserts


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoMain(data: PoMainTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoSub(data: PoSubTable)

    @Query("SELECT * FROM tbl_po_main WHERE po_id = :name")
    suspend fun getPoListById(name : String): List<PoMainAndSubTable>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssertImageTable(data: AssertImageTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNamePlateImage(data: NamePlateImageTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNamePlateText(data: NamePlateTextTable)

    @Query("SELECT * FROM AssertImageTable WHERE po_id = :po_id")
    suspend fun getAssertImageId(po_id : String): List<AssertImageTable>

    @Query("SELECT * FROM NamePlateImageTable WHERE po_id = :po_id")
    suspend fun getNamePlateImageId(po_id : String): List<NamePlateImageTable>


    @Query("SELECT * FROM NamePlateTextTable WHERE po_id = :po_id")
    suspend fun getNamePlateTextId(po_id : String): List<NamePlateTextTable>

}