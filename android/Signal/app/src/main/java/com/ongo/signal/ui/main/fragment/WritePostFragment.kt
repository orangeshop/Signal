package com.ongo.signal.ui.main.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ongo.signal.R
import com.ongo.signal.config.BaseFragment
import com.ongo.signal.databinding.FragmentWritePostBinding
import com.ongo.signal.ui.main.MainViewModel
import com.ongo.signal.ui.main.adapter.ImageAdapter
import com.ongo.signal.util.PermissionUtil
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class WritePostFragment : BaseFragment<FragmentWritePostBinding>(R.layout.fragment_write_post) {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var sttLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private var currentPhotoPath: String? = null
    private var currentTarget: Int = 0
    private lateinit var imageAdapter: ImageAdapter

    override fun init() {
        binding.fragment = this
        setUpSpinner()

        imageAdapter = ImageAdapter({ uri -> onRemoveImageClick(uri) }, true)
        binding.rvImage.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }

        sttLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val speechResults =
                        result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val recognizedText = speechResults?.get(0).toString()
                    when (currentTarget) {
                        R.id.iv_title_mic -> binding.etTitle.setText(recognizedText)
                        R.id.iv_content_mic -> binding.etContent.setText(recognizedText)
                    }
                }
            }

        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    currentPhotoPath?.let { path ->
                        val file = File(path)
                        val uri = Uri.fromFile(file)
                        imageAdapter.addImage(uri)
                    }
                }
            }

        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val uri = result.data!!.data
                    uri?.let {
                        imageAdapter.addImage(uri)
                    }
                }
            }

        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (!PermissionUtil.checkAndRequestPermissions(this, permissions)) {
            makeToast("권한이 필요합니다.")
        }
    }

    private fun setUpSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tag_array,
            R.layout.item_spinnner
        )
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        binding.spinner.adapter = adapter
        binding.spinner.setBackgroundResource(R.drawable.background_spinner)
    }

    fun setupListeners() {
        binding.ivTitleMic.setOnClickListener {
            currentTarget = R.id.iv_title_mic
            startSpeechToText()
        }

        binding.ivContentMic.setOnClickListener {
            currentTarget = R.id.iv_content_mic
            startSpeechToText()
        }
    }

    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "검색어를 말해주세요.")
        }
        sttLauncher.launch(intent)
    }

    fun showImagePickerPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.popup_image_select, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_take_photo -> {
                    takePictureFromCamera()
                    true
                }

                R.id.menu_choose_from_gallery -> {
                    pickImageFromGallery()
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun takePictureFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.ongo.signal.fileprovider",
                    it
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                cameraLauncher.launch(intent)
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    fun onRemoveImageClick(uri: Uri) {
        imageAdapter.removeImage(uri)
    }

    fun onRegisterButtonClick() {
        findNavController().navigate(R.id.action_writePostFragment_to_postFragment)
    }
}