package com.ongo.signal.ui.main.fragment

import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.data.model.main.BoardRequestDTO
import com.ongo.signal.data.model.main.ImageItem
import com.ongo.signal.data.model.main.TagDTO
import com.ongo.signal.data.model.main.UpdateBoardDTO
import com.ongo.signal.databinding.FragmentWritePostBinding
import com.ongo.signal.ui.main.viewmodel.BoardViewModel
import com.ongo.signal.ui.main.viewmodel.ImageViewModel
import com.ongo.signal.ui.main.adapter.ImageAdapter
import com.ongo.signal.util.ImagePickerHelper
import com.ongo.signal.util.PopupMenuHelper
import com.ongo.signal.util.STTHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class WritePostFragment : BaseFragment<FragmentWritePostBinding>(R.layout.fragment_write_post) {

    private val boardViewModel: BoardViewModel by activityViewModels()
    private val imageViewModel: ImageViewModel by activityViewModels()
    private lateinit var sttHelper: STTHelper
    private lateinit var sttLauncher: ActivityResultLauncher<Intent>
    private lateinit var imagePickerHelper: ImagePickerHelper
    private var currentTarget: Int = 0
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var selectedTag: String
    private var selectedTagId: Int = 0


    override fun init() {
        binding.fragment = this
        binding.boardViewModel = boardViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupLaunchers()
        setUpUI()
        observeViewModel()
    }

    private fun setUpUI() {
        setUpSpinner()
        setUpImageAdapter()
        setupListeners()
    }

    private fun setUpSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.tag_array,
            R.layout.item_spinnner
        )
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        binding.spinner.adapter = adapter
        binding.spinner.setBackgroundResource(R.drawable.background_spinner)

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedTag = parent.getItemAtPosition(position).toString()
                selectedTagId = position + 1
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedTag = parent.getItemAtPosition(0).toString()
                selectedTagId = 1
            }
        }
    }

    private fun setUpImageAdapter() {
        imageAdapter = ImageAdapter({ item -> onRemoveImageClick(item) }, true)
        binding.rvImage.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }
    }

    private fun setupLaunchers() {
        sttLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                sttHelper.handleActivityResult(result.resultCode, result.data) { recognizedText ->
                    when (currentTarget) {
                        R.id.iv_title_mic -> {
                            binding.etTitle.setText(recognizedText)
                            boardViewModel.setTitle(recognizedText)
                        }

                        R.id.iv_content_mic -> {
                            binding.etContent.setText(recognizedText)
                            boardViewModel.setContent(recognizedText)
                        }
                    }
                }
            }

        sttHelper = STTHelper(sttLauncher)

        val cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                imagePickerHelper.handleActivityResult(result.resultCode, result.data)
            }

        val galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                imagePickerHelper.handleActivityResult(result.resultCode, result.data)
            }

        imagePickerHelper =
            ImagePickerHelper(requireActivity(), cameraLauncher, galleryLauncher) { uri ->
                imageAdapter.addImage(ImageItem.UriItem(uri))
            }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            boardViewModel.title.collectLatest { title ->
                if (binding.etTitle.text.toString() != title) {
                    binding.etTitle.setText(title)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            boardViewModel.content.collectLatest { content ->
                if (binding.etContent.text.toString() != content) {
                    binding.etContent.setText(content)
                }
            }
        }
    }

    fun setupListeners() {
        binding.ivTitleMic.setOnClickListener {
            currentTarget = R.id.iv_title_mic
            sttHelper.startSpeechToText()
        }

        binding.ivContentMic.setOnClickListener {
            currentTarget = R.id.iv_content_mic
            sttHelper.startSpeechToText()
        }
    }

    fun showImagePickerPopupMenu(view: View) {
        boardViewModel.setTitle(binding.etTitle.text.toString())
        boardViewModel.setContent(binding.etContent.text.toString())
        PopupMenuHelper.showPopupMenu(requireContext(), view, R.menu.popup_image_select) { item ->
            when (item.itemId) {
                R.id.menu_take_photo -> {
                    imagePickerHelper.takePictureFromCamera()
                    true
                }

                R.id.menu_choose_from_gallery -> {
                    imagePickerHelper.pickImageFromGallery()
                    true
                }

                else -> false
            }
        }
    }

    fun onRemoveImageClick(item: ImageItem) {
        imageAdapter.removeImage(item)
    }

    fun onRegisterButtonClick() {
        val title = binding.etTitle.text.toString()
        val content = binding.etContent.text.toString()
        val userId = UserSession.userId
        val writer = "admin"
        val tags = listOf(TagDTO(tagId = selectedTagId, tag = selectedTag))
        val isChipChecked = when {
            binding.chipJunior.isChecked && binding.chipSenior.isChecked -> 0
            binding.chipJunior.isChecked -> 1
            binding.chipSenior.isChecked -> 2
            else -> 0
        }.toLong()

        viewLifecycleOwner.lifecycleScope.launch {
            if (boardViewModel.selectedBoard.value == null) {
                userId?.let {
                    val boardRequestDTO =
                        BoardRequestDTO(
                            userId.toLong(),
                            writer,
                            title,
                            content,
                            isChipChecked,
                            tags
                        )
                    boardViewModel.createBoard(boardRequestDTO) { newBoard ->
                        if (newBoard != null) {
                            uploadImagesAndNavigate(newBoard)
                        } else {
                            Timber.e("Failed to create board")
                        }
                    }
                }
            } else {
                val updateBoardDTO = UpdateBoardDTO(title, content, isChipChecked)
                boardViewModel.updateBoard(
                    boardViewModel.selectedBoard.value!!.id,
                    updateBoardDTO
                ) { updatedBoard ->
                    if (updatedBoard != null) {
                        uploadImagesAndNavigate(updatedBoard)
                    } else {
                        Timber.e("Failed to update board")
                    }
                }
            }
        }
    }

    private fun uploadImagesAndNavigate(board: BoardDTO) {
        Timber.tag("selectedBoard").d(board.toString())
        val boardId = board.id
        val imageItems = imageAdapter.currentList
        val uriItems = imageItems.filterIsInstance<ImageItem.UriItem>()
        if (uriItems.isEmpty()) {
            Timber.d("uriItem isEmpty")
            boardViewModel.clearBoards()
            findNavController().navigate(R.id.action_writePostFragment_to_mainFragment)
        } else {
            val uploadTasks = uriItems.map { item ->
                viewLifecycleOwner.lifecycleScope.async {
                    Timber.d("Uploading image: ${item.uri}")
                    imageViewModel.uploadImage(boardId, item.uri, requireContext())
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    Timber.d("Awaiting upload tasks")
                    uploadTasks.awaitAll()
                } catch (e: Exception) {
                    Timber.e(e, "Failed to upload all images")
                } finally {
                    Timber.d("Image upload completed")
                    findNavController().navigate(R.id.action_writePostFragment_to_mainFragment)
                }
            }
        }
    }
}