# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in proguard-android-optimize.txt

# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.example.jifenapp.**$$serializer { *; }
-keepclassmembers class com.example.jifenapp.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.jifenapp.** {
    kotlinx.serialization.KSerializer serializer(...);
}
