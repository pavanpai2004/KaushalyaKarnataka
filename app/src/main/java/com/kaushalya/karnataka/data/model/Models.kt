package com.kaushalya.karnataka.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Worker(
    @SerialName("id") val id: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("trade_category") val tradeCategory: String = "",
    @SerialName("phone") val phone: String = "",
    @SerialName("profile_photo_url") val profilePhotoUrl: String = "",
    @SerialName("bio") val bio: String = "",
    @SerialName("average_rating") val averageRating: Double = 0.0,
    @SerialName("total_ratings") val totalRatings: Int = 0,
    @SerialName("location") val location: String = "",
    @SerialName("latitude") val latitude: Double = 0.0,
    @SerialName("longitude") val longitude: Double = 0.0,
    @SerialName("experience_years") val experienceYears: Int = 0,
    @SerialName("is_available") val isAvailable: Boolean = true,
    @SerialName("is_verified") val isVerified: Boolean = false,
    @SerialName("role") val role: String = "worker",
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class Service(
    @SerialName("id") val id: String = "",
    @SerialName("worker_id") val workerId: String = "",
    @SerialName("service_name") val serviceName: String = "",
    @SerialName("category") val category: String = "",
    @SerialName("price_type") val priceType: String = "Fixed",
    @SerialName("price") val price: Double = 0.0,
    @SerialName("description") val description: String = "",
    @SerialName("estimated_time") val estimatedTime: String = "",
    @SerialName("is_active") val isActive: Boolean = true
)

@Serializable
data class Review(
    @SerialName("id") val id: String = "",
    @SerialName("worker_id") val workerId: String = "",
    @SerialName("customer_id") val customerId: String = "",
    @SerialName("customer_name") val customerName: String = "",
    @SerialName("rating") val rating: Float = 0f,
    @SerialName("review_text") val reviewText: String = "",
    @SerialName("is_recommended") val isRecommended: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class HireRequest(
    @SerialName("id") val id: String = "",
    @SerialName("worker_id") val workerId: String = "",
    @SerialName("customer_id") val customerId: String = "",
    @SerialName("customer_name") val customerName: String = "",
    @SerialName("customer_phone") val customerPhone: String = "",
    @SerialName("service_requested") val serviceRequested: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("status") val status: String = "Pending",
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class Notification(
    @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("message") val message: String = "",
    @SerialName("type") val type: String = "",
    @SerialName("is_read") val isRead: Boolean = false,
    @SerialName("reference_id") val referenceId: String = "",
    @SerialName("created_at") val createdAt: String? = null
)

// UI State wrappers
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    object Idle : UiState<Nothing>()
}

// Trade categories
object TradeCategories {
    val list = listOf(
        "Electrician",
        "Plumber",
        "Carpenter",
        "Painter",
        "AC Technician",
        "Mason",
        "Welder",
        "Mechanic",
        "Tiler",
        "Pest Control",
        "Gardener",
        "Cleaner"
    )

    val icons = mapOf(
        "Electrician" to "⚡",
        "Plumber" to "🔧",
        "Carpenter" to "🪚",
        "Painter" to "🎨",
        "AC Technician" to "❄️",
        "Mason" to "🧱",
        "Welder" to "🔥",
        "Mechanic" to "🔩",
        "Tiler" to "🏗️",
        "Pest Control" to "🐛",
        "Gardener" to "🌿",
        "Cleaner" to "🧹"
    )
}

// Request status
object RequestStatus {
    const val PENDING = "Pending"
    const val ACCEPTED = "Accepted"
    const val IN_PROGRESS = "In Progress"
    const val COMPLETED = "Completed"
    const val CANCELLED = "Cancelled"
    const val DECLINED = "Declined"
}
