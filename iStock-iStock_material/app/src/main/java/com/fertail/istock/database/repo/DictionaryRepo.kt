package com.fertail.istock.database.repo

import androidx.annotation.WorkerThread
import com.fertail.istock.database.ProjectDeo
import com.fertail.istock.model.AttributesItem
import com.fertail.istock.model.NounTable
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DictionaryRepo @Inject constructor (private val wordDao: ProjectDeo){

    // Observed Flow will notify the observer when the data has changed.
    val allDictionary: Flow<List<NounTable>> = wordDao.observeAllDictionary()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(filter: List<AttributesItem>) {
        wordDao.insertDictionary(filter)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteList(filter: List<AttributesItem>) {
        wordDao.deleteDictionary(filter)
    }
}