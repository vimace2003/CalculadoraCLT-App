# Rotas de navegação usam kotlinx.serialization (@Serializable) refletidas pelo Navigation Compose.
-keepattributes *Annotation*,InnerClasses
-keep,includedescriptorclasses class com.calculadoraclt.app.navigation.**$$serializer { *; }
-keepclassmembers class com.calculadoraclt.app.navigation.** {
    *** Companion;
}
-keepclasseswithmembers class com.calculadoraclt.app.navigation.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# WorkManager e' inicializado internamente pelo SDK do Google Mobile Ads via
# androidx.startup.InitializationProvider. O R8 remove WorkDatabase_Impl por
# achar que nao e' usada (so' e' referenciada por reflexao), o que derruba o
# app na abertura em builds de release. Ver: "Failed to create an instance of
# androidx.work.impl.WorkDatabase".
-keep class androidx.work.impl.WorkDatabase_Impl { *; }
-keep class * extends androidx.work.ListenableWorker {
    <init>(android.content.Context, androidx.work.WorkerParameters);
}
