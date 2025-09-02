package com.example.dandashkora.splash

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dandashkora.MainActivity
import com.example.dandashkora.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Splashactivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val targetAppPackageName = "com.emanuelef.remote_capture"

        // First, check if the problematic app is installed
        if (isAppInstalled(targetAppPackageName, packageManager)) {
            // If it is installed, show a dialog that stops the user from continuing.
            showBlockingDialog()
        } else {
            // If it's not installed, proceed with the normal 3-second splash screen flow.
            lifecycleScope.launch {
                delay(3000)
                val intent = Intent(this@Splashactivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    /**
     * Checks if a specific app is installed on the device.
     */
    private fun isAppInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * Shows a dialog that informs the user and closes the app when the button is pressed.
     */
    private fun showBlockingDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Incompatible Application Detected")
            .setMessage("To continue, please uninstall the 'Remote Capture' application, as it can interfere with our service's security.")
            .setPositiveButton("Exit App") { _, _ ->
                // When the user clicks the button, close the app.
                finish()
            }
            .setCancelable(false) // User cannot dismiss the dialog by tapping outside or pressing back.
            .show()
    }
}