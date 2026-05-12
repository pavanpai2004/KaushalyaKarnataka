package com.kaushalya.karnataka.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class GeminiRepository_Factory implements Factory<GeminiRepository> {
  @Override
  public GeminiRepository get() {
    return newInstance();
  }

  public static GeminiRepository_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GeminiRepository newInstance() {
    return new GeminiRepository();
  }

  private static final class InstanceHolder {
    private static final GeminiRepository_Factory INSTANCE = new GeminiRepository_Factory();
  }
}
