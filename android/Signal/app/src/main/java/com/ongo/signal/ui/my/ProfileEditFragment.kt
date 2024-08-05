package com.ongo.signal.ui.my

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentProfileEditBinding
import com.ongo.signal.ui.MainActivity
import timber.log.Timber


class ProfileEditFragment :
    BaseFragment<FragmentProfileEditBinding>(R.layout.fragment_profile_edit) {

    private val args: ProfileEditFragmentArgs by navArgs()

    override fun init() {

        Timber.d("넘어온 데이터 ${args}")
        initUserData()

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initUserData() {

        val user = args.profileData
        with(binding) {
            if (user.profileImage.isBlank()) {
                ivBasicProfile.setImageResource(R.drawable.basic_profile)
            } else {
                Glide.with(requireActivity())
                    .load(user.profileImage)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(ivBasicProfile)
            }

            tietNickname.setText(user.name)
            tietComment.setText(user.comment)

            if (user.type == "주니어") cgChip.check(R.id.chip_junior)
            else cgChip.check(R.id.chip_senior)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as MainActivity).showBottomNavigation()
    }

}