## Add project specific ProGuard rules here.
## You can control the set of applied configuration files using the
## proguardFiles setting in build.gradle.
##
## For more details, see
##   http://developer.android.com/guide/developing/tools/proguard.html
#
## If your project uses WebView with JS, uncomment the following
## and specify the fully qualified class name to the JavaScript interface
## class:
##-keepclassmembers class fqcn.of.javascript.interface.for.webview {
##   public *;
##}
#
## Uncomment this to preserve the line number information for
## debugging stack traces.
##-keepattributes SourceFile,LineNumberTable
#
## If you keep the line number information, uncomment this to
## hide the original source file name.
##-renamesourcefileattribute SourceFile
#
##压缩级别0-7，Android一般为5(对代码迭代优化的次数)
#-optimizationpasses 5
#
##不使用大小写混合类名
#-dontusemixedcaseclassnames
#
# #混淆时记录日志
#-verbose
#
#
#-keep class io.agora.**{*;}
#
#-keep class org.beyka.tiffbitmapfactory.**{ *; }
#
#
##-------------- xpopup start -------------
#-dontwarn com.lxj.xpopup.widget.**
#-keep class com.lxj.xpopup.widget.**{*;}
##-------------- xpopup end ---------------
#
#
#
##-------------- okhttp3 start-------------
## OkHttp3
## https://github.com/square/okhttp
## okhttp
## JSR 305 annotations are for embedding nullability information.
#-dontwarn javax.annotation.**
#
## A resource is loaded with a relative path so the package of this class must be preserved.
#-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
#
## Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
#-dontwarn org.codehaus.mojo.animal_sniffer.*
#
## OkHttp platform used only on JVM and when Conscrypt dependency is available.
#-dontwarn okhttp3.internal.platform.ConscryptPlatform
##---------------- okhttp end--------------
#
#
##-------------- okio start ------------
## Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
#-dontwarn org.codehaus.mojo.animal_sniffer.*
##-------------- okio end ---------------
#
#
##-------------- jxl end ---------------
#
##指出jar包路徑
#-libraryjars ./libs/jxl-2.6.jar
#
#-dontwarn jxl.**
#-dontwarn jxl.write.**
#
#-keep class jxl.**{ *;}
#-keep class jxl.write.**{ *;}
#-keep class jxl.biff.**{ *;}
#-keep public class * extends jxl.**
#-keep public class * extends jxl.write.**
##-------------- jxl end ---------------
#
