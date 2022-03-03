package com.mesum.reminders

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mesum.reminders.database.ImagesEntitys
import com.mesum.reminders.databinding.FragmentTaskDetailBinding
import com.mesum.reminders.util.NotificationHelper
import kotlinx.android.synthetic.main.fragment_add_task.*
import kotlinx.android.synthetic.main.fragment_task_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.withTestContext
import java.text.SimpleDateFormat


class TaskDetailFragment : Fragment() {

     val navigationArg : TaskDetailFragmentArgs by navArgs()
    private var _binding : FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddTaskViewModel by lazy {
        val activity = requireNotNull(this.activity) {}
        ViewModelProvider(this, AddTaskViewModel.Factory(activity.application)).get(AddTaskViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTaskDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getReminder(navigationArg.taskTitle).observe(viewLifecycleOwner){
            val date = SimpleDateFormat("E, MMM d, yyyy")
            val time = SimpleDateFormat("hh:mm a")
            val dateTime = SimpleDateFormat("d MMM yyyy HH:mm ")
            val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
            task_detail_title_text.text = navigationArg.taskTitle
            task_detail_description_text.text = navigationArg.taskDescription



            if (it.todoDate != "none"){
                card.visibility = View.VISIBLE
                date_time.text = dateTime.format(inputFormat.parse(it.todoDate))
                repeating.text = it.frequency
                ringer.text =it.ringPattern
                repeating2.text = "Default Ringtone"
            }else{
                card.visibility = View.GONE
            }


                   /// savedimage.setImageURI(Uri.parse(viewModel.reminderwithimages(it.title)[1].images))


        viewModel.reminderwithimages(it.title).observe(viewLifecycleOwner){
            showImages(it.images)
        }

               // savedimage.setImageURI(Uri.parse(it.image))
        }

        edit_button.setOnClickListener{
            val action = TaskDetailFragmentDirections.actionTaskDetailFragmentToAddTaskFragment(navigationArg.taskTitle, "Edit")
            findNavController().navigate(action)
        }
        deleteicon.setOnClickListener {
            viewModel.getReminder(navigationArg.taskTitle).observe(viewLifecycleOwner){
                viewModel.deleteItem(it)
                findNavController().navigate(R.id.mainFragment)
            }
        }
    }

    private fun showImages(images: List<ImagesEntitys>) {


        val recyclerView = binding.imageRecyclerView
        val adapter = TaskDetailAdapter()
        adapter.submitList(images)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = adapter

    }


}


