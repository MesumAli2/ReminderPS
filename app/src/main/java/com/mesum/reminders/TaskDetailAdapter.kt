package com.mesum.reminders

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mesum.reminders.database.ImagesEntitys
import com.mesum.reminders.databinding.ImageitemsBinding
import com.mesum.reminders.databinding.ReminderitemBinding
import com.mesum.reminders.domain.ReminderMain

class TaskDetailAdapter() : androidx.recyclerview.widget.ListAdapter<ImagesEntitys, TaskDetailAdapter.ImageViewholder>(
    DiffCAllBack
) {
    companion object DiffCAllBack : DiffUtil.ItemCallback<ImagesEntitys>() {
        override fun areItemsTheSame(oldItem: ImagesEntitys, newItem: ImagesEntitys): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ImagesEntitys, newItem: ImagesEntitys): Boolean {
            return oldItem == newItem
        }
    }

    class ImageViewholder(private var binding: ImageitemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(image: ImagesEntitys){
                binding.images.setImageURI(Uri.parse(image.images))
            }
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageViewholder {
        return ImageViewholder(
            ImageitemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewholder, position: Int) {
        holder.bind(getItem(position))
    }


}