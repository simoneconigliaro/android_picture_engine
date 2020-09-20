package com.simoneconigliaro.pictureengine.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.simoneconigliaro.pictureengine.R
import com.simoneconigliaro.pictureengine.model.PictureDetail
import com.simoneconigliaro.pictureengine.utils.ErrorStateCallback
import com.simoneconigliaro.pictureengine.utils.TextUtil
import com.simoneconigliaro.pictureengine.utils.UIController
import com.simoneconigliaro.pictureengine.utils.UnitUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_picture_detail.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PictureDetailFragment
constructor(
    private val requestManager: RequestManager
) : Fragment(R.layout.fragment_picture_detail), TagAdapter.Interaction {

    private val TAG = "PictureDetailFragment"

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var uiController: UIController

    private lateinit var tagAdapter: TagAdapter

    private var urlToShare: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCustomStatusBarHeight()
        setCustomToolBar()
        initRecyclerView()
        subscribeObservers()

        iv_picture_detail.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_pictureDetailFragment_to_pictureFullFragment)
        })


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.let {
            if (activity is MainActivity) {
                try {
                    uiController = context as UIController
                } catch (e: ClassCastException) {
                    e.printStackTrace()
                }
            }
        }
    }


    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {

                viewState.detailFragmentViews.pictureDetail?.let { pictureDetail ->
                    Log.d(TAG, "subscribeObservers: ${pictureDetail}")
                    setViews(pictureDetail)
                    pictureDetail.tags?.let {
                        tagAdapter.submitList(it)
                    }
                    urlToShare = pictureDetail.urlToShare

                }
            }
        })

        viewModel.errorState.observe(viewLifecycleOwner, Observer { errorState ->
            errorState?.let {
                uiController.onErrorReceived(
                    errorState = it,
                    errorStateCallback = object : ErrorStateCallback {
                        override fun removeErrorFromStack() {
                            viewModel.clearErrorState(0)
                        }
                    }
                )
            }
        })
    }

    private fun initRecyclerView() {
        recycler_view_tags.apply {
            layoutManager = LinearLayoutManager(
                this@PictureDetailFragment.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            tagAdapter =
                TagAdapter(this@PictureDetailFragment)
            adapter = tagAdapter
        }
    }

    private fun setViews(pictureDetail: PictureDetail) {

        val username = pictureDetail.username
        val location = pictureDetail.location
        val likes = UnitUtil.convertUnitNumber(pictureDetail.likes)
        val views = UnitUtil.convertUnitNumber(pictureDetail.views)
        val downloads = UnitUtil.convertUnitNumber(pictureDetail.downloads)
        val name = TextUtil.isFieldBlank(pictureDetail.cameraModel)
        val focalLength = UnitUtil.setUnitFocalLength(pictureDetail.cameraFocalLength)
        val iso = TextUtil.isFieldBlank(pictureDetail.cameraIso)
        val aperture = UnitUtil.setUnitAperture(pictureDetail.cameraAperture)
        val exposureTime = UnitUtil.setUnitExposureTime(pictureDetail.cameraExposureTime)
        val dimensions = TextUtil.areFieldsBlank(pictureDetail.width, pictureDetail.height)

        requestManager.load(pictureDetail.url).into(iv_picture_detail)
        requestManager.load(pictureDetail.userPicture).into(iv_user_picture_detail)

        showLocation(location)
        tv_username_detail.text = username
        tv_likes_detail.text = likes
        tv_views_detail.text = views
        tv_downloads_detail.text = downloads
        tv_camera_name_value.text = name
        tv_camera_focal_length_value.text = focalLength
        tv_camera_iso_value.text = iso
        tv_camera_aperture_value.text = aperture
        tv_camera_exposure_time_value.text = exposureTime
        tv_camera_pic_dimensions_value.text = dimensions
    }

    private fun showLocation(location: String?) {
        if (location.isNullOrBlank()) {
            tv_location_detail.visibility = View.GONE
        } else {
            tv_location_detail.text = (location)
        }
    }

    override fun onItemSelected(tag: String) {
        Toast.makeText(context, tag, Toast.LENGTH_LONG).show()
    }

    private fun setCustomStatusBarHeight() {
        var statusBarHeight = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        custom_status_bar_detail.layoutParams.height = statusBarHeight
    }

    private fun setCustomToolBar() {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(custom_tool_bar)
            (activity as AppCompatActivity).supportActionBar?.title = ""
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
            (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true);
        }
    }

    private fun shareIntent(){
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_TEXT, urlToShare)
        startActivity(Intent.createChooser(i, "Share URL"))
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                return true
            }
            R.id.action_share -> {
                shareIntent()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}