package com.cmpt362.nearby.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Criteria
import android.location.LocationManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.nearby.R
import com.cmpt362.nearby.databinding.ActivityChooseLocationBinding
import com.cmpt362.nearby.viewmodels.ChooseLocationViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class ChooseLocationActivity: AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private lateinit var binding: ActivityChooseLocationBinding
    private lateinit var chooseLocationViewModel: ChooseLocationViewModel
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    private lateinit var gMap: GoogleMap
    private lateinit var markerOptions: MarkerOptions
    private lateinit var locationManager: LocationManager
    private lateinit var marker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseLocationBinding.inflate(layoutInflater)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.chooselocation_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Set up viewmodel
        chooseLocationViewModel = ViewModelProvider(this).get(ChooseLocationViewModel::class.java)

        // Save Button
        saveButton = binding.chooselocationSave
        saveButton.setOnClickListener {
            val returnIntent = Intent()
            returnIntent.putExtra("latitude", chooseLocationViewModel.lat.value)
            returnIntent.putExtra("longitude", chooseLocationViewModel.lng.value)
            setResult(Activity.RESULT_OK, returnIntent)
            Toast.makeText(this, R.string.chooselocation_toast, Toast.LENGTH_SHORT).show()
            finish()
        }

        // Cancel Button
        cancelButton = binding.chooselocationCancel
        cancelButton.setOnClickListener {
            val returnIntent = Intent()
            setResult(Activity.RESULT_CANCELED, returnIntent)
            finish()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap.setOnMapClickListener(this)
        markerOptions = MarkerOptions()
        initLocationManager()

        // For rotation
        if (chooseLocationViewModel.lat.value!! != 0.0 && chooseLocationViewModel.lng.value != 0.0) {
            val latLng = LatLng(chooseLocationViewModel.lat.value!!, chooseLocationViewModel.lng.value!!)
            markerOptions.position(latLng)
            gMap.addMarker(markerOptions)
            val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
            gMap.animateCamera(cameraUpdate)
        }
    }

    override fun onMapClick(latLng: LatLng) {
        gMap.clear()
        markerOptions.position(latLng)
        gMap.addMarker(markerOptions)
        chooseLocationViewModel.lat.value = latLng.latitude
        chooseLocationViewModel.lng.value = latLng.longitude
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
        gMap.animateCamera(cameraUpdate)
    }

    @SuppressLint("MissingPermission")
    fun initLocationManager() {
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            val provider = locationManager.getBestProvider(criteria, true)
            val location = locationManager.getLastKnownLocation(provider!!)
            if (location != null) {
                if (chooseLocationViewModel.lat.value!! == 0.0 && chooseLocationViewModel.lng.value!! == 0.0) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
                    gMap.animateCamera(cameraUpdate)
                    gMap.clear()
                    markerOptions.position(latLng)
                    gMap.addMarker(markerOptions)
                    chooseLocationViewModel.lat.value = location.latitude
                    chooseLocationViewModel.lng.value = location.longitude
                }
            }
        } catch (e: SecurityException) {

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val returnIntent = Intent()
            setResult(Activity.RESULT_CANCELED, returnIntent)
            finish()
        }
        return true
    }
}