package com.kaushalya.karnataka.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import com.kaushalya.karnataka.data.model.HireRequest
import com.kaushalya.karnataka.data.model.Notification
import com.kaushalya.karnataka.data.model.Review
import com.kaushalya.karnataka.data.model.Service
import com.kaushalya.karnataka.data.model.Worker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseRepository @Inject constructor(
    private val supabase: SupabaseClient
) {
    // ─── Workers ─────────────────────────────────────────────────────────────

    fun getAllWorkers(): Flow<List<Worker>> = flow {
        val workers = supabase.postgrest["workers"]
            .select() {
                order("average_rating", Order.DESCENDING)
            }
            .decodeList<Worker>()
        emit(workers)
    }

    fun getWorkersByCategory(category: String): Flow<List<Worker>> = flow {
        val workers = supabase.postgrest["workers"]
            .select {
                filter {
                    eq("trade_category", category)
                }
                order("average_rating", Order.DESCENDING)
            }
            .decodeList<Worker>()
        emit(workers)
    }

    fun getWorkerById(workerId: String): Flow<Worker?> = flow {
        val worker = supabase.postgrest["workers"]
            .select {
                filter {
                    eq("id", workerId)
                }
            }
            .decodeSingleOrNull<Worker>()
        emit(worker)
    }

    fun searchWorkers(query: String): Flow<List<Worker>> = flow {
        val workers = supabase.postgrest["workers"]
            .select {
                filter {
                    or {
                        ilike("name", "%$query%")
                        ilike("trade_category", "%$query%")
                        ilike("location", "%$query%")
                    }
                }
                order("average_rating", Order.DESCENDING)
            }
            .decodeList<Worker>()
        emit(workers)
    }

    suspend fun createOrUpdateWorker(worker: Worker): Result<Unit> = runCatching {
        supabase.postgrest["workers"].upsert(worker)
    }

    suspend fun deleteWorker(workerId: String): Result<Unit> = runCatching {
        supabase.postgrest["workers"].delete {
            filter { eq("id", workerId) }
        }
    }

    // ─── Services ─────────────────────────────────────────────────────────────

    fun getServicesForWorker(workerId: String): Flow<List<Service>> = flow {
        val services = supabase.postgrest["services"]
            .select {
                filter { eq("worker_id", workerId) }
            }
            .decodeList<Service>()
        emit(services)
    }

    suspend fun addService(workerId: String, service: Service): Result<Unit> = runCatching {
        supabase.postgrest["services"].upsert(service.copy(workerId = workerId))
    }

    suspend fun updateService(service: Service): Result<Unit> = runCatching {
        supabase.postgrest["services"].upsert(service)
    }

    suspend fun deleteService(workerId: String, serviceId: String): Result<Unit> = runCatching {
        supabase.postgrest["services"].delete {
            filter {
                eq("worker_id", workerId)
                eq("id", serviceId)
            }
        }
    }

    // ─── Reviews ─────────────────────────────────────────────────────────────

    fun getAllReviews(): Flow<List<Review>> = flow {
        val reviews = supabase.postgrest["reviews"]
            .select()
            .decodeList<Review>()
        emit(reviews)
    }

    fun getReviewsForWorker(workerId: String): Flow<List<Review>> = flow {
        val reviews = supabase.postgrest["reviews"]
            .select {
                filter { eq("worker_id", workerId) }
                order("created_at", Order.DESCENDING)
            }
            .decodeList<Review>()
        emit(reviews)
    }

    suspend fun addReview(workerId: String, review: Review): Result<Unit> = runCatching {
        supabase.postgrest["reviews"].insert(review.copy(workerId = workerId))

        // Call RPC function to recalculate worker rating (SECURITY DEFINER bypasses RLS)
        supabase.postgrest.rpc(
            "update_worker_rating",
            buildJsonObject { put("p_worker_id", workerId) }
        )
    }

    // ─── Hire Requests ─────────────────────────────────────────────────────────

    suspend fun sendHireRequest(request: HireRequest): Result<Unit> = runCatching {
        supabase.postgrest["hire_requests"].insert(request)
    }

    fun getHireRequestsForWorker(workerId: String): Flow<List<HireRequest>> = flow {
        val requests = supabase.postgrest["hire_requests"]
            .select {
                filter { eq("worker_id", workerId) }
                order("created_at", Order.DESCENDING)
            }
            .decodeList<HireRequest>()
        emit(requests)
    }

    fun getHireRequestsForCustomer(customerId: String): Flow<List<HireRequest>> = flow {
        val requests = supabase.postgrest["hire_requests"]
            .select {
                filter { eq("customer_id", customerId) }
                order("created_at", Order.DESCENDING)
            }
            .decodeList<HireRequest>()
        emit(requests)
    }

    suspend fun updateHireRequestStatus(requestId: String, status: String): Result<Unit> = runCatching {
        supabase.postgrest["hire_requests"].update(
            { set("status", status) }
        ) {
            filter { eq("id", requestId) }
        }
    }

    // ─── Notifications ─────────────────────────────────────────────────────────

    fun getNotifications(userId: String): Flow<List<Notification>> = flow {
        val notifications = supabase.postgrest["notifications"]
            .select {
                filter { eq("user_id", userId) }
                order("created_at", Order.DESCENDING)
            }
            .decodeList<Notification>()
        emit(notifications)
    }

    suspend fun markNotificationRead(notificationId: String): Result<Unit> = runCatching {
        supabase.postgrest["notifications"].update(
            { set("is_read", true) }
        ) {
            filter { eq("id", notificationId) }
        }
    }

    suspend fun markAllNotificationsRead(userId: String): Result<Unit> = runCatching {
        supabase.postgrest["notifications"].update(
            { set("is_read", true) }
        ) {
            filter { eq("user_id", userId) }
        }
    }

    // ─── Image Upload ─────────────────────────────────────────────────────────

    suspend fun uploadProfilePhoto(workerId: String, imageBytes: ByteArray): Result<String> = runCatching {
        val path = "$workerId/profile.jpg"
        supabase.storage["profiles"].upload(path, imageBytes, upsert = true)
        supabase.storage["profiles"].publicUrl(path)
    }

    suspend fun uploadPortfolioPhoto(workerId: String, imageBytes: ByteArray, fileName: String): Result<String> = runCatching {
        val path = "$workerId/$fileName"
        supabase.storage["portfolios"].upload(path, imageBytes, upsert = true)
        supabase.storage["portfolios"].publicUrl(path)
    }

    suspend fun getPortfolioPhotos(workerId: String): Result<List<String>> = runCatching {
        val items = supabase.storage["portfolios"].list(workerId)
        items.map { supabase.storage["portfolios"].publicUrl("$workerId/${it.name}") }
    }
}
