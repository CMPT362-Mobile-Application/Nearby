package com.cmpt362.nearby

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cmpt362.nearby.activities.FavouriteActivity
import com.cmpt362.nearby.activities.FilterActivity
import com.cmpt362.nearby.activities.NewPostActivity
import com.cmpt362.nearby.animation.PinDetailAnimation
import com.cmpt362.nearby.database.FirestoreDatabase
import com.cmpt362.nearby.databinding.ActivityMapsBinding
import com.cmpt362.nearby.fragments.PinDetailsFragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationBarView


class MapsActivity : AppCompatActivity(), OnMapReadyCallback{
    // Google map variable
    private lateinit var mMap: GoogleMap
    // Binding for the xml file
    private lateinit var binding: ActivityMapsBinding
    private var detailActive: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Ask for Permissions
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }

        // Create the binding and set content view
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Hides the top tool bar
        supportActionBar?.hide()

        getData()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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
                    startActivity(Intent(this, FavouriteActivity::class.java))
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

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mMap.setOnMarkerClickListener {

            if (detailActive) {
                pinDetailsClose()
                detailActive = false
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(it.position));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f));
                pinDetailsOpen()
                detailActive = true
            }

            return@setOnMarkerClickListener true
        }
    }

    private fun pinDetailsOpen() {
        //Zoom on the pin selected
        val detailsFragment = PinDetailsFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.pin_detail_fragment_container, detailsFragment).commit()

        var animation: Animation? = null
        animation = PinDetailAnimation(binding.pinDetailFragmentContainer, 1000, 0)
        animation.duration = 1000
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
        mMap.uiSettings.isScrollGesturesEnabled = true;
        mMap.uiSettings.isScrollGesturesEnabled = true;
        mMap.uiSettings.isRotateGesturesEnabled = true;
        mMap.uiSettings.isZoomControlsEnabled = true;
        mMap.uiSettings.isZoomGesturesEnabled = true;
        mMap.uiSettings.isTiltGesturesEnabled = true;
        mMap.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = true;
        binding.pinDetailFragmentContainer.startAnimation(animation)
    }

    private fun getData() {
        val db = FirestoreDatabase()
        db.getPost()
    }
}