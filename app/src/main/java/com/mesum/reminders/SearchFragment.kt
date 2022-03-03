package com.mesum.reminders

import android.app.Activity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mesum.reminders.databinding.FragmentSearchBinding

import androidx.appcompat.app.AppCompatActivity


import androidx.navigation.fragment.findNavController
import androidx.work.WorkManager
import com.mesum.reminders.domain.ReminderMain
import kotlinx.android.synthetic.main.fragment_search.*
import java.util.*


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddTaskViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, AddTaskViewModel.Factory(activity.application))
            .get(AddTaskViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        deleteWorkRequest()
      //  setupNavigation()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater?.inflate(R.menu.options_menu, menu)
        val searchView = SearchView(((context as MainActivity).supportActionBar?.themedContext ?: context)!!)
        menu.findItem(R.id.action_search).apply {
            //Set to false tp Expands the search view
            searchView.setIconifiedByDefault(false)
                searchView.requestFocus()
                activity?.let { showKeyboard(it) }
           // inputMethodManager.showSoftInput(searchView, 0)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            actionView = searchView
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean{
                getQuerryedReminder(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                getQuerryedReminder(newText)
                return true
            }
        })
        searchView.setOnClickListener {view ->  }

    }

    private fun getQuerryedReminder(query: String) {
        viewModel.getMultiReminder(query).observe(viewLifecycleOwner){
            val adapter = AddTaskAdapter(viewModel)
            adapter.submitList(it)
            recycler_view_search.adapter = adapter
            recycler_view_search.layoutManager = LinearLayoutManager(this.requireContext())
        }
    }



    //Deletes the workRequest when the value of currentWorker changes
    private fun deleteWorkRequest(){
        viewModel.currentWorker.observe(viewLifecycleOwner){
            WorkManager.getInstance(this.requireContext()).cancelWorkById(UUID.fromString(it))
            Toast.makeText(activity, "Work Request Deleted: $it", Toast.LENGTH_SHORT).show()
        }
    }

   /* private fun setupNavigation() {
        viewModel.openTaskEvent.observe(viewLifecycleOwner, EventObserver {
            openTaskDetails(it)
        })

    }*/
    private fun openTaskDetails(reminder: ReminderMain) {
        val action = SearchFragmentDirections.actionSearchFragmentToTaskDetailFragment(reminder.title, reminder.description)
        findNavController().navigate(action)
        activity?.let { viewModel.hideKeyboard(it) }
    }


    private fun showKeyboard(activity: Activity) {
        val inputManager = activity
            .getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager

        // check if no view has focus:

        inputManager.toggleSoftInput(0,0)


    }



}