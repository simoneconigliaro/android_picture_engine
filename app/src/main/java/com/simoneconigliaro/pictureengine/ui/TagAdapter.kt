package com.simoneconigliaro.pictureengine.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.simoneconigliaro.pictureengine.R
import kotlinx.android.synthetic.main.layout_tag_list_item.view.*

class TagAdapter
constructor(
    private val interaction: Interaction
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TagViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_tag_list_item,
                parent,
                false
            ),
            interaction
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is TagViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    class TagViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: String) {

            itemView.setOnClickListener {
                interaction?.onItemSelected(item)
            }

            itemView.tv_tag_item.text = item
        }
    }

    fun submitList(newList: List<String>) {
        differ.submitList(newList)
    }

    interface Interaction {
        fun onItemSelected(tag: String)
    }
}