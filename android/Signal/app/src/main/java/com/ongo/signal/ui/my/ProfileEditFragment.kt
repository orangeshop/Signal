package com.ongo.signal.ui.my

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentProfileEditBinding
import com.ongo.signal.ui.MainActivity
import timber.log.Timber


class ProfileEditFragment :
    BaseFragment<FragmentProfileEditBinding>(R.layout.fragment_profile_edit) {

    private val args: ProfileEditFragmentArgs by navArgs()

    override fun init() {

        Timber.d("데이터 가져왔어요 ${args.argNumber}")

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as MainActivity).showBottomNavigation()
    }

}