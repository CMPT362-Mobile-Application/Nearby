package com.cmpt362.nearby

import android.os.Bundle
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import com.cmpt362.nearby.animation.PinDetailAnimation
import com.cmpt362.nearby.databinding.ActivityMapsBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
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

        mMap.setOnMapClickListener {
            mMap.addMarker(MarkerOptions().position(it).title("Placed"))
        }

        mMap.setOnMarkerClickListener {
            println("debug: clicked")
            var animation: Animation? = null
            if (detailActive) {
                animation = PinDetailAnimation(binding.pinDetailFragmentContainer, 1000, 1)
                println("debug: closed")
                detailActive = false
            } else {
                animation = PinDetailAnimation(binding.pinDetailFragmentContainer, 1000, 0)
                println("debug: open")
                detailActive = true
            }
            animation.duration = 1000
            binding.pinDetailFragmentContainer.startAnimation(animation)

            return@setOnMarkerClickListener true
        }
    }
}