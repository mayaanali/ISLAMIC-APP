# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Room Entities and DAOs
-keep class com.example.data.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# JSON Models and DTOs (Moshi / Gson / Kotlin Serialization reflection)
-keepclassmembers class com.example.data.** {
    <fields>;
    <methods>;
}
-keep class com.example.utils.QuestVerificationResult { *; }

# OkHttp & Coroutines
-dontwarn okhttp3.**
-dontwarn okio.**
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Strip debug and verbose logs in production
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# Preserve SecurityGuard and Keystore primitives
-keep class com.example.utils.SecurityGuard { *; }


