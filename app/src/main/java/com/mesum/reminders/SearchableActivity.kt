package com.mesum.reminders

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager

import kotlinx.android.synthetic.main.activity_searchable.*

class SearchableActivity : AppCompatActivity() {

    lateinit var navController: NavController


    private val viewModel: AddTaskViewModel by lazy {
        val activity = requireNotNull(this){
            "You can only access thw viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, AddTaskViewModel.Factory(activity.application))
            .get(AddTaskViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchable)

        // Verify the action and get the query
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                Toast.makeText(applicationContext, "$query from searchactivty", Toast.LENGTH_SHORT).show()
                getQueryyedResult(query)
            }
        }
        setSupportActionBar(findViewById(R.id.toolbar))

    }

    private fun getQueryyedResult(query: String) {
    viewModel.getMultiReminder(query).observe(this){
        val adapter = AddTaskAdapter(viewModel)
        adapter.submitList(it)
        search_recyclerview.adapter = adapter
        search_recyclerview.layoutManager = LinearLayoutManager(this)
    }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        menu?.findItem(R.id.action_search)
        val searchManager = this.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = SearchView((this.supportActionBar?.themedContext ?: this))
        menu?.findItem(R.id.action_search)?.apply {
            searchView.setIconifiedByDefault(false)
            searchView?.setSearchableInfo(
                searchManager.getSearchableInfo(componentName)
            )
            actionView = searchView
        }

        return true
    }



}