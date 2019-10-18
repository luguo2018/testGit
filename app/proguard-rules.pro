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

-keep class com.jmhy.sdk.model.** {*;}
-keep class com.jmhy.sdk.common.** {*;}
-keep class com.jmhy.sdk.push.** {*;}
-keep class com.jmhy.sdk.http.ApiRequestListener {*;}
-keepnames class com.jmhy.sdk.http.ApiAsyncTask
-keep class com.jmhy.sdk.sdk.StatisticsSDK {*;}
-keepclasseswithmembers class com.jmhy.sdk.sdk.HashmapToJson {
    public java.lang.String toJson(java.util.HashMap);
}
-keepclasseswithmembers class com.jmhy.sdk.sdk.JmhyApi {
    public static com.jmhy.sdk.sdk.JmhyApi get();
    public com.jmhy.sdk.utils.DeviceInfo getDeviceInfo();
}
-keepclasseswithmembers class com.jmhy.sdk.sdk.PayDataRequest {
    public static <methods>;
}
-keepclasseswithmembers class com.jmhy.sdk.config.AppConfig{
    public static java.lang.String openid;
    public static java.lang.String Token;
    public static int appId;
    public static java.lang.String appKey;
    public static java.lang.String agent;
    public static java.lang.String is_sdk_float_on;
    public static java.lang.String switch_login;
    public static java.lang.String h5GameUrl;
    public static java.lang.String loginH5GameUrl;
    public static <methods>;
}
-keepclasseswithmembers class com.jmhy.sdk.config.WebApi{
    public static <fields>;
    public static <methods>;
}
-keepclasseswithmembers class com.jmhy.sdk.utils.DeviceInfo {
    public java.lang.String getImei();
}
-keepclasseswithmembers class com.jmhy.sdk.utils.Seference {
    public void saveAccount(java.lang.String, java.lang.String, java.lang.String);
}
-keepclasseswithmembers class com.jmhy.sdk.utils.Utils {
    public static <methods>;
}
-keepclasseswithmembers class com.jmhy.sdk.utils.ConfigUtils {
    public static <methods>;
}

-keep class org.apache.** {*;}
-keep class android.net.** {*;}
-keep class com.android.internal.http.multipart.** {*;}
-dontwarn org.apache.**
-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient