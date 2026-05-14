package com.kaushalya.karnataka.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.gotrue.user.UserInfo
import com.kaushalya.karnataka.data.model.UiState
import com.kaushalya.karnataka.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<UiState<UserInfo>>(UiState.Idle)
    val authState: StateFlow<UiState<UserInfo>> = _authState.asStateFlow()

    private val _resetState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val resetState: StateFlow<UiState<Unit>> = _resetState.asStateFlow()

    val currentUser get() = authRepo.currentUser
    val isLoggedIn get() = authRepo.isLoggedIn
    val currentUserId get() = authRepo.currentUserId
    val currentUserName get() = authRepo.currentUserName
    val currentUserPhone get() = authRepo.currentUserPhone

    fun signUp(email: String, password: String, name: String = "", phone: String = "") {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            authRepo.signUp(email, password, name, phone).fold(
                onSuccess = { _authState.value = UiState.Success(it) },
                onFailure = { _authState.value = UiState.Error(it.message ?: "Sign up failed") }
            )
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            authRepo.signIn(email, password).fold(
                onSuccess = { _authState.value = UiState.Success(it) },
                onFailure = { _authState.value = UiState.Error(it.message ?: "Sign in failed") }
            )
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            authRepo.signInAnonymously().fold(
                onSuccess = { _authState.value = UiState.Success(it) },
                onFailure = { _authState.value = UiState.Error(it.message ?: "Failed") }
            )
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetState.value = UiState.Loading
            authRepo.resetPassword(email).fold(
                onSuccess = { _resetState.value = UiState.Success(Unit) },
                onFailure = { _resetState.value = UiState.Error(it.message ?: "Reset failed") }
            )
        }
    }

    fun signOut() {
        authRepo.signOut()
        _authState.value = UiState.Idle
    }

    fun resetAuthState() {
        _authState.value = UiState.Idle
    }
}
