# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/cl-macmini-26/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontwarn android.support.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

#To decode stack trace from fabric/crashlytics
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# OkHttp
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**


# Okio
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**

-keep class com.fugu.CaptureUserData{ *; }
 -keep class com.fugu.CaptureUserData$* {
    *;
 }
-keep class com.fugu.FuguConfig{ *; }
-keep class com.fugu.FuguColorConfig{ *; }
 -keep class com.fugu.FuguColorConfig* {
    *;
 }
-keep class com.fugu.FuguNotificationConfig{ *; }
-keep class com.fugu.model.** { *; }

# glide proguard rules
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
**[] $VALUES;
public *;
}
 -keeppackagenames org.jsoup.nodes

-keep public class org.jsoup.** {
	public *;
}
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}