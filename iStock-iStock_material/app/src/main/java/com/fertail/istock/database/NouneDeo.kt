package com.fertail.istock.database

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fertail.istock.model.NounTable

interface NouneDeo {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNounItem(dictionary: List<NounTable>)


    @Query("SELECT * FROM NounTable ORDER BY localId ASC LIMIT :limit OFFSET :offset")
    suspend fun getNoun(limit: Int, offset: Int): List<NounTable>
}