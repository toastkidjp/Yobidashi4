#-ignorewarnings


# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn jogamp.**
-dontwarn com.jogamp.newt.javafx.**
-dontwarn com.jogamp.newt.swt.**
-dontwarn com.jogamp.newt.nativewindow.javafx.**
-dontwarn com.jogamp.newt.nativewindow.swt.**
-dontwarn com.jogamp.opengl.javafx.**
-dontwarn com.jogamp.opengl.swt.**
-dontwarn org.apache.commons.compress.**

-keep class org.ocpsoft.prettytime.i18n** { *; }
-keep class kotlinx.serialization.internal.** {*;}
-keep class org.apache.lucene.** {*;}
-keep class com.google.gson.** {*;}
-keep class org.objectweb.** {*;}
-keep class org.tukaani.** {*;}
-keep class com.github.luben.** {*;}
