package com.ongo.signal.data.repository

import com.ongo.signal.data.model.main.PostDTO
import com.ongo.signal.data.model.main.TagDTO
import java.util.Date
import javax.inject.Inject

class PostRepository @Inject constructor() {
    private val mockTags = listOf(
        TagDTO(tagId = "1", tag = "Technology"),
        TagDTO(tagId = "2", tag = "Health"),
        TagDTO(tagId = "3", tag = "Science"),
        TagDTO(tagId = "4", tag = "Education"),
        TagDTO(tagId = "5", tag = "Travel"),
        TagDTO(tagId = "6", tag = "Food"),
        TagDTO(tagId = "7", tag = "Art"),
        TagDTO(tagId = "8", tag = "Sports"),
        TagDTO(tagId = "9", tag = "Finance"),
        TagDTO(tagId = "10", tag = "Entertainment")
    )

    private val mockData = List(3) { index ->
        PostDTO(
            postId = index.toString(),
            title = "Title $index",
            content = "Content $index",
            profile = "",
            name = "User $index",
            date = Date(),
            image = null,
            tags = mockTags.shuffled().take(3),
            likeCount = (0..100).random(),
            commentCount = (0..50).random(),
            comment = emptyList()
        )
    }

    fun getPosts(page: Int, pageSize: Int): List<PostDTO> {
        val startIndex = (page - 1) * pageSize
        return if (startIndex < mockData.size) {
            mockData.subList(startIndex, minOf(startIndex + pageSize, mockData.size))
        } else {
            emptyList()
        }
    }
}
