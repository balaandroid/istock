package com.fertail.istock.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.fertail.istock.database.table.PoMainAndSubTable

@Dao
interface PoDeo {

    @Transaction
    @Query("SELECT * FROM tbl_po_main")
    fun getUsersWithPlaylists(): List<PoMainAndSubTable>
}