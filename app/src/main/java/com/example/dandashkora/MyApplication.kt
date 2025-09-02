package com.example.dandashkora // Use your package name

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val lifecycleObserver = AppLifecycleObserver(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)
    }
}