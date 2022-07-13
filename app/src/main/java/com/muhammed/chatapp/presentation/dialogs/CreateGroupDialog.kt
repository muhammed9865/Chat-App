package com.muhammed.chatapp.presentation.dialogs

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.muhammed.chatapp.Filter
import com.muhammed.chatapp.databinding.DialogCreateNewGroupBinding
import com.muhammed.chatapp.presentation.state.CreateGroupStates
import com.muhammed.chatapp.presentation.viewmodel.CreateGroupViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CreateGroupDialog : BottomSheetDialogFragment(), ChipGroup.OnCheckedStateChangeListener {
    private val binding by lazy { DialogCreateNewGroupBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<CreateGroupViewModel>()

    private var isNewInterestAdded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            bottomSheet?.let {
                BottomSheetBehavior.from(bottomSheet).state =
                    BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doOnStateChanged()
        with(binding) {
            groupCategory.setOnCheckedStateChangeListener(this@CreateGroupDialog)
            groupPhoto.setOnClickListener {
                uploadImage()
            }

            crGroupTitle.doOnTextChanged { text, _, _, _ ->
                viewModel.title = text.toString()
            }
            crGroupDescrption.doOnTextChanged { text, _, _, _ ->
                viewModel.description = text.toString()
            }

            addInterest.setOnClickListener {
                addInterest()
            }
            createGroupBtn.setOnClickListener { viewModel.createGroup() }
        }
    }

    override fun onCheckedChanged(group: ChipGroup, checkedIds: MutableList<Int>) {
        with(binding) {
            var filter: Filter = Filter.Default()
            when (checkedIds[0]) {
                categoryArt.id -> filter = Filter.Art()
                categoryHealth.id -> filter = Filter.Health()
                categoryCrypto.id -> filter = Filter.Crypto()
                categoryFinance.id -> filter = Filter.Finance()
                categoryMovies.id -> filter = Filter.Movies()
                categorySports.id -> filter = Filter.Sports()
            }
            viewModel.category = filter.title
        }
    }

    private val selectPhotoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.data != null) {
                val selectedImageUri: Uri = it.data!!.data!!

                binding.groupPhoto.apply {
                    setImageURI(selectedImageUri)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    viewModel.image = drawable.toBitmap()
                }
            }
        }

    private fun uploadImage() {
        if (requireActivity().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent().also {
                it.type = "image/*"
                it.action = Intent.ACTION_GET_CONTENT
            }
            selectPhotoLauncher.launch(intent)
        } else {
            requireActivity().requestPermissions(
                listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE).toTypedArray(),
                1
            )
        }
    }

    private fun addInterest() {
        with(binding) {
            addInterest.visibility = View.GONE

            addInterestText.apply {
                visibility = View.VISIBLE
                showSoftInputOnFocus = true
                requestFocus()
                setOnFocusChangeListener { _  , hasFocus ->
                    if (!hasFocus) {
                        if (editableText.isNotEmpty()) {
                            val chipDrawable = ChipDrawable.createFromAttributes(
                                requireContext(),
                                null,
                                0,
                                com.google.android.material.R.style.Widget_MaterialComponents_Chip_Choice
                            )
                            addInterest.apply {
                                setChipDrawable(chipDrawable)
                                text = editableText.toString()
                                visibility = View.VISIBLE
                                isChecked = true
                            }


                            visibility = View.GONE

                            viewModel.category = editableText.toString()
                        }else {
                            visibility = View.GONE
                            addInterest.visibility = View.VISIBLE
                        }
                    }
                }
            }



        }
    }


    private fun doOnStateChanged() {
        lifecycleScope.launch {
            viewModel.states.collect { state ->
                when (state) {
                    is CreateGroupStates.Failed -> {
                        binding.creatingPb.visibility = View.GONE
                        Log.e("CreateGroup", state.error)
                    }
                    is CreateGroupStates.Creating -> binding.creatingPb.visibility = View.VISIBLE
                    is CreateGroupStates.CreatedSuccessfully -> {
                        binding.creatingPb.visibility = View.GONE
                        dismiss()
                    }
                }

            }
        }
    }

}