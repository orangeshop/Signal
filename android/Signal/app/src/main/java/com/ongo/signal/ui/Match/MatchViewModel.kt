package com.ongo.signal.ui.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.data.model.match.MatchRegistrationRequest
import com.ongo.signal.data.model.match.MatchRegistrationResponse
import com.ongo.signal.data.repository.SignalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val signalRepository: SignalRepository
) : ViewModel() {
    // uiState

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            Timber.d("${throwable.message}")
        }

    fun postMatchRegistration(
        request: MatchRegistrationRequest,
        onSuccess: (MatchRegistrationResponse) -> Unit
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
            signalRepository.postMatchRegistration(request).onSuccess { response ->
                response?.let {
                    onSuccess(response)
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