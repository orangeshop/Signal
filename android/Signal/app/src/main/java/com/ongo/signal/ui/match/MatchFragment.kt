package com.ongo.signal.ui.match

import android.Manifest
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.match.Dot
import com.ongo.signal.data.model.match.MatchPossibleResponse
import com.ongo.signal.data.model.match.MatchRegistrationRequest
import com.ongo.signal.databinding.FragmentMatchBinding
import com.ongo.signal.ui.match.adapter.PossibleUserAdapter
import com.ongo.signal.util.PermissionChecker
import com.ongo.signal.util.RadarView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MatchFragment : BaseFragment<FragmentMatchBinding>(R.layout.fragment_match) {

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
                    fromId = 18,
                    toId = userId,
                    onSuccess = {
                        makeToast("${userName} 님께 매칭 신청을 하였습니다.")
                    }
                )
            },
            onClick = { userId -> binding.cvDot.setDotFocused(userId) }
        )


    override fun init() {
        requireActivity().intent
        arguments?.let { args ->
            if (args.getBoolean("matchNotification")) {
                args.remove("matchNotification")
                showMatchingDialog()
            }
        }
        initViews()
        initAnimation()
    }

    private fun showMatchingDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("매칭 신청")
            .setMessage("매칭이 신청되었습니다")
            .setPositiveButton("수락") { dialog, _ ->
//                UserSession.userId?.let { userId ->
//                    viewModel.postProposeAccept(fromId = userId, toId = { })
//                }
            }
            .setNegativeButton("거절") { dialog, _ ->
                // 거절 버튼 클릭 시 실행할 코드
            }
            .show()
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
                        viewModel.postMatchRegistration(
                            request = MatchRegistrationRequest(
                                currentLocation.latitude,
                                currentLocation.longitude,
                                18
                            ),
                            onSuccess = { response ->
                                hideRequestMatchingWidget()
                                showRadarWidget()
                                viewModel.getMatchPossibleUser(
                                    locationId = response.location_id,
                                    onSuccess = { possibleUsers ->
                                        possibleUsers.forEach { nowUser ->
                                            Timber.d("현재 유저는 ${nowUser}\n")
                                        }
                                        binding.cvDot.addDot(convertToDotList(possibleUsers))
                                        possibleUserAdapter.submitList(possibleUsers.map { it.user })
                                    }
                                )
                            }
                        )
                    }
                }.addOnFailureListener { exception ->
                    makeToast("위치 좌표를 받아올 수 없습니다.")
                }
            }
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

    private fun initRadar() {
        radarView.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        radarView.stop()
    }
}