package com.ongo.signal.ui.match

import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.ongo.signal.data.model.match.LatLng
import com.ongo.signal.data.model.match.MatchRegistrationRequest
import com.ongo.signal.data.repository.SignalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Timer
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val signalRepository: SignalRepository
) : ViewModel() {
    suspend fun postMatchRegistration(request: MatchRegistrationRequest): String {
        val result = viewModelScope.async(Dispatchers.IO) {
            runCatching { signalRepository.postMatchRegistration(request) }
                .onSuccess {
                    Timber.d("성공 ${it.body()}")
                    return@async it.body().toString()
                }
                .onFailure {
                    it.printStackTrace()
                }
            return@async FAILURE_MESSAGE
        }
        return result.await()
    }

    //함수를 호출하기전 권한을 허락 맡으므로 MissingPermission 어노테이션 추가
    @SuppressLint("MissingPermission")
    suspend fun getLocation(fusedLocationProviderClient: FusedLocationProviderClient): LatLng? {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine<LatLng?> { continuation ->
                fusedLocationProviderClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                )
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            continuation.resume(LatLng(it.latitude, it.longitude))
                        } ?: continuation.resume(null)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }
        }
    }

    suspend fun deleteMatchRegistration(userId: Long) {
        viewModelScope.launch {
            signalRepository.deleteMatchRegistration(userId)
        }
    }


    companion object {
        const val FAILURE_MESSAGE = "통신이 되지 않습니다."
    }
}