package com.cmpt362co.genericproject

import android.content.Intent
import android.graphics.Insets.add
import android.os.Bundle
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cmpt362co.genericproject.activities.FilterActivity
import com.cmpt362co.genericproject.animation.PinDetailAnimation
import com.cmpt362co.genericproject.databinding.ActivityMapsBinding
import com.cmpt362co.genericproject.fragments.PinDetailsFragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapsActivity : AppCompatActivity(), OnMapReadyCallback{
    // Google map variable
    private lateinit var mMap: GoogleMap
    // Binding for the xml file
    private lateinit var binding: ActivityMapsBinding
    private var detailActive: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create the binding and set content view
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Hides the top tool bar
        supportActionBar?.hide()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.mainFilterButton.setOnClickListener{
            val intent = Intent(this, FilterActivity::class.java).apply {

            }
            startActivity(intent)
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
        val newFragment: Fragment = PinDetailsFragment()
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.add(R.id.pin_detail_fragment_container, newFragment).commit()
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
}