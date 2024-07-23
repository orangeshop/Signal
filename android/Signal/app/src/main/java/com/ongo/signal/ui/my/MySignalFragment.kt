package com.ongo.signal.ui.my

import androidx.navigation.fragment.findNavController
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.data.model.main.PostDTO
import com.ongo.signal.data.model.main.TagDTO
import com.ongo.signal.databinding.FragmentMySignalBinding
import com.ongo.signal.ui.MainActivity
import com.ongo.signal.ui.main.adapter.ChipAdapter
import com.ongo.signal.ui.my.adapter.PreviewPostAdapter
import java.util.Date

class MySignalFragment : BaseFragment<FragmentMySignalBinding>(R.layout.fragment_my_signal) {

    private val previewPostAdapter = PreviewPostAdapter(onClick = {})

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

    private val mockData = List(10) { index ->
        PostDTO(
            postId = index.toString(),
            title = "Title $index",
            content = "Content $index",
            profile = "",
            name = "User $index",
            date = Date(),
            image = null,
            tags = mockTags.shuffled().take(2),
            likeCount = (0..100).random(),
            commentCount = (0..50).random(),
            comment = emptyList()
        )
    }

    override fun init() {
        binding.rvPostPreview.adapter = previewPostAdapter.apply {
            submitList(mockData)
        }

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as MainActivity).showBottomNavigation()
    }
}