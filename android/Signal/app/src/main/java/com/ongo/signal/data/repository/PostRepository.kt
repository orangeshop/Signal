package com.ongo.signal.data.repository

import com.ongo.signal.data.model.main.PostDTO
import java.util.Date
import javax.inject.Inject

class PostRepository @Inject constructor() {
    private val mockData = List(100) { index ->
        PostDTO(
            postId = index.toString(),
            title = "Title $index",
            content = "Content $index",
            profile = "",
            name = "User $index",
            date = Date(),
            image = null,
            tags = emptyList(),
            likeCount = (0..100).random(),
            commentCount = (0..50).random(),
            comment = emptyList()
        )
    }

    fun getPosts(page: Int): List<PostDTO> {
        // Simulate pagination
        val pageSize = 10
        val startIndex = (page - 1) * pageSize
        return if (startIndex < mockData.size) {
            mockData.subList(startIndex, minOf(startIndex + pageSize, mockData.size))
        } else {
            emptyList()
        }
    }
}
