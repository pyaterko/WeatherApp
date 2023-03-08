package com.example.sampleweatherapp.views.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.example.sampleweatherapp.R
import com.example.sampleweatherapp.databinding.FragmentDailyInfoBinding
import com.example.sampleweatherapp.model.api.models.Daily
import com.example.sampleweatherapp.untils.*
import com.example.sampleweatherapp.views.viewBinding

@Suppress("ktPropBy")
class DailyInfoFragment : BaseFragment<Daily>(R.layout.fragment_daily_info) {

    private val binding by viewBinding(FragmentDailyInfoBinding::bind)

    @SuppressLint("SetTextI18n")
    override fun updateView() {
        binding.apply {
            data?.let {
                tvCurrentData.text = it.dt.getDateFormat(dateFormatter)
                tvTemp.text = it.temp.max.toDegree() + getString(R.string.temp_)
                ivWeather.setIcon(it.weather[0].icon)
                tvTempMorn.text = it.temp.morn.toDegree() + getString(R.string.temp_)
                tvTempDay.text = it.temp.day.toDegree() + getString(R.string.temp_)
                tvTempEve.text = it.temp.eve.toDegree() + getString(R.string.temp_)
                tvTempNight.text = it.temp.night.toDegree() + getString(R.string.temp_)
                tvTempMornFeel.text = it.feels_like.morn.toDegree() + getString(R.string.temp_)
                tvTempDayFeel.text = it.feels_like.day.toDegree() + getString(R.string.temp_)
                tvTempEveFeel.text = it.feels_like.eve.toDegree() + getString(R.string.temp_)
                tvTempNightFeel.text = it.feels_like.night.toDegree() + getString(R.string.temp_)
                humidity.text = it.humidity.toString() + getString(R.string.humidity_)
                windSpeed.setWindSpeedValue(requireContext(), it.wind_speed)
                tvPressure.setPressureValue(requireContext(), it.pressure)
                tvSunset.text = it.sunset.getDateFormat(timeFormatter)
                tvSunrise.text = it.sunrise.getDateFormat(timeFormatter)
                tvWindDirection.text=it.wind_deg.toString()

            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btBack.setOnClickListener {
            fragmentMgr.popBackStack()
        }
        updateView()
    }
}