package com.mesum.reminders

import android.R
import android.app.*
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.database.DataSetObserver
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.format.DateFormat.is24HourFormat
import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.mesum.reminders.util.ReminderWorker
import kotlinx.android.synthetic.main.fragment_add_task.*
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.widget.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mesum.reminders.databinding.FragmentAddTaskBinding
import kotlinx.android.synthetic.main.fragment_add_task.cardView

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager

import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mesum.reminders.domain.ReminderMain
import com.mesum.reminders.util.ReminderNotificationWorker
import kotlinx.android.synthetic.main.fragment_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope

import com.mesum.reminders.database.ImagesEntitys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AddTaskFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding : FragmentAddTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var  filePAth : Uri
    val frequency = arrayOf("Don't repeat", "Every day", "Every Month", "Every year")
    val ringerArray = arrayOf("Ring once", "Ring often")
    val navigationArgs : AddTaskFragmentArgs by navArgs()
    //Calendar instance for setting date & time
    var cal = Calendar.getInstance()
    private val viewModel: AddTaskViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()" }
        ViewModelProvider(this, AddTaskViewModel.Factory(activity.application))
            .get(AddTaskViewModel::class.java)
    }

    companion object{
        val IMAGE_REQUES_CODE = 100
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddTaskBinding.inflate(layoutInflater, container , false)

        getActivity()?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return binding.root




    }





        @RequiresApi(Build.VERSION_CODES.N)
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
          (  activity as AppCompatActivity?)?.supportActionBar?.hide()
            //Checks if theres a navigation argument if there is then updates the UI in Edit Ui format
            if (!navigationArgs.reminderTitle.isNullOrEmpty()){
                editFragment()
              }
            //else if there is no argument provided then Add task fragment functionality
            else {
            newFragment()
            }
            cancel.setOnClickListener {
                findNavController().navigate(AddTaskFragmentDirections.actionAddTaskFragmentToMainFragment())
            }
        }

    private fun newFragment(){
        setExpandableMenu()
        cameropener.setOnClickListener { startCamera() }
        addbtn.setOnClickListener { startCamera() }
        txttime.text = SimpleDateFormat("h:mm a", Locale.US).format(cal.time)
        txtDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(cal.time)
        pickTime()
        pickDate()
        selectFrequency()
        selectRingerFrequency()
        createReminder( "")

    }

    private fun editFragment(){
        viewModel.reminderwithimages(navigationArgs.reminderTitle!!).observe(viewLifecycleOwner){

            binding.apply {
                setExpandableMenu()
                addTitle.setText(it.reminders.title)
                addTitle.isEnabled = false
                addDesciption.setText(it.reminders.description)
                showPhotos(it.images)
                //expandBtn.textOn
                val date = SimpleDateFormat("E, MMM d, yyyy")
                val time = SimpleDateFormat("hh:mm a")
                val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
                if (it.reminders.todoDate != "none") {
                    txttime.setText(time.format(inputFormat.parse(it.reminders.todoDate)))
                    txtDate.setText(date.format(inputFormat.parse(it.reminders.todoDate)))
                }
                cameropener.setOnClickListener { startCamera() }
                addbtn.setOnClickListener { startCamera() }
              //  imageView.setImageURI(Uri.parse(it.image))
                intialFrequency()
                pickDate()
                pickTime()
                selectFrequency()
                selectRingerFrequency()
                createReminder( it.reminders.workerID)

            }

        }
    }

    private fun showPhotos(imageList : List<ImagesEntitys>) {


        for (i in imageList){
            viewModel.paramsCounter += 5
            val imageView = ImageView(activity)
            imageView.id = View.generateViewId()
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(viewModel.paramsCounter, 0, 0, 0)
            layoutParams.height = 300
            layoutParams.width = 250
            imageView.layoutParams = layoutParams
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            imageView.setImageURI(Uri.parse(i.images))
            binding.lineaImages.addView(imageView)
            if (imageList.isNotEmpty()){
                addbtn.visibility = View.VISIBLE
            }

        }
    }



    private fun setExpandableMenu(){
        expandBtn.setOnClickListener {
            activity?.let { it1 -> viewModel.hideKeyboard(it1) }
            if (expandableLayout.visibility == View.GONE) {

                TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                expandableLayout.visibility = View.VISIBLE
                //expandBtn.text = "COLLAPSE"
            } else {
                TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                expandableLayout.visibility = View.GONE
                //expandBtn.text = "EXPAND"
            }
        }

        freqexpandBtn.setOnClickListener{
            activity?.let { it1 -> viewModel.hideKeyboard(it1) }
            if (freqexpandableLayout.visibility == View.GONE){
                TransitionManager.beginDelayedTransition(cardViewfrq, AutoTransition())
                freqexpandableLayout.visibility = View.VISIBLE
            }else{
                TransitionManager.beginDelayedTransition(cardViewfrq, AutoTransition())
                freqexpandableLayout.visibility = View.GONE
            }

        }
    }

    //
    private fun intialFrequency(){
        viewModel.getReminder(navigationArgs.reminderTitle!!).observe(viewLifecycleOwner) {
            val spinnerArrayAdapter =
                ArrayAdapter(requireContext(), R.layout.simple_spinner_item, frequency)
            binding.spinnerFrequency.setSelection(spinnerArrayAdapter.getPosition(it.frequency))
            binding.spinnerFrequency.setAdapter(
                CustomSpinnerAdapter(
                    requireContext(),
                    R.layout.simple_spinner_item,
                    frequency,
                    it.frequency
                )
            )
        }
    }



    //Func for picking the frequency
    private fun selectFrequency(){

        val spin = binding.spinnerFrequency
        //overrides the fun onitemselected below
        spin.onItemSelectedListener = this
        val aa = ArrayAdapter(this.requireContext(),R.layout.simple_spinner_item,frequency)
        aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spin.adapter = aa
    }




    private fun selectRingerFrequency(){
        val spinner = binding.ringerSpinnerFrequency
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long) {
                when(ringerArray[position]){
                    "Ring once" ->{ viewModel.ringerFrequency = "Ring once"}
                    "Ring often" -> {viewModel.ringerFrequency = "Ring often"}

                }
              //  Toast.makeText(activity, "${viewModel.ringerFrequency}", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //if databse has ringerfrequency selected pick that if not then keep defaul to Ring once
                if (navigationArgs.reminderTitle?.isNotEmpty() == true){
                    viewModel.getReminder(navigationArgs.reminderTitle!!).observe(viewLifecycleOwner){
                        viewModel.ringerFrequency = it.frequency
                    }}else{ viewModel.ringerFrequency = "Ring once"}
            }
        }

        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, ringerArray)
        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter

    }
    //Assigns the selected frequency into the viewModel
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(frequency[position]){
            "Don't repeat" -> viewModel.everyDay = "ones"
            "Every day" -> viewModel.everyDay = "everyDay"
            "Every Month" -> viewModel.everyDay = "everyMonth"
            "Every year"-> viewModel.everyDay = "everyYear"

        }
    }

    private fun pickDate(){
        txtDate.setOnClickListener {
            activity?.let {
                DatePickerDialog(
                    it, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                    ).show() } }
    }

    private fun pickTime(){
        txttime.setOnClickListener {
            activity?.let {
                TimePickerDialog(
                    it,
                    timeSetListener,
                    cal.get(Calendar.HOUR),
                    cal.get(Calendar.MINUTE),
                    is24HourFormat(it)).show() } }
    }

    //Listens to change when time is set
    private val timeSetListener = object :TimePickerDialog.OnTimeSetListener{
        override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
            cal.set(Calendar.HOUR, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            updateTimeInView() }
    }
    //listens to changes when date is set
    private val dateSetListener = object : DatePickerDialog.OnDateSetListener{
        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView() }
    }
    private fun updateDateInView(){
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        binding.txtDate.setText(sdf.format(cal.time))
    }
    private fun updateTimeInView() {
        //  val myFormat = "EEE, d MMM yyyy HH:mm:ss Z"
        val myFormat = "hh:mm a"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.txttime.setText(sdf.format(cal.time))
    }

    //navigates to main fragment
    private fun navigate(){
                val action = AddTaskFragmentDirections.actionAddTaskFragmentToMainFragment()
                findNavController().navigate(action)
            }


    //Creates a workRequest upon the choosen item in database
    private fun createReminder( workerID: String){
        if (workerID.isNotEmpty()) button.text = "Update"
        button.setOnClickListener {
            if (workerID.isNotEmpty()){  WorkManager.getInstance(requireContext())
                .cancelWorkById(UUID.fromString(workerID))
            }
            if (expandBtn.isChecked == true) {
                if (cal.time.after(Calendar.getInstance().time)) {
                    viewModel.reminderType = true
                    val today = Calendar.getInstance()
                    //Gets the difference between time selected and today's time, in seconds
                    val diff = (cal.timeInMillis / 1000L) - (today.timeInMillis / 1000L)
                    when (viewModel.everyDay) {
                        "ones" -> {
                            val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                                .setInitialDelay(diff, TimeUnit.SECONDS)
                                .setInputData(
                                    workDataOf(
                                        "title" to binding.addTitle.text.toString(),
                                        "message" to binding.addDesciption.text.toString()
                                    )
                                )
                                .build()
                            WorkManager.getInstance(requireContext()).enqueue(workRequest)
                            //if the reminder is created for more then 15 minutes then create a pre reminder
                            if (diff > 900) {
                                val preworktime15mins = diff - 900
                                val preworkRequest =
                                    OneTimeWorkRequestBuilder<ReminderNotificationWorker>()
                                        .setInitialDelay(preworktime15mins, TimeUnit.SECONDS)
                                        .setInputData(
                                            workDataOf(
                                                "title" to "15 minutes left until \n ${binding.addTitle.text.toString()}",
                                                "message" to binding.addDesciption.text.toString()
                                            )
                                        )
                                        .build()
                                WorkManager.getInstance(requireContext()).enqueue(preworkRequest)
                            }
                            if (!add_title.text.isNullOrEmpty() || !add_desciption.text.isNullOrEmpty() || !txtDate.text.isNullOrEmpty() || !txttime.text.isNullOrEmpty()) {
                                createRingerRequest(diff = diff, perodic = false, null)
                                insertReminder(workRequest.id, "Not Repeating", viewModel.reminderimageslist)
                                navigate()
                                Toast.makeText(activity, "Reminder Created", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        "everyDay" -> {
                            val periodicWorkReq =
                                PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
                                    .setInitialDelay(diff, TimeUnit.SECONDS)
                                    .setInputData(
                                        workDataOf(
                                            "title" to "${binding.addTitle.text.toString()}",
                                            "message" to "${binding.addDesciption.text.toString()}"
                                        )
                                    )
                                    .build()
                            WorkManager.getInstance(requireContext()).enqueue(periodicWorkReq)
                            if (!add_title.text.isNullOrEmpty() || !add_desciption.text.isNullOrEmpty() || !txtDate.text.isNullOrEmpty() || !txttime.text.isNullOrEmpty()) {
                                createRingerRequest(diff = diff, perodic = true, 24)
                                insertReminder(periodicWorkReq.id, "Daily", viewModel.reminderimageslist)
                                navigate()
                                Toast.makeText(activity, "Reminder Created", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        "everyMonth" -> {
                            val periodicWorkReq =
                                PeriodicWorkRequestBuilder<ReminderWorker>(30, TimeUnit.DAYS)
                                    .setInitialDelay(diff, TimeUnit.SECONDS)
                                    .setInputData(
                                        workDataOf(
                                            "title" to "${binding.addTitle.text.toString()}",
                                            "message" to "${binding.addDesciption.text.toString()}"
                                        )
                                    )
                                    .build()
                            WorkManager.getInstance(requireContext()).enqueue(periodicWorkReq)
                            if (!add_title.text.isNullOrEmpty() || !add_desciption.text.isNullOrEmpty() || !txtDate.text.isNullOrEmpty() || !txttime.text.isNullOrEmpty()) {
                                createRingerRequest(diff, perodic = true, 760)
                                insertReminder(periodicWorkReq.id, "Monthly", viewModel.reminderimageslist)
                                navigate()
                                Toast.makeText(activity, "Reminder Created", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        "everyYear" -> {
                            val periodicWorkReq =
                                PeriodicWorkRequestBuilder<ReminderWorker>(365, TimeUnit.DAYS)
                                    .setInitialDelay(diff, TimeUnit.SECONDS)
                                    .setInputData(
                                        workDataOf(
                                            "title" to "${binding.addTitle.text.toString()}",
                                            "message" to "${binding.addDesciption.text.toString()}"
                                        )
                                    )
                                    .build()
                            WorkManager.getInstance(requireContext()).enqueue(periodicWorkReq)
                            if (!add_title.text.isNullOrEmpty() || !add_desciption.text.isNullOrEmpty() || !txtDate.text.isNullOrEmpty() || !txttime.text.isNullOrEmpty()) {
                                createRingerRequest(diff, perodic = true, 8760)
                                insertReminder(periodicWorkReq.id, "Yearly", viewModel.reminderimageslist)
                                navigate()
                                Toast.makeText(activity, "Reminder Created", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }

                }
            } else {
                if (!add_title.text.isNullOrEmpty() || !add_desciption.text.isNullOrEmpty()) {
                    viewModel.insertItem(
                        title = binding.addTitle.text.toString(),
                        description = binding.addDesciption.text.toString(),
                        date = "none",
                        workerID = UUID.fromString("b6745e5c-8b9b-11ec-a8a3-0242ac120002"),
                        frequency = "none",
                        viewModel.ringerFrequency)
                    for (i in viewModel.reminderimageslist){
                        viewModel.insertImages(title = binding.addTitle.text.toString(), i.toString())

                    }
                    navigate()
                    Toast.makeText(activity, "Reminder Created", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


    private fun insertReminder( workerID: UUID, frequency : String, imagesList : List<Uri> ){

        viewModel.insertItem(
            title = binding.addTitle.text.toString(),
            description = binding.addDesciption.text.toString(),
            date = cal.time.toString(),
            workerID = workerID,
            frequency = frequency,
            ringerFrequency = viewModel.ringerFrequency,
        )
        for (i in imagesList){
            viewModel.insertImages(title = binding.addTitle.text.toString(), i.toString())

        }
    }

    private fun createRingerRequest(diff: Long, perodic : Boolean, interval : Long? ){
        if (!perodic){
        when(viewModel.ringerFrequency){

            "Ring often" -> {
                val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                    .setInitialDelay(diff + 70, TimeUnit.SECONDS)
                    .setInputData(workDataOf("title" to binding.addTitle.text.toString(),
                        "message" to binding.addDesciption.text.toString()
                    ))
                    .build()
                WorkManager.getInstance(requireContext()).enqueue(workRequest)
            }
        } }else {
            when (viewModel.ringerFrequency) {
                "Ring often" -> {
                    val workRequest =
                        PeriodicWorkRequestBuilder<ReminderWorker>(interval!!, TimeUnit.HOURS)
                            .setInitialDelay(diff + 60, TimeUnit.SECONDS)
                            .setInputData(
                                workDataOf(
                                    "title" to binding.addTitle.text.toString(),
                                    "message" to binding.addDesciption.text.toString()
                                )
                            )
                            .build()
                    WorkManager.getInstance(requireContext()).enqueue(workRequest)
                }
            }

        }

    }


    override fun onNothingSelected(parent: AdapterView<*>?) {
        viewModel.everyDay = "ones"

    }
    private fun startCamera(){
        viewModel.paramsCounter += 5
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUES_CODE)

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //if the requestCode matches with imageRequestCode 100 and result code = succeeded
        if (requestCode == IMAGE_REQUES_CODE && resultCode == RESULT_OK){
            // image_view.setImageURI(data?.data )
                // If image uri is returned from activity result then addSchedule
          populateImage(data?.data)
        }
    }

    private fun populateImage(data: Uri?) {
        val imageView = ImageView(activity)
        imageView.id = View.generateViewId()
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(viewModel.paramsCounter, 0, 0, 0)
        layoutParams.height = 300
        layoutParams.width = 250
        imageView.layoutParams = layoutParams
        imageView.scaleType = ImageView.ScaleType.FIT_XY
            imageView.setImageURI(data)

        binding.lineaImages.addView(imageView)
        viewModel.reminderimageslist.add(data!!)
        if (viewModel.reminderimageslist.isNotEmpty()) {
            addbtn.visibility = View.VISIBLE
        }
    }
    private fun compressImage(imageUri: Uri?) {
        try {
            //creates a image bitmap
            val imgBitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, imageUri)
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val fileName = String.format("%d.jpg", System.currentTimeMillis())
            val finalFile = File(path, fileName)
            //Uses FileOutputStream to write new file
            val fileOutputStream = FileOutputStream(finalFile)
            //compresses imgBitmap  passing in fileOutputStream
            imgBitmap.compress(Bitmap.CompressFormat.JPEG, 10, fileOutputStream)
           // binding.imageView.setImageURI(Uri.fromFile(finalFile))
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            //createReminder(finalFile.absolutePath, "")
            intent.setData(Uri.fromFile(finalFile))
            activity?.sendBroadcast(intent)

        }catch (e : IOException){

            Log.d("reminderCrash", "Crash caused $e")
        }

    }


    /*fun getRealPathFromUri(context: Context, contentUri: Uri?): String {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.getContentResolver().query(contentUri!!, proj, null, null, null)
            val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }*/

}

class CustomSpinnerAdapter(
     context: Context,
    textViewResourceId: Int,
    var objects: Array<String>,
    defaultText: String
) : ArrayAdapter<String?>(context, textViewResourceId, objects) {
    var firstElement: String? = null
    var isFirstTime = true

    fun setDefaultText(defaultText: String) {
        firstElement = objects[0]
        when (defaultText) {
            "Not Repeating" -> {
                objects[0] = "Don't repeat"
            }
            "Daily" -> {
                objects[0] = "Every day"
            }
            "Monthly" -> {
                objects[0] = "Every Month"
            }
            "Yearly" -> {
                objects[0] = "Every year"
            }
        }
    }



    init {
        setDefaultText(defaultText)
    }
}