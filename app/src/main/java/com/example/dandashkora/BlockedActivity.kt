package com.example.dandashkora // Use your package name

import android.os.Bundle
import android.widget.Button
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity

class BlockedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocked)

        findViewById<Button>(R.id.btnExit).setOnClickListener {
            // finishAffinity() closes the entire app, not just this activity.
            finishAffinity()
        }

        // NEW: The correct way to disable the back button
        onBackPressedDispatcher.addCallback(this) {
            // This callback is enabled, but the lambda is empty.
            // This effectively "catches" the back press and does nothing,
            // preventing the user from leaving the activity.
        }
    }

    // You can now completely remove the old override fun onBackPressed()
}