package com.kaushalya.karnataka.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaushalya.karnataka.data.model.HireRequest
import com.kaushalya.karnataka.data.model.Notification
import com.kaushalya.karnataka.data.model.Review
import com.kaushalya.karnataka.data.model.Service
import com.kaushalya.karnataka.data.model.UiState
import com.kaushalya.karnataka.data.model.Worker
import com.kaushalya.karnataka.data.repository.AuthRepository
import com.kaushalya.karnataka.data.repository.GeminiRepository
import com.kaushalya.karnataka.data.repository.SupabaseRepository
import com.kaushalya.karnataka.util.ImageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkerViewModel @Inject constructor(
    private val supabaseRepo: SupabaseRepository,
    private val geminiRepo: GeminiRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _workerState = MutableStateFlow<UiState<Worker>>(UiState.Idle)
    val workerState: StateFlow<UiState<Worker>> = _workerState.asStateFlow()

    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _portfolioPhotos = MutableStateFlow<List<String>>(emptyList())
    val portfolioPhotos: StateFlow<List<String>> = _portfolioPhotos.asStateFlow()

    private val _hireRequests = MutableStateFlow<List<HireRequest>>(emptyList())
    val hireRequests: StateFlow<List<HireRequest>> = _hireRequests.asStateFlow()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _bioState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val bioState: StateFlow<UiState<String>> = _bioState.asStateFlow()

    private val _saveState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val saveState: StateFlow<UiState<Unit>> = _saveState.asStateFlow()

    private val _hireState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val hireState: StateFlow<UiState<Unit>> = _hireState.asStateFlow()

    fun loadWorker(workerId: String) {
        viewModelScope.launch {
            _workerState.value = UiState.Loading
            try {
                supabaseRepo.getWorkerById(workerId).collect { worker ->
                    _workerState.value = if (worker != null) UiState.Success(worker)
                    else UiState.Error("Worker not found")
                }
            } catch (e: Exception) {
                _workerState.value = UiState.Error(e.message ?: "Failed to load worker")
            }
        }
        loadServices(workerId)
        loadReviews(workerId)
        loadPortfolio(workerId)
    }

    private fun loadServices(workerId: String) {
        viewModelScope.launch {
            try {
                supabaseRepo.getServicesForWorker(workerId).collect {
                    _services.value = it
                }
            } catch (_: Exception) {}
        }
    }

    private fun loadReviews(workerId: String) {
        viewModelScope.launch {
            try {
                supabaseRepo.getReviewsForWorker(workerId).collect {
                    _reviews.value = it
                }
            } catch (_: Exception) {}
        }
    }

    private fun loadPortfolio(workerId: String) {
        viewModelScope.launch {
            supabaseRepo.getPortfolioPhotos(workerId).onSuccess {
                _portfolioPhotos.value = it
            }
        }
    }

    fun loadHireRequests(userId: String, isWorker: Boolean) {
        viewModelScope.launch {
            try {
                if (isWorker) {
                    supabaseRepo.getHireRequestsForWorker(userId).collect {
                        _hireRequests.value = it
                    }
                } else {
                    supabaseRepo.getHireRequestsForCustomer(userId).collect {
                        _hireRequests.value = it
                    }
                }
            } catch (_: Exception) {}
        }
    }

    fun loadNotifications(userId: String) {
        viewModelScope.launch {
            try {
                supabaseRepo.getNotifications(userId).collect {
                    _notifications.value = it
                }
            } catch (_: Exception) {}
        }
    }

    fun markNotificationRead(notificationId: String) {
        viewModelScope.launch {
            supabaseRepo.markNotificationRead(notificationId)
        }
    }

    fun markAllNotificationsRead(userId: String) {
        viewModelScope.launch {
            supabaseRepo.markAllNotificationsRead(userId)
            _notifications.value = _notifications.value.map { it.copy(isRead = true) }
        }
    }

    fun saveWorker(worker: Worker) {
        viewModelScope.launch {
            _saveState.value = UiState.Loading
            supabaseRepo.createOrUpdateWorker(worker).fold(
                onSuccess = { _saveState.value = UiState.Success(Unit) },
                onFailure = { _saveState.value = UiState.Error(it.message ?: "Save failed") }
            )
        }
    }

    fun addService(workerId: String, service: Service) {
        viewModelScope.launch {
            supabaseRepo.addService(workerId, service).onSuccess {
                loadServices(workerId)
            }
        }
    }

    fun deleteService(workerId: String, serviceId: String) {
        viewModelScope.launch {
            supabaseRepo.deleteService(workerId, serviceId).onSuccess {
                _services.value = _services.value.filter { it.id != serviceId }
            }
        }
    }

    fun submitReview(workerId: String, review: Review) {
        viewModelScope.launch {
            _saveState.value = UiState.Loading
            supabaseRepo.addReview(workerId, review).fold(
                onSuccess = {
                    _saveState.value = UiState.Success(Unit)
                    // Reload worker data so the profile card shows updated rating
                    loadWorker(workerId)
                },
                onFailure = { _saveState.value = UiState.Error(it.message ?: "Review failed") }
            )
        }
    }

    fun sendHireRequest(workerId: String, serviceRequested: String, description: String = "", preferredTiming: String = "") {
        viewModelScope.launch {
            _hireState.value = UiState.Loading

            // Get name/phone from auth metadata first
            var customerName = authRepo.currentUserName
            var customerPhone = authRepo.currentUserPhone

            // Fallback: if metadata is empty (existing users), try workers table
            if (customerName.isBlank() || customerPhone.isBlank()) {
                try {
                    supabaseRepo.getWorkerById(authRepo.currentUserId).collect { worker ->
                        if (worker != null) {
                            if (customerName.isBlank()) customerName = worker.name
                            if (customerPhone.isBlank()) customerPhone = worker.phone
                        }
                    }
                } catch (_: Exception) { }
            }

            val request = HireRequest(
                workerId = workerId,
                customerId = authRepo.currentUserId,
                customerName = customerName,
                customerPhone = customerPhone,
                serviceRequested = serviceRequested,
                description = description,
                preferredTiming = preferredTiming
            )
            supabaseRepo.sendHireRequest(request).fold(
                onSuccess = { _hireState.value = UiState.Success(Unit) },
                onFailure = { _hireState.value = UiState.Error(it.message ?: "Request failed") }
            )
        }
    }

    fun updateRequestStatus(requestId: String, status: String) {
        viewModelScope.launch {
            supabaseRepo.updateHireRequestStatus(requestId, status)
        }
    }

    fun generateBio(workerName: String, tradeCategory: String) {
        viewModelScope.launch {
            _bioState.value = UiState.Loading
            geminiRepo.generateWorkerBio(workerName, tradeCategory, _services.value).fold(
                onSuccess = { _bioState.value = UiState.Success(it) },
                onFailure = { _bioState.value = UiState.Error(it.message ?: "AI unavailable") }
            )
        }
    }

    fun uploadProfilePhoto(workerId: String, uri: Uri, context: Context) {
        viewModelScope.launch {
            val compressedUri = ImageUtils.compressImage(context, uri) ?: uri
            val bytes = context.contentResolver.openInputStream(compressedUri)?.readBytes() ?: return@launch
            supabaseRepo.uploadProfilePhoto(workerId, bytes).onSuccess { url ->
                val currentWorker = (_workerState.value as? UiState.Success)?.data ?: return@onSuccess
                saveWorker(currentWorker.copy(profilePhotoUrl = url))
            }
        }
    }

    fun uploadPortfolioPhoto(workerId: String, uri: Uri, context: Context) {
        viewModelScope.launch {
            val compressedUri = ImageUtils.compressImage(context, uri) ?: uri
            val bytes = context.contentResolver.openInputStream(compressedUri)?.readBytes() ?: return@launch
            val fileName = "photo_${System.currentTimeMillis()}.jpg"
            supabaseRepo.uploadPortfolioPhoto(workerId, bytes, fileName).onSuccess { url ->
                _portfolioPhotos.value = _portfolioPhotos.value + url
            }
        }
    }

    fun resetSaveState() { _saveState.value = UiState.Idle }
    fun resetHireState() { _hireState.value = UiState.Idle }
    fun resetBioState() { _bioState.value = UiState.Idle }
}
