package com.example.pengene.data.remote.api

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    val supabase = createSupabaseClient(
        supabaseUrl = "https://ljgllzhnwxeizsmnfbui.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImxqZ2xsemhud3hlaXpzbW5mYnVpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDc4OTI1NzQsImV4cCI6MjA2MzQ2ODU3NH0.AAW0qsOZ_WvrjEgr2Nlvy3-bwkWQt6R6a2VdAAeFP7I"
    ) {
        install(Postgrest)
        install(Auth)
        install(Storage)
    }
}