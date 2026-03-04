# Gson ProGuard rules
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements com.google.gson.TypeAdapterFactory
-keep public class * implements com.google.gson.JsonSerializer
-keep public class * implements com.google.gson.JsonDeserializer
-keep class com.google.gson.internal.bind.TypeAdapters

# Prevent R8 from removing or renaming fields in your model classes
-keep class com.thesourceofcode.gita.model.** { *; }
