package com.ongo.signal.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.data.model.main.BoardPagingSource
import com.ongo.signal.data.model.main.BoardRequestDTO
import com.ongo.signal.data.model.main.UpdateBoardDTO
import com.ongo.signal.data.repository.main.board.BoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val boardRepository: BoardRepository
) : ViewModel() {

    private val _items = MutableSharedFlow<PagingData<BoardDTO>>(replay = 1)
    val items: SharedFlow<PagingData<BoardDTO>> = _items

    private val _hotBoards = MutableStateFlow<List<BoardDTO>>(emptyList())
    val hotBoards: StateFlow<List<BoardDTO>> = _hotBoards

    private val _selectedBoard = MutableStateFlow<BoardDTO?>(null)
    val selectedBoard: StateFlow<BoardDTO?> = _selectedBoard

    private val _selectedTag = MutableStateFlow<String?>(null)
    val selectedTag: StateFlow<String?> = _selectedTag

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content

    init {
        loadHotBoards()
        loadBoards()
    }

    private fun loadBoards() {
        viewModelScope.launch {
            Timber.d("Loading boards without tag")
            Pager(
                config = PagingConfig(
                    pageSize = 5,
                    enablePlaceholders = false,
                    initialLoadSize = 5
                ),
                pagingSourceFactory = {
                    BoardPagingSource(boardRepository)
                }
            ).flow.cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    Timber.d("Loaded boards: $pagingData")
                    _items.emit(pagingData)
                }
        }
    }

    private fun loadBoardsByTag(tag: String) {
        Timber.d("Loading boards with tag: $tag")
        viewModelScope.launch {
            Pager(
                config = PagingConfig(
                    pageSize = 5,
                    enablePlaceholders = false,
                    initialLoadSize = 5
                ),
                pagingSourceFactory = {
                    BoardPagingSource(boardRepository, tag)
                }
            ).flow
                .cachedIn(viewModelScope)
                .catch { e -> Timber.e(e, "Exception in loadBoardsByTag") }
                .collectLatest { pagingData ->
                    Timber.d("Loaded boards by tag: $pagingData")
                    _items.emit(pagingData)
                }
        }
    }

    private fun loadHotBoards() {
        viewModelScope.launch {
            runCatching {
                boardRepository.getHotSignal()
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    val hotList = response.body() ?: emptyList()
                    Timber.d("Loaded hot boards: $hotList")
                    _hotBoards.emit(hotList)
                } else {
                    Timber.e("Failed to load hot boards: ${response.errorBody()?.string()}")
                }
            }.onFailure { e ->
                Timber.e(e, "Failed to load hot boards")
            }
        }
    }

    private fun loadHotSignalByTag(tag: String) {
        viewModelScope.launch {
            runCatching {
                boardRepository.getHotSignalByTag(tag, 0, 5)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    val hotList = response.body() ?: emptyList()
                    Timber.d("Loaded hot signal boards by tag: $hotList")
                    _hotBoards.emit(hotList)
                } else {
                    Timber.e(
                        "Failed to load hot signal boards by tag: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }.onFailure { e ->
                Timber.e(e, "Failed to load hot signal boards by tag")
            }
        }
    }

    fun createBoard(boardRequestDTO: BoardRequestDTO, onSuccess: (BoardDTO?) -> Unit) {
        viewModelScope.launch {
            runCatching {
                boardRepository.writeBoard(boardRequestDTO)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    val newBoard = response.body()
                    Timber.d("Board created in boardViewModel: $newBoard")
                    _selectedBoard.value = newBoard
                    onSuccess(newBoard)
                } else {
                    Timber.e("Failed to create board: ${response.errorBody()?.string()}")
                    onSuccess(null)
                }
            }.onFailure { e ->
                Timber.e(e, "Failed to create board")
                onSuccess(null)
            }
        }
    }

    fun updateBoard(boardId: Long, updateBoardDTO: UpdateBoardDTO, onSuccess: (BoardDTO?) -> Unit) {
        viewModelScope.launch {
            runCatching {
                boardRepository.updateBoard(boardId, updateBoardDTO)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    val updatedBoard = response.body()
                    _selectedBoard.value = updatedBoard
                    Timber.d("Board updated in boardViewModel: $updatedBoard")
                    onSuccess(updatedBoard)
                } else {
                    Timber.e("Failed to update board: ${response.errorBody()?.string()}")
                    onSuccess(null)
                }
            }.onFailure { e ->
                Timber.e(e, "Failed to update board")
                onSuccess(null)
            }
        }
    }

    fun deleteBoard(boardId: Long) {
        viewModelScope.launch {
            runCatching {
                boardRepository.deleteBoard(boardId)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    Timber.d("Board deleted")
                    loadBoards()
                    loadHotBoards()
                    clearBoards()
                } else {
                    Timber.e("Failed to delete board: ${response.errorBody()?.string()}")
                }
            }.onFailure { e ->
                Timber.e(e, "Failed to delete board")
            }
        }
    }

    fun searchBoard(keyword: String) {
        Timber.d("search board by keyword: $keyword")
        viewModelScope.launch {
            runCatching {
                boardRepository.searchBoard(keyword)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    val searchResults = response.body() ?: emptyList()
                    Timber.d("Search results: $searchResults")
                    val pagingData = PagingData.from(searchResults)
                    _items.emit(pagingData)
                } else {
                    Timber.e("Failed to search boards: ${response.errorBody()?.string()}")
                }
            }.onFailure { e ->
                Timber.e(e, "Failed to search boards")
            }
        }
    }

    fun loadBoardDetails(boardId: Long) {
        Timber.d("loadBoardDetails called with boardId: $boardId")
        viewModelScope.launch {
            runCatching {
                boardRepository.readBoardById(boardId)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    val boardDetailDTO = response.body()
                    boardDetailDTO?.let {
                        _selectedBoard.value = it
                    }
                } else {
                    Timber.e("Failed to load board details: ${response.errorBody()?.string()}")
                }
            }.onFailure { e ->
                Timber.e(e, "Failed to load board details")
            }
        }
    }

    fun onThumbClick(board: BoardDTO) {
        viewModelScope.launch {
            runCatching {
                boardRepository.boardLike(board.id)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    response.body()?.let { newLikeCount ->
                        val updatedBoard = board.copy(liked = newLikeCount)
                        _selectedBoard.value = updatedBoard
                        _items.emit(_items.replayCache.first().map {
                            if (it.id == board.id) updatedBoard else it
                        })
                        Timber.d("Board liked: $newLikeCount")
                    }
                } else {
                    Timber.e("Failed to like board: ${response.errorBody()?.string()}")
                }
            }.onFailure { e ->
                Timber.e(e, "Failed to like board")
            }
        }
    }

    fun clearBoards() {
        viewModelScope.launch {
            Timber.d("Clearing boards")
            _items.emit(PagingData.empty())
            setSelectedTag(null)
        }
    }

    fun setSelectedTag(tag: String?) {
        _selectedTag.value = tag
        if (_selectedTag.value.isNullOrEmpty()) {
            loadBoards()
            loadHotBoards()
        } else {
            loadBoardsByTag(_selectedTag.value!!)
            loadHotSignalByTag(_selectedTag.value!!)
        }
    }

    fun selectBoard(board: BoardDTO) {
        _selectedBoard.value = board
    }

    fun clearBoard() {
        _selectedBoard.value = null
        _title.value = ""
        _content.value = ""
    }

    fun setTitle(title: String) {
        _title.value = title
    }

    fun setContent(content: String) {
        _content.value = content
    }

    fun updateBoardCommentCount(boardId: Long) {
        viewModelScope.launch {
            runCatching {
                boardRepository.readBoardById(boardId)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    response.body()?.let { updatedBoard ->
                        _items.emit(_items.replayCache.first().map {
                            if (it.id == boardId) updatedBoard else it
                        })
                        Timber.d("Board updated with new comment count: $updatedBoard")
                    }
                } else {
                    Timber.e(
                        "Failed to update board comment count: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }.onFailure { e ->
                Timber.e(e, "Failed to update board comment count")
            }
        }
    }
}
