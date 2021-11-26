package com.example.reminders

import android.R.id
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reminders.databinding.ReminderitemBinding
import com.example.reminders.domain.ReminderMain
import android.widget.CompoundButton

import android.R.id.checkbox
import android.content.Context
import android.widget.Toast
import com.example.reminders.databinding.FragmentAddTaskBinding


class AddTaskAdapter(private var viewModel: AddTaskViewModel) : ListAdapter<ReminderMain, AddTaskAdapter.TaskViewHolder>(DiffCAllBack) {
    companion object DiffCAllBack : DiffUtil.ItemCallback<ReminderMain>() {
        override fun areItemsTheSame(oldItem: ReminderMain, newItem: ReminderMain): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ReminderMain, newItem: ReminderMain): Boolean {
            return oldItem.title == newItem.title
        }
    }

    class TaskViewHolder(private var binding: ReminderitemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reminder: ReminderMain, viewModel: AddTaskViewModel) {
            binding.apply {

                viewmodel = viewModel
                reminders = reminder
                executePendingBindings()

                titleText.text = reminder.title

                checkbox.setOnCheckedChangeListener { _, _ ->
                    if (checkbox.isChecked) {
                        val strike = StrikethroughSpan()
                        val spannable = SpannableStringBuilder()
                        spannable.append(reminder.title)
                            .setSpan(
                                strike,
                                spannable.length - reminder.title.length,
                                spannable.length,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )

                        titleText.text = spannable
                        titleText.setOnClickListener { this.viewmodel!!.deleteItem(reminder) }

                    } else {

                        titleText.text = reminder.title
                    }

                }
            }
        }}

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            return TaskViewHolder(ReminderitemBinding.inflate(LayoutInflater.from(parent.context)))
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            //Each item in viewHolder
            var reminder = getItem(position)
            holder.bind(reminder, viewModel)

        }
    }
