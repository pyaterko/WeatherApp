package com.example.sampleweatherapp.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.elveum.elementadapter.simpleAdapter
import com.example.sampleweatherapp.R
import com.example.sampleweatherapp.databinding.ActivityMainBinding
import com.example.sampleweatherapp.databinding.ItemHorizontalBinding
import com.example.sampleweatherapp.model.api.models.Daily
import com.example.sampleweatherapp.model.api.models.Hourly
import com.example.sampleweatherapp.model.api.models.WeatherData
import com.example.sampleweatherapp.presenters.MainPresenter
import com.example.sampleweatherapp.untils.*
import com.example.sampleweatherapp.views.fragments.DailyListFragment
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import moxy.presenter.InjectPresenter
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainActivity : MvpAppCompatActivity(), MainView {

    @Inject
    lateinit var ptr: MainPresenter

    @Suppress("ktPropBy")
    private val presenter by moxyPresenter { ptr }


    private var location: Location? = null
    private val geoService by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    private val locationRequest by lazy { initLocationRequest() }
    private val locationCallback by lazy { initLocationCallback() }
    private val requestPermissionLauncher by lazy {
        requestPermissionLauncher()
    }
    private val hourlyAdapter = getHourlyAdapter()

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkPermissions()
        presenter.enable()
        binding.rvMainHorizontal.adapter = hourlyAdapter
        binding.bottomSheet.isNestedScrollingEnabled = true
        launchDailyListFragment()
        setSizeBottomSheetContainer()
        getUpToDateData()
        initSearchAndSettingsButton()
        initRefresh()
    }

    @SuppressLint("ResourceAsColor")
    private fun initRefresh() {
        binding.refresh.apply {
            isRefreshing = true
            setColorSchemeColors(R.color.purple_700)
            setProgressViewEndTarget(false, 320)
            setOnRefreshListener {
                initGeoService()
            }
        }
    }

    private fun initSearchAndSettingsButton() {
        binding.btFilled.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in, android.R.anim.fade_out)
            // finish()
        }
        binding.btSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            overridePendingTransition(R.anim.slide_out, android.R.anim.fade_out)
//            finish()
        }
    }

    private fun getUpToDateData() {
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

    private fun setSizeBottomSheetContainer() {
        val x = ScreenSizeCompat.getScreenSize(this).width
        val y = ScreenSizeCompat.getScreenSize(this).height
        binding.mainBottomSheetContainer.layoutParams =
            CoordinatorLayout.LayoutParams(x, (y * COEFFICIENT_SCREEN_SIZE).roundToInt())
    }

    private fun launchDailyListFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fr_container, DailyListFragment(), DailyListFragment::class.simpleName)
            .commit()
    }


    //--------------------location_code-----------------------
    private fun initLocationRequest() =
        LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            UPDATE_INTERVAL
        )
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(UPDATE_INTERVAL)
            .setMaxUpdateDelayMillis(UPDATE_INTERVAL)
            .build()

    private fun initLocationCallback() = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val currentLocation = locationResult.lastLocation
            location = currentLocation
            location?.let {
                presenter.refresh(
                    it.latitude.toString(),
                    it.longitude.toString()
                )
            }
            removeDeoService()
        }
    }

    private fun removeDeoService() {
        geoService.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    private fun initGeoService() {
        if (Manifest.permission.ACCESS_FINE_LOCATION.checkPermission(this)) {
            geoService.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        }
    }

    //--------------------request permission-----------------------
    private fun checkPermissions() {
        checkEnabledGPS()
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

    private fun checkEnabledGPS() {
        val locManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled =
            locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isEnabled) {
            showGPSEnabledDialog()
        }
    }

    private fun showGPSEnabledDialog() {
        val builder = AlertDialog.Builder(this)
        val dialog = builder.create()
        with(dialog) {
            setTitle(context.getString(R.string.gps_disabled))
            setMessage(context.getString(R.string.is_gps_enabled))
            setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok)) { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.no)) { _, _ ->
                dismiss()
            }
            show()
        }
    }

    //--------------------adapters-----------------------


    @SuppressLint("SetTextI18n")
    private fun getHourlyAdapter() =
        simpleAdapter<Hourly, ItemHorizontalBinding> {
            areItemsSame = { oldItem, newItem -> oldItem == newItem }
            bind {
                tvTime.text = it.dt.getDateFormat(timeFormatter)
                tvTemperature.text = it.temp.toDegree() + getString(R.string.temp_)
                ivAirHumidity.text = it.humidity.toString() + getString(R.string.humidity_)
                ivWeather.setIcon(it.weather[0].icon)
            }
            listeners { }
        }

    //--------------------moxy code-----------------------

    override fun displayLocation(value: String?) {
        binding.tvCityName.text = value
    }

    @SuppressLint("SetTextI18n", "ResourceType")
    override fun displayCurrentData(data: WeatherData) = with(binding) {
        tvDate.text = data.current.dt.getDateFormat(dayFormatter)
        tvClarity.text = data.current.weather[0].description
        ivWeather.setIcon(data.current.weather[0].icon)
        tvTemperature.text = data.current.temp.toDegree() + getString(R.string.temp_)
        tvMinValue.text = data.daily[0].temp.min.toDegree() + getString(R.string.temp_)
        tvMaxValue.text = data.daily[0].temp.max.toDegree() + getString(R.string.temp_)
        tvAirHumidityValue.text = data.daily[0].humidity.toString() + getString(R.string.humidity_)
        tvWindSpeedValue.setWindSpeedValue(this@MainActivity, data.current.wind_speed)
        tvPressureValue.setPressureValue(this@MainActivity, data.current.pressure)
        tvSunriseValue.text = data.daily[0].sunrise.getDateFormat(timeFormatter)
        tvSunsetValue.text = data.daily[0].sunset.getDateFormat(timeFormatter)
    }


    override fun displayHourlyData(data: List<Hourly>) {
        hourlyAdapter.submitList(data)
    }

    override fun displayWeeklyData(data: List<Daily>) {
        (supportFragmentManager.findFragmentByTag(DailyListFragment::class.simpleName) as DailyListFragment)
            .setValueData(data)
    }

    override fun displayError(error: Throwable) {

    }

    override fun setLoading(flag: Boolean) {
        binding.refresh.isRefreshing = flag
    }
}
