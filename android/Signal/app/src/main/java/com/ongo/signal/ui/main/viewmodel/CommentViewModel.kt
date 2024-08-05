package com.ongo.signal.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.data.model.main.CommentDTO
import com.ongo.signal.data.model.main.CommentDTOItem
import com.ongo.signal.data.model.main.CommentRequestDTO
import com.ongo.signal.data.repository.main.comment.CommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentRepository: CommentRepository
) : ViewModel() {
    private val _comments = MutableStateFlow(CommentDTO())
    val comments: StateFlow<CommentDTO> = _comments

    fun loadComments(boardId: Long) {
        viewModelScope.launch {
            runCatching {
                commentRepository.readComments(boardId)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    _comments.value = response.body() ?: CommentDTO()
                    Timber.d(response.body().toString())
                } else {
                    Timber.e("Failed to load comments: ${response.errorBody()?.string()}")
                }
            }.onFailure { e ->
                Timber.e(e, "Failed to load comments")
            }
        }
    }

    fun createComment(commentDto: CommentDTOItem) {
        viewModelScope.launch {
            runCatching {
                commentRepository.writeComment(commentDto.boardId, commentDto)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    loadComments(commentDto.boardId)
                    Timber.d("Comment created: ${response.body()}")
                } else {
                    Timber.e("Failed to create comment: ${response.errorBody()?.string()}")
                }
            }.onFailure { e ->
                Timber.e(e, "Failed to create comment")
            }
        }
    }

    fun updateComment(boardId: Long, commentId: Long, commentRequestDTO: CommentRequestDTO) {
        viewModelScope.launch {
            runCatching {
                commentRepository.updateComment(boardId, commentId, commentRequestDTO)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    loadComments(boardId)
                    Timber.d("Comment updated: ${response.body()}")
                } else {
                    Timber.e("Failed to update comment: ${response.errorBody()?.string()}")
                }
            }.onFailure { e ->
                Timber.e(e, "Failed to update comment")
            }
        }
    }

    fun deleteComment(boardId: Long, commentId: Long) {
        viewModelScope.launch {
            runCatching {
                commentRepository.deleteComment(boardId, commentId)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    loadComments(boardId)
                    Timber.d("Comment deleted")
                } else {
                    Timber.e("Failed to delete comment: ${response.errorBody()?.string()}")
                }
            }.onFailure { e ->
                Timber.e(e, "Failed to delete comment")
            }
        }
    }
}
