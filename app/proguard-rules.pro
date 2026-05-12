# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keep class com.kaushalya.karnataka.data.model.** { *; }
-keep class io.github.jan.supabase.** { *; }
-keep class io.ktor.** { *; }
-dontwarn org.slf4j.**
