package com.dpkv.color_trading_admin_dpkv.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dpkv.color_trading_admin_dpkv.data.response.ApiResponse
import retrofit2.Response


class BasePagingSource<T : Any, R : Any>(
    private val apiCall: suspend (page: Int, limit: Int) -> Response<ApiResponse<R>>,
    private val mapResponse: (R) -> List<T>
) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: 1
        val limit = params.loadSize

        return try {
            val response = apiCall(page, limit)
            
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true && apiResponse.data != null) {
                    val items = mapResponse(apiResponse.data)
                    
                    LoadResult.Page(
                        data = items,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (items.isEmpty() || items.size < limit) null else page + 1
                    )
                } else {
                    LoadResult.Error(Exception(apiResponse?.error ?: apiResponse?.message ?: "Unknown error"))
                }
            } else {
                LoadResult.Error(Exception("HTTP ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
