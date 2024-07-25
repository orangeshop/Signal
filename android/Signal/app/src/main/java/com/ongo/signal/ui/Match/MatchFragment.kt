package com.ongo.signal.ui.match

import android.Manifest
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.data.model.match.MatchRegistrationRequest
import com.ongo.signal.databinding.FragmentMatchBinding
import com.ongo.signal.util.RadarView
import com.ssafy.firebase_b.util.PermissionChecker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
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


    override fun init() {
        initViews()
        initAnimation()
    }


    private fun initAnimation() {
        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_alpha)
        val matchView = requireView().findViewById<View>(R.id.cl_match)
        matchView.startAnimation(anim)
    }

    private fun initViews() {
        radarView = binding.rvRadar

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
                                Timber.d("${response}")
                                hideRequestMatchingWidget()
                                showRadarWidget()
                            }
                        )
                    }
                }.addOnFailureListener { exception ->
                    makeToast("위치 좌표를 받아올 수 없습니다.")
                }
            }
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
//            ivProfile.visibility = View.VISIBLE
//            tvUserId.visibility = View.VISIBLE
//            tvIntroduce.visibility = View.VISIBLE
//            btnMatching.visibility = View.VISIBLE
        }
        initRadar()

        lifecycleScope.launch {
            binding.cvDot.addDot(500f, 200f)
            binding.cvDot.addDot(200f, 300f)
            binding.cvDot.addDot(400f, 800f)
            delay(2000L)
            binding.cvDot.addDot(700f, 700f)
        }
    }

    private fun initRadar() {
        radarView.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        radarView.stop()
    }
}