package com.ongo.signal.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.data.model.main.BoardPagingSource
import com.ongo.signal.data.model.main.BoardRequestDTO
import com.ongo.signal.data.model.main.UpdateBoardDTO
import com.ongo.signal.data.repository.main.board.BoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val boardRepository: BoardRepository
) : ViewModel() {

    private val _hotBoards = MutableStateFlow<List<BoardDTO>>(emptyList())
    val hotBoards: StateFlow<List<BoardDTO>> = _hotBoards

    private val _selectedBoard = MutableStateFlow<BoardDTO?>(null)
    val selectedBoard: StateFlow<BoardDTO?> = _selectedBoard

    private val _selectedTag = MutableStateFlow<String?>(null)
    val selectedTag: StateFlow<String?> = _selectedTag

    private val _isSearchState = MutableStateFlow(false)
    val isSearchState: StateFlow<Boolean> = _isSearchState

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content

    private val _pagingData = MutableStateFlow<Flow<PagingData<BoardDTO>>?>(null)
    val pagingData = _pagingData.asStateFlow()

    init {
        loadHotBoards()
        loadBoards()
    }

    private fun loadBoards() = viewModelScope.launch(Dispatchers.IO) {
        Timber.d("Loading boards without tag")
        val newPager = Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false,
                initialLoadSize = 5
            ),
            pagingSourceFactory = {
                BoardPagingSource(boardRepository)
            }
        ).flow.cachedIn(viewModelScope)
        _pagingData.emit(newPager)
    }

    fun onThumbClick(board: BoardDTO, userId: Long, bind: (BoardDTO) -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                boardRepository.boardLike(board.id, userId)
            }.onSuccess { response ->
                Timber.tag("onThumbClick").d(response.toString())
                if (!response.isSuccessful) return@launch
                val count = response.body()?.likedCount ?: return@launch
                val isLiked = response.body()?.isLiked ?: return@launch
                val newBoard = board.copy(liked = count, isLiked = isLiked)
                val pagingFlow = pagingData.value ?: return@launch
                val newPagingFlow = pagingFlow.map { pagingData ->
                    pagingData.map { existingBoard ->
                        if (existingBoard.id == newBoard.id) newBoard else existingBoard
                    }
                }
                _pagingData.value = newPagingFlow

                Timber.tag("boardLike")
                    .d("boardLike: ${board.liked} boardIsLiked: ${board.isLiked}")
                withContext(Dispatchers.Main) {
                    bind(newBoard)
                }

            }.onFailure { e ->
                Timber.e(e, "Failed to like board")
            }
        }

    fun onThumbClick(board: BoardDTO, userId: Long) = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            boardRepository.boardLike(board.id, userId)
        }.onSuccess { response ->
            Timber.tag("onThumbClick").d(response.toString())
            if (!response.isSuccessful) return@launch
            val count = response.body()?.likedCount ?: return@launch
            val updatedIsLiked = !board.isLiked
            val newBoard = board.copy(liked = count, isLiked = updatedIsLiked)
            val pagingFlow = pagingData.value ?: return@launch
            val newPagingFlow = pagingFlow.map { pagingData ->
                pagingData.map { existingBoard ->
                    if (existingBoard.id == newBoard.id) newBoard else existingBoard
                }
            }
            _pagingData.value = newPagingFlow
            _selectedBoard.emit(newBoard)
        }.onFailure { e ->
            Timber.e(e, "Failed to like board")
        }
    }

    fun searchBoard(keyword: String) = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            boardRepository.searchBoard(keyword)
        }.onSuccess { response ->
            if (!response.isSuccessful) return@launch
            val boards = response.body() ?: return@launch
            val pager = PagingData.from(boards)
            _pagingData.emit(flowOf(pager))
        }.onFailure { e ->
            Timber.e(e, "Failed to search boards")
        }
    }

    private fun loadBoardsByTag(tag: String) = viewModelScope.launch(Dispatchers.IO) {
        val newPager = Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false,
                initialLoadSize = 5
            ),
            pagingSourceFactory = {
                BoardPagingSource(boardRepository, tag)
            }
        ).flow.cachedIn(viewModelScope).collectLatest { newPager ->
            _pagingData.emit(flowOf(newPager))
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

    fun clearBoards() {
        viewModelScope.launch {
            setSelectedTag(null)
        }
    }

    fun setSelectedTag(tag: String?) {
        _selectedTag.value = tag
        if (_selectedTag.value.isNullOrEmpty()) {
            loadBoards()
            loadHotBoards()
        } else {
            _pagingData.value = flowOf(PagingData.empty())
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

    fun setSearchState(isSearch: Boolean) {
        _isSearchState.value = isSearch
    }

    fun updateBoardCommentCount(boardId: Long) {
        viewModelScope.launch {
            runCatching {
                boardRepository.readBoardById(boardId)
            }.onSuccess { _ ->
                if (_selectedTag.value == null) {
                    loadBoards()
                    loadHotBoards()
                } else {
                    loadBoardsByTag(tag = _selectedTag.value!!)
                    loadHotSignalByTag(tag = _selectedTag.value!!)
                }
            }.onFailure { e ->
                Timber.e(e, "Failed to update board comment count")
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

    private fun loadHotBoards() = viewModelScope.launch(Dispatchers.IO) {

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
