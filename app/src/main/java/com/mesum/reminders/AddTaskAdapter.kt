package com.mesum.reminders

import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.mesum.reminders.databinding.ReminderitemBinding
import com.mesum.reminders.domain.ReminderMain
import java.text.SimpleDateFormat
import android.os.Environment

import android.provider.MediaStore

import android.media.ExifInterface
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Bitmap

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.core.content.contentValuesOf
import java.io.*
import java.lang.Exception


class AddTaskAdapter(private var viewModel:AddTaskViewModel) : ListAdapter<ReminderMain, AddTaskAdapter.TaskViewHolder>(
    DiffCAllBack
) {
    companion object DiffCAllBack : DiffUtil.ItemCallback<ReminderMain>() {
        override fun areItemsTheSame(oldItem: ReminderMain, newItem: ReminderMain): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: ReminderMain, newItem: ReminderMain): Boolean {
            return oldItem.title == newItem.title
        }
    }

    class TaskViewHolder(private var binding: ReminderitemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(reminder: ReminderMain, viewModel: AddTaskViewModel) {
            binding.apply {

                this.reminders = reminder
                viewmodel = viewModel
                reminders = reminder
                executePendingBindings()

                titleText.text = reminder.title
               // dateTime.text = reminder.date

                val date = SimpleDateFormat("E, MMM d, yyyy")
                val time = SimpleDateFormat("hh:mm a")
                val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")

                //val fullSizeBitmap = BitmapFactory.decodeFile(reminder.image)
              //  val reducedBitmap = ImageResizer.reduceBitmapSize(fullSizeBitmap, 2073600)
                //var file : File = getBitmapFile(reducedBitmap)

                if (reminder.date != "none"  ) {
                    dateTime.text = date.format(inputFormat.parse(reminder.date))
                    timeTime.text = time.format(inputFormat.parse(reminder.date))
                }


                checkbox.setOnCheckedChangeListener { _, _ ->}
                    if (checkbox.isChecked) {
                        val strike = StrikethroughSpan()
                        val spannable = SpannableStringBuilder()
                        spannable.append(reminder.title)
                            .setSpan(
                                strike,
                                spannable.length - reminder.title.length,
                                spannable.length,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        titleText.text = spannable

                       // this.viewmodel!!.completeTask(reminder.title, true)



                    } else {
                        titleText.text = reminder.title
                      //  this.viewmodel!!.completeTask(reminder.title, false)

                    }


            }
        }

    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):TaskViewHolder {
            return TaskViewHolder(
                ReminderitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            //Each item in viewHolder
            val reminder = getItem(position)
            holder.bind(reminder, viewModel)

        }








    }
