package com.mesum.reminders

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import androidx.lifecycle.ViewModelProvider
import com.mesum.reminders.domain.ReminderMain
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.mesum.reminders.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.security.Permission


class MainActivity : AppCompatActivity()  {
    //private lateinit var drawerLayout: DrawerLayout
   private lateinit var navController: NavController
    private val viewModel: AddTaskViewModel by lazy {
        val activity = requireNotNull(this) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, AddTaskViewModel.Factory(activity.application))
            .get(AddTaskViewModel::class.java)
    }
    private lateinit var layout: View
    private lateinit var binding: ActivityMainBinding
    val permissionArray  = arrayOf("android.permission.ACCESS_NOTIFICATION_POLICY", "android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE")

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions(permissionArray, 80)

        //Configures dark mode or light mode according to user preferences
       if (viewModel.isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.Theme_Reminders)
       }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
           setTheme(R.style.Theme_Reminders)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        layout = binding.mainLayout

        setContentView(view)
        setSupportActionBar(findViewById(R.id.toolbar))
        //setupNavigationDrawer()
        //NavController manages navigation between the fragments,  used in action bar
         navController = findNavController(R.id.nav_host_fragment)
        //Configures the app bar to show drawer symbol in these specified fragment
        //Configures the app bar to not display the up bar in the main fragment and statistics fragment

        //Sets up action bar with navController to navigate up
        setupActionBarWithNavController( navController)
        //Navigates to fragment designated to the menu item, when a menu item is tapped.
        findViewById<NavigationView>(R.id.nav_view)
            .setupWithNavController(navController)

    }


    override fun onBackPressed() {
        super.onBackPressed()
        this.supportActionBar?.show()

    }


        //Method is called whenever chooses to navigate up using the action bar
        override fun onSupportNavigateUp(): Boolean {
            viewModel.hideKeyboard(this)
            return findNavController(R.id.nav_host_fragment).navigateUp()|| super.onSupportNavigateUp()

        }

        //Extension function to show snackBar
    private fun View.showSnackbar(
            view: View, msg: String, length: Int, actionMessage: CharSequence?, action: (View) -> Unit)
        { val snackBar = Snackbar.make(view, msg, length)
            if (actionMessage != null){
                snackBar.setAction(actionMessage){
                    action(this)
                }.show()
            }else{
                snackBar.show() }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
            } else {
                Log.i("Permission: ", "Denied") }
        }

    /**
     * Displays an alert dialog to get the user's confirmation before deleting the item.
     */
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.permission_required))
            .setCancelable(false)
            .setNegativeButton("Deny") { _, _ ->  }
            .setPositiveButton("Allow") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_NOTIFICATION_POLICY)        }
            .show()
    }



  /* private fun onClickRequestPermission(view: View) {
        when {
            //if permission is already granted
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE
            ) == PackageManager.PERMISSION_GRANTED -> {
                //In the case that the permission is granted, display a snackbar that announces thant.

               layout.showSnackbar(view, getString(R.string.permission_granted), Snackbar.LENGTH_SHORT, null){}
            }

            //If permission is required then display the required message in SnackBar
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE) -> {
                showConfirmationDialog()
            }
          /*  else -> {
                //if permission hasn't been asked yet
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_NOTIFICATION_POLICY)
            }*/
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE
                )
            }
        }
    }*/

}

