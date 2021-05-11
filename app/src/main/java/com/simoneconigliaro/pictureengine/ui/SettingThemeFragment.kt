package com.simoneconigliaro.pictureengine.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.simoneconigliaro.pictureengine.R
import com.simoneconigliaro.pictureengine.persistence.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_setting_theme.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingThemeFragment
constructor(
    private val preferencesManager: PreferencesManager
) : Fragment(R.layout.fragment_setting_theme) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolBar()

        setCheckRadioGroup()

        radio_light.setOnClickListener { saveTheme(MODE_NIGHT_NO) }
        radio_dark.setOnClickListener { saveTheme(MODE_NIGHT_YES) }
        radio_system_default.setOnClickListener { saveTheme(MODE_NIGHT_FOLLOW_SYSTEM) }
    }

    private fun saveTheme(theme: Int) {
        lifecycleScope.launch {
            preferencesManager.saveTheme(theme)
        }
    }

    private fun setCheckRadioGroup() {

        val theme = getDefaultNightMode()

        when (theme) {
            MODE_NIGHT_NO -> radio_group_theme.check(R.id.radio_light)
            MODE_NIGHT_YES -> radio_group_theme.check(R.id.radio_dark)
            MODE_NIGHT_FOLLOW_SYSTEM -> radio_group_theme.check(R.id.radio_system_default)
            else -> radio_group_theme.check(R.id.radio_system_default)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initToolBar() {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(set_theme_tool_bar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
            (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true)
        }
    }


}