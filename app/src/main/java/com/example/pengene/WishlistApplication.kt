package com.example.pengene

import android.app.Application
import com.example.pengene.data.remote.api.SupabaseClient
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WishlistApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize SupabaseClient with application context
        SupabaseClient.initialize(this)
    }
}