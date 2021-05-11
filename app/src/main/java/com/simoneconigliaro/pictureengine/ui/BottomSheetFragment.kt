package com.simoneconigliaro.pictureengine.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simoneconigliaro.pictureengine.R
import com.simoneconigliaro.pictureengine.persistence.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import javax.inject.Inject


@AndroidEntryPoint
class BottomSheetFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layout_about.setOnClickListener {
            showAboutDialog()
        }

        layout_theme.setOnClickListener {
            findNavController().navigate(R.id.action_bottomSheetFragment_to_settingThemeFragment)

        }
    }

    private fun showAboutDialog() {
        MaterialDialog(requireContext()).show {
            cornerRadius(16f)
            title(R.string.about)
            message(R.string.about_message)
            positiveButton(R.string.ok) { dialog ->
                dialog.dismiss()
            }
        }
    }
}