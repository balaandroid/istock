package com.fertail.istock.ui.verification.adapter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fertail.istock.database.ProjectDeo
import com.fertail.istock.model.AllMasterItem
import kotlinx.coroutines.delay

class MasterPagingSource(
    private val dao: ProjectDeo,
    private val query: String
) : PagingSource<Int, AllMasterItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AllMasterItem> {
        val page = params.key ?: 0

        return try {
            val entities = dao.getAllMaster(params.loadSize, page * params.loadSize, query)

            val tempList :  ArrayList<AllMasterItem> = ArrayList()

            entities.forEach {

              val test =  tempList.filter {   s -> (s.id.equals(it.id))}

                if (test.isEmpty()){
                    tempList.add(it)
                }
            }

            // simulate page loading
            if (page != 0) delay(1000)

            LoadResult.Page(
                data = tempList,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (tempList.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, AllMasterItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}