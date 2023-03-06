package com.example.sampleweatherapp.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.sampleweatherapp.R
import com.example.sampleweatherapp.databinding.ActivitySettingsBinding
import com.example.sampleweatherapp.untils.SettingsHolder
import com.google.android.material.button.MaterialButtonToggleGroup

class SettingsActivity : AppCompatActivity() {

    private val binding: ActivitySettingsBinding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding){
            toolbar.setNavigationOnClickListener { exitOnBackPressed() }
            setOnBackPressed()
            setSavedSettings()
            listOf( groupTemp,groupWindSpeed,groupPressure).forEach {
                it.addOnButtonCheckedListener(ToggleButtonClickListener)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SettingsHolder.onDestroy()
    }

    private fun setSavedSettings() = with(binding) {
        groupTemp.check(SettingsHolder.temp.checkedViewId)
        groupWindSpeed.check(SettingsHolder.windSpeed.checkedViewId)
        groupPressure.check(SettingsHolder.pressure.checkedViewId)
    }

    private fun setOnBackPressed() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exitOnBackPressed()
                }
            })
    }

    private fun exitOnBackPressed() {
        Intent(this@SettingsActivity, MainActivity::class.java).apply {
            startActivity(this)
            overridePendingTransition(R.anim.slide_in, android.R.anim.fade_out)
            finish()
        }
    }

    private object ToggleButtonClickListener : MaterialButtonToggleGroup.OnButtonCheckedListener {
        override fun onButtonChecked(
            group: MaterialButtonToggleGroup?,
            checkedId: Int,
            isChecked: Boolean,
        ) {
            when (checkedId to isChecked) {
                R.id.degree_c to true -> SettingsHolder.temp =
                    SettingsHolder.Setting.TEMP_CELSIUS
                R.id.degree_f to true -> SettingsHolder.temp =
                    SettingsHolder.Setting.TEMP_FAHRENHEIT
                R.id.degree_m_c to true -> SettingsHolder.temp =
                    SettingsHolder.Setting.WIND_SPEED_MS
                R.id.degree_km_h to true -> SettingsHolder.temp =
                    SettingsHolder.Setting.WIND_SPEED_KMH
                R.id.degree_mmhg to true -> SettingsHolder.temp =
                    SettingsHolder.Setting.PRESSURE_MMHG
                R.id.degree_hpa to true -> SettingsHolder.temp =
                    SettingsHolder.Setting.PRESSURE_HPA
            }
        }

    }
}