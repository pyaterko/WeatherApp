package com.example.sampleweatherapp

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

 fun String.checkPermission(context: Context): Boolean {
    return when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(context, this) -> true
        else -> false
    }
}