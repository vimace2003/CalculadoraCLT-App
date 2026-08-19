# Rotas de navegação usam kotlinx.serialization (@Serializable) refletidas pelo Navigation Compose.
-keepattributes *Annotation*,InnerClasses
-keep,includedescriptorclasses class com.calculadoraclt.app.navigation.**$$serializer { *; }
-keepclassmembers class com.calculadoraclt.app.navigation.** {
    *** Companion;
}
-keepclasseswithmembers class com.calculadoraclt.app.navigation.** {
    kotlinx.serialization.KSerializer serializer(...);
}
