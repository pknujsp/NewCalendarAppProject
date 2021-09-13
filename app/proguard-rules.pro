# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#keep
-keep public class * { public protected *; }
-keep class com.zerodsoft.calendarplatform.retrofit.queryresponse.** { *; }
-keep class com.zerodsoft.calendarplatform.retrofit.paremeters.** { *; }
-keep class com.zerodsoft.calendarplatform.room.dto.** { *; }
-keepclassmembers class com.zerodsoft.calendarplatform.room.dto.** { *; }
-keep enum com.zerodsoft.common.** { *;}
-keep class com.zerodsoft.common.** { *;}
-keep class com.zerodsoft.etc.** { *;}
-keep class com.zerodsoft.navermap.** { *;}
-keep class com.zerodsoft.sgis.** { *;}
-keep class com.naver.** { *;}
-keep class com.luckycatlabs.sunrisesunset.** { *;}