package com.example.sampleweatherapp.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.elveum.elementadapter.simpleAdapter
import com.example.sampleweatherapp.R
import com.example.sampleweatherapp.databinding.ActivityMainBinding
import com.example.sampleweatherapp.databinding.ItemHorizontalBinding
import com.example.sampleweatherapp.databinding.ItemVerticalBinding
import com.example.sampleweatherapp.model.api.models.Daily
import com.example.sampleweatherapp.model.api.models.Hourly
import com.example.sampleweatherapp.model.api.models.WeatherData
import com.example.sampleweatherapp.presenters.MainPresenter
import com.example.sampleweatherapp.untils.*
import com.google.android.gms.location.*
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import kotlin.concurrent.thread


class MainActivity : MvpAppCompatActivity(), MainView {

    @Suppress("ktPropBy")
    private val presenter by moxyPresenter { MainPresenter() }


    private var interval = 20000L
    private lateinit var location: Location
    private val geoService by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    private val locationRequest by lazy { initLocationRequest() }
    private val locationCallback by lazy { initLocationCallback() }
    private val requestPermissionLauncher by lazy {
        requestPermissionLauncher()
    }
    private val hourlyAdapter = getHourlyAdapter()
    private val dailyAdapter = getDailyAdapter()
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkPermissions()
        presenter.enable()
        binding.rvMainHorizontal.adapter = hourlyAdapter
        binding.rvMainVertical.adapter = dailyAdapter
        binding.btFilled.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_out, android.R.anim.fade_out)
            finish()
        }
        binding.btSettings.setOnClickListener {

        }
        if (intent.hasExtra(COORDINATES)) {
            val coordinates = intent.extras?.getBundle(COORDINATES)
            val lat = coordinates?.getString(LAT)
            val lon = coordinates?.getString(LON)
            if (lat != null && lon != null) {
                presenter.refresh(lat, lon)
            }
        } else {
            initGeoService()
        }
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
            val currentLocation = locationResult.lastLocation
            currentLocation?.let { location = it }
            presenter.refresh(location.latitude.toString(), location.longitude.toString())
        }
    }

    @SuppressLint("MissingPermission")
    private fun initGeoService() {
        if (Manifest.permission.ACCESS_FINE_LOCATION.checkPermission(this)) {
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

    //--------------------request permission-----------------------
    private fun checkPermissions() {
        if (!Manifest.permission.ACCESS_FINE_LOCATION.checkPermission(this)) {
            DialogManager.showDialogForRationaleRequestLocationPermission(this) {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
                requestBackgroundLocationPermission()
            }
            if (Build.VERSION.SDK_INT >= 33) {
                requestNotificationPermission()
            }

        }
    }

    private fun requestPermissionLauncher() =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted ->
            if (isGranted.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
                initGeoService()
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        if (!Manifest.permission.POST_NOTIFICATIONS.checkPermission(this)) {
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

    //--------------------adapters-----------------------
    @SuppressLint("SetTextI18n")
    private fun getHourlyAdapter() =
        simpleAdapter<Hourly, ItemHorizontalBinding> {
            areItemsSame = { oldItem, newItem -> oldItem == newItem }
            bind {
                tvTime.text = it.dt.getDateFormat(timeFormatter)
                tvTemperature.text = it.temp.toInt().toString() + getString(R.string.temp_)
                ivAirHumidity.text = it.humidity.toString() + getString(R.string.humidity_)
                ivWeather.setIcon(it.weather[0].icon)
            }
            listeners { }
        }

    @SuppressLint("SetTextI18n")
    private fun getDailyAdapter() =
        simpleAdapter<Daily, ItemVerticalBinding> {
            areItemsSame = { oldItem, newItem -> oldItem == newItem }
            bind {
                tvDay.text = it.dt.getDateFormat(dateFormatter)
                tvMax.text = it.temp.max.toInt().toString()
                tvMin.text = it.temp.min.toInt().toString()
                tvAirHumidityValue.text = it.humidity.toString() + getString(R.string.humidity_)
                ivWeather.setIcon(it.weather[0].icon)
            }
            listeners { }
        }
    //--------------------moxy code-----------------------

    override fun displayLocation(value: String?) {
        binding.tvCityName.text = value
    }

    @SuppressLint("SetTextI18n")
    override fun displayCurrentData(data: WeatherData) = with(binding) {
        tvDate.text = data.current.dt.getDateFormat(dayFormatter)
        ivWeather.setIcon(data.current.weather[0].icon)
        tvTemperature.text = data.current.temp.toInt().toString()
        tvClarity.text = data.current.weather[0].description
        tvMinValue.text = data.daily[0].temp.min.toInt().toString() + getString(R.string.temp_)
        tvMaxValue.text = data.daily[0].temp.max.toInt().toString() + getString(R.string.temp_)
        tvAirHumidityValue.text = data.daily[0].humidity.toString() + getString(R.string.humidity_)
        tvWindSpeedValue.text = data.daily[0].wind_speed.toString() + getString(R.string.speed_w)
        tvPressureValue.text = data.daily[0].pressure.toString()
        tvSunriseValue.text = data.daily[0].sunrise.getDateFormat(timeFormatter)
        tvSunsetValue.text = data.daily[0].sunset.getDateFormat(timeFormatter)
    }

    override fun displayHourlyData(data: List<Hourly>) {
        hourlyAdapter.submitList(data)
    }

    override fun displayWeeklyData(data: List<Daily>) {
        dailyAdapter.submitList(data)
    }

    override fun displayError(error: Throwable) {

    }

    override fun setLoading(flag: Boolean) {

    }

    companion object {
        private const val timeFormatter = "HH:mm"
        private const val dayFormatter = "dd:MMMM"
        private const val dateFormatter = "dd EEEE"
    }

}