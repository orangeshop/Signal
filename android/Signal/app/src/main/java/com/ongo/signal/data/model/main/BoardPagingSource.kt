package com.ongo.signal.data.model.main

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ongo.signal.data.repository.main.board.BoardRepository
import timber.log.Timber
import javax.inject.Inject

class BoardPagingSource @Inject constructor(
    private val boardRepository: BoardRepository,
    private val tag: String? = null
) : PagingSource<Int, BoardDTO>() {
    override fun getRefreshKey(state: PagingState<Int, BoardDTO>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BoardDTO> {
        val nextPage = params.key ?: 0
        val pageSize = params.loadSize
        Timber.tag("pager").d("Requested page: $nextPage, Load size: $pageSize")

        return try {
            val response = if (tag.isNullOrEmpty()) {
                boardRepository.readBoard(nextPage, pageSize)
            } else {
                boardRepository.getRecentSignalByTag(tag, nextPage, pageSize)
            }
            Timber.tag("pager").d("API response: $response")

            if (response.isSuccessful) {
                val responseBody = response.body() ?: emptyList()
                Timber.tag("pager").d("Response body: $responseBody")

                LoadResult.Page(
                    data = responseBody,
                    prevKey = if (nextPage == 0) null else nextPage - 1,
                    nextKey = if (responseBody.isEmpty()) null else nextPage + 1
                )
            } else {
                Timber.tag("pager").e("Error response: ${response.errorBody()?.string()}")
                LoadResult.Error(Exception("Error: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception while loading data")
            LoadResult.Error(e)
        }
    }
}
