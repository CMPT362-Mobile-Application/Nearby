package com.cmpt362.nearby.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.location.Criteria
import android.location.LocationManager
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.telephony.TelephonyManager
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.nearby.R
import com.cmpt362.nearby.classes.Color
import com.cmpt362.nearby.classes.IconType
import com.cmpt362.nearby.classes.Post
import com.cmpt362.nearby.classes.Util
import com.cmpt362.nearby.database.FirestoreDatabase
import com.cmpt362.nearby.databinding.ActivityNewPostBinding
import com.cmpt362.nearby.viewmodels.NewPostViewModel
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import com.cmpt362.nearby.viewmodels.PostsViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class NewPostActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: ActivityNewPostBinding

    private lateinit var newPostViewModel: NewPostViewModel
    private val START_TEXT_KEY = "START TEXT KEY"
    private val END_TEXT_KEY = "END TEXT KEY"
    private val LOCATION_TEXT_KEY = "LOCATION TEXT KEY"

    private lateinit var cameraImgUri: Uri
    private lateinit var cameraImgFile: File
    private val cameraFileName = "camera.jpg"
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var galleryResult: ActivityResultLauncher<Intent>

    private lateinit var locationResult: ActivityResultLauncher<Intent>

    private lateinit var deviceUUID: TelephonyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        //cloudStorage = FirebaseStorage.getInstance()
        deviceUUID = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        // Setup view model for storing date/time entry and state
        newPostViewModel = ViewModelProvider(this)[NewPostViewModel::class.java]

        // Set up Category Spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.addpost_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.addpostCategoryspinner.adapter = adapter
        }

        // Set up Is Event toggle
        binding.addpostEvent.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.addpostEventstartlayout.visibility = RelativeLayout.VISIBLE
                binding.addpostEventendlayout.visibility = RelativeLayout.VISIBLE
            } else {
                binding.addpostEventstartlayout.visibility = RelativeLayout.GONE
                binding.addpostEventendlayout.visibility = RelativeLayout.GONE
            }
        }

        // Set radio buttons
        binding.addpostIconGroup.check(R.id.addpost_icon_group_none)
        if(newPostViewModel.icon.value == null) {newPostViewModel.icon.value = IconType.NONE}
        binding.addpostIconGroup.setOnCheckedChangeListener { radGrp, id ->
            when (id) {
                R.id.addpost_icon_group_none -> {newPostViewModel.icon.value = IconType.NONE}
                R.id.addpost_icon_group_food -> {newPostViewModel.icon.value = IconType.FOOD}
                R.id.addpost_icon_group_game -> {newPostViewModel.icon.value = IconType.GAME}
                R.id.addpost_icon_group_sport -> {newPostViewModel.icon.value = IconType.SPORT}
            }
        }
        // Set radio buttons
        binding.addpostColorGroup.check(R.id.addpost_color_group_grey)
        if(newPostViewModel.color.value == null) {newPostViewModel.color.value = Color.GREY}
        binding.addpostColorGroup.setOnCheckedChangeListener { radGrp, id ->
            when (id) {
                R.id.addpost_color_group_grey -> {newPostViewModel.color.value = Color.GREY}
                R.id.addpost_color_group_red -> {newPostViewModel.color.value = Color.RED}
                R.id.addpost_color_group_green -> {newPostViewModel.color.value = Color.GREEN}
                R.id.addpost_color_group_blue -> {newPostViewModel.color.value = Color.BLUE}
            }
        }

        // Event Set Start button
        binding.addpostEventstartbutton.setOnClickListener {
            newPostViewModel.startOrEnd.value = "start"
            showDateTimePicker()
        }

        // Event set end button
        binding.addpostEventendbutton.setOnClickListener {
            newPostViewModel.startOrEnd.value = "end"
            showDateTimePicker()
        }

        // Add image button
        binding.addpostAddimagebutton.setOnClickListener {
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

        // On Choose Location Result
        locationResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                val lat = it.data?.getDoubleExtra("latitude", 0.0)
                val lng = it.data?.getDoubleExtra("longitude", 0.0)
                newPostViewModel.latitude.value = lat
                newPostViewModel.longitude.value = lng
                binding.addpostCurrlocation.text = "${String.format("%.5f", lat)}, ${String.format("%.5f", lng)}"
            }
        }

        // Listen for image bitmap changes
        newPostViewModel.imageBitmap.observe(this) {
            binding.addpostImageview.setImageBitmap(it)
            binding.addpostImageview.visibility = View.VISIBLE
            binding.addpostAddimagebutton.text = getString(R.string.addpost_changeimage)
        }

        // Set Location Button
        binding.addpostSetlocation.setOnClickListener {
            openSetLocationActivity()
        }

        // Create Button
        binding.addpostCreate.setOnClickListener {
            if(createPost()) {
                finish()
            }
        }

        // Cancel Button
        binding.addpostCancel.setOnClickListener {
            finish()
        }

        // Restore from saved instance state
        if (savedInstanceState?.getString(START_TEXT_KEY) != null)
            binding.addpostEventstarttext.text = savedInstanceState.getString(START_TEXT_KEY)
        if (savedInstanceState?.getString(END_TEXT_KEY) != null)
            binding.addpostEventendtext.text = savedInstanceState.getString(END_TEXT_KEY)
        if (savedInstanceState?.getString(LOCATION_TEXT_KEY) != null)
            binding.addpostCurrlocation.text = savedInstanceState.getString(LOCATION_TEXT_KEY)

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

    fun openSetLocationActivity() {
        val intent = Intent(this, ChooseLocationActivity::class.java)
        locationResult.launch(intent)
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
            binding.addpostEventstarttext.text = dateTime
        }
        else if (newPostViewModel.startOrEnd.value == "end") {
            newPostViewModel.endCalendar.value?.set(Calendar.HOUR_OF_DAY, hourOfDay)
            newPostViewModel.endCalendar.value?.set(Calendar.MINUTE, minute)

            // Update TextView
            val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.US)
            dateFormat.timeZone = newPostViewModel.endCalendar.value?.timeZone!!
            val dateTime = dateFormat.format(newPostViewModel.endCalendar.value?.time!!)
            binding.addpostEventendtext.text = dateTime
        }
    }

    private fun createPost(): Boolean {
        // Grab text fields
        // val userid = deviceUUID.imei // Where is the user id stored?
        val title = binding.addpostName.text.toString()
        val info = binding.addpostDescription.text.toString()

        if (title.isEmpty()) {
            Toast.makeText(this, R.string.addpost_toast_notitle, Toast.LENGTH_SHORT).show()
            return false
        }

        // Image
        val imageBitmap = newPostViewModel.imageBitmap.value

        // This should probably be uploaded at this point to Firebase, then get the URL
        // Create a storage reference from our app
        //val storageRef = cloudStorage.reference

        // Create a reference to "mountains.jpg"
        //val imageReference = storageRef.child("images/${deviceUUID.getImei()}+${Calendar.getInstance().timeInMillis}")

        // While the file names are the same, the references point to different files
        var imageURL = ""
        if (imageBitmap != null) {
            val imageUuid = Util.makeUuid()
            CoroutineScope(IO).launch {
                val bitmap = imageBitmap
                val baos = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                val storage = Firebase.storage("gs://cmpt362-nearby")
                val storageRef = storage.reference
                val imageRef = storageRef.child("images/$imageUuid.jpg")

                val uploadTask = imageRef.putBytes(data)
                uploadTask.addOnFailureListener {
                    // Error
                }.addOnSuccessListener { taskSnapshot ->
                    // Success!
                }
            }
            imageURL = "images/$imageUuid.jpg"
        } else {
            imageURL = "null"
        }

        // Category
        val tag = binding.addpostCategoryspinner.selectedItem as String
        var icon : IconType = when(binding.addpostIconGroup.checkedRadioButtonId) {
            R.id.addpost_icon_group_none -> IconType.NONE
            R.id.addpost_icon_group_food -> IconType.FOOD
            R.id.addpost_icon_group_game -> IconType.GAME
            R.id.addpost_icon_group_sport -> IconType.SPORT
            else -> IconType.NONE
        }
        var color : Color = when(binding.addpostColorGroup.checkedRadioButtonId) {
            R.id.addpost_color_group_grey -> Color.GREY
            R.id.addpost_color_group_red -> Color.RED
            R.id.addpost_color_group_green -> Color.GREEN
            R.id.addpost_color_group_blue -> Color.BLUE
            else -> Color.GREY
        }
//        if (binding.addpostCategoryspinner.selectedItemPosition < IconType.values().size) {
//            icon = IconType.fromInt(binding.addpostCategoryspinner.selectedItemPosition)!!
//            color = Color.fromInt(binding.addpostCategoryspinner.selectedItemPosition)!!
//        }

        // Event data
        val isEvent = binding.addpostEvent.isChecked

        if (isEvent) {
            if (binding.addpostEventstarttext.text.toString() == getString(R.string.addpost_nostartdate)) {
                Toast.makeText(this, R.string.addpost_toast_nostart, Toast.LENGTH_SHORT).show()
                return false
            } else if (binding.addpostEventendtext.text.toString() == getString(R.string.addpost_noenddate)) {
                Toast.makeText(this, R.string.addpost_toast_noend, Toast.LENGTH_SHORT).show()
                return false
            }
        }

        // Location
        var latitude = newPostViewModel.latitude.value
        var longitude = newPostViewModel.longitude.value
        if (latitude == 0.0 || longitude == 0.0) { // Get current location
            try {
                val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                val criteria = Criteria()
                criteria.accuracy = Criteria.ACCURACY_FINE
                val provider = locationManager.getBestProvider(criteria, true)
                val location = locationManager.getLastKnownLocation(provider!!)
                latitude = location?.latitude
                longitude = location?.longitude
            } catch (e: SecurityException) {
                Toast.makeText(this, R.string.addpost_toast_location, Toast.LENGTH_SHORT).show()
                return false
            }
        }
        if (latitude == null || longitude == null) {
            Toast.makeText(this, R.string.addpost_toast_location, Toast.LENGTH_SHORT).show()
            return false
        }
        val geoPoint = GeoPoint(latitude, longitude)

        val startTimestamp =
            if (isEvent) Timestamp(newPostViewModel.startCalendar.value!!.time)
            else Timestamp(Calendar.getInstance().time)

        val endTimestamp =
            if (isEvent) Timestamp(Calendar.getInstance().time)
            else startTimestamp

        // Create Post object to upload to Firebase
        val newPost = Post(
            title = title,
            startTime = startTimestamp,
            endTime = endTimestamp,
            location = geoPoint,
            info = info,
            tag = tag,
            iconType = icon,
            iconColor = color,
            isEvent = isEvent,
            imageUrl = imageURL
        )

        FirestoreDatabase.addPost(newPost)

        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(START_TEXT_KEY, binding.addpostEventstarttext.text.toString())
        outState.putString(END_TEXT_KEY, binding.addpostEventendtext.text.toString())
        outState.putString(LOCATION_TEXT_KEY, binding.addpostCurrlocation.text.toString())
    }
}