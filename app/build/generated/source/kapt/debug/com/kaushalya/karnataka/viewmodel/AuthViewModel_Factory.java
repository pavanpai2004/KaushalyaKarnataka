package com.kaushalya.karnataka.viewmodel;

import com.kaushalya.karnataka.data.repository.AuthRepository;
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
public final class AuthViewModel_Factory implements Factory<AuthViewModel> {
  private final Provider<AuthRepository> authRepoProvider;

  public AuthViewModel_Factory(Provider<AuthRepository> authRepoProvider) {
    this.authRepoProvider = authRepoProvider;
  }

  @Override
  public AuthViewModel get() {
    return newInstance(authRepoProvider.get());
  }

  public static AuthViewModel_Factory create(Provider<AuthRepository> authRepoProvider) {
    return new AuthViewModel_Factory(authRepoProvider);
  }

  public static AuthViewModel newInstance(AuthRepository authRepo) {
    return new AuthViewModel(authRepo);
  }
}
