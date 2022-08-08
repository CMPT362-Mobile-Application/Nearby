package com.cmpt362.nearby

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Typeface
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.nearby.activities.FavouriteActivity
import com.cmpt362.nearby.activities.FilterActivity
import com.cmpt362.nearby.activities.NewPostActivity
import com.cmpt362.nearby.animation.PinDetailAnimation
import com.cmpt362.nearby.classes.Color
import com.cmpt362.nearby.classes.IconType
import com.cmpt362.nearby.classes.Post
import com.cmpt362.nearby.classes.Util
import com.cmpt362.nearby.database.PostFilter
import com.cmpt362.nearby.databinding.ActivityMapsBinding
import com.cmpt362.nearby.fragments.PinDetailsFragment
import com.cmpt362.nearby.viewmodels.PostsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {
    // Google map variable
    private lateinit var mMap: GoogleMap
    // Binding for the xml file
    private lateinit var binding: ActivityMapsBinding
    private var detailActive: Boolean = false
    private var sentUserToLocation: Boolean = false
    private var detailsFragment: PinDetailsFragment? = null
    private val postFilter: PostFilter by lazy {
        with (this.getSharedPreferences(
            FilterActivity.PREFERENCES_KEY,Context.MODE_PRIVATE)) {

            PostFilter.Builder()
                .earliest(Util.millisToTimeStamp(
                    getLong(FilterActivity.LATEST_DATETIME_FILTER_KEY, -1L)))
                .latest(Util.millisToTimeStamp(
                    getLong(FilterActivity.EARLIEST_DATETIME_FILTER_KEY, -1L)))
                .build()
        }
    }

    private lateinit var postsViewModel: PostsViewModel

    private lateinit var favouriteResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askForPermissions()

        // Create the binding and set content view
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Hides the top tool bar
        supportActionBar?.hide()

        postsViewModel = ViewModelProvider(this)[PostsViewModel::class.java]

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Setup listener for FavouriteActivity
        favouriteResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null && mMap != null) {
                if(detailActive) { // Close current pin details fragment
                    pinDetailsClose()
                    detailActive = false
                }

                val id = it.data?.getStringExtra("id")
                if (id != null) {
                    val post = postsViewModel.idPostPairs.value!!.find { pair ->
                        pair.first == id }!!.second
                    // Scroll map
                    val latLng = LatLng(post.location.latitude, post.location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f))

                    // Open details fragment
                    pinDetailsOpen(post, id)
                    detailActive = true
                }

            }
        }

        binding.mapNavBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.app_bar_filter -> {
                    startActivity(Intent(this, FilterActivity::class.java))
                    return@setOnItemSelectedListener true
                }
                R.id.app_bar_new -> {
                    startActivity(Intent(this, NewPostActivity::class.java))
                    return@setOnItemSelectedListener true
                }
                R.id.app_bar_favourite -> {
                    val intent = Intent(this, FavouriteActivity::class.java)
                    favouriteResult.launch(intent)
                    return@setOnItemSelectedListener true
                }
                else -> false
            }

        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            moveMapToLocation()
        }

        mMap.setOnMarkerClickListener {

            detailActive = if (detailActive) {
                pinDetailsClose()
                false
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(it.position));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
                // There will be posts for sure if the marker is loaded
                val index = it.title!!.toInt()
                val idPostPair = postsViewModel.idPostPairs.value!![index]
                pinDetailsOpen(idPostPair.second, idPostPair.first)
                true
            }

            return@setOnMarkerClickListener true
        }

        mMap.setOnMapClickListener {
            if (detailActive) {
                pinDetailsClose()
                detailActive = false
            }
        }

        postsViewModel.idPostPairs.observe(this) {
            println("debug: Calling post list observe")
            mMap.clear()
            val markerOptions = MarkerOptions()
            val filteredPairs = postFilter.filter(it)
            for (pair in filteredPairs) {
                val post = pair.second
                val latLng = LatLng(post.location.latitude, post.location.longitude)
                markerOptions.position(latLng)
                markerOptions.title(it.indexOf(pair).toString())
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getBitmapForIcon(post.iconType, post.iconColor)))
                mMap.addMarker(markerOptions)
            }
        }
    }


    private fun getBitmapForIcon(typeVal: Int, colorVal: Int): Bitmap {
        val type = IconType.fromInt(typeVal)
        val color = Color.fromInt(colorVal)
        val drawableId = when (type) {
            IconType.NONE -> when (color) {
                Color.GREY -> R.drawable.general_grey
                Color.RED -> R.drawable.general_red
                Color.GREEN -> R.drawable.general_green
                Color.BLUE -> R.drawable.general_blue
            }
            IconType.FOOD -> when (color) {
                Color.GREY -> R.drawable.dining_grey
                Color.RED -> R.drawable.dining_red
                Color.GREEN -> R.drawable.dining_green
                Color.BLUE -> R.drawable.dining_blue
            }
            IconType.GAME -> when (color) {
                Color.GREY -> R.drawable.gamepad_grey
                Color.RED -> R.drawable.gamepad_red
                Color.GREEN -> R.drawable.gamepad_green
                Color.BLUE -> R.drawable.gamepad_blue
            }
            IconType.SPORT -> when (color) {
                Color.GREY -> R.drawable.running_grey
                Color.RED -> R.drawable.running_red
                Color.GREEN -> R.drawable.running_green
                Color.BLUE -> R.drawable.running_blue
            }
        }
        val drawable = AppCompatResources.getDrawable(this, drawableId)?.mutate()
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable?.setBounds(0, 0, 100, 100)
        drawable?.draw(canvas)
        return bitmap
    }

    private fun pinDetailsOpen(post: Post, id: String) {
        //Zoom on the pin selected

        var animation: Animation? = null
        animation = PinDetailAnimation(binding.pinDetailFragmentContainer, 1000, 0)
        animation.duration = 1000
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationStart(animation: Animation?) {
                detailsFragment = PinDetailsFragment(post, id)
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.pin_detail_fragment_container, detailsFragment!!).commit()
                binding.pinDetailFragmentContainer.visibility = View.VISIBLE
            }
        })
        mMap.uiSettings.isScrollGesturesEnabled = false;
        mMap.uiSettings.isRotateGesturesEnabled = false;
        mMap.uiSettings.isZoomControlsEnabled = false;
        mMap.uiSettings.isZoomGesturesEnabled = false;
        mMap.uiSettings.isTiltGesturesEnabled = false;
        mMap.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = false;
        binding.pinDetailFragmentContainer.startAnimation(animation)
    }

    private fun pinDetailsClose() {
        var animation: Animation? = null
        animation = PinDetailAnimation(binding.pinDetailFragmentContainer, 1000, 1)
        animation.duration = 1000
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                // Remember to destroy the fragment
                binding.pinDetailFragmentContainer.visibility = View.GONE
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.remove(detailsFragment!!).commit()
                detailsFragment = null
            }
        })
        mMap.uiSettings.isScrollGesturesEnabled = true;
        mMap.uiSettings.isScrollGesturesEnabled = true;
        mMap.uiSettings.isRotateGesturesEnabled = true;
        mMap.uiSettings.isZoomControlsEnabled = true;
        mMap.uiSettings.isZoomGesturesEnabled = true;
        mMap.uiSettings.isTiltGesturesEnabled = true;
        mMap.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = true;
        binding.pinDetailFragmentContainer.startAnimation(animation)
    }

    private fun moveMapToLocation() {
        try {
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            val provider = locationManager.getBestProvider(criteria, true)
            val location = locationManager.getLastKnownLocation(provider!!)
            if (location != null && !sentUserToLocation) {
                val latitude = location.latitude
                val longitude = location.longitude
                val latLng = LatLng(latitude, longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f))
                sentUserToLocation = true
            }
            locationManager.requestLocationUpdates(provider, 1000, 0f, this)
        } catch (e: SecurityException) {

        }
    }

    override fun onLocationChanged(location: Location) {
        if (!sentUserToLocation) {
            val latLng = LatLng(location.latitude, location.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f))
            sentUserToLocation = true
        }
    }

    private fun askForPermissions() {
        val permissions = ArrayList<String>()
        //Ask for Permissions
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.CAMERA)

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)

        if (permissions.size > 0)
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(),0)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (!grantResults.contains(PackageManager.PERMISSION_DENIED)) { // no denied permissions
                Toast.makeText(this, getString(R.string.perms_granted), Toast.LENGTH_SHORT).show()
                moveMapToLocation()
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setCancelable(false)
                builder.setMessage(R.string.perms_required)
                builder.setTitle(R.string.perms_required_title)
                if (resources.getString(R.string.mode) == "Day")
                    builder.setIcon(R.drawable.ic_round_warning_black)
                else
                    builder.setIcon(R.drawable.ic_round_warning_white)

                builder.setPositiveButton(R.string.perms_required_yes, DialogInterface.OnClickListener { dialog, which ->
                    askForPermissions()
                })

                builder.setNegativeButton(R.string.perms_required_no, DialogInterface.OnClickListener { dialog, which ->
                    finish()
                })

                val dialog = builder.create()
                // Style the buttons using code since this is an AlertDialog
                dialog.setOnShowListener {
                    val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    positiveButton.setTextColor(android.graphics.Color.parseColor("#1A60ED"))
                    positiveButton.isAllCaps = false
                    positiveButton.typeface = Typeface.DEFAULT_BOLD
                    positiveButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    positiveButton.letterSpacing = 0f

                    val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    negativeButton.setTextColor(android.graphics.Color.parseColor("#FF3333"))
                    negativeButton.isAllCaps = false
                    negativeButton.typeface = Typeface.DEFAULT_BOLD
                    negativeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    negativeButton.letterSpacing = 0f
                }
                dialog.show()
            }
        }
    }

    override fun onBackPressed() {
        if (detailActive) {
            pinDetailsClose()
            detailActive = false
        } else {
            finish()
        }
    }
}