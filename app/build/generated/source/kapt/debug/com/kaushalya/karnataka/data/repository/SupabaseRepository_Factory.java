package com.kaushalya.karnataka.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.github.jan.supabase.SupabaseClient;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class SupabaseRepository_Factory implements Factory<SupabaseRepository> {
  private final Provider<SupabaseClient> supabaseProvider;

  public SupabaseRepository_Factory(Provider<SupabaseClient> supabaseProvider) {
    this.supabaseProvider = supabaseProvider;
  }

  @Override
  public SupabaseRepository get() {
    return newInstance(supabaseProvider.get());
  }

  public static SupabaseRepository_Factory create(Provider<SupabaseClient> supabaseProvider) {
    return new SupabaseRepository_Factory(supabaseProvider);
  }

  public static SupabaseRepository newInstance(SupabaseClient supabase) {
    return new SupabaseRepository(supabase);
  }
}
