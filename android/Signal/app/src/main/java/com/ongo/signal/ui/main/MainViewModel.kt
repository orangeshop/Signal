package com.ongo.signal.ui.main

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.data.model.main.BoardImagesDTO
import com.ongo.signal.data.model.main.BoardImagesItemDTO
import com.ongo.signal.data.model.main.BoardRequestDTO
import com.ongo.signal.data.model.main.CommentDTO
import com.ongo.signal.data.model.main.CommentDTOItem
import com.ongo.signal.data.model.main.CommentRequestDTO
import com.ongo.signal.data.model.main.TagDTO
import com.ongo.signal.data.model.main.UpdateBoardDTO
import com.ongo.signal.data.repository.main.board.BoardRepository
import com.ongo.signal.data.repository.main.comment.CommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val boardRepository: BoardRepository,
    private val commentRepository: CommentRepository
) : ViewModel() {

    private val _boards = MutableStateFlow<List<BoardDTO>>(emptyList())
    val boards: StateFlow<List<BoardDTO>> = _boards

    private val _hotSignalBoards = MutableStateFlow<List<BoardDTO>>(emptyList())
    val hotSignalBoards: StateFlow<List<BoardDTO>> = _hotSignalBoards

    private val _selectedBoard = MutableStateFlow<BoardDTO?>(null)
    val selectedBoard: StateFlow<BoardDTO?> = _selectedBoard

    private val _currentUserId = MutableStateFlow(0)
    val currentUserId: StateFlow<Int> = _currentUserId

    private val _comments = MutableStateFlow(CommentDTO())
    val comments: StateFlow<CommentDTO> = _comments

    private val _boardImages = MutableStateFlow<Map<Int, List<BoardImagesItemDTO>>>(emptyMap())
    val boardImages: StateFlow<Map<Int, List<BoardImagesItemDTO>>> = _boardImages

    private val _selectedTag = MutableStateFlow<String?>(null)
    val selectedTag: StateFlow<String?> = _selectedTag

    private var isSearch: Boolean = false

    init {
        loadBoards()
        loadHotSignalBoards()
        _currentUserId.value = 3
    }

    fun loadBoards() {
        if (isSearch) return
        viewModelScope.launch {
            try {
                val response = boardRepository.readBoard()
                if (response.isSuccessful) {
                    val boardsList = response.body() ?: emptyList()
                    if (_boards.value != boardsList) {
                        _boards.value = boardsList
                        loadImagesForBoards()
                        Timber.d(_boards.value.toString())
                    }
                } else {
                    Timber.d(response.errorBody().toString())
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load boards")
            }
        }
    }

    private fun loadImagesForBoards() {
        viewModelScope.launch {
            try {
                val response = boardRepository.getBoardImages()
                if (response.isSuccessful) {
                    val images = response.body() ?: BoardImagesDTO()
                    val imagesMap = images.groupBy { it.boardId }
                    if (_boardImages.value != imagesMap) {
                        _boardImages.value = imagesMap

                        val updatedBoards = _boards.value.map { board ->
                            val updatedImages = imagesMap[board.id] ?: emptyList()
                            board.copy(imageUrls = updatedImages.map { it.fileUrl })
                        }
                        if (_boards.value != updatedBoards) {
                            _boards.value = updatedBoards
                        }
                    }
                } else {
                    Timber.d("Response failed")
                    Timber.d(response.errorBody()?.string() ?: "Unknown error")
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load images")
            }
        }
    }

    private fun loadHotSignalBoards() {
        viewModelScope.launch {
            try {
                val response = boardRepository.getHotSignal()
                if (response.isSuccessful) {
                    val hotSignalList = response.body() ?: emptyList()
                    if (_hotSignalBoards.value != hotSignalList) {
                        _hotSignalBoards.value = hotSignalList
                        Timber.d(_hotSignalBoards.value.toString())
                    }
                } else {
                    Timber.d(response.errorBody().toString())
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load hot signal boards")
            }
        }
    }

    fun loadHotAndRecentSignalBoardsByTag(tag: String, page: Int, limit: Int) {
        viewModelScope.launch {
            try {
                val hotSignalDeferred =
                    async { boardRepository.getHotSignalByTag(tag, page, limit) }
                val recentSignalDeferred =
                    async { boardRepository.getRecentSignalByTag(tag, page, limit) }

                Timber.d(tag)
                Timber.d(page.toString())
                Timber.d(limit.toString())
                Timber.d(hotSignalDeferred.toString())

                val hotSignalResponse = hotSignalDeferred.await()
                val recentSignalResponse = recentSignalDeferred.await()

                if (hotSignalResponse.isSuccessful) {
                    val hotSignalList = hotSignalResponse.body() ?: emptyList()
                    if (_hotSignalBoards.value != hotSignalList) {
                        _hotSignalBoards.value = hotSignalList
                        Timber.d(_hotSignalBoards.value.toString())
                    }
                } else {
                    Timber.d(hotSignalResponse.errorBody().toString())
                }

                if (recentSignalResponse.isSuccessful) {
                    val recentSignalList = recentSignalResponse.body() ?: emptyList()
                    if (_boards.value != recentSignalList) {
                        _boards.value = recentSignalList
                        Timber.d(_boards.value.toString())
                    }
                } else {
                    Timber.d(recentSignalResponse.errorBody().toString())
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load hot and recent signal boards by tag")
            }
        }
    }

    fun setSelectedTag(tag: String) {
        _selectedTag.value = tag
    }

    fun clearSelectedTag() {
        _selectedTag.value = null
    }

    fun searchBoard(keyword: String) {
        isSearch = true
        viewModelScope.launch {
            try {
                val response = boardRepository.searchBoard(keyword)
                if (response.isSuccessful) {
                    val searchResults = response.body() ?: emptyList()
                    Timber.d("Search results: $searchResults")
                    _boards.value = searchResults
                    Timber.d("Boards StateFlow updated: ${_boards.value}")
                } else {
                    Timber.d(response.errorBody().toString())
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to search boards")
            }
        }
    }

    fun clearSearch() {
        isSearch = false
        loadBoards()
    }

    fun createBoard(
        userId: Int,
        writer: String,
        title: String,
        content: String,
        tags: List<TagDTO>
    ): Deferred<Unit> {
        return viewModelScope.async {
            val boardRequestDTO = BoardRequestDTO(
                userId = userId,
                writer = writer,
                title = title,
                content = content,
                tags = tags
            )
            Timber.d("Creating Board with title: $title, content: $content")
            try {
                val response = boardRepository.writeBoard(boardRequestDTO)
                Timber.d("Server response: $response")
                if (response.isSuccessful) {
                    _selectedBoard.value = response.body()?.let {
                        Timber.d("Parsed BoardDTO - id: ${it.id}, title: ${it.title}, content: ${it.content}")
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
                            userId = it.userId,
                            tags = it.tags
                        )
                    }
                } else {
                    Timber.d(response.errorBody().toString())
                    Timber.d("response is failed")
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to create board")
            }
        }
    }

    fun updateBoard(
        boardId: Int,
        title: String,
        content: String,
        tags: List<TagDTO>
    ): Deferred<Unit> {
        return viewModelScope.async {
            val currentBoard = _selectedBoard.value
            currentBoard?.let {
                val boardRequestDTO = UpdateBoardDTO(
                    title = title,
                    content = content
                )
                Timber.d("Updating Board with title: $title, content: $content")
                Timber.d("BoardRequestDTO: $boardRequestDTO")
                try {
                    val response = boardRepository.updateBoard(boardId, boardRequestDTO)
                    if (response.isSuccessful) {
                        val boardResponse = response.body()
                        Timber.d("Server response: $boardResponse")
                        boardResponse?.let {
                            Timber.d("Parsed BoardDTO - id: ${it.id}, title: ${it.title}, content: ${it.content}")
                            _selectedBoard.value = BoardDTO(
                                id = it.id,
                                writer = it.writer,
                                userId = it.userId,
                                title = it.title,
                                content = it.content,
                                reference = it.reference,
                                liked = it.liked,
                                type = it.type,
                                createdDate = it.createdDate,
                                modifiedDate = it.modifiedDate,
                                comments = it.comments,
                                imageUrls = it.imageUrls,
                                tags = it.tags
                            )
                        }
                    } else {
                        Timber.d(response.errorBody().toString())
                        Timber.d("Response failed")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to update board")
                }
            }
        }
    }

    fun loadBoardDetails(boardId: Int) {
        viewModelScope.launch {
            try {
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
                            imageUrls = it.imageUrls,
                            comments = it.comments,
                            userId = it.userId,
                            tags = it.tags
                        )
                    }
                } else {
                    Timber.d(response.errorBody().toString())
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load board details")
            }
        }
    }

    fun deleteBoard(boardId: Int) {
        viewModelScope.launch {
            try {
                val response = boardRepository.deleteBoard(boardId)
                if (response.isSuccessful) {
                    Timber.d("Board deleted successfully: ${response.body()}")
                } else {
                    Timber.d(response.errorBody().toString())
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete board")
            }
        }
    }

    fun selectBoard(boardDTO: BoardDTO) {
        _selectedBoard.value = boardDTO
        Timber.d(_selectedBoard.value.toString())
    }

    fun clearBoard() {
        _selectedBoard.value = null
    }

    fun loadComments(boardId: Int) {
        viewModelScope.launch {
            try {
                val response = commentRepository.readComments(boardId)
                if (response.isSuccessful) {
                    _comments.value = response.body() ?: CommentDTO()
                } else {
                    Timber.d("response Failed")
                    Timber.d(response.errorBody().toString())
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load comments")
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
            try {
                val response = commentRepository.writeComment(boardId, commentDto)
                if (response.isSuccessful) {
                    loadComments(boardId.toInt())
                    Timber.d(response.message())
                } else {
                    Timber.d(response.errorBody().toString())
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to create comment")
            }
        }
    }

    fun updateComment(boardId: Long, commentId: Long, content: String) {
        val commentRequestDTO = CommentRequestDTO(
            boardId = boardId.toInt(),
            content = content,
            userId = _currentUserId.value
        )
        viewModelScope.launch {
            val response = commentRepository.updateComment(boardId, commentId, commentRequestDTO)
            if (response.isSuccessful) {
                loadComments(boardId.toInt())
                Timber.d(response.message())
            } else {
                Timber.d(response.errorBody().toString())
            }
        }
    }

    fun deleteComment(boardId: Int, commentId: Long) {
        viewModelScope.launch {
            val response = commentRepository.deleteComment(boardId.toLong(), commentId)
            if (response.isSuccessful) {
                loadComments(boardId)
            } else {
                Timber.d(response.errorBody()?.string())
            }
        }
    }

    fun onThumbClick(board: BoardDTO) {
        viewModelScope.launch {
            try {
                val response = boardRepository.boardLike(board.id.toLong())
                if (response.isSuccessful) {
                    val updatedBoard = board.copy(liked = board.liked + 1)
                    val updatedBoards = _boards.value.map {
                        if (it.id == board.id) updatedBoard else it
                    }
                    _boards.value = updatedBoards
                    _selectedBoard.value = updatedBoard
                    Timber.d("Like updated successfully")
                } else {
                    Timber.e("Failed to update like: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error updating like")
            }
        }
    }

    fun uploadImage(boardId: Long, uri: Uri, context: Context): Deferred<Unit> {
        return viewModelScope.async {
            try {
                val file = createFileFromUri(uri, context)
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                val response = boardRepository.uploadImage(boardId, body)
                if (response.isSuccessful) {
                    response.body()?.string()?.let { url ->
                        Timber.d("Image upload is Successful: $url")
                        addImageUrlToBoard(boardId, url)
                    }
                } else {
                    throw Exception("Image upload failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to upload image")
                throw e
            }
        }
    }

    private fun createFileFromUri(uri: Uri, context: Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_image.jpg")
        try {
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            outputStream.close()
            inputStream?.close()
        } catch (e: IOException) {
            Timber.e(e, "Failed to create file from URI")
        }
        return file
    }

    private fun addImageUrlToBoard(boardId: Long, url: String) {
        viewModelScope.launch {
            _selectedBoard.value = _selectedBoard.value?.let { board ->
                if (board.id.toLong() == boardId) {
                    board.copy(imageUrls = board.imageUrls?.plus(url))
                } else {
                    board
                }
            }
        }
    }
}
