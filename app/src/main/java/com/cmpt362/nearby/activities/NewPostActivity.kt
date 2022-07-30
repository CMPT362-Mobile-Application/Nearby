package com.cmpt362.nearby.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.nearby.R
import com.cmpt362.nearby.databinding.ActivityNewPostBinding
import com.cmpt362.nearby.viewmodels.NewPostViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class NewPostActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: ActivityNewPostBinding
    private lateinit var categorySpinner: Spinner
    private lateinit var eventSwitch: SwitchCompat
    private lateinit var eventStartLayout: RelativeLayout
    private lateinit var eventEndLayout: RelativeLayout
    private lateinit var eventStartButton: Button
    private lateinit var eventEndButton: Button
    private lateinit var eventStartTextView: TextView
    private lateinit var eventEndTextView: TextView
    private lateinit var addImageButton: Button
    private lateinit var imageView: ImageView

    private lateinit var newPostViewModel: NewPostViewModel
    private val START_TEXT_KEY = "START TEXT KEY"
    private val END_TEXT_KEY = "END TEXT KEY"

    private lateinit var cameraImgUri: Uri
    private lateinit var cameraImgFile: File
    private val cameraFileName = "camera.jpg"
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var galleryResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)

        // Setup view model for storing date/time entry and state
        newPostViewModel = ViewModelProvider(this).get(NewPostViewModel::class.java)

        // Set up Category Spinner
        categorySpinner = binding.addpostCategoryspinner
        ArrayAdapter.createFromResource(
            this,
            R.array.addpost_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        }

        // Set up Is Event toggle
        eventSwitch = binding.addpostEvent
        eventStartLayout = binding.addpostEventstartlayout
        eventEndLayout = binding.addpostEventendlayout
        eventStartTextView = binding.addpostEventstarttext
        eventEndTextView = binding.addpostEventendtext
        eventSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                eventStartLayout.visibility = RelativeLayout.VISIBLE
                eventEndLayout.visibility = RelativeLayout.VISIBLE
            } else {
                eventStartLayout.visibility = RelativeLayout.GONE
                eventEndLayout.visibility = RelativeLayout.GONE
            }
        }

        // Event Set Start button
        eventStartButton = binding.addpostEventstartbutton
        eventStartButton.setOnClickListener {
            newPostViewModel.startOrEnd.value = "start"
            showDateTimePicker()
        }

        // Event set end button
        eventEndButton = binding.addpostEventendbutton
        eventEndButton.setOnClickListener {
            newPostViewModel.startOrEnd.value = "end"
            showDateTimePicker()
        }

        // Add image button
        addImageButton = binding.addpostAddimagebutton
        addImageButton.setOnClickListener {
            showAddPhotoDialog()
        }

        // Setup image file and uri
        cameraImgFile = File(getExternalFilesDir(null), cameraFileName)
        cameraImgUri = FileProvider.getUriForFile(this, "com.cmpt362.nearby", cameraImgFile)

        // On Camera result
        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val bitmap = BitmapFactory.decodeFile(cameraImgUri.path)
                val matrix = Matrix()
                // Orientation help: https://stackoverflow.com/questions/11026615/captured-photo-orientation-is-changing-in-android
                val exif = ExifInterface(cameraImgUri.path!!)
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL)
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
                }
                newPostViewModel.imageBitmap.value = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.width, bitmap.height, matrix, true)
            }
        }

        // On Gallery result
        galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data?.data != null) {
                val uri: Uri = it.data?.data!! // null check done in if
                val source: ImageDecoder.Source
                val bitmap: Bitmap
                // We need to get the image from the URI returned, the following if/else does that
                // Source: https://stackoverflow.com/questions/3879992/how-to-get-bitmap-from-an-uri
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    source = ImageDecoder.createSource(this.contentResolver, uri)
                    bitmap = ImageDecoder.decodeBitmap(source)
                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                }
                val matrix = Matrix()
                newPostViewModel.imageBitmap.value = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.width, bitmap.height, matrix, true)
            }
        }

        // Listen for image bitmap changes
        imageView = binding.addpostImageview
        newPostViewModel.imageBitmap.observe(this) {
            imageView.setImageBitmap(it)
        }

        // Restore from saved instance state
        if (savedInstanceState?.getString(START_TEXT_KEY) != null)
            eventStartTextView.text = savedInstanceState.getString(START_TEXT_KEY)
        if (savedInstanceState?.getString(END_TEXT_KEY) != null)
            eventEndTextView.text = savedInstanceState.getString(END_TEXT_KEY)

        setContentView(binding.root)
    }

    fun showAddPhotoDialog() {
        // Get strings for dialog
        val options = arrayOf(
            getString(R.string.addpost_camera),
            getString(R.string.addpost_gallery)
        )
        val title = getString(R.string.addpost_addimagetitle)

        // Create and show dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setItems(options, DialogInterface.OnClickListener() {
            dialog, which ->
            when (which) {
                0 -> useCamera() // Camera option
                1 -> useGallery() // Gallery option
            }
        })
        builder.show()
    }

    fun useCamera() {
        // Launch Camera intent
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImgUri)
        cameraResult.launch(intent)
    }

    fun useGallery() {
        // Launch Gallery intent
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        galleryResult.launch(intent)
    }

    fun showDateTimePicker() {
        val cal = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this, this, cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    // Will respond when the date has been entered and will open time picker dialog
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val cal = Calendar.getInstance()
        // Store result in whichever button initiated the request
        if (newPostViewModel.startOrEnd.value == "start")
            newPostViewModel.startCalendar.value?.set(year, month, dayOfMonth)
        else if (newPostViewModel.startOrEnd.value == "end")
            newPostViewModel.endCalendar.value?.set(year, month, dayOfMonth)

        // Open TimePickerDialog
        val timePickerDialog = TimePickerDialog(this, this,
            cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false)
        timePickerDialog.show()
    }

    // Will respond once the time has been set, and update the text in the activity
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        // Store result in whichever button initiated the request
        if (newPostViewModel.startOrEnd.value == "start") {
            newPostViewModel.startCalendar.value?.set(Calendar.HOUR_OF_DAY, hourOfDay)
            newPostViewModel.startCalendar.value?.set(Calendar.MINUTE, minute)

            // Update TextView
            val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.US)
            dateFormat.timeZone = newPostViewModel.startCalendar.value?.timeZone!!
            val dateTime = dateFormat.format(newPostViewModel.startCalendar.value?.time!!)
            eventStartTextView.text = dateTime
        }
        else if (newPostViewModel.startOrEnd.value == "end") {
            newPostViewModel.endCalendar.value?.set(Calendar.HOUR_OF_DAY, hourOfDay)
            newPostViewModel.endCalendar.value?.set(Calendar.MINUTE, minute)

            // Update TextView
            val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.US)
            dateFormat.timeZone = newPostViewModel.endCalendar.value?.timeZone!!
            val dateTime = dateFormat.format(newPostViewModel.endCalendar.value?.time!!)
            eventEndTextView.text = dateTime
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(START_TEXT_KEY, eventStartTextView.text.toString())
        outState.putString(END_TEXT_KEY, eventEndTextView.text.toString())
    }
}