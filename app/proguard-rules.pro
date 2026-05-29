# ProGuard rules for Shopping_Site_Andrio
# Applies to both R8 optimization and ProGuard minification

# === Data models (Gson serialization) ===
-keep class com.example.shopping_site_andrio.data.model.** { *; }

# === Retrofit ===
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# === Gson ===
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.stream.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# === OkHttp ===
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# === Hilt / Dagger ===
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# === Kotlin ===
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-keepclassmembers class * {
    public <init>(...);
}

# === Compose ===
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# === DataStore ===
-keep class androidx.datastore.** { *; }

# === Inner classes for data classes ===
-keepclassmembers class com.example.shopping_site_andrio.** {
    public <init>(...);
}
