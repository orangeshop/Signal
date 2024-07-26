package com.ongo.signal.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.data.model.main.BoardRequestDTO
import com.ongo.signal.data.model.main.CommentDTO
import com.ongo.signal.data.model.main.CommentDTOItem
import com.ongo.signal.data.repository.main.board.BoardRepository
import com.ongo.signal.data.repository.main.comment.CommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val boardRepository: BoardRepository,
    private val commentRepository: CommentRepository
) : ViewModel() {

    private val _boards = MutableStateFlow<List<BoardDTO>>(emptyList())
    val boards: StateFlow<List<BoardDTO>> = _boards

    private val _selectedBoard = MutableStateFlow<BoardDTO?>(null)
    val selectedBoard: StateFlow<BoardDTO?> = _selectedBoard

    private val _currentUserId = MutableStateFlow(0)
    val currentUserId: StateFlow<Int> = _currentUserId

    private val _comments = MutableStateFlow(CommentDTO())
    val comments: StateFlow<CommentDTO> = _comments


    init {
        loadBoards()
        _currentUserId.value = 3
    }

    fun loadBoards() {
        viewModelScope.launch {
            val response = boardRepository.readBoard()
            if (response.isSuccessful) {
                _boards.value = response.body() ?: emptyList()
            } else {
                Timber.d(response.errorBody().toString())
            }
        }
    }

    fun createBoard(userId: Int, writer: String, title: String, content: String) {
        val boardRequestDTO =
            BoardRequestDTO(userId = userId, writer = writer, title = title, content = content)
        viewModelScope.launch {
            val response = boardRepository.writeBoard(boardRequestDTO)
            if (response.isSuccessful) {
                _selectedBoard.value = response.body()?.let {
                    BoardDTO(
                        id = it.id,
                        writer = it.writer,
                        title = it.title,
                        content = it.content,
                        createdDate = it.createdDate,
                        modifiedDate = it.modifiedDate,
                        liked = it.liked,
                        reference = it.reference,
                        type = it.type,
                        comments = it.comments,
                        userId = it.userId
                    )
                }
            } else {
                Timber.d(response.errorBody().toString())
            }
        }
    }

    fun loadBoardDetails(boardId: Int) {
        viewModelScope.launch {
            val response = boardRepository.readBoardById(boardId)
            if (response.isSuccessful) {
                _selectedBoard.value = response.body()?.let {
                    BoardDTO(
                        id = it.id,
                        writer = it.writer,
                        title = it.title,
                        content = it.content,
                        createdDate = it.createdDate,
                        modifiedDate = it.modifiedDate,
                        liked = it.liked,
                        reference = it.reference,
                        type = it.type,
                        comments = it.comments,
                        userId = it.userId
                    )
                }
            } else {
                Timber.d(response.errorBody().toString())
            }
        }
    }

    fun updateBoard(boardId: Int, title: String, content: String) {
        val currentBoard = _selectedBoard.value
        currentBoard?.let {
            val boardRequestDTO = BoardRequestDTO(
                userId = it.userId,
                writer = it.writer,
                title = title,
                content = content
            )
            viewModelScope.launch {
                boardRepository.updateBoard(boardId, boardRequestDTO)
            }
        }
    }

    fun deleteBoard(boardId: Int) {
        viewModelScope.launch {
            val response = boardRepository.deleteBoard(boardId)
            if (response.isSuccessful) {
                Timber.d("Board deleted successfully: ${response.body()}")
            } else {
                Timber.d(response.errorBody().toString())
            }
        }
    }

    fun selectBoard(boardDTO: BoardDTO) {
        _selectedBoard.value = boardDTO
    }

    fun clearBoard() {
        _selectedBoard.value = null
    }

    fun loadComments(boardId: Int) {
        viewModelScope.launch {
            val response = commentRepository.readComments(boardId)
            if (response.isSuccessful) {
                _comments.value = response.body() ?: CommentDTO()
            } else {
                Timber.d(response.errorBody().toString())
            }
        }
    }

    fun createComment(boardId: Long, writer: String, content: String) {
        val commentDto = CommentDTOItem(
            boardId = boardId.toInt(),
            writer = writer,
            content = content,
            createdDate = "",
            id = 0,
            modifiedDate = "",
            userId = _currentUserId.value,
            url = ""
        )
        viewModelScope.launch {
            val response = commentRepository.writeComment(boardId, commentDto)
            if (response.isSuccessful) {
                loadComments(boardId.toInt())
                Timber.d(response.message())
            } else {
                Timber.d(response.errorBody().toString())
            }
        }
    }
}

