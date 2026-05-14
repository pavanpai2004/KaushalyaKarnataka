package com.kaushalya.karnataka.data.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u001c\n\u0002\u0010\u0012\n\u0002\b\u0007\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J,\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\f\u0010\rJ,\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\u0010H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0011\u0010\u0012J$\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\u0014\u001a\u00020\u0015H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0016\u0010\u0017J,\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u0019\u001a\u00020\tH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u001a\u0010\u001bJ$\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\tH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u001d\u0010\u001eJ\u0012\u0010\u001f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0!0 J\u0012\u0010\"\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00150!0 J\u001a\u0010#\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020$0!0 2\u0006\u0010%\u001a\u00020\tJ\u001a\u0010&\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020$0!0 2\u0006\u0010\b\u001a\u00020\tJ\u001a\u0010\'\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020(0!0 2\u0006\u0010)\u001a\u00020\tJ*\u0010*\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0!0\u00062\u0006\u0010\b\u001a\u00020\tH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b+\u0010\u001eJ\u001a\u0010,\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0!0 2\u0006\u0010\b\u001a\u00020\tJ\u001a\u0010-\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100!0 2\u0006\u0010\b\u001a\u00020\tJ\u0016\u0010.\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00150 2\u0006\u0010\b\u001a\u00020\tJ\u001a\u0010/\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00150!0 2\u0006\u00100\u001a\u00020\tJ$\u00101\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010)\u001a\u00020\tH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b2\u0010\u001eJ$\u00103\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u00104\u001a\u00020\tH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b5\u0010\u001eJ\u001a\u00106\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00150!0 2\u0006\u00107\u001a\u00020\tJ$\u00108\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u00109\u001a\u00020$H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b:\u0010;J,\u0010<\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010=\u001a\u00020\t2\u0006\u0010>\u001a\u00020\tH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b?\u0010\u001bJ$\u0010@\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\u000f\u001a\u00020\u0010H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\bA\u0010BJ4\u0010C\u001a\b\u0012\u0004\u0012\u00020\t0\u00062\u0006\u0010\b\u001a\u00020\t2\u0006\u0010D\u001a\u00020E2\u0006\u0010F\u001a\u00020\tH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\bG\u0010HJ,\u0010I\u001a\b\u0012\u0004\u0012\u00020\t0\u00062\u0006\u0010\b\u001a\u00020\t2\u0006\u0010D\u001a\u00020EH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\bJ\u0010KR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006L"}, d2 = {"Lcom/kaushalya/karnataka/data/repository/SupabaseRepository;", "", "supabase", "Lio/github/jan/supabase/SupabaseClient;", "(Lio/github/jan/supabase/SupabaseClient;)V", "addReview", "Lkotlin/Result;", "", "workerId", "", "review", "Lcom/kaushalya/karnataka/data/model/Review;", "addReview-0E7RQCE", "(Ljava/lang/String;Lcom/kaushalya/karnataka/data/model/Review;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "addService", "service", "Lcom/kaushalya/karnataka/data/model/Service;", "addService-0E7RQCE", "(Ljava/lang/String;Lcom/kaushalya/karnataka/data/model/Service;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "createOrUpdateWorker", "worker", "Lcom/kaushalya/karnataka/data/model/Worker;", "createOrUpdateWorker-gIAlu-s", "(Lcom/kaushalya/karnataka/data/model/Worker;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteService", "serviceId", "deleteService-0E7RQCE", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteWorker", "deleteWorker-gIAlu-s", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllReviews", "Lkotlinx/coroutines/flow/Flow;", "", "getAllWorkers", "getHireRequestsForCustomer", "Lcom/kaushalya/karnataka/data/model/HireRequest;", "customerId", "getHireRequestsForWorker", "getNotifications", "Lcom/kaushalya/karnataka/data/model/Notification;", "userId", "getPortfolioPhotos", "getPortfolioPhotos-gIAlu-s", "getReviewsForWorker", "getServicesForWorker", "getWorkerById", "getWorkersByCategory", "category", "markAllNotificationsRead", "markAllNotificationsRead-gIAlu-s", "markNotificationRead", "notificationId", "markNotificationRead-gIAlu-s", "searchWorkers", "query", "sendHireRequest", "request", "sendHireRequest-gIAlu-s", "(Lcom/kaushalya/karnataka/data/model/HireRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateHireRequestStatus", "requestId", "status", "updateHireRequestStatus-0E7RQCE", "updateService", "updateService-gIAlu-s", "(Lcom/kaushalya/karnataka/data/model/Service;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "uploadPortfolioPhoto", "imageBytes", "", "fileName", "uploadPortfolioPhoto-BWLJW6A", "(Ljava/lang/String;[BLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "uploadProfilePhoto", "uploadProfilePhoto-0E7RQCE", "(Ljava/lang/String;[BLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class SupabaseRepository {
    @org.jetbrains.annotations.NotNull()
    private final io.github.jan.supabase.SupabaseClient supabase = null;
    
    @javax.inject.Inject()
    public SupabaseRepository(@org.jetbrains.annotations.NotNull()
    io.github.jan.supabase.SupabaseClient supabase) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.kaushalya.karnataka.data.model.Worker>> getAllWorkers() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.kaushalya.karnataka.data.model.Worker>> getWorkersByCategory(@org.jetbrains.annotations.NotNull()
    java.lang.String category) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.kaushalya.karnataka.data.model.Worker> getWorkerById(@org.jetbrains.annotations.NotNull()
    java.lang.String workerId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.kaushalya.karnataka.data.model.Worker>> searchWorkers(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.kaushalya.karnataka.data.model.Service>> getServicesForWorker(@org.jetbrains.annotations.NotNull()
    java.lang.String workerId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.kaushalya.karnataka.data.model.Review>> getAllReviews() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.kaushalya.karnataka.data.model.Review>> getReviewsForWorker(@org.jetbrains.annotations.NotNull()
    java.lang.String workerId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.kaushalya.karnataka.data.model.HireRequest>> getHireRequestsForWorker(@org.jetbrains.annotations.NotNull()
    java.lang.String workerId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.kaushalya.karnataka.data.model.HireRequest>> getHireRequestsForCustomer(@org.jetbrains.annotations.NotNull()
    java.lang.String customerId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.kaushalya.karnataka.data.model.Notification>> getNotifications(@org.jetbrains.annotations.NotNull()
    java.lang.String userId) {
        return null;
    }
}