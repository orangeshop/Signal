package com.ongo.signal.ui.main.fragment

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentMainBinding
import com.ongo.signal.ui.main.adapter.TodayPostAdapter
import com.ongo.signal.ui.main.viewmodel.BoardViewModel
import com.ongo.signal.ui.main.viewmodel.CommentViewModel
import com.ongo.signal.util.KeyboardUtils
import com.ongo.signal.util.STTHelper
import com.ongo.signal.util.TTSHelper
import com.ongo.signal.util.ViewAnimation.fadeIn
import com.ongo.signal.util.ViewAnimation.fadeOut
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {

    private val boardViewModel: BoardViewModel by activityViewModels()
    private val commentViewModel: CommentViewModel by activityViewModels()
    private lateinit var todayPostAdapter: TodayPostAdapter
    private lateinit var ttsHelper: TTSHelper
    private lateinit var sttHelper: STTHelper
    private lateinit var sttLauncher: ActivityResultLauncher<Intent>
    private val handler = Handler(Looper.getMainLooper())
    private var shouldScrollToTop = true

    override fun init() {
        binding.fragment = this
        binding.lifecycleOwner = viewLifecycleOwner
        binding.boardViewModel = boardViewModel
        ttsHelper = TTSHelper(requireContext())

        sttLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                sttHelper.handleActivityResult(result.resultCode, result.data) { recognizedText ->
                    binding.etSearch.setText(recognizedText)
                }
            }
        sttHelper = STTHelper(sttLauncher)

        setUpAdapter()
        observeViewModels()
    }

    private fun observeViewModels() {
        observeBoards()
        observeComments()
        observeImageViewState()
        observeHotBoards()
    }

    private fun observeComments() {
        commentViewModel.setOnCommentChangedListener { boardId ->
            boardViewModel.updateBoardCommentCount(boardId)
        }
    }

    private fun observeBoards() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                boardViewModel.pagingData.collectLatest { pagingFlow ->
                    pagingFlow?.collectLatest { pagingData ->
                        todayPostAdapter.submitData(pagingData)
                    }
                }
            }
        }
    }

    private fun observeImageViewState() {
        viewLifecycleOwner.lifecycleScope.launch {
            boardViewModel.isSearchState.collectLatest { isSearch ->
                if (isSearch) {
                    binding.ivSearch.fadeOut()
                    binding.ivMic.fadeOut()
                    binding.ivRefresh.fadeIn()
                } else {
                    binding.ivSearch.fadeIn()
                    binding.ivMic.fadeIn()
                    binding.ivRefresh.fadeOut()
                }
            }
        }
    }

    private fun observeHotBoards() {
        viewLifecycleOwner.lifecycleScope.launch {
            boardViewModel.hotBoards.collectLatest {
                boardViewModel.hotBoards.collectLatest { hotBoards ->
                    todayPostAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    fun onChipClicked(tag: String) {
        shouldScrollToTop = true
        if (boardViewModel.selectedTag.value == tag) {
            boardViewModel.clearBoards()
        } else {
            boardViewModel.setSelectedTag(tag)
        }
    }

    fun onFABClicked() {
        boardViewModel.clearBoard()
        findNavController().navigate(R.id.action_mainFragment_to_writePostFragment)
    }

    fun onMicClicked() {
        sttHelper.startSpeechToText()
    }

    private fun onTitleClicked(position: Int) {
        val board = boardViewModel.hotBoards.value.getOrNull(position)
        board?.let {
            boardViewModel.selectBoard(it)
            gotoPost()
        }
    }

    fun searchKeyword() {
        val keyword = binding.etSearch.text.toString()
        if (keyword.isEmpty()) {
            makeToast("검색어를 입력해주세요.")
        } else {
            shouldScrollToTop = true
            boardViewModel.searchBoard(keyword)
            KeyboardUtils.hideKeyboard(binding.etSearch)
            boardViewModel.setSearchState(true)
            binding.ivSearch.fadeOut()
            binding.ivMic.fadeOut()
            binding.ivRefresh.fadeIn()
        }
    }

    fun loadAllBoards() {
        shouldScrollToTop = true
        boardViewModel.clearBoards()
        binding.etSearch.setText("")
        KeyboardUtils.hideKeyboard(binding.etSearch)
        binding.chipGroup.clearCheck()
        boardViewModel.setSearchState(false)
        binding.ivSearch.fadeIn()
        binding.ivMic.fadeIn()
        binding.ivRefresh.fadeOut()
    }

    private fun View.fadeIn() {
        fadeIn(this, requireContext())
    }

    private fun View.fadeOut() {
        fadeOut(this, requireContext())
    }

    private fun setUpAdapter() {
        todayPostAdapter = TodayPostAdapter(
            onItemClicked = { board ->
                boardViewModel.selectBoard(board)
                Timber.d(boardViewModel.selectedBoard.toString())
                gotoPost()
            },
            onTTSClicked = { content ->
                ttsHelper.speak(content)
            },
            viewModel = boardViewModel,
            onTitleClicked = { position ->
                onTitleClicked(position)
            },
        ).apply {
            addLoadStateListener { loadState ->
                if (loadState.source.refresh is LoadState.Loading) {
                    showProgressDialog()
                } else if (loadState.source.refresh is LoadState.NotLoading && shouldScrollToTop) {
                    hideProgressDialog()
                    binding.rvPost.scrollToPosition(0)
                    shouldScrollToTop = false
                } else {
                    hideProgressDialog()
                }
            }
        }

        binding.rvPost.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = todayPostAdapter
        }
    }

    fun gotoPost() {
        findNavController().navigate(R.id.action_mainFragment_to_postFragment)
    }

    override fun onDestroyView() {
        ttsHelper.shutdown()
        handler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }
}
