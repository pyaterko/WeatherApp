package com.example.sampleweatherapp

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import com.google.android.material.dialog.MaterialAlertDialogBuilder


object DialogManager {

    fun showNotificationPermissionRationale(
        context: Context,
        onClickPositiveButton: () -> Unit,
    ) {

        MaterialAlertDialogBuilder(
            context,
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        )
            .setTitle(context.getString(R.string.notification_permission))
            .setMessage(context.getString(R.string.to_show_notification))
            .setPositiveButton(context.getString(R.string.ok)) { _, _ ->
                if (Build.VERSION.SDK_INT >= 33) {
                    onClickPositiveButton()
                }
            }
            .setNegativeButton(context.getString(R.string.no), null)
            .show()
    }


    fun showDialogLocationPermission(context: Context, onClickPositiveButton: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        with(dialog) {
            setTitle(context.getString(R.string.location_permission_needed))
            setMessage(context.getString(R.string.this_app_needs_the_Location_permission))
            setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok)) { _, _ ->
                onClickPositiveButton()
            }
            show()
        }
    }

}

