package com.example.dandashkora // Use your package name

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class AppLifecycleObserver(private val application: Application) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        // This method is called whenever the app comes to the foreground.
        val targetAppPackageName = "com.emanuelef.remote_capture"

        if (isAppInstalled(targetAppPackageName, application.packageManager)) {
            // If the app is found, launch the BlockedActivity.
            val intent = Intent(application, BlockedActivity::class.java).apply {
                // These flags clear the existing activity stack and start fresh.
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            application.startActivity(intent)
        }
    }

    private fun isAppInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}