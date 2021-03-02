package com.simoneconigliaro.pictureengine.ui

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.simoneconigliaro.pictureengine.R
import com.simoneconigliaro.pictureengine.utils.BitmapUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_picture_crop.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.io.IOException

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PictureCropFragment : Fragment(R.layout.fragment_picture_crop) {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAspectRatioCropImage()
        initToolBar()
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {

                viewState.detailFragmentViews.pictureDetail?.let { pictureDetail ->
                    setImageToCropView(pictureDetail.regularUrl)
                }
            }
        })
    }

    private fun setAspectRatioCropImage() {
        val width = requireContext().resources.displayMetrics.widthPixels
        val height = requireContext().resources.displayMetrics.heightPixels
        cropImageView.setAspectRatio(width, height)
    }

    private fun setImageToCropView(imageURL: String?){
        CoroutineScope(IO).launch {
            val bitmap = BitmapUtil.downloadBitmapFromUrl(imageURL)
            bitmap?.let {
                withContext(Main){
                    cropImageView.setImageBitmap(it)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.crop_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                return true
            }
            R.id.action_set_wallpaper -> {
                setWallpaper(cropImageView.croppedImage)
                findNavController().popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initToolBar() {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(set_wallpaper_tool_bar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
            (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true)
        }
    }

    private fun setWallpaper(bitmap: Bitmap?) {
        val wallpaperManager = WallpaperManager.getInstance(requireContext())
        try {
            wallpaperManager.setBitmap(bitmap)
        } catch (e: IOException) {
            Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}