package com.simoneconigliaro.pictureengine.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.bumptech.glide.RequestManager
import com.simoneconigliaro.pictureengine.ui.PictureCropFragment
import com.simoneconigliaro.pictureengine.ui.PictureDetailFragment
import com.simoneconigliaro.pictureengine.ui.PictureFullFragment
import com.simoneconigliaro.pictureengine.ui.PictureListFragment
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
    private val requestManager: RequestManager
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {

        when (className) {

            PictureListFragment::class.java.name -> {
                return PictureListFragment(requestManager)
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
            else -> {
                return super.instantiate(classLoader, className)
            }
        }
    }
}