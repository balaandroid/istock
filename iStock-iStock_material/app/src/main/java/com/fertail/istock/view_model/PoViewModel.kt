package com.fertail.istock.view_model

import androidx.lifecycle.ViewModel
import com.fertail.istock.database.ProjectDeo
import com.fertail.istock.database.table.*
import com.fertail.istock.model.NounTable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PoViewModel @Inject constructor(private val database: ProjectDeo) : ViewModel() {

    fun getAllPO(): Flow<List<PoMainAndSubTable>> {
        return database.getPoList()
    }

    fun getAllSubPO(id : String): Flow<List<PoSubTable>> {
        return database.getSubPoList(id)
    }

    fun getSubPOById(id : String): SubPoWithAsserts {
        return database.getSubPOById(id)
    }

    suspend fun getAllPOById(name : String): List<PoMainAndSubTable> {
        return database.getPoListById(name)
    }

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
//        onError("Exception handled: ${throwable.localizedMessage}")
    }

    fun addPoToTable(data: PoMainTable) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            database.insertPoMain(data)
        }
    }

    fun addSubPoToTable(data: PoSubTable) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            database.insertPoSub(data)
        }

    }

    fun saveAssertImage(absolutePath: String, id: String) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            var data = AssertImageTable()
            data.image_url = absolutePath
            data.po_id = id
            database.insertAssertImageTable(data)
        }
    }

    fun saveNamePlateImage(absolutePath: String, id: String) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            var data = NamePlateImageTable()
            data.image_url = absolutePath
            data.po_id = id
            database.insertNamePlateImage(data)
        }
    }

    fun saveNamePlateText(dsc: String, id: String) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            var data = NamePlateTextTable()
            data.text = dsc
            data.po_id = id
            database.insertNamePlateText(data)
        }
    }

    suspend fun getAssertImageId(name : String): List<AssertImageTable> {
        return database.getAssertImageId(name)
    }


    suspend fun getNamePlateImageId(name : String): List<NamePlateImageTable> {
        return database.getNamePlateImageId(name)
    }
    suspend fun getNamePlateTextId(name : String): List<NamePlateTextTable> {
        return database.getNamePlateTextId(name)
    }
}