package com.simoneconigliaro.pictureengine.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.simoneconigliaro.pictureengine.R
import com.simoneconigliaro.pictureengine.model.Picture
import kotlinx.android.synthetic.main.layout_picture_list_item.view.*

class PictureAdapter
constructor(
    private val requestManager: RequestManager,
    private val interaction: Interaction
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Picture>() {
        override fun areItemsTheSame(oldItem: Picture, newItem: Picture): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Picture, newItem: Picture): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


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

    interface Interaction {
        fun onItemSelected(id: String)
    }


}