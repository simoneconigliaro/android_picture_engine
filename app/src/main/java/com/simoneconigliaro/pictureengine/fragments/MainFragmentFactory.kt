package com.simoneconigliaro.pictureengine.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.simoneconigliaro.pictureengine.ui.detail.DetailFragment
import com.simoneconigliaro.pictureengine.ui.list.ListFragment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainFragmentFactory
@Inject
constructor(

) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {

        when (className) {

            ListFragment::class.java.name -> {
                return ListFragment()
            }

            DetailFragment::class.java.name -> {
                return DetailFragment()
            }

            else -> {
                return super.instantiate(classLoader, className)
            }
        }
    }
}