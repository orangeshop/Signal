package com.ongo.signal.ui.match

import android.Manifest
import android.view.View
import android.view.animation.AnimationUtils
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentMatchBinding
import com.ongo.signal.util.RadarView
import com.ssafy.firebase_b.util.PermissionChecker

class MatchFragment : BaseFragment<FragmentMatchBinding>(R.layout.fragment_match) {

    private lateinit var radarView: RadarView
    private val checker = PermissionChecker(this)
    private val runtimePermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )


    override fun init() {
        initViews()
        initAnimation()
        checkPermission()
    }

    private fun checkPermission() {
        if (!checker.checkPermission(requireContext(), runtimePermissions)) {
            checker.setOnGrantedListener {}
            checker.requestPermissionLauncher.launch(runtimePermissions)
        }
    }

    private fun initAnimation() {
        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_alpha)
        val matchView = requireView().findViewById<View>(R.id.cl_match)
        matchView.startAnimation(anim)
    }

    private fun initViews() {
        radarView = binding.rvRadar

        binding.btnComplete.setOnClickListener {
            hideRequestMatchingWidget()
            showRadarWidget()
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
            ivProfile.visibility = View.VISIBLE
            tvUserId.visibility = View.VISIBLE
            tvIntroduce.visibility = View.VISIBLE
            btnMatching.visibility = View.VISIBLE
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