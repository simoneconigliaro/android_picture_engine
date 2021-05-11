package com.simoneconigliaro.pictureengine.persistence

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PreferencesManager(context: Context) {

    private val USER_PREFERENCES_NAME = "user_preferences"
    private val SORT_ORDER_KEY = stringPreferencesKey("sort_order")
    private val THEME_KEY = intPreferencesKey("theme")
    private val SORT_ORDER_DEFAULT_VALUE = "popular"
    private val THEME_DEFAULT_VALUE = -1

    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = USER_PREFERENCES_NAME)

    private val mDataStore: DataStore<Preferences> = context.datastore

    suspend fun saveSortOrder(value: String) {
        mDataStore.edit { preferences ->
            preferences[SORT_ORDER_KEY] = value
        }
    }

    suspend fun saveTheme(value: Int) {
        mDataStore.edit { preferences ->
            preferences[THEME_KEY] = value
        }
    }

    val getTheme: Flow<Int> = mDataStore.data
        .map { preferences ->
            preferences[THEME_KEY] ?: THEME_DEFAULT_VALUE
        }

    suspend fun getSortOrder(): String {
        val preferences = mDataStore.data.first()
        return preferences[SORT_ORDER_KEY] ?: SORT_ORDER_DEFAULT_VALUE
    }

}
