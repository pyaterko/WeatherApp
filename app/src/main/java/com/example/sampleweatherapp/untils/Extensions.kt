package com.example.sampleweatherapp.untils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.sampleweatherapp.R
import com.example.sampleweatherapp.model.api.models.geocod.GeoCodeItem
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import java.text.SimpleDateFormat
import java.util.*

fun Boolean.getIconFavorite() = if (this) {
    R.drawable.baseline_star_rate_24
} else {
    R.drawable.baseline_star_border_24
}

fun GeoCodeItem.getCityName() = when (Locale.getDefault().displayLanguage) {
    "русский" -> local_names?.ru
    "English" -> local_names?.en
    else -> name
}

@SuppressLint("ResourceType")
fun TextView.setPressureValue(context: Context, data: Int) {
    val pressure = SettingsHolder.pressure
    this.text = context.getString(
        pressure.measureUnitStringRes,
        pressure.getValue(data.toDouble())
    )
}

@SuppressLint("ResourceType")
fun TextView.setWindSpeedValue(context: Context, data: Double) {
    val windSpeed = SettingsHolder.windSpeed
    this.text = context.getString(
        windSpeed.measureUnitStringRes,
        windSpeed.getValue(data)
    )
}


fun ImageView.setIcon(icon: String) {
    Glide.with(this.context)
        .load("http://openweathermap.org/img/wn/$icon@2x.png")
        .centerCrop()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(R.drawable.baseline_wb_sunny_24)
        .error(R.drawable.baseline_wb_twilight_24)
        .into(this)
}

fun String.checkPermission(context: Context): Boolean {
    return when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(context, this) -> true
        else -> false
    }
}

@SuppressLint("SimpleDateFormat")
fun Long.getDateFormat(format: String): String {
    val calendar = Calendar.getInstance()
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    calendar.timeInMillis = this * 1000
    return sdf.format(calendar.time)
}

fun Double.toDegree() = SettingsHolder.temp.getValue(this)

fun TextInputEditText.createObservable(): Flowable<String> {
    return Flowable.create({
        addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(p0: Editable?) {
                it.onNext(p0.toString())
            }
        })
    }, BackpressureStrategy.BUFFER)
}

private abstract class SimpleTextWatcher : TextWatcher {
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun afterTextChanged(p0: Editable?) {}
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
}