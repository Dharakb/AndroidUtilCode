package com.androidutilcode.recyclerViewAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.androidutilcode.databinding.ListItemBinding
import com.androidutilcode.models.ThemesData

class ThemesNameListAdapter
    : ListAdapter<ThemesData, ThemesNameListAdapter.ViewHolder>(CardThemeListCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ListItemBinding =
            ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ThemesData) {
            with(binding) {
                themesData = item
                executePendingBindings()
            }
        }
    }

    private class CardThemeListCallBack : DiffUtil.ItemCallback<ThemesData>() {

        override fun areItemsTheSame(oldItem: ThemesData, newItem: ThemesData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ThemesData, newItem: ThemesData): Boolean {
            return oldItem.id == newItem.id
        }
    }
}