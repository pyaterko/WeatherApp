package com.example.sampleweatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.sampleweatherapp.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private var interval = 10000L
    private lateinit var location: Location
    private val geoService by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    private val locationRequest by lazy { initLocationRequest() }
    private val locationCallback by lazy { initLocationCallback() }
    private val requestPermissionLauncher by lazy {
        requestPermissionLauncher()
    }
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkPermissions()

    }

    //--------------------location_code-----------------------
    private fun initLocationRequest() =
        LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            interval
        )
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(interval)
            .setMaxUpdateDelayMillis(interval)
            .build()

    private fun initLocationCallback() = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            // val currentLocation = locationResult.lastLocation
            for (loc in locationResult.locations) {
                location = loc

            }
            Log.d("initLocationCallback","$location")
            // currentLocation?.let { location = it }
        }
    }
    //--------------------location_code-----------------------

    //--------------------request permission-----------------------
    private fun checkPermissions() {
        if (!Manifest.permission.ACCESS_FINE_LOCATION.checkPermission(this)) {
            DialogManager.showDialogForRationaleRequestLocationPermission(this) {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
            }
        }
        if (Build.VERSION.SDK_INT >= 33) {
            requestNotificationPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestPermissionLauncher() =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted ->
            if (isGranted.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
                requestBackgroundLocationPermission()
                geoService.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )
            thread {
            Thread.sleep(15000)
                    geoService.removeLocationUpdates(locationCallback)
            }
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        if (!Manifest.permission.POST_NOTIFICATIONS.checkPermission(this)){
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                DialogManager.showRequestNotificationPermissionRationale(this) {
                    requestPermissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
                }
            } else {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
            }
        }
    }

    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!Manifest.permission.ACCESS_BACKGROUND_LOCATION.checkPermission(this)) {
                DialogManager.showDialogBackgroundLocationPermission(this) {
                    requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
                }
            }
        }
    }

    //--------------------request permission-----------------------
}