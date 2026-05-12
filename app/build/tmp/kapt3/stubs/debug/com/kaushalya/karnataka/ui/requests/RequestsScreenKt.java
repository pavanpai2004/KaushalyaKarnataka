package com.kaushalya.karnataka.ui.requests;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000,\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001aH\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0007\u001a0\u0010\t\u001a\u00020\u00012\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\u000b2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u0010H\u0007\u00a8\u0006\u0011"}, d2 = {"RequestCard", "", "request", "Lcom/kaushalya/karnataka/data/model/HireRequest;", "onClick", "Lkotlin/Function0;", "onAccept", "onDecline", "onComplete", "RequestsScreen", "onRequestClick", "Lkotlin/Function1;", "", "workerViewModel", "Lcom/kaushalya/karnataka/viewmodel/WorkerViewModel;", "authViewModel", "Lcom/kaushalya/karnataka/viewmodel/AuthViewModel;", "app_debug"})
public final class RequestsScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void RequestsScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onRequestClick, @org.jetbrains.annotations.NotNull()
    com.kaushalya.karnataka.viewmodel.WorkerViewModel workerViewModel, @org.jetbrains.annotations.NotNull()
    com.kaushalya.karnataka.viewmodel.AuthViewModel authViewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void RequestCard(@org.jetbrains.annotations.NotNull()
    com.kaushalya.karnataka.data.model.HireRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAccept, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDecline, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onComplete) {
    }
}