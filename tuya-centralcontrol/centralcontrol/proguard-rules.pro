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
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-repackageclasses 'com.tuya.smart.centralcontrol'

-keep class com.tuya.sdk.**{*;}

-keep class com.tuya.smart.home.**{*;}

-keep class com.tuya.smart.mqtt.**{*;}

-keepnames interface com.tuya.smart.home.**{*;}

-keep class com.telink.crypto.**{*;}
-dontwarn  com.telink.**

-keep class com.tuya.sdk.blelib.**{*;}
-keep class com.tuya.sdk.blescan.**{*;}


-keep class com.tuya.smart.sdk.**{*;}
-keepnames interface com.tuya.smart.sdk.**{*;}

-keep class com.tuya.smart.common.**{*;}

-keep class com.tuya.smart.android.**{*;}
-keepnames interface com.tuya.smart.android.**{*;}
