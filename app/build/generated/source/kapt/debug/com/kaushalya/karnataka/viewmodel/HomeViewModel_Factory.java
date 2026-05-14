package com.kaushalya.karnataka.viewmodel;

import com.kaushalya.karnataka.data.repository.AuthRepository;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<SupabaseRepository> repositoryProvider;

  private final Provider<AuthRepository> authRepoProvider;

  public HomeViewModel_Factory(Provider<SupabaseRepository> repositoryProvider,
      Provider<AuthRepository> authRepoProvider) {
    this.repositoryProvider = repositoryProvider;
    this.authRepoProvider = authRepoProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(repositoryProvider.get(), authRepoProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<SupabaseRepository> repositoryProvider,
      Provider<AuthRepository> authRepoProvider) {
    return new HomeViewModel_Factory(repositoryProvider, authRepoProvider);
  }

  public static HomeViewModel newInstance(SupabaseRepository repository, AuthRepository authRepo) {
    return new HomeViewModel(repository, authRepo);
  }
}
