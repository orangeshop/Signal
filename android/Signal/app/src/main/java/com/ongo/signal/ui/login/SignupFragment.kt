package com.ongo.signal.ui.login

import android.content.Intent
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentSignupBinding
import com.ongo.signal.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SignupFragment : BaseFragment<FragmentSignupBinding>(R.layout.fragment_signup) {

    private val viewModel: SignupViewModel by viewModels()

    override fun init() {
        initViews()
    }

    private fun initViews() {
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
                Timber.d("회원가입 쐇다 ${viewModel.uiState}")
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
}