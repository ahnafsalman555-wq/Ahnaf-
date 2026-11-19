package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "rahabar_prefs")

enum class ReaderThemeMode {
    CREAM,
    LIGHT,
    DARK
}

data class AppPreferences(
    val currentUserUid: String? = null,
    val isGuestMode: Boolean = false,
    val languageCode: String = "bn", // Bengali default
    val darkMode: Boolean = false,
    val lowDataMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val readerFontSizeSp: Float = 17f,
    val readerLineSpacing: Float = 1.65f,
    val readerThemeMode: ReaderThemeMode = ReaderThemeMode.CREAM
)

class UserPreferencesDataStore(private val context: Context) {
    private object Keys {
        val CURRENT_USER_UID = stringPreferencesKey("current_user_uid")
        val IS_GUEST_MODE = booleanPreferencesKey("is_guest_mode")
        val LANGUAGE_CODE = stringPreferencesKey("language_code")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val LOW_DATA_MODE = booleanPreferencesKey("low_data_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val READER_FONT_SIZE = floatPreferencesKey("reader_font_size")
        val READER_LINE_SPACING = floatPreferencesKey("reader_line_spacing")
        val READER_THEME = stringPreferencesKey("reader_theme")
    }

    val preferencesFlow: Flow<AppPreferences> = context.dataStore.data.map { prefs ->
        AppPreferences(
            currentUserUid = prefs[Keys.CURRENT_USER_UID],
            isGuestMode = prefs[Keys.IS_GUEST_MODE] ?: false,
            languageCode = prefs[Keys.LANGUAGE_CODE] ?: "bn",
            darkMode = prefs[Keys.DARK_MODE] ?: false,
            lowDataMode = prefs[Keys.LOW_DATA_MODE] ?: false,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: true,
            readerFontSizeSp = prefs[Keys.READER_FONT_SIZE] ?: 17f,
            readerLineSpacing = prefs[Keys.READER_LINE_SPACING] ?: 1.65f,
            readerThemeMode = ReaderThemeMode.entries.firstOrNull {
                it.name == prefs[Keys.READER_THEME]
            } ?: ReaderThemeMode.CREAM
        )
    }

    suspend fun setSession(uid: String?, isGuest: Boolean) {
        context.dataStore.edit { prefs ->
            if (uid == null) prefs.remove(Keys.CURRENT_USER_UID) else prefs[Keys.CURRENT_USER_UID] = uid
            prefs[Keys.IS_GUEST_MODE] = isGuest
        }
    }

    suspend fun setLanguage(languageCode: String) {
        context.dataStore.edit { it[Keys.LANGUAGE_CODE] = languageCode }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DARK_MODE] = enabled }
    }

    suspend fun setLowDataMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.LOW_DATA_MODE] = enabled }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun updateReaderSettings(fontSizeSp: Float, lineSpacing: Float, themeMode: ReaderThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[Keys.READER_FONT_SIZE] = fontSizeSp
            prefs[Keys.READER_LINE_SPACING] = lineSpacing
            prefs[Keys.READER_THEME] = themeMode.name
        }
    }
}
