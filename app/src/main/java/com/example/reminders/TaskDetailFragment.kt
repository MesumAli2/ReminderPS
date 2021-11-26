package com.example.reminders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.reminders.databinding.FragmentTaskDetailBinding


class TaskDetailFragment : Fragment() {

    private val navigationArg : TaskDetailFragmentArgs by navArgs()
    private var _binding : FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel : AddTaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTaskDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.taskDetailTitleText.text = navigationArg.tasktitle
        binding.taskDetailDescriptionText.text = navigationArg.taskdescription
    }
}