package com.simoneconigliaro.pictureengine.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.simoneconigliaro.pictureengine.R
import com.simoneconigliaro.pictureengine.model.Picture
import kotlinx.android.synthetic.main.layout_picture_list_item.view.*


class PictureAdapter
constructor(
    private val requestManager: RequestManager,
    private val interaction: Interaction
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = "PictureAdapter"

    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Picture>() {
        override fun areItemsTheSame(oldItem: Picture, newItem: Picture): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Picture, newItem: Picture): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(
        PictureRecyclerChangeCallback(this),
        AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
    )

    // adding this callback to the differ fixed some weird behaviour when querying and filtering the list
    internal inner class PictureRecyclerChangeCallback(
        private val adapter: PictureAdapter
    ) : ListUpdateCallback {
        // it gets triggered when an item on the list changes (one of its properties or the object itself)
        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload)
        }

        // when a list item is rearranged
        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetChanged()
        }

        // when a list item is inserted
        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count)
        }

        // when a list item is removed
        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetChanged()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PictureViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_picture_list_item,
                parent,
                false
            ),
            requestManager,
            interaction
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is PictureViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    class PictureViewHolder
    constructor(
        itemView: View,
        private val requestManager: RequestManager,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Picture) {

            itemView.setOnClickListener {
                interaction?.onItemSelected(item.id)
            }

            itemView.tv_username.text = item.username

            requestManager.load(item.userPicture).into(itemView.iv_user_picture)

            requestManager.load(item.url).into(itemView.iv_picture)
        }
    }

    fun submitList(newList: List<Picture>?) {
        differ.submitList(newList)
    }

    fun getList(): List<Picture> {
        return differ.currentList
    }

    interface Interaction {
        fun onItemSelected(id: String)
    }
}