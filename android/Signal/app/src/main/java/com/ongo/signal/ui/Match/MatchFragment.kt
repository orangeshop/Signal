package com.ongo.signal.ui.match

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentMatchBinding

class MatchFragment : BaseFragment<FragmentMatchBinding>(R.layout.fragment_match) {
    override fun init() {
        initAnimation()
    }

    private fun initAnimation(){
        val anim = AnimationUtils.loadAnimation(requireContext(),R.anim.anim_alpha)
        val matchView = requireView().findViewById<View>(R.id.cl_match)
        matchView.startAnimation(anim)
    }
}