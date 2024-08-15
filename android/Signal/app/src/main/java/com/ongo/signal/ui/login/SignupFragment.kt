package com.ongo.signal.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.config.UserSession
import com.ongo.signal.databinding.FragmentSignupBinding
import com.ongo.signal.ui.MainActivity
import com.ongo.signal.ui.video.repository.VideoRepository
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class SignupFragment : BaseFragment<FragmentSignupBinding>(R.layout.fragment_signup) {

    @Inject
    lateinit var videoRepository: VideoRepository

    private val viewModel: SignupViewModel by viewModels()

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

                viewModel.setImageFile(imageFile)
            }
        }
    }

    override fun init() {
        initViews()
    }

    private fun initViews() {

        binding.ivBasicProfile.setOnClickListener {
            openGallery()
        }

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.tietId.addTextChangedListener { id ->
            viewModel.setUserId(id.toString())
            viewModel.setPossible(null)
        }

        binding.btnIdCheck.setOnClickListener {
            if (binding.tietId.text.toString().isBlank()) {
                makeToast("아이디를 입력해주세요.")
                return@setOnClickListener
            }
            viewModel.checkDuplicatedId(binding.tietId.text.toString()) { isPossible ->
                binding.tvPossibleId.visibility = View.GONE
                binding.tvImpossibleId.visibility = View.GONE
                if (isPossible) binding.tvPossibleId.visibility = View.VISIBLE
                else binding.tvImpossibleId.visibility = View.VISIBLE
            }
        }

        binding.tietPassword.addTextChangedListener { password ->
            viewModel.setPassword(password.toString())
        }

        binding.tietPasswordCheck.addTextChangedListener { passwordCheck ->
            viewModel.setPasswordCheck(passwordCheck.toString())
        }

        binding.tietNickname.addTextChangedListener { name ->
            viewModel.setName(name.toString())
        }

        binding.cgChip.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds[0] == 1) viewModel.setType("주니어")
            else viewModel.setType("시니어")
        }

        binding.btnComplete.setOnClickListener {
            val result = viewModel.checkUIState()
            if (result.first) {
                viewModel.postSignup { userId ->
                    viewModel.uiState.imageFile?.let { imageFile ->
                        viewModel.postProfileImage(userId, imageFile) {
                            videoRepository.login(
                                UserSession.userId.toString(), UserSession.userEncodePassword!!
                            ) { isDone, reason ->
                                if (!isDone) {
                                    makeToast(reason.toString())
                                } else {
                                    if (isAdded && !isDetached) {
                                        val intent =
                                            Intent(requireContext(), MainActivity::class.java)
                                        startActivity(intent)
                                        requireActivity().finish()
                                    }
                                }
                            }
                        }
                    } ?: run {
                        videoRepository.login(
                            UserSession.userId.toString(), UserSession.userEncodePassword!!
                        ) { isDone, reason ->
                            if (!isDone) {
                                makeToast(reason.toString())
                            } else {
                                if (isAdded && !isDetached) {
                                    val intent =
                                        Intent(requireContext(), MainActivity::class.java)
                                    startActivity(intent)
                                    requireActivity().finish()
                                }
                            }
                        }
                    }
                }
            } else {
                makeToast("${result.second}")
            }
        }
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

}