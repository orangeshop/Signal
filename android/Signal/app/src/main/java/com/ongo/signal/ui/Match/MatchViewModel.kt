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
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val signalRepository: SignalRepository
) : ViewModel() {
    // uiState

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            when(throwable.message) {
                "asd" -> {
                    // show Toast
                    // uiEffect
                }
                "zxc" -> {
                    // quit the app
                    // uiEffect
                }
            }
            // uiState.value.update {
            //   it.copy(toastMessage = "${throwable.message}.", isToastShowing = true)
            // }
        }

    fun postMatchRegistration(
        request: MatchRegistrationRequest,
        onSuccess: (String) -> Unit
    ) {
//        val result = viewModelScope.async(Dispatchers.IO) {
//            runCatching { signalRepository.postMatchRegistration(request) }
//                .onSuccess {
//                    Timber.d("성공 ${it.body()}")
//                    return@async "200"
//                }
//                .onFailure {
//                    it.printStackTrace()
//                }
//            return@async FAILURE_MESSAGE
//        }
//        return result.await()
        viewModelScope.launch(coroutineExceptionHandler) {
            signalRepository.postMatchRegistration(request).onSuccess { res ->
                res?.let {
                    println(it.latitude)
                    println(it.longitude)
                    println(it.user_id)
                    println(it.location_id)
                }
            }.onFailure { throw it }
        }
    }

    //함수를 호출하기전 권한을 허락 맡으므로 MissingPermission 어노테이션 추가
//    @SuppressLint("MissingPermission")
//    suspend fun getLocation(fusedLocationProviderClient: FusedLocationProviderClient): LatLng? {
//        return withContext(Dispatchers.IO) {
//            suspendCancellableCoroutine<LatLng?> { continuation ->
//                fusedLocationProviderClient.getCurrentLocation(
//                    Priority.PRIORITY_HIGH_ACCURACY,
//                    null
//                )
//                    .addOnSuccessListener { location: Location? ->
//                        location?.let {
//                            continuation.resume(LatLng(it.latitude, it.longitude))
//                        } ?: continuation.resume(null)
//                    }
//                    .addOnFailureListener { exception ->
//                        continuation.resumeWithException(exception)
//                    }
//            }
//        }
//    }

    suspend fun deleteMatchRegistration(userId: Long) {
        viewModelScope.launch {
            signalRepository.deleteMatchRegistration(userId)
        }
    }


    companion object {
        const val FAILURE_MESSAGE = "통신이 되지 않습니다."
    }
}