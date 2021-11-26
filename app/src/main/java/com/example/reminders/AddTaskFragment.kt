package com.example.reminders

import android.annotation.SuppressLint
import android.app.*
import android.app.AlarmManager.INTERVAL_DAY
import android.app.AlarmManager.RTC_WAKEUP
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.example.reminders.databinding.FragmentAddTaskBinding
import com.example.reminders.util.ReminderWorker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.fragment_add_task.*
import android.text.format.DateFormat;
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class AddTaskFragment : Fragment() {

    private var _binding : FragmentAddTaskBinding? = null
    private val binding get() = _binding!!

    var year = 0;
    var month = 0;
    var day = 0;
    var hour = 0;
    var minute = 0;
    var cal = Calendar.getInstance()
    private val viewModel: AddTaskViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()" }
        ViewModelProvider(this, AddTaskViewModel.Factory(activity.application))
            .get(AddTaskViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddTaskBinding.inflate(layoutInflater, container , false)
        return binding.root
    }
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            binding.apply {
                textViewdate!!.text = "--/--/----"

                txtDate.setOnClickListener {
                    activity?.let {
                        DatePickerDialog(
                            it, dateSetListener,
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)).show() }
                }
                txttime.setOnClickListener {
                    activity?.let {
                        TimePickerDialog(
                            it,
                            timeSetListener,
                            cal.get(Calendar.HOUR),
                            cal.get(Calendar.MINUTE),
                            is24HourFormat(it)).show() }
                }
                button.setOnClickListener{
                    var c = Calendar.getInstance()
                    c.set(year, month, day, hour, minute)
                    val today = Calendar.getInstance()
                    Toast.makeText(activity, "${(cal.timeInMillis/1000L) - (today.timeInMillis/1000L)}", Toast.LENGTH_SHORT).show()
                    var diff = (cal.timeInMillis/1000L) - (today.timeInMillis/1000L)
                    val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                        .setInitialDelay(diff, TimeUnit.SECONDS)
                        .setInputData(workDataOf("title" to "${addTitle.text.toString()}",
                            "message" to "${addDesciption.text.toString()}"))
                        .build()
                    WorkManager.getInstance(requireContext()).enqueue(workRequest)
                    viewModel.insertItem(title = addTitle.text.toString(), description = addDesciption.text.toString())
                    navigate()
                }
            }
        }
    private val timeSetListener = object :TimePickerDialog.OnTimeSetListener{
        override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
            cal.set(Calendar.HOUR, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            updateTimeInView() }
    }
            private val dateSetListener = object : DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, month)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    updateDateInView() }
            }
            private fun updateDateInView(){
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.UK)
                binding.textViewdate.text = sdf.format(cal.time)
                this.year= cal.get(Calendar.YEAR)
                this.day = cal.get(Calendar.MONTH)
                this.month = cal.get(Calendar.MONTH)

                Toast.makeText(activity, "${this.month}", Toast.LENGTH_SHORT).show()
            }
            private fun updateTimeInView() {
                val myFormat = "EEE, d MMM yyyy HH:mm:ss Z"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                this.minute = cal.get(Calendar.MINUTE)
                this.hour = cal.get(Calendar.HOUR)
               binding.textViewdate.text = sdf.format(cal.time)
            }

            fun navigate(){
                val action = AddTaskFragmentDirections.actionAddTaskFragmentToMainFragment()
                findNavController().navigate(action)
            }

}