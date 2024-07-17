package com.ongo.signal.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.data.model.main.PostDTO
import com.ongo.signal.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<PostDTO>>(emptyList())
    val posts: StateFlow<List<PostDTO>> = _posts

    private var currentPage = 1

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            val newPosts = repository.getPosts(currentPage)
            _posts.value = _posts.value.toMutableList().apply { addAll(newPosts) }
            currentPage++
        }
    }
}
