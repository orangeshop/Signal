package com.ongo.signal.ui.login

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentSignupBinding
import com.ongo.signal.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SignupFragment : BaseFragment<FragmentSignupBinding>(R.layout.fragment_signup) {

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
                binding.ivBasicProfile.setImageURI(imageUri)
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
                viewModel.postSignup {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            } else {
                makeToast("${result.second}")
            }
        }
    }

    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

}