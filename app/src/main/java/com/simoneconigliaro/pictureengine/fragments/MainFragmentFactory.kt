package com.simoneconigliaro.pictureengine.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.bumptech.glide.RequestManager
import com.simoneconigliaro.pictureengine.ui.PictureDetailFragment
import com.simoneconigliaro.pictureengine.ui.PictureListFragment
import javax.inject.Inject
import javax.inject.Singleton

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
                return PictureDetailFragment()
            }

            else -> {
                return super.instantiate(classLoader, className)
            }
        }
    }
}