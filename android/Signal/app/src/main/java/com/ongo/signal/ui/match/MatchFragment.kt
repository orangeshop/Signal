package com.ongo.signal.ui.match

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.chip.Chip
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.config.CreateChatRoom
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.match.Dot
import com.ongo.signal.data.model.match.MatchPossibleResponse
import com.ongo.signal.data.model.match.MatchRegistrationRequest
import com.ongo.signal.databinding.FragmentMatchBinding
import com.ongo.signal.ui.match.adapter.PossibleUserAdapter
import com.ongo.signal.ui.video.CallActivity
import com.ongo.signal.ui.video.repository.VideoRepository
import com.ongo.signal.ui.video.service.VideoService
import com.ongo.signal.ui.video.service.VideoServiceRepository
import com.ongo.signal.ui.video.util.DataModel
import com.ongo.signal.ui.video.util.DataModelType
import com.ongo.signal.ui.video.util.getCameraAndMicPermission
import com.ongo.signal.util.PermissionChecker
import com.ongo.signal.util.RadarView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MatchFragment : BaseFragment<FragmentMatchBinding>(R.layout.fragment_match),
    VideoService.Listener {

    private lateinit var radarView: RadarView
    private val checker = PermissionChecker(this)
    private val runtimePermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val viewModel: MatchViewModel by viewModels()
    private val possibleUserAdapter =
        PossibleUserAdapter(
            onMatchClick = { userId, userName ->
                viewModel.postProposeMatch(
                    fromId = UserSession.userId!!,
                    toId = userId,
                    onSuccess = {
                        makeToast("${userName} 님께 매칭 신청을 하였습니다.")
                    }
                )
            },
            onClick = { userId -> binding.cvDot.setDotFocused(userId) }
        )

    //video
    @Inject
    lateinit var videoRepository: VideoRepository

    @Inject
    lateinit var videoServiceRepository: VideoServiceRepository


    override fun init() {

        startVideoService()

        arguments?.let { args ->
            viewModel.setOtherUserId(args.getLong("otherUserId", 0L))
            viewModel.setOtherUserName(args.getString("otherUserName", ""))

            val notyTitle = args.getString("matchNotification")
            args.remove("matchNotification")
            args.remove("otherUserId")
            args.remove("otherUserName")
            if (notyTitle == "요청") {
                showMatchingDialog()
            }
        }
        initViews()
        initAnimation()
    }

    private fun startVideoService() {
        VideoService.listener = this
        videoServiceRepository.startService(UserSession.userId.toString())
    }

    private fun showMatchingDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_match, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvUsername: TextView = dialogView.findViewById(R.id.tv_username)
        val btnDeny: Button = dialogView.findViewById(R.id.btn_deny)
        val btnAccept: Button = dialogView.findViewById(R.id.btn_accept)

        tvUsername.text = viewModel.otherUserName

        btnAccept.setOnClickListener {
            UserSession.userId?.let { userId ->
                viewModel.postProposeAccept(
                    fromId = userId,
                    toId = viewModel.otherUserId!!,
                    1
                ) {
                    UserSession.userId?.let { nowId ->
                        viewModel.otherUserId?.let { otherId ->
                            CreateChatRoom.Create(nowId, otherId)
                            findNavController().navigate(R.id.action_matchFragment_to_chatFragment)
                        }
                    }

                    dialog.dismiss()
                }
            }
        }

        btnDeny.setOnClickListener {
            UserSession.userId?.let { userId ->
                viewModel.postProposeAccept(
                    fromId = userId,
                    toId = viewModel.otherUserId!!,
                    0
                ) {
                    Timber.d("매칭이 거절되었습니다")
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }


    private fun initAnimation() {
        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_alpha)
        val matchView = requireView().findViewById<View>(R.id.matchFragment)
        matchView.startAnimation(anim)
    }

    private fun initViews() {
        radarView = binding.rvRadar
        binding.rvPossibleMatch.adapter = possibleUserAdapter

        binding.btnComplete.setOnClickListener {
            if (!checker.checkPermission(requireContext(), runtimePermissions)) {
                checker.setOnGrantedListener {}
                checker.requestPermissionLauncher.launch(runtimePermissions)
                makeToast("권한을 허락해주셔야 매칭을 진행할 수 있습니다.")
                return@setOnClickListener
            }
            if (viewModel.selectType.isNullOrBlank()) {
                makeToast("원하는 상대의 타입을 골라주세요.")
                return@setOnClickListener
            }
            // context 들고가는 작업은 view에서 꼭 할것
            // view 죽으면 viewmodel 에서 팅길 수 있음
            // when viewModel dead?
            // requireActivity, requireContext difference
            LocationServices.getFusedLocationProviderClient(requireContext()).run {
                getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).addOnSuccessListener { currentLocation ->
                    lifecycleScope.launch {
                        Timber.d("매칭 쐈어요 아이디는 ${UserSession.userName} ${UserSession.userId}")
                        while (true) {
                            viewModel.postMatchRegistration(
                                request = MatchRegistrationRequest(
                                    currentLocation.latitude,
                                    currentLocation.longitude,
                                    viewModel.selectType!!,
                                    UserSession.userId!!
                                ),
                                onSuccess = { response ->
                                    hideRequestMatchingWidget()
                                    showRadarWidget()
                                    viewModel.getMatchPossibleUser(
                                        locationId = response.location_id,
                                        onSuccess = { possibleUsers ->
//                                            possibleUsers.forEach { nowUser ->
//                                                Timber.d("현재 유저는 ${nowUser}")
//                                            }
                                            binding.cvDot.addDot(convertToDotList(possibleUsers))
                                            possibleUserAdapter.submitList(possibleUsers.map { it.user })
                                        }
                                    )
                                }
                            )
                            delay(1000L)
                        }
                    }
                }.addOnFailureListener { exception ->
                    makeToast("위치 좌표를 받아올 수 없습니다.")
                }
            }
        }

        binding.cgChip.setOnCheckedStateChangeListener { group, checkedIds ->
            when {
                checkedIds.isEmpty() -> {
                    viewModel.setMemberType("")
                }

                checkedIds.size == 2 -> {
                    viewModel.setMemberType("모두")
                }

                else -> {
                    val selectedChipId = checkedIds[0]
                    val selectedChip: Chip? = group.findViewById(selectedChipId)
                    val selectedType = selectedChip?.text.toString()
                    viewModel.setMemberType(selectedType)
                }
            }
            Timber.d("칩 확인 ${viewModel.selectType}")
        }

        binding.btnVideo.setOnClickListener {
            getCameraAndMicPermission {
                videoRepository.sendConnectionRequest("25", true) {
                    if (it){
                        Timber.d("성공적으로 영통 보냄")
                        startActivity(Intent(requireContext(),CallActivity::class.java).apply {
                            putExtra("target","25")
                            putExtra("isVideoCall",true)
                            putExtra("isCaller",true)
                        })

                    }
                }
            }
//            viewModel.postProposeVideoCall(1, 1) {
//                Timber.d("영통 성공")
//            }
        }

        binding.btnAccept.setOnClickListener {
//            viewModel.postProposeVideoCallAccept(1, 1, 1) {
//                Timber.d("영통 수락 성공")
//            }
        }
    }

    private fun convertToDotList(responseList: List<MatchPossibleResponse>): List<Dot> {
        return responseList.map { response ->
            Dot(
                userId = response.user.userId,
                userName = response.user.name,
                distance = response.dist,
                quadrant = response.quadrant
            )
        }
    }

    private fun hideRequestMatchingWidget() {
        with(binding) {
            tvWhat.visibility = View.GONE
            tvWant.visibility = View.GONE
            tvMission.visibility = View.GONE
            cgChip.visibility = View.GONE
            btnComplete.visibility = View.GONE
            rvRadar.visibility = View.VISIBLE
        }
    }

    private fun showRadarWidget() {
        with(binding) {
            rvRadar.visibility = View.VISIBLE
            tvUser.visibility = View.VISIBLE
            tvClickGuide.visibility = View.VISIBLE
            cvDot.visibility = View.VISIBLE
            rvPossibleMatch.visibility = View.VISIBLE
//            ivProfile.visibility = View.VISIBLE
//            tvUserId.visibility = View.VISIBLE
//            tvIntroduce.visibility = View.VISIBLE
//            btnMatching.visibility = View.VISIBLE
        }
        initRadar()

    }

    override fun onCallReceived(model: DataModel) {
        requireActivity().runOnUiThread {
            binding.apply {
                val isVideoCall = model.type == DataModelType.StartVideoCall
                val isVideoCallText = if (isVideoCall) "Video" else "Audio"
                incomingCallTitleTv.text = "${model.sender} 님이 $isVideoCallText 영상통화를 요청합니다."
                incomingCallLayout.isVisible = true
                acceptButton.setOnClickListener {
                    getCameraAndMicPermission {
                        incomingCallLayout.isVisible = false
                        //create an intent to go to video call activity
                        startActivity(Intent(requireContext(), CallActivity::class.java).apply {
                            putExtra("target", model.sender)
                            putExtra("isVideoCall", isVideoCall)
                            putExtra("isCaller", false)
                        })
                    }
                }
                declineButton.setOnClickListener {
                    incomingCallLayout.isVisible = false
                }
            }
        }
    }

    private fun initRadar() {
        radarView.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        radarView.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoServiceRepository.stopService()
    }
}