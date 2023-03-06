package com.example.sampleweatherapp.untils

import android.content.SharedPreferences
import androidx.annotation.IdRes
import com.example.sampleweatherapp.R
import java.util.*
import kotlin.math.roundToInt


const val TEMP = "TEMP"
const val WIND_SPEED = "WIND_SPEED"
const val PRESSURE = "PRESSURE"

object SettingsHolder {
    private lateinit var preferences: SharedPreferences

    var temp = Setting.TEMP_CELSIUS
    var windSpeed = Setting.WIND_SPEED_MS
    var pressure = Setting.PRESSURE_HPA

    fun onCreate(preferences: SharedPreferences) {
        this.preferences = preferences
        temp = getSetting(preferences.getInt(TEMP, C))
        windSpeed = getSetting(preferences.getInt(WIND_SPEED, MS))
        pressure = getSetting(preferences.getInt(PRESSURE, HPA))
    }

    fun onDestroy() {
        val editor = preferences.edit()
        editor.putInt(TEMP, temp.prefConst)
        editor.putInt(WIND_SPEED, windSpeed.prefConst)
        editor.putInt(PRESSURE, pressure.prefConst)
        editor.apply()
    }

    private fun getSetting(@IdRes id: Int) = when (id) {
        C -> Setting.TEMP_CELSIUS
        F -> Setting.TEMP_FAHRENHEIT
        MS -> Setting.WIND_SPEED_MS
        KMH -> Setting.WIND_SPEED_KMH
        MM_HG -> Setting.PRESSURE_MMHG
        HPA -> Setting.PRESSURE_HPA
        else -> throw InputMismatchException("no such meaning")
    }


    const val C = 1
    const val F = 2
    const val MS = 3
    const val KMH = 4
    const val MM_HG = 5
    const val HPA = 6

    enum class Setting(
        @IdRes val checkedViewId: Int,
        @IdRes val measureUnitStringRes: Int,
        @IdRes val prefConst: Int,
    ) {
        TEMP_FAHRENHEIT(R.id.degree_f, R.string.f_, F) {
            override fun getValue(initValue: Double) =
                valueToString { (initValue - 273.15) * (9 / 5) * 32 }
        },
        TEMP_CELSIUS(R.id.degree_c, R.string.c, C) {
            override fun getValue(initValue: Double) =
                valueToString { initValue - 273.15 }
        },
        WIND_SPEED_MS(R.id.degree_m_c, R.string.m_c, MS) {
            override fun getValue(initValue: Double) =
                valueToString { initValue }
        },
        WIND_SPEED_KMH(R.id.degree_km_h, R.string.km_h, KMH) {
            override fun getValue(initValue: Double) =
                valueToString { initValue * 3.6 }
        },
        PRESSURE_MMHG(R.id.degree_mmhg, R.string.mmHg, MM_HG) {
            override fun getValue(initValue: Double) =
                valueToString { initValue / 1.33322387415 }
        },
        PRESSURE_HPA(R.id.degree_hpa, R.string.hpa, HPA) {
            override fun getValue(initValue: Double) =
                valueToString { initValue }
        };

        abstract fun getValue(initValue: Double): String
        protected fun valueToString(formula: () -> Double) = formula().roundToInt().toString()
    }
}