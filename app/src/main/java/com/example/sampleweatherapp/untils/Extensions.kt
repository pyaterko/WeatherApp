package com.example.sampleweatherapp.untils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.sampleweatherapp.R
import java.text.SimpleDateFormat
import java.util.*

fun ImageView.setIcon(icon: String) {
    Glide.with(this.context)
        .load("http://openweathermap.org/img/wn/$icon@2x.png")
        .diskCacheStrategy(DiskCacheStrategy.NONE)
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
