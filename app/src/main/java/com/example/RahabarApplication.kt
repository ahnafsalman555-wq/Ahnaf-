package com.example

import android.app.Application
import com.example.audio.QuranAudioPlayer
import com.example.data.local.RahabarDatabase
import com.example.data.local.UserPreferencesDataStore
import com.example.data.repository.RahabarRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class RahabarApplication : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: RahabarDatabase by lazy { RahabarDatabase.getInstance(this) }
    val preferences: UserPreferencesDataStore by lazy { UserPreferencesDataStore(this) }
    val repository: RahabarRepository by lazy { RahabarRepository(database, preferences) }
    val quranAudioPlayer: QuranAudioPlayer by lazy { QuranAudioPlayer() }

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            repository.ensureSeeded()
        }
    }
}
