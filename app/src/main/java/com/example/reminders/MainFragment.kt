package com.example.reminders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.reminders.database.DatabaseReminders
import com.example.reminders.database.ReminderDatabase
import com.example.reminders.databinding.FragmentMainBinding
import com.example.reminders.domain.ReminderMain
import com.example.reminders.util.EventObserver
import com.example.reminders.util.NotificationHelper
import com.example.reminders.util.ReminderWorker
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.concurrent.TimeUnit


class MainFragment : Fragment() {

        private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddTaskViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, AddTaskViewModel.Factory(activity.application))
            .get(AddTaskViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.editTaskFab.setOnClickListener{
            Toast.makeText(activity, "Add new Task", Toast.LENGTH_SHORT).show()
            val action = MainFragmentDirections.actionMainFragmentToAddTaskFragment()
            findNavController().navigate(action)
          //  NotificationHelper(it.context).createNotification("Todo Created", "A new Todo Has been created! ")

            val myWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(30, TimeUnit.SECONDS)
                .setInputData(workDataOf("title" to "Reminder created",
                    "message" to "A new reminder has been created "))
                .build()
            WorkManager.getInstance(requireContext()).enqueue(myWorkRequest)
        }

        viewModel.reminders.observe(viewLifecycleOwner){
            val adapter = AddTaskAdapter(viewModel)
            adapter.submitList(it)
            Toast.makeText(activity, "$it", Toast.LENGTH_LONG).show()
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        }
            setupNavigation()
    }

    private fun setupNavigation() {
        viewModel.openTaskEvent.observe(viewLifecycleOwner, EventObserver {
            openTaskDetails(it)
        })

    }
    private fun openTaskDetails(reminder: ReminderMain) {
        val action = MainFragmentDirections.actionMainFragmentToTaskDetailFragment(reminder.title, reminder.description)
        findNavController().navigate(action)
    }


}