package com.huertabeja.pedidos

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*

object SupabaseClient {

    private const val SUPABASE_URL = "https://pahjkcjiwxhohqfuurhw.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBhaGprY2ppd3hob2hxZnV1cmh3Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc2Mjk2NTg2MywiZXhwIjoyMDc4NTQxODYzfQ.1PsBJNga0xE0v9EOfoECCCun47BL2-wo8HBtweF0In4"

    val client by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            install(Auth)
            install(Postgrest)

            httpEngine = CIO.create {
                requestTimeout = 30_000

                endpoint {
                    connectTimeout = 15_000
                    socketTimeout = 30_000
                    connectAttempts = 3
                }
            }
        }
    }
}