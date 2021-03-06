package com.simoneconigliaro.pictureengine.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.bumptech.glide.RequestManager
import com.simoneconigliaro.pictureengine.persistence.PreferencesManager
import com.simoneconigliaro.pictureengine.ui.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@FlowPreview
@Singleton
class MainFragmentFactory
@Inject
constructor(
    private val requestManager: RequestManager,
    private val preferencesManager: PreferencesManager
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {

        when (className) {

            PictureListFragment::class.java.name -> {
                return PictureListFragment(requestManager, preferencesManager)
            }
            PictureDetailFragment::class.java.name -> {
                return PictureDetailFragment(requestManager)
            }
            PictureFullFragment::class.java.name -> {
                return PictureFullFragment(requestManager)
            }
            PictureCropFragment::class.java.name -> {
                return PictureCropFragment()
            }
            PictureSearchFragment::class.java.name -> {
                return PictureSearchFragment(requestManager)
            }
            SettingThemeFragment::class.java.name -> {
                return SettingThemeFragment(preferencesManager)
            }
            else -> {
                return super.instantiate(classLoader, className)
            }
        }
    }
}