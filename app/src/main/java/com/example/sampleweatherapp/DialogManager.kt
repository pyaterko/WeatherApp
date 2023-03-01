package com.example.sampleweatherapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import com.example.sampleweatherapp.databinding.DialogForRequestLocationPermissionRationaleBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


object DialogManager {

    fun showRequestNotificationPermissionRationale(
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


    fun showDialogBackgroundLocationPermission(context: Context, onClickPositiveButton: () -> Unit) {
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
    fun showDialogForRationaleRequestLocationPermission(
        context: Context,
        onClickPositiveButton: () -> Unit,
    ) {
        val builder = AlertDialog.Builder(context)
        val binding =
            DialogForRequestLocationPermissionRationaleBinding.inflate(
                LayoutInflater.from(context),
                null,
                false
            )

        builder

            .setView(binding.root)
        val dialog = builder.create()
        binding.apply {
            setDataForDialogForRationaleRequestLocationPermission(
                onClickPositiveButton,
                dialog
            )
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
    @SuppressLint("SetTextI18n")
    private fun DialogForRequestLocationPermissionRationaleBinding.setDataForDialogForRationaleRequestLocationPermission(
        onClickPositiveButton: () -> Unit,
        dialog: AlertDialog,
    ) {
        buttonYes.setOnClickListener {
            onClickPositiveButton()
            dialog.dismiss()
        }
        buttonNo.setOnClickListener {
            dialog.dismiss()
        }
    }
}

