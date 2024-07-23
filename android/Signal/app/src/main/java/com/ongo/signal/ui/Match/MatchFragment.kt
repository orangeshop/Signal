package com.ongo.signal.ui.match

import android.Manifest
import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.view.View
import android.view.animation.AnimationUtils
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentMatchBinding
import com.ongo.signal.util.RadarView
import com.ssafy.firebase_b.util.PermissionChecker
import timber.log.Timber
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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

            hideRequestMatchingWidget()
            showRadarWidget()
            getLocation()
        }

    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {

        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { success: Location? ->
                success?.let { location ->
                    Timber.d("${location.latitude}, ${location.longitude}")
                    val geocoder = Geocoder(requireContext(), Locale.KOREA)
                    val address = geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    ) as List<Address>
                    Timber.d("위치는 : $address")
                    Timber.d("거리는 : ${calculateDistance(36.1020544,128.4213672,36.1020544,127.4213672)}")
                }
            }
            .addOnFailureListener { fail ->
                Timber.d("fail : $fail")
            }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
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