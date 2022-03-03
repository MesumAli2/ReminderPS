package com.mesum.reminders

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkManager
import com.mesum.reminders.databinding.FragmentMainBinding
import com.mesum.reminders.domain.ReminderMain
import com.mesum.reminders.util.EventObserver
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.*


class MainFragment : Fragment()  {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddTaskViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, AddTaskViewModel.Factory(activity.application)).get(AddTaskViewModel::class.java)
    }
    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Makes the option menu visible in the fragment


        setHasOptionsMenu(true) }

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
        (  activity as AppCompatActivity?)?.supportActionBar?.show()

        // Submits the list to recyclerView adapter matching the predicate
         fullui()
    }
    private fun fullui(){
        updateUI()
        deleteWorkRequest()
        setupNavigation()
    }


    //Updates UI with active an completed reminders
    private fun updateUI() {
        //Live data observer changes in the current state of  reminders
        viewModel.reminders.observe(viewLifecycleOwner) {

            //If user launches the application for the fitst time then show the welcome screen
            if (it.isNullOrEmpty()) {
                welcome.visibility = View.VISIBLE
                welcome_descrip.visibility = View.VISIBLE
                welcome_addBtn.visibility = View.VISIBLE
                welcome_addBtn.setOnClickListener { addTaskNavigation() }
            }


            //Adapter for displaying the active reminders
              val adapter = AddTaskAdapter(viewModel)
            adapter.submitList(it.filter { it.isActive })
            if(it.filter { it.isCompleted }.isEmpty()){
                complete_text.visibility = View.INVISIBLE
                recycler_completed.visibility = View.GONE
            }else{complete_text.visibility = View.VISIBLE
                recycler_completed.visibility = View.VISIBLE
            }
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this.requireContext())


            //Adapter for displaying the completed reminder
            val secondAdapter = AddTaskAdapter(viewModel)
            secondAdapter.submitList( it.filter { it.isCompleted })
            //If there are no active item hide the active recyclerView
            if(it.filter { it.isActive }.isEmpty()){
                recyclerView.visibility = View.GONE
                active_text.visibility = View.INVISIBLE
            }
            //Else if there are active reminder show active recycler view
            else {
                active_text.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
            }
            recycler_completed.adapter = secondAdapter
            recycler_completed.layoutManager = LinearLayoutManager(this.requireContext())

        }
    }
        //Deletes the workRequest when the value of currentWorker changes
        private fun deleteWorkRequest(){
            viewModel.currentWorker.observe(viewLifecycleOwner){
                WorkManager.getInstance(this.requireContext()).cancelWorkById(UUID.fromString(it))
            }
        }

    //Navigates to the reminder selected when openTaskEvent value is updated
    private fun setupNavigation() {
        viewModel.openTaskEvent.observe(viewLifecycleOwner, EventObserver {
            openTaskDetails(it)
        })

    }
    private fun openTaskDetails(reminder: ReminderMain) {
        val action = MainFragmentDirections.actionMainFragmentToTaskDetailFragment(reminder.title, reminder.description)
        findNavController().navigate(action)
    }

    private fun addTaskNavigation(){
        val action = MainFragmentDirections.actionMainFragmentToAddTaskFragment("", "Add")
        findNavController().navigate(action)
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()


        this.menu = menu

        inflater.inflate(R.menu.add, menu)
        inflater.inflate(R.menu.options_menu, menu)
        inflater.inflate(R.menu.filter_menu, menu)
        inflater.inflate(R.menu.set_menus, menu)

if (viewModel.isNightModeOn){
        menu.findItem(R.id.additem).setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_add_white))
        menu.findItem(R.id.menu_filter).setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_filter_list_white))
        menu.findItem(R.id.set_menu).setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_settings_white))
        menu.findItem(R.id.action_search).setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_search_white))
}

       /* val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = SearchView(((context as MainActivity).supportActionBar?.themedContext ?: context)!!)

        menu.findItem(R.id.action_search).apply {
            searchView.setIconifiedByDefault(false)
            searchView?.setSearchableInfo(
                searchManager.getSearchableInfo(ComponentName(requireActivity(), SearchableActivity::class.java))
            )
            actionView = searchView
        }*/
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_search ->
            { findNavController().navigate(R.id.action_mainFragment_to_searchFragment)
                return true
            }
            R.id.completed ->
            { findNavController().navigate(R.id.statisticsFragment)
                return true
            }
            R.id.settings ->
            {
                val action = MainFragmentDirections.actionMainFragmentToSettingsFragment()
                findNavController().navigate(action)
            }
            R.id.menu_filter ->
            {showFilteringPopUpMenu()
            return  true}

            R.id.additem ->{

                addTaskNavigation()
            }
            R.id.set_menu ->{
                showSettingsPopMenu()
            }

        }


        return super.onOptionsItemSelected(item);
    }

    private fun showSettingsPopMenu() {
        //find the menu item with drawable
        val view = activity?.findViewById<View>(R.id.set_menu) ?: return
        PopupMenu(requireContext(), view).run {
            //inflate the settings menu file with settings and stats menu item
            menuInflater.inflate(R.menu.settings_menu, menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.settings ->{
                        val action =MainFragmentDirections.actionMainFragmentToSettingsFragment()
                        findNavController().navigate(action)
                    }
                    R.id.completed ->{
                        val action = MainFragmentDirections.actionMainFragmentToStatisticsFragment()
                        findNavController().navigate(action)
                    }
                    }
                true }
            show()
        }
    }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_tasks, menu)
            setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.active -> {
                                updateUI()
                                val adapter =
                                 AddTaskAdapter(viewModel)
                                adapter.submitList(viewModel.reminders.value?.filter { it.isActive })
                            adapter.setHasStableIds(true)
                                complete_text.visibility = View.INVISIBLE
                                recycler_completed.visibility = View.GONE
                                recyclerView.adapter = adapter
                                recyclerView.layoutManager = LinearLayoutManager(activity)
                                deleteWorkRequest()
                               // setupNavigation()
                        }
                        R.id.completed -> {
                            updateUI()
                                val secondAdapter =
                                  AddTaskAdapter(viewModel)
                                secondAdapter.submitList( viewModel.reminders.value?.filter { it.isCompleted })
                            secondAdapter.setHasStableIds(true)
                                recyclerView.visibility = View.GONE
                                active_text.visibility = View.INVISIBLE
                                recycler_completed.adapter = secondAdapter
                                recycler_completed.layoutManager = LinearLayoutManager(activity)
                                deleteWorkRequest()
                               setupNavigation()
                        }
                        R.id.all ->{
                            fullui()
                        } }
                true }
            show()
        }
    }



}