package com.mobile.app.composewidget

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.io.IOException


private const val PREFERENCES_NAME = "ComposeWidget"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)


suspend inline fun <T : Any> DataStore<Preferences>.storeValue(key: Preferences.Key<T>, value: T?) {
    edit { preferences ->
        if (value == null) {
            preferences.remove(key)
        } else {
            preferences[key] = value
        }
    }
}

fun <T : Any> DataStore<Preferences>.readValue(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
    return data.catch { recoverOrThrow(it) }.map { it[key]?:defaultValue }
}

suspend fun FlowCollector<Preferences>.recoverOrThrow(throwable: Throwable) {
    if (throwable is IOException) {
        emit(emptyPreferences())
    } else {
        throw throwable
    }
}
