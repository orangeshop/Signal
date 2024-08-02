package com.ongo.signal.ui.match

import android.Manifest
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
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
                    fromId = UserSession.userId!!,
                    toId = userId,
                    onSuccess = {
                        makeToast("${userName} 님께 매칭 신청을 하였습니다.")
                    }
                )
            },
            onClick = { userId -> binding.cvDot.setDotFocused(userId) }
        )


    override fun init() {
        arguments?.let { args ->
            if (args.getBoolean("matchNotification")) {
                args.remove("matchNotification")
                viewModel.setOtherUserId(args.getLong("otherUserId", 0L))
                Timber.d("서비스에서 가져온 값${args.getLong("otherUserId", 0L)}")
                viewModel.setOtherUserName(args.getString("otherUserName", ""))
                showMatchingDialog()
            }
        }
        initViews()
        initAnimation()
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

        tvUsername.text = "User1"

        btnAccept.setOnClickListener {
            UserSession.userId?.let { userId ->
                Timber.d("매칭 수락할게요 !! ${userId} ${viewModel.otherUserId!!}")
                viewModel.postProposeAccept(
                    fromId = userId,
                    toId = viewModel.otherUserId!!,
                    1
                ) {
                    Timber.d("매칭이 수락되었습니다")
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
                        viewModel.postMatchRegistration(
                            request = MatchRegistrationRequest(
                                currentLocation.latitude,
                                currentLocation.longitude,
                                UserSession.userId!!
                            ),
                            onSuccess = { response ->
                                hideRequestMatchingWidget()
                                showRadarWidget()
                                viewModel.getMatchPossibleUser(
                                    locationId = response.location_id,
                                    onSuccess = { possibleUsers ->
                                        possibleUsers.forEach { nowUser ->
                                            Timber.d("현재 유저는 ${nowUser}")
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