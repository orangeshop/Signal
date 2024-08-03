package com.ongo.signal.config

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DataStoreClass(val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dataStore")
    private val isLogin = booleanPreferencesKey("isLogin")

    //TODO Proto 파일로 바꿔서 저장
    private val userId = longPreferencesKey("userId")
    private val userLoginId = stringPreferencesKey("userLoginId")
    private val userName = stringPreferencesKey("userName")
    private val userPassword = stringPreferencesKey("userPassword")
    private val profileImage = stringPreferencesKey("profileImage")
    private val accessToken = stringPreferencesKey("accessToken")
    private val refreshToken = stringPreferencesKey("refreshToken")


    val isLoginData: Flow<Boolean> = context.dataStore.data
        .catch { _ ->
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[isLogin] ?: false
        }

    val userIdData: Flow<Long> = context.dataStore.data
        .catch { _ ->
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[userId] ?: -1
        }

    val userLoginIdData: Flow<String> = context.dataStore.data
        .catch { _ ->
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[userLoginId] ?: ""
        }

    val userNameData: Flow<String> = context.dataStore.data
        .catch { _ ->
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[userName] ?: ""
        }

    val userPasswordData: Flow<String> = context.dataStore.data
        .catch { _ ->
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[userPassword] ?: ""
        }

    val profileImageData: Flow<String> = context.dataStore.data
        .catch { _ ->
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[profileImage] ?: ""
        }

    val accessTokenData: Flow<String> = context.dataStore.data
        .catch { _ ->
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[accessToken] ?: ""
        }

    val refreshTokenData: Flow<String> = context.dataStore.data
        .catch { _ ->
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[refreshToken] ?: ""
        }

    suspend fun setIsLogin(nowIsLogin: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[isLogin] = nowIsLogin
        }
    }

    suspend fun setUserId(nowUserId: Long) {
        context.dataStore.edit { preferences ->
            preferences[userId] = nowUserId
        }
    }

    suspend fun setUserLoginId(nowUserLoginId: String) {
        context.dataStore.edit { preferences ->
            preferences[userLoginId] = nowUserLoginId
        }
    }



    suspend fun setUserName(nowUserName: String) {
        context.dataStore.edit { preferences ->
            preferences[userName] = nowUserName
        }
    }

    suspend fun setPassword(nowUserPassword: String) {
        context.dataStore.edit { preferences ->
            preferences[userPassword] = nowUserPassword
        }
    }


    suspend fun setProfileImage(nowProfileImage: String) {
        context.dataStore.edit { preferences ->
            preferences[profileImage] = nowProfileImage
        }
    }

    suspend fun setAccessToken(nowAccessToken: String) {
        context.dataStore.edit { preferences ->
            preferences[accessToken] = nowAccessToken
        }
    }

    suspend fun setRefreshToken(nowRefreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[refreshToken] = nowRefreshToken
        }
    }


    suspend fun clearData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

}