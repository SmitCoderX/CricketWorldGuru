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
-keepattributes Annotation
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-dontwarn com.crashlytics.**
# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-printmapping build/outputs/mapping/release/mapping.txt
-printmapping build/outputs/mapping/release/seeds.txt
-printmapping build/outputs/mapping/release/usage.txt

-keep class com.wedoapps.cricketLiveLine.*
-keep public class com.wedoapps.cricketLiveLine.* { }
-keep class com.wedoapps.cricketLiveLine.utils.* {  }

-keepclassmembernames class com.wedoapps.cricketLiveLine.model.* { }
-keepclassmembernames class com.wedoapps.cricketLiveLine.utils.* {  }
-keep public class com.wedoapps.cricketLiveLine.utils.ApplicationList { *; }
-keep public class com.wedoapps.cricketLiveLine.utils.GetApplicationList { *; }
-keep public class com.wedoapps.cricketLiveLine.utils.Apps { *; }
-keep public class com.wedoapps.cricketLiveLine.utils.Apps { *; }
-keep public class com.wedoapps.cricketLiveLine.model.info.* { *; }
-keep public class com.wedoapps.cricketLiveLine.model.info.Info { *; }
-keep public class com.wedoapps.cricketLiveLine.model.* { *; }
-keep public class com.wedoapps.cricketLiveLine.model.matchBet.* { *; }
-keep public class com.wedoapps.cricketLiveLine.model.Series.* { *; }
-keep public class com.wedoapps.cricketLiveLine.model.sessionBet.* { *; }
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule.*
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
-dontwarn android.support.v7.**
-keep class android.support.v7.* {}
-keep interface android.support.v7.* {}
-keepattributes *Annotation,Signature
-dontwarn com.github.siyamed.**
-keep class com.github.siyamed.shapeimageview.*{ *; }