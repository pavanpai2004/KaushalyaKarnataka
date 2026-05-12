package com.kaushalya.karnataka.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val supabase: SupabaseClient
) {
    val currentUser: UserInfo? get() = supabase.auth.currentUserOrNull()
    val isLoggedIn: Boolean get() = supabase.auth.currentSessionOrNull() != null
    val currentUserId: String get() = supabase.auth.currentUserOrNull()?.id ?: ""

    suspend fun signUp(email: String, password: String): Result<UserInfo> = runCatching {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        supabase.auth.currentUserOrNull() ?: throw Exception("Sign up failed: null user")
    }

    suspend fun signIn(email: String, password: String): Result<UserInfo> = runCatching {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        supabase.auth.currentUserOrNull() ?: throw Exception("Sign in failed: null user")
    }

    fun signOut() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                supabase.auth.signOut()
            } catch (_: Exception) {
                // Ignore sign out errors
            }
        }
    }

    suspend fun signInAnonymously(): Result<UserInfo> = runCatching {
        supabase.auth.signInAnonymously()
        supabase.auth.currentUserOrNull() ?: throw Exception("Anonymous sign in failed")
    }

    suspend fun resetPassword(email: String): Result<Unit> = runCatching {
        supabase.auth.resetPasswordForEmail(email)
    }
}
