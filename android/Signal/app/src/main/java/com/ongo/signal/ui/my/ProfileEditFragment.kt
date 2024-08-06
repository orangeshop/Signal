package com.ongo.signal.ui.my

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.my.ProfileEditRequest
import com.ongo.signal.data.model.my.ProfileEditUiState
import com.ongo.signal.databinding.FragmentProfileEditBinding
import com.ongo.signal.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class ProfileEditFragment :
    BaseFragment<FragmentProfileEditBinding>(R.layout.fragment_profile_edit) {

    private val args: ProfileEditFragmentArgs by navArgs()
    private val viewModel: ProfileEditViewModel by viewModels()

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            result.data?.data?.let { imageUri ->
                Glide.with(requireActivity())
                    .load(imageUri)
                    .transform(CircleCrop())
                    .into(binding.ivBasicProfile)

                val file = createFileFromUri(imageUri, requireContext())
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageFile = MultipartBody.Part.createFormData("file", file.name, requestFile)

                viewModel.setChanged(true)
                viewModel.setImageFile(imageFile)
            }
        }
    }

    override fun init() {

        initUserData()

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSignup.setOnClickListener {
            if (viewModel.profileEditUiState.name.isBlank()) {
                makeToast("닉네임을 입력해주세요")
                return@setOnClickListener
            }

            UserSession.userId?.let { userId ->
                viewModel.putUserProfile(
                    userId,
                    ProfileEditRequest(
                        loginId = UserSession.userLoginId!!,
                        name = viewModel.profileEditUiState.name,
                        type = viewModel.profileEditUiState.type,
                        comment = viewModel.profileEditUiState.comment
                    )
                ) {
                    if (viewModel.profileEditUiState.isProfileImageChanged) {

                        if (viewModel.profileEditUiState.isProfileImageExisted) {
                            Timber.d("풋 할거에요")
                            viewModel.putProfileImage(
                                userId,
                                viewModel.profileEditUiState.imageFile!!
                            ) {
                                onSuccessEditProfile()
                            }
                        } else {
                            Timber.d("포스트 할거에요")
                            viewModel.postProfileImage(
                                userId,
                                viewModel.profileEditUiState.imageFile!!
                            ) {
                                onSuccessEditProfile()
                            }
                        }
                    } else {
                        onSuccessEditProfile()
                    }

                }

            }
        }

        binding.ivBasicProfile.setOnClickListener {
            openGallery()
        }

        binding.tietNickname.addTextChangedListener { name ->
            viewModel.setName(name.toString())
        }

        binding.tietComment.addTextChangedListener { comment ->
            viewModel.setComment(comment.toString())
        }

        binding.cgChip.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds[0] == 1) viewModel.setType("주니어")
            else viewModel.setType("시니어")
        }
    }

    private fun onSuccessEditProfile() {
        makeToast("프로필이 성공적으로 수정되었습니다.")
        findNavController().popBackStack()
    }

    private fun initUserData() {

        val user = args.profileData
        with(binding) {

            UserSession.userLoginId = user.loginId

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
        Timber.d("프로필 이그지스트 셋팅 ${user.profileImage}")
        viewModel.setMyProfileEditUiState(
            ProfileEditUiState(
                name = user.name,
                type = user.type,
                comment = user.comment,
                isProfileImageExisted = user.profileImage.isNotBlank()
            )
        )
    }

    private fun createFileFromUri(uri: Uri, context: Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_image.jpg")
        try {
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            outputStream.close()
            inputStream?.close()
        } catch (e: IOException) {
            Timber.e(e, "Failed to create file from URI")
        }
        return file
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as MainActivity).showBottomNavigation()
    }

}