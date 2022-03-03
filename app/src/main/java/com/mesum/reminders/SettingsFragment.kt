package com.mesum.reminders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.mesum.reminders.databinding.FragmentSettingsBinding
import kotlinx.android.synthetic.main.fragment_settings.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.mesum.reminders.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import android.content.Intent
import android.app.PendingIntent

import android.app.AlarmManager
import android.transition.AutoTransition
import android.transition.TransitionManager
import androidx.appcompat.app.AppCompatActivity
import androidx.test.core.app.ApplicationProvider
import java.util.*
import com.jakewharton.processphoenix.ProcessPhoenix
import kotlinx.android.synthetic.main.fragment_add_task.*


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddTaskViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, AddTaskViewModel.Factory(activity.application)).get(AddTaskViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root

    }
    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setExpandableMenu()
        DarkMode.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            viewModel.editSharePref.putBoolean("NightMode", true)
            viewModel.editSharePref.apply()
            //Restarts the application to the current activty
            ProcessPhoenix.triggerRebirth(activity);
        }
        LightMode.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            viewModel.editSharePref.putBoolean("NightMode", false)
            viewModel.editSharePref.apply()
            ProcessPhoenix.triggerRebirth(activity);
        }
        sendbutton.setOnClickListener{
            sendAppIssues()
        }


    }

    private fun setExpandableMenu() {
        expandBtnset.setOnClickListener {
            activity?.let { it1 -> viewModel.hideKeyboard(it1) }
            if (expandableLayoutMode.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(cardViewmode, AutoTransition())
                expandableLayoutMode.visibility = View.VISIBLE
                //expandBtn.text = "COLLAPSE"
            } else {
                TransitionManager.beginDelayedTransition(cardViewmode, AutoTransition())
                expandableLayoutMode.visibility = View.GONE
                //expandBtn.text = "EXPAND"
            }
        }

        expandBtnReviewer.setOnClickListener{
            if (expandableLayoutReviewer.visibility == View.GONE){
                TransitionManager.beginDelayedTransition(reviewCardView, AutoTransition())
                expandableLayoutReviewer.visibility = View.VISIBLE
            }else{
                TransitionManager.beginDelayedTransition(reviewCardView, AutoTransition())
                expandableLayoutReviewer.visibility = View.GONE
            }
        }

    }


    private fun sendAppIssues() {



        val address = "smesumali@yahoo.com"
        val appSummary = getString(R.string.application_improvement_detail, bugFeedback.text.toString(), uiFeedback.text.toString(),"No other allowed " )
        val intent = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
            .putExtra(Intent.EXTRA_SUBJECT, "Application Feedback")
            .putExtra(Intent.EXTRA_TEXT, appSummary)

        if (activity?.packageManager?.resolveActivity(intent, 0) != null) {
            startActivity(intent)
        }


    }


    private fun restartSelf() {
        val am = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am[AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 500] =
            PendingIntent.getActivity(
                activity, 0, requireActivity().intent, PendingIntent.FLAG_ONE_SHOT
                        or PendingIntent.FLAG_CANCEL_CURRENT
            )
        val i = requireActivity().baseContext.packageManager
            .getLaunchIntentForPackage(requireActivity().baseContext.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
    }
    private fun restartApp() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext<Context>(),
            MainActivity::class.java
        )
        val mPendingIntentId: Int = 20
        val mPendingIntent = PendingIntent.getActivity(
            ApplicationProvider.getApplicationContext<Context>(),
            mPendingIntentId,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val mgr = ApplicationProvider.getApplicationContext<Context>()
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr[AlarmManager.RTC, System.currentTimeMillis() + 100] = mPendingIntent
        System.exit(0)
    }

}