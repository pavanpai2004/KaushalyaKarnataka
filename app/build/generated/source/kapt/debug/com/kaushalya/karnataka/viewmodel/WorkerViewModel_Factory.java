package com.kaushalya.karnataka.viewmodel;

import com.kaushalya.karnataka.data.repository.AuthRepository;
import com.kaushalya.karnataka.data.repository.GeminiRepository;
import com.kaushalya.karnataka.data.repository.SupabaseRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class WorkerViewModel_Factory implements Factory<WorkerViewModel> {
  private final Provider<SupabaseRepository> supabaseRepoProvider;

  private final Provider<GeminiRepository> geminiRepoProvider;

  private final Provider<AuthRepository> authRepoProvider;

  public WorkerViewModel_Factory(Provider<SupabaseRepository> supabaseRepoProvider,
      Provider<GeminiRepository> geminiRepoProvider, Provider<AuthRepository> authRepoProvider) {
    this.supabaseRepoProvider = supabaseRepoProvider;
    this.geminiRepoProvider = geminiRepoProvider;
    this.authRepoProvider = authRepoProvider;
  }

  @Override
  public WorkerViewModel get() {
    return newInstance(supabaseRepoProvider.get(), geminiRepoProvider.get(), authRepoProvider.get());
  }

  public static WorkerViewModel_Factory create(Provider<SupabaseRepository> supabaseRepoProvider,
      Provider<GeminiRepository> geminiRepoProvider, Provider<AuthRepository> authRepoProvider) {
    return new WorkerViewModel_Factory(supabaseRepoProvider, geminiRepoProvider, authRepoProvider);
  }

  public static WorkerViewModel newInstance(SupabaseRepository supabaseRepo,
      GeminiRepository geminiRepo, AuthRepository authRepo) {
    return new WorkerViewModel(supabaseRepo, geminiRepo, authRepo);
  }
}
